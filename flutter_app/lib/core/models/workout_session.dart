import 'package:freezed_annotation/freezed_annotation.dart';

import 'exercise_set.dart';

part 'workout_session.freezed.dart';
part 'workout_session.g.dart';

@freezed
abstract class WorkoutSession with _$WorkoutSession {
  const factory WorkoutSession({
    required String id,
    required DateTime startedAt,
    DateTime? endedAt,
    String? templateId,
    String? notes,
    @Default(<ExerciseSet>[]) List<ExerciseSet> sets,
    @Default(1) int schemaVersion,
  }) = _WorkoutSession;

  const WorkoutSession._();

  factory WorkoutSession.fromJson(Map<String, dynamic> json) =>
      _$WorkoutSessionFromJson(json);

  bool get isCompleted => endedAt != null;

  double get totalVolume => sets
      .where((set) => set.completed)
      .fold<double>(0, (total, set) => total + set.volume);
}
