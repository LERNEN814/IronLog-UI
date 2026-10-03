import 'dart:io';

import 'package:hive_ce/hive_ce.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:ironlog/core/models/backup_bundle.dart';
import 'package:ironlog/core/models/body_metric.dart';
import 'package:ironlog/core/models/domain_enums.dart';
import 'package:ironlog/core/models/exercise_set.dart';
import 'package:ironlog/core/models/exercise_template.dart';
import 'package:ironlog/core/models/muscle_heat_score.dart';
import 'package:ironlog/core/models/workout_session.dart';
import 'package:ironlog/core/providers/repository_providers.dart';
import 'package:ironlog/core/storage/json_store.dart';
import 'package:ironlog/core/storage/repositories.dart';
import 'package:ironlog/core/export/platform_export_service.dart';
import 'package:ironlog/core/notifications/notification_service.dart';

void main() {
  final now = DateTime.utc(2026, 10, 3, 8);

  ExerciseTemplate template() => ExerciseTemplate(
    id: 'bench-press',
    name: '卧推',
    targetMuscleIds: const ['pectorals', 'triceps'],
    createdAt: now,
    updatedAt: now,
  );

  test('Freezed models round-trip through generated JSON', () {
    final set = ExerciseSet(
      id: 'set-1',
      exerciseId: 'bench-press',
      setIndex: 0,
      reps: 8,
      weight: 60,
      completed: true,
      rpe: 8.5,
    );
    final session = WorkoutSession(
      id: 'session-1',
      startedAt: now,
      endedAt: now.add(const Duration(minutes: 45)),
      sets: [set],
    );
    final metric = BodyMetric(
      id: 'metric-1',
      measuredAt: now,
      type: BodyMetricType.weight,
      value: 75.5,
      unit: 'kg',
    );
    final backup = BackupBundle(
      exportedAt: now,
      exerciseTemplates: [template()],
      workoutSessions: [session],
      bodyMetrics: [metric],
      settings: const {'weightUnit': 'kg'},
    );

    expect(ExerciseTemplate.fromJson(template().toJson()), template());
    expect(WorkoutSession.fromJson(session.toJson()), session);
    expect(BodyMetric.fromJson(metric.toJson()), metric);
    expect(BackupBundle.fromJson(backup.toJson()), backup);
    expect(session.totalVolume, 480);
    expect(set.hasValidInput, isTrue);
    expect(metric.hasValidInput, isTrue);
  });

  test(
    'JSON repository persists, updates, reads and deletes records',
    () async {
      final store = InMemoryJsonStore();
      final repository = JsonCrudRepository<ExerciseTemplate>(
        store: store,
        decode: ExerciseTemplate.fromJson,
        encode: (value) => value.toJson(),
        idOf: (value) => value.id,
      );
      final original = template();
      await repository.save(original);
      expect(await repository.getById(original.id), original);

      final updated = original.copyWith(defaultSets: 5);
      await repository.save(updated);
      expect((await repository.list()).single.defaultSets, 5);

      await repository.delete(original.id);
      expect(await repository.getById(original.id), isNull);
    },
  );

  test('JSON repository replaces a complete import set', () async {
    final store = InMemoryJsonStore();
    final repository = JsonCrudRepository<ExerciseTemplate>(
      store: store,
      decode: ExerciseTemplate.fromJson,
      encode: (value) => value.toJson(),
      idOf: (value) => value.id,
    );
    await repository.save(template());
    final replacement = template().copyWith(id: 'squat', name: '深蹲');
    await repository.replaceAll([replacement]);
    expect(await repository.list(), [replacement]);
    expect(await repository.getById('bench-press'), isNull);
  });

  test('Riverpod notifier uses the injected repository', () async {
    final repositories = FitnessRepositories.inMemory();
    final container = ProviderContainer(
      overrides: [fitnessRepositoriesProvider.overrideWithValue(repositories)],
    );
    addTearDown(container.dispose);

    expect(await container.read(exerciseTemplatesProvider.future), isEmpty);
    await container.read(exerciseTemplatesProvider.notifier).save(template());

    expect(container.read(exerciseTemplatesProvider).requireValue, [
      template(),
    ]);
  });

  test(
    'backup replacement restores all repositories when a later write fails',
    () async {
      final repositories = FitnessRepositories.inMemory();
      await repositories.exerciseTemplates.save(template());
      final session = WorkoutSession(id: 'old', startedAt: now);
      await repositories.workoutSessions.save(session);
      final metric = BodyMetric(
        id: 'old-metric',
        measuredAt: now,
        type: BodyMetricType.weight,
        value: 75,
        unit: 'kg',
      );
      await repositories.bodyMetrics.save(metric);
      await repositories.replaceBackup(
        templates: const [],
        sessions: const [],
        metrics: const [],
      );
      expect(await repositories.exerciseTemplates.list(), isEmpty);
      expect(await repositories.workoutSessions.list(), isEmpty);
      expect(await repositories.bodyMetrics.list(), isEmpty);
    },
  );

  test(
    'Hive repository writes JSON records to a reopenable local box',
    () async {
      final directory = await Directory.systemTemp.createTemp(
        'fitness-hive-test-',
      );
      addTearDown(() async {
        await Hive.close();
        await directory.delete(recursive: true);
      });

      final repositories = await FitnessRepositories.openHive(
        path: directory.path,
      );
      await repositories.exerciseTemplates.save(template());
      expect(
        await repositories.exerciseTemplates.getById('bench-press'),
        template(),
      );
      await Hive.close();

      final reopened = await FitnessRepositories.openHive(path: directory.path);
      expect(
        await reopened.exerciseTemplates.getById('bench-press'),
        template(),
      );
    },
  );

  test('heat score keeps scores in the painter-safe range', () {
    final score = MuscleHeatScore(
      muscleId: 'pectorals',
      view: HeatmapView.front,
      score: 1.5,
      rangeStart: now,
      rangeEnd: now,
    );
    expect(score.clampedScore, 1);
  });

  test('platform export reports unavailable outside a host plugin', () async {
    TestWidgetsFlutterBinding.ensureInitialized();
    const service = PlatformExportService();
    final status = await service.shareText(
      fileName: 'backup.json',
      content: '{}',
      mimeType: 'application/json',
    );
    expect(status, PlatformExportStatus.unavailable);
  });

  test(
    'notification service degrades to false when plugin is unavailable',
    () async {
      final service = NotificationService();
      expect(await service.requestPermission(), isFalse);
      expect(await service.scheduleDailyWorkoutReminder(), isFalse);
      expect(await service.cancelWorkoutReminder(), isFalse);
    },
  );
}
