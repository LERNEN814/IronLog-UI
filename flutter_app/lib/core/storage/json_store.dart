import 'dart:convert';

import 'package:hive_ce/hive_ce.dart';

/// Small persistence contract used by repositories. Values are JSON maps so
/// the same repository can run against Hive in production or an in-memory
/// implementation in tests and previews.
abstract interface class JsonStore {
  Future<List<Map<String, dynamic>>> readAll();

  Future<void> write(String key, Map<String, dynamic> value);

  Future<void> delete(String key);

  Future<void> clear();

  /// Replaces the complete store in one backend operation where supported.
  Future<void> replaceAll(Map<String, Map<String, dynamic>> values);
}

class HiveJsonStore implements JsonStore {
  HiveJsonStore(this.box);

  final Box<dynamic> box;

  @override
  Future<List<Map<String, dynamic>>> readAll() async {
    final values = <Map<String, dynamic>>[];
    for (final value in box.values) {
      try {
        final decoded = value is String ? jsonDecode(value) : value;
        if (decoded is Map) {
          values.add(Map<String, dynamic>.from(decoded));
        }
      } on FormatException {
        // A corrupt record is ignored so one bad entry does not block the
        // rest of the user's local records. Import validation handles full
        // backup payloads separately.
      }
    }
    return values;
  }

  @override
  Future<void> write(String key, Map<String, dynamic> value) =>
      box.put(key, jsonEncode(value));

  @override
  Future<void> delete(String key) => box.delete(key);

  @override
  Future<void> clear() => box.clear();

  @override
  Future<void> replaceAll(Map<String, Map<String, dynamic>> values) async {
    final snapshot = Map<dynamic, dynamic>.from(box.toMap());
    try {
      await box.clear();
      await box.putAll({
        for (final entry in values.entries) entry.key: jsonEncode(entry.value),
      });
    } on Object {
      await box.clear();
      await box.putAll(snapshot);
      rethrow;
    }
  }
}

class InMemoryJsonStore implements JsonStore {
  final Map<String, Map<String, dynamic>> _values = {};

  @override
  Future<List<Map<String, dynamic>>> readAll() async => _values.values
      .map((value) => Map<String, dynamic>.from(value))
      .toList(growable: false);

  @override
  Future<void> write(String key, Map<String, dynamic> value) async {
    _values[key] = Map<String, dynamic>.from(value);
  }

  @override
  Future<void> delete(String key) async {
    _values.remove(key);
  }

  @override
  Future<void> clear() async {
    _values.clear();
  }

  @override
  Future<void> replaceAll(Map<String, Map<String, dynamic>> values) async {
    final snapshot = Map<String, Map<String, dynamic>>.from(_values);
    try {
      _values
        ..clear()
        ..addAll(
          values.map(
            (key, value) => MapEntry(key, Map<String, dynamic>.from(value)),
          ),
        );
    } on Object {
      _values
        ..clear()
        ..addAll(snapshot);
      rethrow;
    }
  }
}
