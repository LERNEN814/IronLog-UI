import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:ironlog/app.dart';
import 'package:ironlog/core/models/exercise_set.dart';
import 'package:ironlog/core/models/exercise_template.dart';
import 'package:ironlog/core/models/workout_session.dart';
import 'package:ironlog/core/providers/repository_providers.dart';
import 'package:ironlog/core/storage/repositories.dart';
import 'package:ironlog/features/dashboard/presentation/dashboard_page.dart';
import 'package:ironlog/features/heatmap/presentation/widgets/muscle_heatmap.dart';

void main() {
  testWidgets('dashboard renders the reference heatmap', (tester) async {
    await tester.pumpWidget(buildApp());
    await tester.pumpAndSettle();

    expect(find.text('训练概览'), findsOneWidget);
    expect(find.text('肌肉训练热力图'), findsOneWidget);
    expect(find.text('本周训练'), findsOneWidget);
    expect(find.byType(MuscleHeatmap), findsOneWidget);
  });

  testWidgets('dashboard exposes an accessible default heatmap date range', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();
    try {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      final rangeButton = find.widgetWithText(OutlinedButton, '最近 28 天');
      expect(rangeButton, findsOneWidget);
      final semanticsNode = tester.getSemantics(rangeButton);
      expect(semanticsNode.flagsCollection.isButton, isTrue);
      expect(semanticsNode.label, '最近 28 天');
      expect(find.text('最近 28 天'), findsOneWidget);
    } finally {
      semantics.dispose();
    }
  });

  testWidgets(
    'selected date range with no completed sessions renders the heatmap empty state',
    (tester) async {
      final repositories = FitnessRepositories.inMemory();
      final createdAt = DateTime(2026, 1, 1);
      await repositories.exerciseTemplates.save(
        ExerciseTemplate(
          id: 'bench',
          name: '卧推',
          targetMuscleIds: const ['pectorals'],
          createdAt: createdAt,
          updatedAt: createdAt,
        ),
      );
      await repositories.workoutSessions.save(
        WorkoutSession(
          id: 'january-session',
          startedAt: DateTime(2026, 1, 15, 9),
          endedAt: DateTime(2026, 1, 15, 10),
          templateId: 'bench',
          sets: const [
            ExerciseSet(
              id: 'january-set',
              exerciseId: 'bench',
              setIndex: 0,
              reps: 8,
              weight: 60,
              completed: true,
            ),
          ],
        ),
      );

      final semantics = tester.ensureSemantics();
      try {
        await tester.pumpWidget(
          ProviderScope(
            overrides: [
              fitnessRepositoriesProvider.overrideWithValue(repositories),
              heatmapDateRangeProvider.overrideWith(
                (ref) => DateTimeRange(
                  start: DateTime(2026, 2, 1),
                  end: DateTime(2026, 2, 2),
                ),
              ),
            ],
            child: const FitnessRecordApp(),
          ),
        );
        await tester.pumpAndSettle();

        expect(find.text('肌肉训练热力图'), findsOneWidget);
        final noDataText = find.text('所选日期范围内没有已完成训练，调整日期后重试。');
        expect(noDataText, findsOneWidget);
        expect(
          tester.getSemantics(noDataText).label,
          contains('所选日期范围内没有已完成训练，热力图显示为空'),
        );
        final rangeButton = find.widgetWithText(OutlinedButton, '02/01-02/02');
        expect(rangeButton, findsOneWidget);
      } finally {
        semantics.dispose();
      }
    },
  );

  testWidgets(
    'heatmap date range picker can be cancelled without changing default',
    (tester) async {
      final semantics = tester.ensureSemantics();
      try {
        await tester.pumpWidget(buildApp());
        await tester.pumpAndSettle();
        await tester.tap(find.widgetWithText(OutlinedButton, '最近 28 天'));
        await tester.pumpAndSettle();
        expect(find.byType(DateRangePickerDialog), findsOneWidget);
        await tester.binding.handlePopRoute();
        await tester.pumpAndSettle();
        expect(find.widgetWithText(OutlinedButton, '最近 28 天'), findsOneWidget);
        expect(
          tester
              .getSemantics(find.widgetWithText(OutlinedButton, '最近 28 天'))
              .label,
          '最近 28 天',
        );
      } finally {
        semantics.dispose();
      }
    },
  );

  testWidgets('workout history exposes an accessible date filter', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();
    try {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();
      await tester.tap(find.text('训练'));
      await tester.pumpAndSettle();
      final dateFilter = find.bySemanticsLabel('训练历史日期筛选，未选择日期');
      expect(dateFilter, findsOneWidget);
      expect(find.text('日期'), findsOneWidget);
    } finally {
      semantics.dispose();
    }
  });

  testWidgets(
    'workout history date filter can be cancelled without changing state',
    (tester) async {
      final semantics = tester.ensureSemantics();
      try {
        await tester.pumpWidget(buildApp());
        await tester.pumpAndSettle();
        await tester.tap(find.text('训练'));
        await tester.pumpAndSettle();
        await tester.tap(find.text('日期'));
        await tester.pumpAndSettle();
        expect(find.byType(DateRangePickerDialog), findsOneWidget);
        await tester.binding.handlePopRoute();
        await tester.pumpAndSettle();
        expect(find.text('日期'), findsOneWidget);
        expect(find.bySemanticsLabel('训练历史日期筛选，未选择日期'), findsOneWidget);
      } finally {
        semantics.dispose();
      }
    },
  );

  testWidgets('template reorder handle exposes an accessible drag target', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();
    try {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();
      await tester.tap(find.text('训练'));
      await tester.pumpAndSettle();

      expect(find.text('动作库与训练模板'), findsOneWidget);
      expect(find.textContaining('0 个动作，可编辑目标肌群和次数'), findsOneWidget);
      expect(find.text('还没有动作模板'), findsOneWidget);
    } finally {
      semantics.dispose();
    }
  });

  testWidgets('template drag reorders the real list and persists the order', (
    tester,
  ) async {
    // The training page contains several cards; use a phone-sized viewport
    // tall enough to keep the reorder list in the render tree without an
    // unrelated dashboard card overflow.
    tester.view.physicalSize = const Size(1080, 2400);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    final repositories = FitnessRepositories.inMemory();
    final firstDate = DateTime(2026, 10, 1);
    await repositories.exerciseTemplates.save(
      ExerciseTemplate(
        id: 'bench',
        name: '卧推',
        targetMuscleIds: const ['pectorals'],
        createdAt: firstDate,
        updatedAt: firstDate,
      ),
    );
    await repositories.exerciseTemplates.save(
      ExerciseTemplate(
        id: 'squat',
        name: '深蹲',
        targetMuscleIds: const ['quadriceps'],
        createdAt: firstDate.add(const Duration(minutes: 1)),
        updatedAt: firstDate.add(const Duration(minutes: 1)),
      ),
    );

    await tester.pumpWidget(buildApp(repositories: repositories));
    await tester.pumpAndSettle();
    await tester.tap(find.text('训练'));
    await tester.pumpAndSettle();

    final firstHandle = find.bySemanticsLabel('拖动 卧推 调整顺序');
    expect(firstHandle, findsOneWidget);
    expect(find.text('深蹲'), findsOneWidget);
    expect(
      tester.getCenter(find.text('卧推')).dy,
      lessThan(tester.getCenter(find.text('深蹲')).dy),
    );

    // Exercise the production ReorderableDragStartListener rather than
    // invoking the callback directly. This covers the real drag path.
    await tester.drag(firstHandle, const Offset(0, 180));
    await tester.pumpAndSettle();

    expect(
      tester.getCenter(find.text('深蹲')).dy,
      lessThan(tester.getCenter(find.text('卧推')).dy),
    );
    final settings = await repositories.settings.read();
    expect(settings.templateOrder, ['squat', 'bench']);

    // Recreate the app with the same repository to model a process restart.
    await tester.pumpWidget(buildApp(repositories: repositories));
    await tester.pumpAndSettle();
    await tester.tap(find.text('训练'));
    await tester.pumpAndSettle();
    expect(
      tester.getCenter(find.text('深蹲')).dy,
      lessThan(tester.getCenter(find.text('卧推')).dy),
    );
  });

  testWidgets('trends page exposes the personal record section', (
    tester,
  ) async {
    await tester.pumpWidget(buildApp());
    await tester.pumpAndSettle();
    await tester.tap(find.text('趋势'));
    await tester.pumpAndSettle();
    expect(find.text('个人纪录（PR）'), findsOneWidget);
  });
}
