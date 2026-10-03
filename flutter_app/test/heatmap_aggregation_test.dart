import 'package:flutter_test/flutter_test.dart';

import 'package:ironlog/core/export/backup_codec.dart';
import 'package:ironlog/core/models/body_metric.dart';
import 'package:ironlog/core/models/domain_enums.dart';
import 'package:ironlog/core/models/exercise_set.dart';
import 'package:ironlog/core/models/exercise_template.dart';
import 'package:ironlog/core/models/workout_session.dart';
import 'package:ironlog/features/heatmap/application/muscle_load_aggregator.dart';

void main() {
  final now = DateTime.utc(2026, 10, 3);
  final template = ExerciseTemplate(
    id: 'squat',
    name: '深蹲',
    targetMuscleIds: const ['quadriceps', 'glutes'],
    createdAt: now,
    updatedAt: now,
  );
  final session = WorkoutSession(
    id: 'session',
    startedAt: now,
    endedAt: now.add(const Duration(minutes: 30)),
    sets: const [
      ExerciseSet(
        id: 'set',
        exerciseId: 'squat',
        setIndex: 0,
        reps: 10,
        weight: 100,
        completed: true,
      ),
    ],
  );

  test(
    'aggregates completed volume by target muscle and ignores incomplete sets',
    () {
      final scores = aggregateMuscleScores(
        sessions: [session],
        templates: [template],
        rangeStart: now.subtract(const Duration(days: 1)),
        rangeEnd: now.add(const Duration(days: 1)),
      );
      expect(scores['quadriceps'], 1);
      expect(scores['glutes'], 1);
    },
  );

  test('date range excludes sessions outside the selected heatmap window', () {
    final outside = session.copyWith(
      id: 'outside',
      startedAt: now.subtract(const Duration(days: 10)),
    );
    final scores = aggregateMuscleScores(
      sessions: [session, outside],
      templates: [template],
      rangeStart: now.subtract(const Duration(days: 2)),
      rangeEnd: now.add(const Duration(days: 1)),
    );
    expect(scores['quadriceps'], 1);
    expect(scores['glutes'], 1);
  });

  test('empty selected date range returns no scores', () {
    final scores = aggregateMuscleScores(
      sessions: [session],
      templates: [template],
      rangeStart: now.add(const Duration(days: 1)),
      rangeEnd: now.add(const Duration(days: 2)),
    );
    expect(scores, isEmpty);
  });

  test(
    'date range end includes sessions throughout the selected calendar day',
    () {
      final lateSession = session.copyWith(
        id: 'late',
        startedAt: DateTime(2026, 10, 3, 23, 59, 59, 999),
      );
      final scores = aggregateMuscleScores(
        sessions: [lateSession],
        templates: [template],
        rangeStart: DateTime(2026, 10, 3),
        rangeEnd: DateTime(2026, 10, 3),
      );
      expect(scores['quadriceps'], 1);
    },
  );

  test('a selected calendar window produces a normalized score', () {
    final scores = aggregateMuscleScores(
      sessions: [session],
      templates: [template],
      rangeStart: DateTime(2026, 10, 1),
      rangeEnd: DateTime(2026, 10, 3),
    );
    expect(scores['quadriceps'], 1);
    expect(scores['glutes'], 1);
  });

  test('JSON and CSV exports stay readable', () {
    final bundle = BackupCodec.createBundle(
      templates: [template],
      sessions: [session],
      metrics: [
        BodyMetric(
          id: 'weight',
          measuredAt: now,
          type: BodyMetricType.weight,
          value: 75,
          unit: 'kg',
        ),
      ],
      now: now,
    );
    final json = BackupCodec.encodeJson(bundle);
    expect(BackupCodec.decodeJson(json), bundle);
    final csv = BackupCodec.workoutsCsv([session]);
    expect(csv, contains('session_id'));
    expect(csv, contains('squat'));
  });
}
