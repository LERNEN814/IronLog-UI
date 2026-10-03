import 'dart:convert';

import 'package:hive_ce/hive_ce.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'app_settings.dart';

/// Persistence boundary for small user preferences.
///
/// Implementations must treat malformed or unavailable data as defaults. This
/// keeps settings failures isolated from the workout repositories.
abstract interface class SettingsStore {
  Future<AppSettings> read();

  Future<void> write(AppSettings settings);
}

class InMemorySettingsStore implements SettingsStore {
  InMemorySettingsStore([this._settings = AppSettings.defaults]);

  AppSettings _settings;

  @override
  Future<AppSettings> read() async => _settings;

  @override
  Future<void> write(AppSettings settings) async {
    _settings = settings;
  }
}

class SharedPreferencesSettingsStore implements SettingsStore {
  SharedPreferencesSettingsStore(this.preferences);

  static const _key = 'ironlog.settings.v1';
  final SharedPreferences preferences;

  @override
  Future<AppSettings> read() async {
    try {
      final raw = preferences.getString(_key);
      if (raw == null || raw.isEmpty) return AppSettings.defaults;
      final decoded = jsonDecode(raw);
      if (decoded is Map) {
        return AppSettings.fromJson(Map<String, dynamic>.from(decoded));
      }
    } on Object {
      // A stale/corrupt preference must not block app startup.
    }
    return AppSettings.defaults;
  }

  @override
  Future<void> write(AppSettings settings) async {
    final ok = await preferences.setString(_key, jsonEncode(settings.toJson()));
    if (!ok) throw StateError('设置未能写入本地存储');
  }
}

/// Optional Hive fallback for environments where the preferences plugin is
/// unavailable. It is used by the composition root only when explicitly
/// requested, while normal Android startup uses SharedPreferences.
class HiveSettingsStore implements SettingsStore {
  HiveSettingsStore(this.box);

  static const _key = 'settings';
  final Box<dynamic> box;

  @override
  Future<AppSettings> read() async {
    try {
      final value = box.get(_key);
      final decoded = value is String ? jsonDecode(value) : value;
      if (decoded is Map) {
        return AppSettings.fromJson(Map<String, dynamic>.from(decoded));
      }
    } on Object {
      // Keep defaults on malformed local settings.
    }
    return AppSettings.defaults;
  }

  @override
  Future<void> write(AppSettings settings) =>
      box.put(_key, jsonEncode(settings.toJson()));
}
