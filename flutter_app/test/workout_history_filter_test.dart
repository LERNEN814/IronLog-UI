import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:ironlog/core/models/exercise_set.dart';
import 'package:ironlog/core/models/exercise_template.dart';
import 'package:ironlog/core/models/workout_session.dart';
import 'package:ironlog/features/dashboard/presentation/dashboard_page.dart';
import 'package:ironlog/core/settings/app_settings.dart';

void main() {
  final firstTemplate = ExerciseTemplate(
    id: 'bench',
    name: '卧推',
    targetMuscleIds: const ['pectorals', 'triceps'],
    createdAt: DateTime(2026, 10, 1),
    updatedAt: DateTime(2026, 10, 1),
  );
  final secondTemplate = ExerciseTemplate(
    id: 'squat',
    name: '深蹲',
    targetMuscleIds: const ['quadriceps', 'glutes'],
    createdAt: DateTime(2026, 10, 2),
    updatedAt: DateTime(2026, 10, 2),
  );

  WorkoutSession session(String id, DateTime startedAt, String exerciseId) =>
      WorkoutSession(
        id: id,
        startedAt: startedAt,
        endedAt: startedAt.add(const Duration(minutes: 40)),
        templateId: exerciseId,
        sets: [
          ExerciseSet(
            id: '$id-1',
            exerciseId: exerciseId,
            setIndex: 0,
            reps: 8,
            weight: 60,
            completed: true,
          ),
        ],
      );

  test('orders templates by persisted IDs and appends new templates', () {
    final ordered = orderExerciseTemplates(
      [secondTemplate, firstTemplate],
      const ['bench', 'missing'],
    );

    expect(ordered.map((template) => template.id), ['bench', 'squat']);
  });

  test('reordered template IDs survive settings persistence round trip', () {
    const order = ['squat', 'bench'];
    const settings = AppSettings(templateOrder: order);
    final restored = AppSettings.fromJson(settings.toJson());
    expect(restored.templateOrder, order);
    final afterRestart = orderExerciseTemplates([
      firstTemplate,
      secondTemplate,
    ], restored.templateOrder);
    expect(afterRestart.map((template) => template.id), order);
  });

  test('reordering keeps unknown IDs out and persists the complete order', () {
    final reordered = orderExerciseTemplates(
      [firstTemplate, secondTemplate],
      const ['squat', 'missing', 'bench'],
    );
    expect(reordered.map((template) => template.id), ['squat', 'bench']);

    final settings = AppSettings(
      templateOrder: [for (final template in reordered) template.id],
    );
    final restored = AppSettings.fromJson(settings.toJson());
    expect(restored.templateOrder, ['squat', 'bench']);
  });

  test('filters workout history by date, action, and target muscle', () {
    final sessions = [
      session('bench-session', DateTime(2026, 10, 3, 9), 'bench'),
      session('squat-session', DateTime(2026, 10, 5, 9), 'squat'),
    ];

    expect(
      filterWorkoutSessions(
        sessions: sessions,
        templates: [firstTemplate, secondTemplate],
        dateRange: DateTimeRange(
          start: DateTime(2026, 10, 3),
          end: DateTime(2026, 10, 3),
        ),
      ).map((item) => item.id),
      ['bench-session'],
    );
    expect(
      filterWorkoutSessions(
        sessions: sessions,
        templates: [firstTemplate, secondTemplate],
        actionId: 'squat',
      ).map((item) => item.id),
      ['squat-session'],
    );
    expect(
      filterWorkoutSessions(
        sessions: sessions,
        templates: [firstTemplate, secondTemplate],
        muscleId: 'triceps',
      ).map((item) => item.id),
      ['bench-session'],
    );
  });

  test('multi-set sessions preserve completed volume and JSON round trip', () {
    final original = WorkoutSession(
      id: 'multi',
      startedAt: DateTime(2026, 10, 3, 9),
      endedAt: DateTime(2026, 10, 3, 10),
      templateId: 'bench',
      sets: [
        const ExerciseSet(
          id: 'multi-1',
          exerciseId: 'bench',
          setIndex: 0,
          reps: 8,
          weight: 60,
          completed: true,
        ),
        const ExerciseSet(
          id: 'multi-2',
          exerciseId: 'bench',
          setIndex: 1,
          reps: 6,
          weight: 60,
          completed: false,
          rpe: 8,
        ),
      ],
    );

    expect(original.totalVolume, 480);
    expect(WorkoutSession.fromJson(original.toJson()), original);
  });
}
