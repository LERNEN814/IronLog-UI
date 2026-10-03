import '../../../core/models/workout_session.dart';

/// Returns the heaviest completed set for each exercise in kilograms.
///
/// PR is deliberately defined as absolute completed-set weight, rather than
/// an estimated one-repetition maximum, because the app stores no exercise
/// movement metadata and this rule is stable across units and backups.
Map<String, double> personalRecordsByExercise(
  Iterable<WorkoutSession> sessions,
) {
  final records = <String, double>{};
  for (final session in sessions) {
    for (final set in session.sets) {
      if (!set.completed || set.reps <= 0 || set.weight < 0) continue;
      final previous = records[set.exerciseId] ?? 0;
      if (set.weight > previous) records[set.exerciseId] = set.weight;
    }
  }
  return records;
}
