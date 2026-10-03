import '../../../core/models/exercise_template.dart';
import '../../../core/models/workout_session.dart';

/// Converts completed sets into a normalized muscle load map. Scores are
/// normalized against the largest load in the selected window, so changing
/// kg/lb display units does not change the heatmap result.
Map<String, double> aggregateMuscleScores({
  required Iterable<WorkoutSession> sessions,
  required Iterable<ExerciseTemplate> templates,
  DateTime? rangeStart,
  DateTime? rangeEnd,
}) {
  // Date-range controls represent calendar days in local time. Normalize the
  // optional end boundary so callers cannot accidentally exclude a session
  // later on the selected end date.
  final normalizedEnd = rangeEnd == null
      ? null
      : DateTime(rangeEnd.year, rangeEnd.month, rangeEnd.day, 23, 59, 59, 999);
  final templateById = {
    for (final template in templates) template.id: template,
  };
  final loads = <String, double>{};
  for (final session in sessions) {
    if (rangeStart != null && session.startedAt.isBefore(rangeStart)) continue;
    if (normalizedEnd != null && session.startedAt.isAfter(normalizedEnd)) {
      continue;
    }
    final sets = session.sets.where((set) => set.completed);
    for (final set in sets) {
      final targetIds =
          templateById[set.exerciseId]?.targetMuscleIds ?? const <String>[];
      if (targetIds.isEmpty) continue;
      final perMuscle = set.volume / targetIds.length;
      for (final muscleId in targetIds) {
        loads[muscleId] = (loads[muscleId] ?? 0) + perMuscle;
      }
    }
  }
  if (loads.isEmpty) return const <String, double>{};
  final values = loads.values.toList()..sort();
  final p95Index = ((values.length - 1) * .95).round();
  final baseline = values[p95Index] <= 0 ? 1 : values[p95Index];
  return {
    for (final entry in loads.entries)
      entry.key: (entry.value / baseline).clamp(0.0, 1.0).toDouble(),
  };
}

double totalCompletedVolume(Iterable<WorkoutSession> sessions) =>
    sessions.fold<double>(0, (sum, session) => sum + session.totalVolume);
