import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/body_metric.dart';
import '../models/exercise_template.dart';
import '../models/workout_session.dart';
import '../storage/repositories.dart';

/// The app starts with an offline in-memory repository. The composition root
/// can override this provider with `FitnessRepositories.openHive(...)` after
/// the platform storage path is available; widgets do not know which backend
/// is active.
final fitnessRepositoriesProvider = Provider<FitnessRepositories>(
  (ref) => FitnessRepositories.inMemory(),
);

final exerciseTemplatesRepositoryProvider =
    Provider<CrudRepository<ExerciseTemplate>>(
      (ref) => ref.watch(fitnessRepositoriesProvider).exerciseTemplates,
    );

final workoutSessionsRepositoryProvider =
    Provider<CrudRepository<WorkoutSession>>(
      (ref) => ref.watch(fitnessRepositoriesProvider).workoutSessions,
    );

final bodyMetricsRepositoryProvider = Provider<CrudRepository<BodyMetric>>(
  (ref) => ref.watch(fitnessRepositoriesProvider).bodyMetrics,
);

final exerciseTemplatesProvider =
    AsyncNotifierProvider<ExerciseTemplatesNotifier, List<ExerciseTemplate>>(
      ExerciseTemplatesNotifier.new,
    );

class ExerciseTemplatesNotifier extends AsyncNotifier<List<ExerciseTemplate>> {
  late CrudRepository<ExerciseTemplate> _repository;

  @override
  Future<List<ExerciseTemplate>> build() async {
    _repository = ref.watch(exerciseTemplatesRepositoryProvider);
    return _repository.list();
  }

  Future<void> save(ExerciseTemplate template) async {
    await _mutate(() => _repository.save(template));
  }

  Future<void> delete(String id) async {
    await _mutate(() => _repository.delete(id));
  }

  Future<void> replaceAll(Iterable<ExerciseTemplate> templates) async {
    await _mutate(() => _repository.replaceAll(templates));
  }

  Future<void> _mutate(Future<void> Function() mutation) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      await mutation();
      return _repository.list();
    });
  }
}

final workoutSessionsProvider =
    AsyncNotifierProvider<WorkoutSessionsNotifier, List<WorkoutSession>>(
      WorkoutSessionsNotifier.new,
    );

class WorkoutSessionsNotifier extends AsyncNotifier<List<WorkoutSession>> {
  late CrudRepository<WorkoutSession> _repository;

  @override
  Future<List<WorkoutSession>> build() async {
    _repository = ref.watch(workoutSessionsRepositoryProvider);
    return _repository.list();
  }

  Future<void> save(WorkoutSession session) async {
    await _mutate(() => _repository.save(session));
  }

  Future<void> delete(String id) async {
    await _mutate(() => _repository.delete(id));
  }

  Future<void> replaceAll(Iterable<WorkoutSession> sessions) async {
    await _mutate(() => _repository.replaceAll(sessions));
  }

  Future<void> _mutate(Future<void> Function() mutation) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      await mutation();
      return _repository.list();
    });
  }
}

final bodyMetricsProvider =
    AsyncNotifierProvider<BodyMetricsNotifier, List<BodyMetric>>(
      BodyMetricsNotifier.new,
    );

class BodyMetricsNotifier extends AsyncNotifier<List<BodyMetric>> {
  late CrudRepository<BodyMetric> _repository;

  @override
  Future<List<BodyMetric>> build() async {
    _repository = ref.watch(bodyMetricsRepositoryProvider);
    return _repository.list();
  }

  Future<void> save(BodyMetric metric) async {
    await _mutate(() => _repository.save(metric));
  }

  Future<void> delete(String id) async {
    await _mutate(() => _repository.delete(id));
  }

  Future<void> replaceAll(Iterable<BodyMetric> metrics) async {
    await _mutate(() => _repository.replaceAll(metrics));
  }

  Future<void> _mutate(Future<void> Function() mutation) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      await mutation();
      return _repository.list();
    });
  }
}
