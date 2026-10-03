import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:intl/intl.dart';
import 'package:uuid/uuid.dart';

import '../../../core/export/backup_codec.dart';
import '../../../core/models/body_metric.dart';
import '../../../core/models/domain_enums.dart';
import '../../../core/models/exercise_set.dart';
import '../../../core/models/exercise_template.dart';
import '../../../core/models/workout_session.dart';
import '../../workout/application/personal_records.dart';
import '../../../core/providers/repository_providers.dart';
import '../../../core/providers/notification_providers.dart';
import '../../../core/providers/settings_providers.dart';
import '../../../core/providers/export_providers.dart';
import '../../../core/export/platform_export_service.dart';
import '../../../core/settings/app_settings.dart';
import '../../heatmap/application/muscle_load_aggregator.dart';
import '../../heatmap/domain/muscle_region.dart';
import '../../heatmap/presentation/widgets/muscle_heatmap.dart';

final selectedHeatmapRegionProvider = StateProvider<String?>((ref) => null);
final selectedMuscleViewProvider = StateProvider<MuscleView>(
  (ref) => MuscleView.front,
);
final heatmapDateRangeProvider = StateProvider<DateTimeRange?>((ref) => null);
final templateOrderProvider = StateProvider<List<String>>((ref) => const []);

List<ExerciseTemplate> orderExerciseTemplates(
  List<ExerciseTemplate> templates,
  List<String> order,
) {
  final byId = {for (final template in templates) template.id: template};
  final ordered = <ExerciseTemplate>[];
  for (final id in order) {
    final template = byId.remove(id);
    if (template != null) ordered.add(template);
  }
  final remaining = byId.values.toList()
    ..sort((a, b) => a.createdAt.compareTo(b.createdAt));
  return [...ordered, ...remaining];
}

List<WorkoutSession> filterWorkoutSessions({
  required List<WorkoutSession> sessions,
  required List<ExerciseTemplate> templates,
  DateTimeRange? dateRange,
  String? actionId,
  String? muscleId,
}) {
  bool matchesDate(DateTime date) {
    if (dateRange == null) return true;
    final local = date.toLocal();
    final start = DateTime(
      dateRange.start.year,
      dateRange.start.month,
      dateRange.start.day,
    );
    final end = DateTime(
      dateRange.end.year,
      dateRange.end.month,
      dateRange.end.day,
      23,
      59,
      59,
      999,
    );
    return !local.isBefore(start) && !local.isAfter(end);
  }

  return sessions.where((session) {
    if (!matchesDate(session.startedAt)) return false;
    if (actionId != null &&
        session.sets.every((set) => set.exerciseId != actionId)) {
      return false;
    }
    if (muscleId != null) {
      final exerciseIds = session.sets.map((set) => set.exerciseId).toSet();
      final hasMuscle = templates.any(
        (template) =>
            exerciseIds.contains(template.id) &&
            template.targetMuscleIds.contains(muscleId),
      );
      if (!hasMuscle) return false;
    }
    return true;
  }).toList();
}

class DashboardPage extends ConsumerStatefulWidget {
  const DashboardPage({super.key});

  @override
  ConsumerState<DashboardPage> createState() => _DashboardPageState();
}

class _DashboardPageState extends ConsumerState<DashboardPage> {
  var _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    final pages = <Widget>[
      const _OverviewPage(),
      const _WorkoutLogPage(),
      const _TrendsPage(),
      const _SettingsPage(),
    ];
    return Scaffold(
      body: IndexedStack(index: _selectedIndex, children: pages),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _selectedIndex,
        onDestinationSelected: (index) =>
            setState(() => _selectedIndex = index),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.dashboard_outlined),
            selectedIcon: Icon(Icons.dashboard_rounded),
            label: '概览',
          ),
          NavigationDestination(
            icon: Icon(Icons.calendar_month_outlined),
            selectedIcon: Icon(Icons.calendar_month_rounded),
            label: '训练',
          ),
          NavigationDestination(
            icon: Icon(Icons.show_chart_outlined),
            selectedIcon: Icon(Icons.show_chart_rounded),
            label: '趋势',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings_rounded),
            label: '设置',
          ),
        ],
      ),
    );
  }
}

class _OverviewPage extends ConsumerWidget {
  const _OverviewPage();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessions =
        ref.watch(workoutSessionsProvider).valueOrNull ??
        const <WorkoutSession>[];
    final templates =
        ref.watch(exerciseTemplatesProvider).valueOrNull ??
        const <ExerciseTemplate>[];
    final now = DateTime.now();
    final weekStart = DateTime(
      now.year,
      now.month,
      now.day,
    ).subtract(Duration(days: now.weekday - 1));
    final weekSessions = sessions
        .where((session) => !session.startedAt.toLocal().isBefore(weekStart))
        .toList(growable: false);
    final heatmapRange = ref.watch(heatmapDateRangeProvider);
    final heatmapEnd = heatmapRange?.end == null
        ? DateTime.now()
        : DateTime(
            heatmapRange!.end.year,
            heatmapRange.end.month,
            heatmapRange.end.day,
            23,
            59,
            59,
            999,
          );
    final heatmapStart =
        heatmapRange?.start ??
        DateTime.now().subtract(const Duration(days: 28));
    final scores = aggregateMuscleScores(
      sessions: sessions,
      templates: templates,
      rangeStart: heatmapStart,
      rangeEnd: heatmapEnd,
    );
    final selectedRegionId = ref.watch(selectedHeatmapRegionProvider);
    final selectedView = ref.watch(selectedMuscleViewProvider);
    final recent = [...sessions]
      ..sort((a, b) => b.startedAt.compareTo(a.startedAt));
    final selected = selectedRegionId == null
        ? null
        : muscleRegions.cast<MuscleRegion?>().firstWhere(
            (region) => region?.regionId == selectedRegionId,
            orElse: () => null,
          );
    return SafeArea(
      child: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            titleSpacing: 20,
            title: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '训练概览',
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700),
                ),
                Text(
                  '离线记录 · 本地优先',
                  style: TextStyle(fontSize: 12, color: Color(0xff8f95a8)),
                ),
              ],
            ),
            actions: [
              IconButton(
                tooltip: '开始训练',
                icon: const Icon(Icons.add_circle_outline_rounded),
                onPressed: () => _showWorkoutEditor(context, ref),
              ),
              const SizedBox(width: 12),
            ],
          ),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 8, 20, 32),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                _SummaryRow(
                  sessions: weekSessions,
                  totalVolume: totalCompletedVolume(weekSessions),
                  unit:
                      ref.watch(appSettingsProvider).valueOrNull?.weightUnit ??
                      WeightUnit.kg,
                ),
                const Gap(16),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(12, 16, 12, 12),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Padding(
                          padding: EdgeInsets.symmetric(horizontal: 4),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                '肌肉训练热力图',
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w700,
                                ),
                              ),
                              Tooltip(
                                message: '分数来自已完成训练组',
                                child: Icon(
                                  Icons.info_outline_rounded,
                                  size: 18,
                                ),
                              ),
                            ],
                          ),
                        ),
                        const Gap(12),
                        Center(
                          child: SegmentedButton<MuscleView>(
                            segments: const [
                              ButtonSegment(
                                value: MuscleView.front,
                                label: Text('前视'),
                              ),
                              ButtonSegment(
                                value: MuscleView.back,
                                label: Text('后视'),
                              ),
                            ],
                            selected: {selectedView},
                            onSelectionChanged: (selection) {
                              ref
                                      .read(selectedMuscleViewProvider.notifier)
                                      .state =
                                  selection.first;
                              ref
                                      .read(
                                        selectedHeatmapRegionProvider.notifier,
                                      )
                                      .state =
                                  null;
                            },
                          ),
                        ),
                        const Gap(12),
                        Semantics(
                          button: true,
                          label: heatmapRange == null
                              ? '热力图日期范围，最近 28 天'
                              : '热力图日期范围，${DateFormat('yyyy年MM月dd日').format(heatmapRange.start)}至${DateFormat('yyyy年MM月dd日').format(heatmapRange.end)}',
                          child: Wrap(
                            alignment: WrapAlignment.center,
                            crossAxisAlignment: WrapCrossAlignment.center,
                            spacing: 8,
                            children: [
                              OutlinedButton.icon(
                                onPressed: () async {
                                  final picked = await showDateRangePicker(
                                    context: context,
                                    firstDate: DateTime(2020),
                                    lastDate: DateTime.now(),
                                    initialDateRange: heatmapRange,
                                  );
                                  if (picked != null && context.mounted) {
                                    ref
                                            .read(
                                              heatmapDateRangeProvider.notifier,
                                            )
                                            .state =
                                        picked;
                                  }
                                },
                                icon: const Icon(
                                  Icons.date_range_rounded,
                                  size: 17,
                                ),
                                label: Text(
                                  heatmapRange == null
                                      ? '最近 28 天'
                                      : '${DateFormat('MM/dd').format(heatmapRange.start)}-${DateFormat('MM/dd').format(heatmapRange.end)}',
                                ),
                              ),
                              if (heatmapRange != null)
                                IconButton(
                                  tooltip: '恢复最近 28 天',
                                  onPressed: () =>
                                      ref
                                              .read(
                                                heatmapDateRangeProvider
                                                    .notifier,
                                              )
                                              .state =
                                          null,
                                  icon: const Icon(
                                    Icons.clear_rounded,
                                    size: 18,
                                  ),
                                ),
                            ],
                          ),
                        ),
                        const Gap(8),
                        MuscleHeatmap(
                          scores: scores,
                          activeView: selectedView,
                          selectedRegionId: selectedRegionId,
                          onRegionTap: (region) =>
                              ref
                                  .read(selectedHeatmapRegionProvider.notifier)
                                  .state = region
                                  .regionId,
                        ),
                        const Gap(12),
                        const _HeatLegend(),
                        if (scores.isEmpty)
                          const Padding(
                            padding: EdgeInsets.only(top: 12),
                            child: _HeatmapNoDataState(),
                          ),
                        if (selected != null) ...[
                          const Gap(12),
                          _SelectedRegionSummary(
                            region: selected,
                            score: scores[selected.groupId] ?? 0,
                            hasData: scores.containsKey(selected.groupId),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
                const Gap(16),
                _SectionHeader(
                  title: '最近训练',
                  action: recent.isEmpty ? '开始记录' : '查看训练',
                ),
                const Gap(8),
                if (recent.isEmpty)
                  _EmptyState(
                    icon: Icons.fitness_center_outlined,
                    title: '还没有训练记录',
                    subtitle: '完成第一组训练后，热力图会自动更新。',
                    actionLabel: '记录一组训练',
                    onAction: () => _showWorkoutEditor(context, ref),
                  )
                else
                  ...recent
                      .take(3)
                      .map(
                        (session) => Padding(
                          padding: const EdgeInsets.only(bottom: 8),
                          child: _WorkoutTile(session: session),
                        ),
                      ),
              ]),
            ),
          ),
        ],
      ),
    );
  }
}

class _SummaryRow extends StatelessWidget {
  const _SummaryRow({
    required this.sessions,
    required this.totalVolume,
    required this.unit,
  });
  final List<WorkoutSession> sessions;
  final double totalVolume;
  final WeightUnit unit;

  @override
  Widget build(BuildContext context) => Row(
    children: [
      Expanded(
        child: _MetricCard(
          label: '本周训练',
          value: '${sessions.length}',
          unit: '次',
          icon: Icons.local_fire_department_rounded,
          color: const Color(0xffff8f4a),
        ),
      ),
      const Gap(8),
      Expanded(
        child: _MetricCard(
          label: '完成总量',
          value: NumberFormat.compact().format(totalVolume),
          unit: unit.name,
          icon: Icons.monitor_weight_outlined,
          color: const Color(0xff73a8df),
        ),
      ),
    ],
  );
}

class _MetricCard extends StatelessWidget {
  const _MetricCard({
    required this.label,
    required this.value,
    required this.unit,
    required this.icon,
    required this.color,
  });
  final String label;
  final String value;
  final String unit;
  final IconData icon;
  final Color color;

  @override
  Widget build(BuildContext context) => Card(
    child: Padding(
      padding: const EdgeInsets.all(14),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: color, size: 20),
          const Gap(10),
          Text(label, style: const TextStyle(color: Color(0xff9aa0b2))),
          const Gap(2),
          RichText(
            text: TextSpan(
              style: DefaultTextStyle.of(context).style,
              children: [
                TextSpan(
                  text: value,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w700,
                  ),
                ),
                TextSpan(
                  text: ' $unit',
                  style: const TextStyle(color: Color(0xff9aa0b2)),
                ),
              ],
            ),
          ),
        ],
      ),
    ),
  );
}

class _HeatLegend extends StatelessWidget {
  const _HeatLegend();
  @override
  Widget build(BuildContext context) => Row(
    children: [
      const Text('训练刺激', style: TextStyle(color: Color(0xff9aa0b2))),
      const Gap(8),
      Expanded(
        child: Container(
          height: 8,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(8),
            gradient: const LinearGradient(
              colors: [
                Color(0xff53677f),
                Color(0xff3d8eaa),
                Color(0xfff39a3d),
                Color(0xffffb52e),
                Color(0xffe92f2f),
              ],
            ),
          ),
        ),
      ),
      const Gap(8),
      const Text('高', style: TextStyle(color: Color(0xff9aa0b2))),
    ],
  );
}

class _HeatmapNoDataState extends StatelessWidget {
  const _HeatmapNoDataState();

  @override
  Widget build(BuildContext context) => Semantics(
    container: true,
    liveRegion: true,
    label: '所选日期范围内没有已完成训练，热力图显示为空',
    child: Text(
      '所选日期范围内没有已完成训练，调整日期后重试。',
      style: TextStyle(color: Color(0xff9aa0b2)),
    ),
  );
}

class _SelectedRegionSummary extends StatelessWidget {
  const _SelectedRegionSummary({
    required this.region,
    required this.score,
    required this.hasData,
  });
  final MuscleRegion region;
  final double score;
  final bool hasData;
  @override
  Widget build(BuildContext context) => Container(
    width: double.infinity,
    padding: const EdgeInsets.all(12),
    decoration: BoxDecoration(
      color: const Color(0xff202638),
      borderRadius: BorderRadius.circular(8),
    ),
    child: Row(
      children: [
        const Icon(
          Icons.touch_app_outlined,
          size: 18,
          color: Color(0xffffb52e),
        ),
        const Gap(8),
        Expanded(
          child: Text(
            '${region.label} · ${region.side == MuscleSide.left
                ? '左侧'
                : region.side == MuscleSide.right
                ? '右侧'
                : '双侧'}',
            style: const TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
        Text(
          hasData ? '${(score * 100).round()}%' : '暂无数据',
          style: const TextStyle(
            color: Color(0xffffb52e),
            fontWeight: FontWeight.w700,
          ),
        ),
      ],
    ),
  );
}

class _WorkoutLogPage extends ConsumerStatefulWidget {
  const _WorkoutLogPage();

  @override
  ConsumerState<_WorkoutLogPage> createState() => _WorkoutLogPageState();
}

class _WorkoutLogPageState extends ConsumerState<_WorkoutLogPage> {
  DateTimeRange? _dateRange;
  String? _actionFilter;
  String? _muscleFilter;

  List<ExerciseTemplate> _orderedTemplates(
    List<ExerciseTemplate> templates,
    List<String> order,
  ) => orderExerciseTemplates(templates, order);

  void _clearFilters() {
    setState(() {
      _dateRange = null;
      _actionFilter = null;
      _muscleFilter = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    final ref = this.ref;
    final sessionsState = ref.watch(workoutSessionsProvider);
    final sessions = sessionsState.valueOrNull ?? const <WorkoutSession>[];
    final templates =
        ref.watch(exerciseTemplatesProvider).valueOrNull ??
        const <ExerciseTemplate>[];
    final settingsOrder = ref
        .watch(appSettingsProvider)
        .valueOrNull
        ?.templateOrder;
    final orderedTemplates = _orderedTemplates(
      templates,
      settingsOrder ?? ref.watch(templateOrderProvider),
    );
    final activeActionFilter =
        orderedTemplates.any((template) => template.id == _actionFilter)
        ? _actionFilter
        : null;
    final muscles = {
      for (final template in templates) ...template.targetMuscleIds,
    }.toList()..sort();
    final activeMuscleFilter = muscles.contains(_muscleFilter)
        ? _muscleFilter
        : null;
    final filtered = filterWorkoutSessions(
      sessions: sessions,
      templates: templates,
      dateRange: _dateRange,
      actionId: activeActionFilter,
      muscleId: activeMuscleFilter,
    );
    final sorted = filtered..sort((a, b) => b.startedAt.compareTo(a.startedAt));
    final hasFilters =
        _dateRange != null ||
        activeActionFilter != null ||
        activeMuscleFilter != null;
    return SafeArea(
      child: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            title: const Text('训练记录'),
            actions: [
              IconButton(
                tooltip: '新建训练',
                onPressed: () => _showWorkoutEditor(context, ref),
                icon: const Icon(Icons.add_rounded),
              ),
            ],
          ),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 12, 20, 4),
            sliver: SliverToBoxAdapter(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(8, 8, 8, 4),
                  child: Column(
                    children: [
                      ListTile(
                        title: const Text('动作库与训练模板'),
                        subtitle: Text('${templates.length} 个动作，可编辑目标肌群和次数'),
                        trailing: IconButton(
                          tooltip: '添加动作模板',
                          onPressed: () => _showTemplateEditor(context, ref),
                          icon: const Icon(Icons.add_rounded),
                        ),
                      ),
                      if (orderedTemplates.isNotEmpty)
                        ReorderableListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: orderedTemplates.length,
                          onReorderItem: (oldIndex, newIndex) {
                            final ids = orderedTemplates
                                .map((template) => template.id)
                                .toList();
                            final id = ids.removeAt(oldIndex);
                            ids.insert(newIndex, id);
                            ref.read(templateOrderProvider.notifier).state =
                                ids;
                            final settings = ref
                                .read(appSettingsProvider)
                                .valueOrNull;
                            if (settings != null) {
                              ref
                                  .read(appSettingsProvider.notifier)
                                  .setTemplateOrder(settings, ids);
                            }
                          },
                          itemBuilder: (context, index) {
                            final template = orderedTemplates[index];
                            return ListTile(
                              key: ValueKey(template.id),
                              dense: true,
                              leading: const Icon(
                                Icons.fitness_center_outlined,
                                size: 20,
                              ),
                              title: Text(template.name),
                              subtitle: Text(
                                '${template.defaultSets} 组 × ${template.defaultReps} 次 · ${template.targetMuscleIds.join(' · ')}',
                              ),
                              onTap: () => _showTemplateEditor(
                                context,
                                ref,
                                existing: template,
                              ),
                              trailing: Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  IconButton(
                                    tooltip: '删除模板',
                                    onPressed: () async {
                                      final confirmed = await showDialog<bool>(
                                        context: context,
                                        builder: (dialogContext) => AlertDialog(
                                          title: const Text('删除动作模板？'),
                                          content: Text(
                                            '删除“${template.name}”不会删除已有训练记录。',
                                          ),
                                          actions: [
                                            TextButton(
                                              onPressed: () => Navigator.pop(
                                                dialogContext,
                                                false,
                                              ),
                                              child: const Text('取消'),
                                            ),
                                            FilledButton(
                                              onPressed: () => Navigator.pop(
                                                dialogContext,
                                                true,
                                              ),
                                              child: const Text('删除'),
                                            ),
                                          ],
                                        ),
                                      );
                                      if (confirmed == true &&
                                          context.mounted) {
                                        await ref
                                            .read(
                                              exerciseTemplatesProvider
                                                  .notifier,
                                            )
                                            .delete(template.id);
                                      }
                                    },
                                    icon: const Icon(
                                      Icons.delete_outline_rounded,
                                      size: 20,
                                    ),
                                  ),
                                  ReorderableDragStartListener(
                                    index: index,
                                    child: Semantics(
                                      button: true,
                                      label: '拖动 ${template.name} 调整顺序',
                                      hint: '长按后拖动以调整动作模板顺序',
                                      child: const Padding(
                                        padding: EdgeInsets.all(8),
                                        child: Icon(Icons.drag_handle_rounded),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            );
                          },
                        )
                      else
                        const Padding(
                          padding: EdgeInsets.only(bottom: 10),
                          child: Text(
                            '还没有动作模板',
                            style: TextStyle(color: Color(0xff9aa0b2)),
                          ),
                        ),
                    ],
                  ),
                ),
              ),
            ),
          ),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 8, 20, 0),
            sliver: SliverToBoxAdapter(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(12, 8, 12, 8),
                  child: Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    crossAxisAlignment: WrapCrossAlignment.center,
                    children: [
                      DropdownButton<String>(
                        value: activeActionFilter ?? '',
                        hint: const Text('全部动作'),
                        underline: const SizedBox.shrink(),
                        items: [
                          const DropdownMenuItem(
                            value: '',
                            child: Text('全部动作'),
                          ),
                          ...orderedTemplates.map(
                            (template) => DropdownMenuItem(
                              value: template.id,
                              child: Text(template.name),
                            ),
                          ),
                        ],
                        onChanged: (value) => setState(
                          () => _actionFilter = value == null || value.isEmpty
                              ? null
                              : value,
                        ),
                      ),
                      DropdownButton<String>(
                        value: activeMuscleFilter ?? '',
                        hint: const Text('全部肌群'),
                        underline: const SizedBox.shrink(),
                        items: [
                          const DropdownMenuItem(
                            value: '',
                            child: Text('全部肌群'),
                          ),
                          ...muscles.map(
                            (muscle) => DropdownMenuItem(
                              value: muscle,
                              child: Text(muscle),
                            ),
                          ),
                        ],
                        onChanged: (value) => setState(
                          () => _muscleFilter = value == null || value.isEmpty
                              ? null
                              : value,
                        ),
                      ),
                      Semantics(
                        button: true,
                        label: _dateRange == null
                            ? '训练历史日期筛选，未选择日期'
                            : '训练历史日期筛选，${DateFormat('yyyy年MM月dd日').format(_dateRange!.start)}至${DateFormat('yyyy年MM月dd日').format(_dateRange!.end)}',
                        child: OutlinedButton.icon(
                          onPressed: () async {
                            final picked = await showDateRangePicker(
                              context: context,
                              firstDate: DateTime(2020),
                              lastDate: DateTime.now().add(
                                const Duration(days: 1),
                              ),
                              initialDateRange: _dateRange,
                            );
                            if (picked != null && mounted) {
                              setState(() => _dateRange = picked);
                            }
                          },
                          icon: const Icon(Icons.date_range_rounded, size: 18),
                          label: Text(
                            _dateRange == null
                                ? '日期'
                                : '${DateFormat('MM/dd').format(_dateRange!.start)}-${DateFormat('MM/dd').format(_dateRange!.end)}',
                          ),
                        ),
                      ),
                      if (hasFilters)
                        TextButton.icon(
                          onPressed: _clearFilters,
                          icon: const Icon(Icons.clear_rounded, size: 18),
                          label: const Text('清除筛选'),
                        ),
                    ],
                  ),
                ),
              ),
            ),
          ),
          if (sessionsState.isLoading && sessions.isEmpty)
            const SliverFillRemaining(
              child: Center(child: CircularProgressIndicator()),
            )
          else if (sessionsState.hasError)
            SliverFillRemaining(
              child: _ErrorState(
                message: '训练记录加载失败',
                onRetry: () => ref.invalidate(workoutSessionsProvider),
              ),
            )
          else if (sorted.isEmpty)
            SliverFillRemaining(
              child: _EmptyState(
                icon: Icons.calendar_month_outlined,
                title: hasFilters ? '没有符合筛选的训练' : '训练历史为空',
                subtitle: hasFilters
                    ? '调整日期、动作或肌群筛选后重试。'
                    : templates.isEmpty
                    ? '先添加一个动作模板，再记录训练。'
                    : '记录一组后这里会保留完整历史。',
                actionLabel: hasFilters
                    ? '清除筛选'
                    : templates.isEmpty
                    ? '添加动作模板'
                    : '开始训练',
                onAction: hasFilters
                    ? _clearFilters
                    : () => templates.isEmpty
                          ? _showTemplateEditor(context, ref)
                          : _showWorkoutEditor(context, ref),
              ),
            )
          else
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(20, 12, 20, 32),
              sliver: SliverList(
                delegate: SliverChildBuilderDelegate((context, index) {
                  final session = sorted[index];
                  return Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Dismissible(
                      key: ValueKey(session.id),
                      background: Container(
                        color: Colors.red.shade800,
                        alignment: Alignment.centerRight,
                        padding: const EdgeInsets.only(right: 20),
                        child: const Icon(Icons.delete_outline),
                      ),
                      direction: DismissDirection.endToStart,
                      onDismissed: (_) => ref
                          .read(workoutSessionsProvider.notifier)
                          .delete(session.id),
                      child: _WorkoutTile(
                        session: session,
                        expanded: true,
                        onTap: () =>
                            _showWorkoutEditor(context, ref, existing: session),
                      ),
                    ),
                  );
                }, childCount: sorted.length),
              ),
            ),
        ],
      ),
    );
  }
}

class _WorkoutTile extends StatelessWidget {
  const _WorkoutTile({
    required this.session,
    this.expanded = false,
    this.onTap,
  });
  final WorkoutSession session;
  final bool expanded;
  final VoidCallback? onTap;
  @override
  Widget build(BuildContext context) {
    final settings =
        ProviderScope.containerOf(context)
            .read(appSettingsProvider)
            .valueOrNull ??
        AppSettings.defaults;
    final displayVolume = settings.weightUnit == WeightUnit.lb
        ? session.totalVolume * 2.2046226218
        : session.totalVolume;
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: const Color(0xffff8f4a).withValues(alpha: .16),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Icon(
                  Icons.fitness_center_rounded,
                  color: Color(0xffffa463),
                ),
              ),
              const Gap(12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      DateFormat('MM月dd日 · HH:mm')
                          .format(session.startedAt.toLocal()),
                      style: const TextStyle(fontWeight: FontWeight.w700),
                    ),
                    const Gap(3),
                    Text(
                      '${session.sets.length} 组 · ${displayVolume.toStringAsFixed(0)} ${settings.weightUnit.name} · ${session.isCompleted ? '已完成' : '进行中'}',
                      style: const TextStyle(color: Color(0xff9aa0b2)),
                    ),
                    if (expanded && session.notes?.isNotEmpty == true)
                      Text(
                        session.notes!,
                        style: const TextStyle(color: Color(0xffc9ceda)),
                      ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right_rounded, color: Color(0xff8f95a8)),
            ],
          ),
        ),
      ),
    );
  }
}

class _TrendsPage extends ConsumerWidget {
  const _TrendsPage();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final metrics =
        ref.watch(bodyMetricsProvider).valueOrNull ?? const <BodyMetric>[];
    final sessions =
        ref.watch(workoutSessionsProvider).valueOrNull ??
        const <WorkoutSession>[];
    final settings =
        ref.watch(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
    final templates =
        ref.watch(exerciseTemplatesProvider).valueOrNull ??
        const <ExerciseTemplate>[];
    final volumeMultiplier = settings.weightUnit == WeightUnit.lb
        ? 2.2046226218
        : 1.0;
    final weights =
        metrics.where((metric) => metric.type == BodyMetricType.weight).toList()
          ..sort((a, b) => a.measuredAt.compareTo(b.measuredAt));
    final spots = [
      for (var i = 0; i < weights.length; i++)
        FlSpot(i.toDouble(), weights[i].value * volumeMultiplier),
    ];
    final maxY = weights.isEmpty
        ? 100.0
        : weights
                  .map((metric) => metric.value * volumeMultiplier)
                  .reduce(mathMax) +
              5;
    final minY = weights.isEmpty
        ? 0.0
        : (weights
                      .map((metric) => metric.value * volumeMultiplier)
                      .reduce(mathMin) -
                  5)
              .clamp(0, double.infinity);
    final records = personalRecordsByExercise(sessions);
    final templateNames = {
      for (final template in templates) template.id: template.name,
    };
    return SafeArea(
      child: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            title: const Text('趋势与身体数据'),
            actions: [
              IconButton(
                tooltip: '记录体重',
                onPressed: () => _showMetricEditor(context, ref),
                icon: const Icon(Icons.add_rounded),
              ),
            ],
          ),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 12, 20, 32),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                Card(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(14, 16, 14, 12),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          '体重趋势',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                        const Gap(14),
                        if (weights.isEmpty)
                          const _ChartEmpty()
                        else
                          SizedBox(
                            height: 220,
                            child: LineChart(
                              LineChartData(
                                minY: minY.toDouble(),
                                maxY: maxY,
                                gridData: const FlGridData(show: false),
                                titlesData: const FlTitlesData(
                                  leftTitles: AxisTitles(
                                    sideTitles: SideTitles(
                                      showTitles: true,
                                      reservedSize: 32,
                                    ),
                                  ),
                                  bottomTitles: AxisTitles(
                                    sideTitles: SideTitles(showTitles: false),
                                  ),
                                  topTitles: AxisTitles(
                                    sideTitles: SideTitles(showTitles: false),
                                  ),
                                  rightTitles: AxisTitles(
                                    sideTitles: SideTitles(showTitles: false),
                                  ),
                                ),
                                borderData: FlBorderData(show: false),
                                lineBarsData: [
                                  LineChartBarData(
                                    spots: spots,
                                    isCurved: true,
                                    color: const Color(0xffff8f4a),
                                    barWidth: 3,
                                    dotData: const FlDotData(show: true),
                                    belowBarData: BarAreaData(
                                      show: true,
                                      color: const Color(0xffff8f4a)
                                          .withValues(alpha: .12),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        const Gap(12),
                        Text(
                          weights.isEmpty
                              ? '输入体重后会在这里显示趋势。'
                              : '最近：${(weights.last.value * volumeMultiplier).toStringAsFixed(1)} ${settings.weightUnit.name}',
                          style: const TextStyle(color: Color(0xff9aa0b2)),
                        ),
                      ],
                    ),
                  ),
                ),
                const Gap(12),
                _StatRow(
                  label: '累计训练量',
                  value:
                      '${(totalCompletedVolume(sessions) * volumeMultiplier).toStringAsFixed(0)} ${settings.weightUnit.name}',
                ),
                _StatRow(label: '完成训练', value: '${sessions.length} 次'),
                const Gap(12),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(14, 16, 14, 12),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          '个人纪录（PR）',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                        const Gap(8),
                        if (records.isEmpty)
                          const Text(
                            '完成训练组后会在这里显示每个动作的最高重量。',
                            semanticsLabel: '暂无个人纪录，完成训练组后显示最高重量',
                            style: TextStyle(color: Color(0xff9aa0b2)),
                          )
                        else
                          for (final entry in records.entries)
                            _StatRow(
                              label: templateNames[entry.key] ?? entry.key,
                              value:
                                  '${(entry.value * volumeMultiplier).toStringAsFixed(1)} ${settings.weightUnit.name}',
                            ),
                      ],
                    ),
                  ),
                ),
                _StatRow(label: '身体数据', value: '${metrics.length} 条'),
                if (weights.isNotEmpty) ...[
                  const Gap(12),
                  Card(
                    child: Column(
                      children: [
                        const ListTile(
                          title: Text('最近体重记录'),
                          subtitle: Text('点按记录可编辑或删除'),
                        ),
                        for (final metric in weights.reversed.take(5))
                          ListTile(
                            dense: true,
                            title: Text(
                              '${(metric.value * volumeMultiplier).toStringAsFixed(1)} ${settings.weightUnit.name}',
                            ),
                            subtitle: Text(
                              DateFormat('yyyy年MM月dd日')
                                  .format(metric.measuredAt.toLocal()),
                            ),
                            onTap: () => _showMetricEditor(
                              context,
                              ref,
                              existing: metric,
                            ),
                          ),
                      ],
                    ),
                  ),
                ],
              ]),
            ),
          ),
        ],
      ),
    );
  }
}

double mathMin(double a, double b) => a < b ? a : b;
double mathMax(double a, double b) => a > b ? a : b;

class _StatRow extends StatelessWidget {
  const _StatRow({required this.label, required this.value});
  final String label;
  final String value;
  @override
  Widget build(BuildContext context) => ListTile(
    contentPadding: const EdgeInsets.symmetric(horizontal: 4),
    title: Text(label),
    trailing: Text(
      value,
      style: const TextStyle(
        fontWeight: FontWeight.w700,
        color: Color(0xffffb36f),
      ),
    ),
  );
}

class _ChartEmpty extends StatelessWidget {
  const _ChartEmpty();
  @override
  Widget build(BuildContext context) => const SizedBox(
    height: 150,
    child: Center(
      child: Text('暂无体重数据', style: TextStyle(color: Color(0xff9aa0b2))),
    ),
  );
}

class _SettingsPage extends ConsumerWidget {
  const _SettingsPage();
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final settings =
        ref.watch(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
    return SafeArea(
      child: CustomScrollView(
        slivers: [
          const SliverAppBar(pinned: true, title: Text('设置')),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(20, 12, 20, 32),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                Card(
                  child: Column(
                    children: [
                      ListTile(
                        title: const Text('重量单位'),
                        subtitle: const Text('内部重量统一按 kg 保存，热力分数不受单位切换影响'),
                        trailing: SegmentedButton<WeightUnit>(
                          segments: const [
                            ButtonSegment(
                              value: WeightUnit.kg,
                              label: Text('kg'),
                            ),
                            ButtonSegment(
                              value: WeightUnit.lb,
                              label: Text('lb'),
                            ),
                          ],
                          selected: {settings.weightUnit},
                          onSelectionChanged: (selection) async {
                            final saved = await ref
                                .read(appSettingsProvider.notifier)
                                .setWeightUnit(settings, selection.first.name);
                            if (!saved && context.mounted) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(
                                  content: Text('单位设置保存失败，当前页面仍可继续使用。'),
                                ),
                              );
                            }
                          },
                        ),
                      ),
                      const Divider(height: 1),
                      SwitchListTile(
                        title: const Text('训练提醒'),
                        subtitle: Text(
                          settings.remindersEnabled
                              ? '已开启每日非精确提醒'
                              : '关闭时仍可正常记录训练',
                        ),
                        value: settings.remindersEnabled,
                        onChanged: (value) async {
                          final service = ref.read(notificationServiceProvider);
                          if (!value) {
                            final cancelled = await service
                                .cancelWorkoutReminder();
                            final saved = await ref
                                .read(appSettingsProvider.notifier)
                                .setReminderEnabled(settings, false);
                            if (context.mounted && (!cancelled || !saved)) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(
                                  content: Text('提醒已关闭；通知服务未完全响应，但训练记录不受影响。'),
                                ),
                              );
                            }
                            return;
                          }
                          final granted = await service.requestPermission();
                          if (!context.mounted) return;
                          if (!granted) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(
                                content: Text('通知权限未开启，训练记录仍可正常使用。'),
                              ),
                            );
                            return;
                          }
                          final scheduled = await service
                              .scheduleDailyWorkoutReminder();
                          final saved =
                              scheduled &&
                              await ref
                                  .read(appSettingsProvider.notifier)
                                  .setReminderEnabled(settings, true);
                          if (context.mounted && (!scheduled || !saved)) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(
                                content: Text('通知服务不可用，训练记录仍可正常使用。'),
                              ),
                            );
                          } else if (context.mounted) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('每日提醒已开启。')),
                            );
                          }
                        },
                      ),
                    ],
                  ),
                ),
                const Gap(12),
                Card(
                  child: Column(
                    children: [
                      ListTile(
                        leading: const Icon(Icons.data_object_rounded),
                        title: const Text('导出 JSON 备份'),
                        subtitle: const Text('可往返解析的完整本地数据'),
                        onTap: () => _exportData(context, ref, asCsv: false),
                      ),
                      const Divider(height: 1),
                      ListTile(
                        leading: const Icon(Icons.table_chart_outlined),
                        title: const Text('导出 CSV'),
                        subtitle: const Text('适合查看训练组明细'),
                        onTap: () => _exportData(context, ref, asCsv: true),
                      ),
                      const Divider(height: 1),
                      ListTile(
                        leading: const Icon(Icons.file_open_outlined),
                        title: const Text('导入 JSON 备份'),
                        subtitle: const Text('校验版本后替换本机记录（失败自动回滚）'),
                        onTap: () => _importJson(context, ref),
                      ),
                    ],
                  ),
                ),
                const Gap(12),
                const Text(
                  '训练记录保存在本机。通知权限或分享能力不可用时，核心记录和导出预览仍可继续。',
                  style: TextStyle(color: Color(0xff8f95a8), fontSize: 12),
                ),
              ]),
            ),
          ),
        ],
      ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  const _SectionHeader({required this.title, required this.action});
  final String title;
  final String action;
  @override
  Widget build(BuildContext context) => Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(
        title,
        style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w700),
      ),
      Text(
        action,
        style: const TextStyle(color: Color(0xffffa463), fontSize: 12),
      ),
    ],
  );
}

class _EmptyState extends StatelessWidget {
  const _EmptyState({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.actionLabel,
    required this.onAction,
  });
  final IconData icon;
  final String title;
  final String subtitle;
  final String actionLabel;
  final VoidCallback onAction;
  @override
  Widget build(BuildContext context) => Card(
    child: Padding(
      padding: const EdgeInsets.all(22),
      child: Column(
        children: [
          Icon(icon, color: const Color(0xff9aa0b2), size: 28),
          const Gap(10),
          Text(title, style: const TextStyle(fontWeight: FontWeight.w700)),
          const Gap(5),
          Text(
            subtitle,
            textAlign: TextAlign.center,
            style: const TextStyle(color: Color(0xff9aa0b2)),
          ),
          const Gap(14),
          FilledButton.tonalIcon(
            onPressed: onAction,
            icon: const Icon(Icons.add_rounded),
            label: Text(actionLabel),
          ),
        ],
      ),
    ),
  );
}

class _ErrorState extends StatelessWidget {
  const _ErrorState({required this.message, required this.onRetry});
  final String message;
  final VoidCallback onRetry;
  @override
  Widget build(BuildContext context) => Center(
    child: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        const Icon(
          Icons.error_outline_rounded,
          color: Color(0xffff8f4a),
          size: 30,
        ),
        const Gap(8),
        Text(message),
        const Gap(10),
        OutlinedButton.icon(
          onPressed: onRetry,
          icon: const Icon(Icons.refresh_rounded),
          label: const Text('重试'),
        ),
      ],
    ),
  );
}

Future<void> _showTemplateEditor(
  BuildContext context,
  WidgetRef ref, {
  ExerciseTemplate? existing,
}) async {
  final name = TextEditingController(text: existing?.name ?? '');
  final sets = TextEditingController(text: '${existing?.defaultSets ?? 3}');
  final reps = TextEditingController(text: '${existing?.defaultReps ?? 10}');
  final equipment = TextEditingController(text: existing?.equipment ?? '');
  var category = existing?.category ?? 'strength';
  final selectedMuscles = <String>{
    ...(existing?.targetMuscleIds ?? const ['pectorals']),
  };
  final result = await showDialog<ExerciseTemplate>(
    context: context,
    builder: (context) => StatefulBuilder(
      builder: (context, setState) => AlertDialog(
        title: Text(existing == null ? '添加动作模板' : '编辑动作模板'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: name,
                autofocus: true,
                decoration: const InputDecoration(labelText: '动作名称'),
              ),
              const Gap(10),
              Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: sets,
                      keyboardType: TextInputType.number,
                      decoration: const InputDecoration(labelText: '目标组数'),
                    ),
                  ),
                  const Gap(10),
                  Expanded(
                    child: TextField(
                      controller: reps,
                      keyboardType: TextInputType.number,
                      decoration: const InputDecoration(labelText: '目标次数'),
                    ),
                  ),
                ],
              ),
              const Gap(10),
              DropdownButtonFormField<String>(
                initialValue: category,
                decoration: const InputDecoration(labelText: '训练分类'),
                items: const [
                  DropdownMenuItem(value: 'strength', child: Text('力量')),
                  DropdownMenuItem(value: 'cardio', child: Text('有氧')),
                  DropdownMenuItem(value: 'mobility', child: Text('活动度')),
                ],
                onChanged: (value) {
                  if (value != null) setState(() => category = value);
                },
              ),
              const Gap(10),
              TextField(
                controller: equipment,
                decoration: const InputDecoration(labelText: '器械（可选）'),
              ),
              const Gap(10),
              Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  '目标肌群',
                  style: Theme.of(context).textTheme.labelLarge,
                ),
              ),
              Wrap(
                spacing: 6,
                children:
                    [
                          'pectorals',
                          'deltoids',
                          'biceps',
                          'triceps',
                          'abs',
                          'lats',
                          'glutes',
                          'quadriceps',
                          'hamstrings',
                          'calves',
                        ]
                        .map(
                          (muscle) => FilterChip(
                            label: Text(muscle),
                            selected: selectedMuscles.contains(muscle),
                            onSelected: (value) => setState(
                              () => value
                                  ? selectedMuscles.add(muscle)
                                  : selectedMuscles.remove(muscle),
                            ),
                          ),
                        )
                        .toList(),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () {
              final parsedSets = int.tryParse(sets.text) ?? 0;
              final parsedReps = int.tryParse(reps.text) ?? 0;
              if (name.text.trim().isEmpty ||
                  parsedSets <= 0 ||
                  parsedReps <= 0 ||
                  selectedMuscles.isEmpty) {
                return;
              }
              final now = DateTime.now().toUtc();
              Navigator.pop(
                context,
                existing?.copyWith(
                      name: name.text.trim(),
                      category: category,
                      equipment: equipment.text.trim().isEmpty
                          ? null
                          : equipment.text.trim(),
                      targetMuscleIds: selectedMuscles.toList(),
                      defaultSets: parsedSets,
                      defaultReps: parsedReps,
                      updatedAt: now,
                    ) ??
                    ExerciseTemplate(
                      id: const Uuid().v4(),
                      name: name.text.trim(),
                      category: category,
                      equipment: equipment.text.trim().isEmpty
                          ? null
                          : equipment.text.trim(),
                      targetMuscleIds: selectedMuscles.toList(),
                      defaultSets: parsedSets,
                      defaultReps: parsedReps,
                      createdAt: now,
                      updatedAt: now,
                    ),
              );
            },
            child: const Text('保存'),
          ),
        ],
      ),
    ),
  );
  if (result != null && context.mounted) {
    await ref.read(exerciseTemplatesProvider.notifier).save(result);
  }
}

class _WorkoutSetDraft {
  _WorkoutSetDraft({
    required this.id,
    required int reps,
    required double weight,
    double? rpe,
    String? note,
    required this.completed,
  }) : unit = WeightUnit.kg,
       reps = TextEditingController(text: '$reps'),
       weight = TextEditingController(text: '$weight'),
       rpe = TextEditingController(text: rpe?.toString() ?? ''),
       note = TextEditingController(text: note ?? '');

  factory _WorkoutSetDraft.fromSet(ExerciseSet set, WeightUnit displayUnit) {
    final draft = _WorkoutSetDraft(
      id: set.id,
      reps: set.reps,
      weight: displayUnit == WeightUnit.lb
          ? set.weight * 2.2046226218
          : set.weight,
      rpe: set.rpe,
      note: set.note,
      completed: set.completed,
    );
    draft.unit = displayUnit;
    return draft;
  }

  final String id;
  final TextEditingController reps;
  final TextEditingController weight;
  final TextEditingController rpe;
  final TextEditingController note;
  bool completed;
  WeightUnit unit;

  void dispose() {
    reps.dispose();
    weight.dispose();
    rpe.dispose();
    note.dispose();
  }
}

Future<void> _showWorkoutEditor(
  BuildContext context,
  WidgetRef ref, {
  WorkoutSession? existing,
}) async {
  final templates =
      ref.read(exerciseTemplatesProvider).valueOrNull ??
      const <ExerciseTemplate>[];
  if (templates.isEmpty) {
    await _showTemplateEditor(context, ref);
    return;
  }
  final selectedTemplateId =
      existing?.templateId ??
      (existing?.sets.isNotEmpty == true
          ? existing!.sets.first.exerciseId
          : null);
  var selected = templates.firstWhere(
    (template) => template.id == selectedTemplateId,
    orElse: () => templates.first,
  );
  final settings =
      ref.read(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
  final displayUnit = settings.weightUnit;
  double toDisplay(double kg) =>
      displayUnit == WeightUnit.lb ? kg * 2.2046226218 : kg;
  double toKg(double value) =>
      displayUnit == WeightUnit.lb ? value / 2.2046226218 : value;
  final drafts = existing?.sets.isNotEmpty == true
      ? existing!.sets
            .map((set) => _WorkoutSetDraft.fromSet(set, displayUnit))
            .toList()
      : [
          for (var i = 0; i < selected.defaultSets; i++)
            _WorkoutSetDraft(
              id: const Uuid().v4(),
              reps: selected.defaultReps,
              weight: toDisplay(20),
              note: null,
              completed: true,
            ),
        ];
  final notes = TextEditingController(text: existing?.notes ?? '');
  for (final draft in drafts) {
    draft.unit = displayUnit;
  }
  final result = await showDialog<WorkoutSession>(
    context: context,
    builder: (context) => StatefulBuilder(
      builder: (context, setState) => AlertDialog(
        title: Text(existing == null ? '记录训练' : '编辑训练'),
        content: SizedBox(
          width: 460,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                DropdownButtonFormField<ExerciseTemplate>(
                  initialValue: selected,
                  decoration: const InputDecoration(labelText: '动作'),
                  items: [
                    for (final template in templates)
                      DropdownMenuItem(
                        value: template,
                        child: Text(template.name),
                      ),
                  ],
                  onChanged: (value) {
                    if (value != null) setState(() => selected = value);
                  },
                ),
                const Gap(8),
                for (var index = 0; index < drafts.length; index++)
                  _SetDraftEditor(
                    key: ValueKey(drafts[index].id),
                    index: index,
                    draft: drafts[index],
                    onChanged: () => setState(() {}),
                    onRemove: drafts.length <= 1
                        ? null
                        : () => setState(() => drafts.removeAt(index)),
                  ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: TextButton.icon(
                    onPressed: () => setState(
                      () => drafts.add(
                        _WorkoutSetDraft(
                          id: const Uuid().v4(),
                          reps: selected.defaultReps,
                          weight: toDisplay(20),
                          note: null,
                          completed: true,
                        ),
                      ),
                    ),
                    icon: const Icon(Icons.add_rounded),
                    label: const Text('添加一组'),
                  ),
                ),
                TextField(
                  controller: notes,
                  decoration: const InputDecoration(labelText: '备注（可选）'),
                ),
              ],
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () {
              final parsedSets = <ExerciseSet>[];
              for (var index = 0; index < drafts.length; index++) {
                final draft = drafts[index];
                final parsedReps = int.tryParse(draft.reps.text) ?? 0;
                final parsedWeight = double.tryParse(draft.weight.text) ?? -1;
                final parsedRpe = draft.rpe.text.trim().isEmpty
                    ? null
                    : double.tryParse(draft.rpe.text);
                if (parsedReps <= 0 ||
                    parsedWeight < 0 ||
                    (parsedRpe != null && (parsedRpe < 0 || parsedRpe > 10))) {
                  return;
                }
                parsedSets.add(
                  ExerciseSet(
                    id: draft.id,
                    exerciseId: selected.id,
                    setIndex: index,
                    reps: parsedReps,
                    weight: toKg(parsedWeight),
                    unit: displayUnit,
                    rpe: parsedRpe,
                    note: draft.note.text.trim().isEmpty
                        ? null
                        : draft.note.text.trim(),
                    completed: draft.completed,
                  ),
                );
              }
              final now = DateTime.now().toUtc();
              Navigator.pop(
                context,
                WorkoutSession(
                  id: existing?.id ?? const Uuid().v4(),
                  startedAt:
                      existing?.startedAt ??
                      now.subtract(const Duration(minutes: 35)),
                  endedAt: now,
                  templateId: selected.id,
                  notes: notes.text.trim().isEmpty ? null : notes.text.trim(),
                  sets: parsedSets,
                ),
              );
            },
            child: const Text('保存'),
          ),
        ],
      ),
    ),
  );
  for (final draft in drafts) {
    draft.dispose();
  }
  notes.dispose();
  if (result != null && context.mounted) {
    await ref.read(workoutSessionsProvider.notifier).save(result);
  }
}

class _SetDraftEditor extends StatelessWidget {
  const _SetDraftEditor({
    super.key,
    required this.index,
    required this.draft,
    required this.onChanged,
    required this.onRemove,
  });

  final int index;
  final _WorkoutSetDraft draft;
  final VoidCallback onChanged;
  final VoidCallback? onRemove;

  @override
  Widget build(BuildContext context) => Card(
    margin: const EdgeInsets.only(bottom: 8),
    child: Padding(
      padding: const EdgeInsets.fromLTRB(10, 4, 4, 8),
      child: Column(
        children: [
          Row(
            children: [
              Text(
                '第 ${index + 1} 组',
                style: const TextStyle(fontWeight: FontWeight.w700),
              ),
              const Spacer(),
              Checkbox(
                value: draft.completed,
                onChanged: (value) {
                  draft.completed = value ?? false;
                  onChanged();
                },
              ),
              const Text('完成'),
              if (onRemove != null)
                IconButton(
                  tooltip: '删除这一组',
                  onPressed: onRemove,
                  icon: const Icon(Icons.remove_circle_outline_rounded),
                ),
            ],
          ),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: draft.reps,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: '次数'),
                ),
              ),
              const Gap(8),
              Expanded(
                child: TextField(
                  controller: draft.weight,
                  keyboardType: const TextInputType.numberWithOptions(
                    decimal: true,
                  ),
                  decoration: InputDecoration(
                    labelText: '重量（${draft.unit.name}）',
                  ),
                ),
              ),
              const Gap(8),
              Expanded(
                child: TextField(
                  controller: draft.rpe,
                  keyboardType: const TextInputType.numberWithOptions(
                    decimal: true,
                  ),
                  decoration: const InputDecoration(labelText: 'RPE'),
                ),
              ),
            ],
          ),
          TextField(
            controller: draft.note,
            decoration: const InputDecoration(labelText: '组备注（可选）'),
            textInputAction: TextInputAction.next,
          ),
        ],
      ),
    ),
  );
}

Future<void> _showMetricEditor(
  BuildContext context,
  WidgetRef ref, {
  BodyMetric? existing,
}) async {
  final settings =
      ref.read(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
  final displayValue = existing == null
      ? null
      : (settings.weightUnit == WeightUnit.lb
            ? existing.value * 2.2046226218
            : existing.value);
  final value = TextEditingController(
    text: displayValue?.toStringAsFixed(1) ?? '',
  );
  final result = await showDialog<BodyMetric>(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(existing == null ? '记录体重' : '编辑体重'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          TextField(
            controller: value,
            autofocus: true,
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            decoration: InputDecoration(
              labelText: '体重（${settings.weightUnit.name}）',
            ),
          ),
          if (existing != null)
            Align(
              alignment: Alignment.centerRight,
              child: TextButton.icon(
                onPressed: () =>
                    Navigator.pop(context, existing.copyWith(value: -1)),
                icon: const Icon(Icons.delete_outline_rounded),
                label: const Text('删除记录'),
              ),
            ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        FilledButton(
          onPressed: () {
            final parsed = double.tryParse(value.text);
            if (parsed == null || parsed <= 0 || parsed > 500) return;
            final unit = settings.weightUnit;
            Navigator.pop(
              context,
              BodyMetric(
                id: existing?.id ?? const Uuid().v4(),
                measuredAt: existing?.measuredAt ?? DateTime.now().toUtc(),
                type: BodyMetricType.weight,
                value: unit == WeightUnit.lb ? parsed / 2.2046226218 : parsed,
                unit: 'kg',
              ),
            );
          },
          child: const Text('保存'),
        ),
      ],
    ),
  );
  if (result != null && context.mounted) {
    if (result.value < 0) {
      await ref.read(bodyMetricsProvider.notifier).delete(result.id);
    } else {
      await ref.read(bodyMetricsProvider.notifier).save(result);
    }
  }
}

Future<void> _exportData(
  BuildContext context,
  WidgetRef ref, {
  required bool asCsv,
}) async {
  final templates =
      ref.read(exerciseTemplatesProvider).valueOrNull ??
      const <ExerciseTemplate>[];
  final sessions =
      ref.read(workoutSessionsProvider).valueOrNull ?? const <WorkoutSession>[];
  final metrics =
      ref.read(bodyMetricsProvider).valueOrNull ?? const <BodyMetric>[];
  final settings =
      ref.read(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
  final content = asCsv
      ? BackupCodec.workoutsCsv(sessions)
      : BackupCodec.encodeJson(
          BackupCodec.createBundle(
            templates: templates,
            sessions: sessions,
            metrics: metrics,
            settings: settings.toJson(),
          ),
        );
  await showDialog<void>(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(asCsv ? 'CSV 导出预览' : 'JSON 备份预览'),
      content: SizedBox(
        width: 520,
        child: SingleChildScrollView(
          child: SelectableText(content, style: const TextStyle(fontSize: 11)),
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        FilledButton.icon(
          onPressed: () async {
            final status = await ref
                .read(platformExportServiceProvider)
                .shareText(
                  fileName: asCsv
                      ? 'workout-export.csv'
                      : 'fitness-backup.json',
                  content: content,
                  mimeType: asCsv ? 'text/csv' : 'application/json',
                );
            if (!context.mounted) return;
            final message = switch (status) {
              PlatformExportStatus.shared => '分享面板已打开，取消分享不会修改本地数据。',
              PlatformExportStatus.unavailable => '当前平台不支持系统分享，请使用复制预览。',
              PlatformExportStatus.failed => '系统分享失败，请使用复制预览。',
            };
            ScaffoldMessenger.of(context)
                .showSnackBar(SnackBar(content: Text(message)));
          },
          icon: const Icon(Icons.share_rounded),
          label: const Text('分享'),
        ),
        FilledButton.icon(
          onPressed: () async {
            try {
              await Clipboard.setData(ClipboardData(text: content));
              if (context.mounted) {
                Navigator.pop(context);
                ScaffoldMessenger.of(context)
                    .showSnackBar(const SnackBar(content: Text('已复制导出内容')));
              }
            } on Object {
              if (context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('复制失败，导出内容仍可手动选择。')),
                );
              }
            }
          },
          icon: const Icon(Icons.copy_rounded),
          label: const Text('复制'),
        ),
      ],
    ),
  );
}

Future<void> _importJson(BuildContext context, WidgetRef ref) async {
  final input = TextEditingController();
  final source = await showDialog<String>(
    context: context,
    builder: (context) => AlertDialog(
      title: const Text('导入 JSON 备份'),
      content: SizedBox(
        width: 520,
        child: TextField(
          controller: input,
          autofocus: true,
          minLines: 8,
          maxLines: 16,
          keyboardType: TextInputType.multiline,
          decoration: const InputDecoration(
            hintText: '粘贴由本应用导出的 JSON 内容',
            border: OutlineInputBorder(),
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        FilledButton(
          onPressed: () => Navigator.pop(context, input.text),
          child: const Text('校验并导入'),
        ),
      ],
    ),
  );
  input.dispose();
  if (source == null || source.trim().isEmpty || !context.mounted) return;
  try {
    final bundle = BackupCodec.decodeJson(source);
    var settingsRestored = true;
    if (bundle.settings.isNotEmpty) {
      settingsRestored = await ref
          .read(appSettingsProvider.notifier)
          .restore(AppSettings.fromJson(bundle.settings));
    }
    await ref
        .read(fitnessRepositoriesProvider)
        .replaceBackup(
          templates: bundle.exerciseTemplates,
          sessions: bundle.workoutSessions,
          metrics: bundle.bodyMetrics,
        );
    ref.invalidate(exerciseTemplatesProvider);
    ref.invalidate(workoutSessionsProvider);
    ref.invalidate(bodyMetricsProvider);
    if (context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            settingsRestored
                ? '已导入 ${bundle.exerciseTemplates.length} 个动作、${bundle.workoutSessions.length} 次训练和 ${bundle.bodyMetrics.length} 条身体数据。'
                : '数据已导入，但设置写入失败；请检查权限后重试。',
          ),
        ),
      );
    }
  } on Object catch (error) {
    if (context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            '导入失败：${error is FormatException ? error.message : '格式不可用'}',
          ),
        ),
      );
    }
  }
}
