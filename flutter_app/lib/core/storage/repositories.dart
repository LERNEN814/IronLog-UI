import 'package:hive_ce/hive_ce.dart';

import '../models/body_metric.dart';
import '../models/exercise_template.dart';
import '../models/workout_session.dart';
import '../settings/settings_store.dart';
import 'json_store.dart';

abstract interface class CrudRepository<T> {
  Future<List<T>> list();

  Future<T?> getById(String id);

  Future<void> save(T item);

  Future<void> delete(String id);

  Future<void> clear();

  Future<void> replaceAll(Iterable<T> items);
}

typedef JsonDecoder<T> = T Function(Map<String, dynamic> json);
typedef JsonEncoder<T> = Map<String, dynamic> Function(T value);
typedef IdReader<T> = String Function(T value);

class JsonCrudRepository<T> implements CrudRepository<T> {
  JsonCrudRepository({
    required this.store,
    required this.decode,
    required this.encode,
    required this.idOf,
  });

  final JsonStore store;
  final JsonDecoder<T> decode;
  final JsonEncoder<T> encode;
  final IdReader<T> idOf;

  @override
  Future<List<T>> list() async {
    final records = await store.readAll();
    return records.map(decode).toList(growable: false);
  }

  @override
  Future<T?> getById(String id) async {
    for (final item in await list()) {
      if (idOf(item) == id) return item;
    }
    return null;
  }

  @override
  Future<void> save(T item) => store.write(idOf(item), encode(item));

  @override
  Future<void> delete(String id) => store.delete(id);

  @override
  Future<void> clear() => store.clear();

  @override
  Future<void> replaceAll(Iterable<T> items) =>
      store.replaceAll({for (final item in items) idOf(item): encode(item)});
}

class InMemoryCrudRepository<T> implements CrudRepository<T> {
  InMemoryCrudRepository(this.idOf, [Iterable<T> initial = const []]) {
    for (final value in initial) {
      _items[idOf(value)] = value;
    }
  }

  final IdReader<T> idOf;
  final Map<String, T> _items = {};

  @override
  Future<List<T>> list() async => _items.values.toList(growable: false);

  @override
  Future<T?> getById(String id) async => _items[id];

  @override
  Future<void> save(T item) async {
    _items[idOf(item)] = item;
  }

  @override
  Future<void> delete(String id) async {
    _items.remove(id);
  }

  @override
  Future<void> clear() async {
    _items.clear();
  }

  @override
  Future<void> replaceAll(Iterable<T> items) async {
    _items
      ..clear()
      ..addEntries(items.map((item) => MapEntry(idOf(item), item)));
  }
}

class FitnessRepositories {
  FitnessRepositories({
    required this.exerciseTemplates,
    required this.workoutSessions,
    required this.bodyMetrics,
    required this.settings,
  });

  factory FitnessRepositories.inMemory() => FitnessRepositories(
    exerciseTemplates: InMemoryCrudRepository<ExerciseTemplate>(
      (value) => value.id,
    ),
    workoutSessions: InMemoryCrudRepository<WorkoutSession>(
      (value) => value.id,
    ),
    bodyMetrics: InMemoryCrudRepository<BodyMetric>((value) => value.id),
    settings: InMemorySettingsStore(),
  );

  final CrudRepository<ExerciseTemplate> exerciseTemplates;
  final CrudRepository<WorkoutSession> workoutSessions;
  final CrudRepository<BodyMetric> bodyMetrics;
  final SettingsStore settings;

  Future<void> replaceBackup({
    required Iterable<ExerciseTemplate> templates,
    required Iterable<WorkoutSession> sessions,
    required Iterable<BodyMetric> metrics,
  }) async {
    final oldTemplates = await exerciseTemplates.list();
    final oldSessions = await workoutSessions.list();
    final oldMetrics = await bodyMetrics.list();
    try {
      await exerciseTemplates.replaceAll(templates);
      await workoutSessions.replaceAll(sessions);
      await bodyMetrics.replaceAll(metrics);
    } on Object {
      await exerciseTemplates.replaceAll(oldTemplates);
      await workoutSessions.replaceAll(oldSessions);
      await bodyMetrics.replaceAll(oldMetrics);
      rethrow;
    }
  }

  static Future<FitnessRepositories> openHive({
    String? path,
    SettingsStore? settings,
  }) async {
    if (path != null) Hive.init(path);
    final exerciseBox = await Hive.openBox<dynamic>('exercise_templates');
    final workoutBox = await Hive.openBox<dynamic>('workout_sessions');
    final bodyMetricBox = await Hive.openBox<dynamic>('body_metrics');
    final settingsBox = await Hive.openBox<dynamic>('app_settings');
    return FitnessRepositories(
      exerciseTemplates: JsonCrudRepository<ExerciseTemplate>(
        store: HiveJsonStore(exerciseBox),
        decode: ExerciseTemplate.fromJson,
        encode: (value) => value.toJson(),
        idOf: (value) => value.id,
      ),
      workoutSessions: JsonCrudRepository<WorkoutSession>(
        store: HiveJsonStore(workoutBox),
        decode: WorkoutSession.fromJson,
        encode: (value) => value.toJson(),
        idOf: (value) => value.id,
      ),
      bodyMetrics: JsonCrudRepository<BodyMetric>(
        store: HiveJsonStore(bodyMetricBox),
        decode: BodyMetric.fromJson,
        encode: (value) => value.toJson(),
        idOf: (value) => value.id,
      ),
      settings: settings ?? HiveSettingsStore(settingsBox),
    );
  }
}
