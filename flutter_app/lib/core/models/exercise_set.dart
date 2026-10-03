import 'package:freezed_annotation/freezed_annotation.dart';

import 'domain_enums.dart';

part 'exercise_set.freezed.dart';
part 'exercise_set.g.dart';

@freezed
abstract class ExerciseSet with _$ExerciseSet {
  const factory ExerciseSet({
    required String id,
    required String exerciseId,
    required int setIndex,
    required int reps,
    required double weight,
    @Default(WeightUnit.kg) WeightUnit unit,
    double? rpe,
    @Default(false) bool completed,
    String? note,
  }) = _ExerciseSet;

  const ExerciseSet._();

  factory ExerciseSet.fromJson(Map<String, dynamic> json) =>
      _$ExerciseSetFromJson(json);

  double get volume => weight * reps;

  bool get hasValidInput =>
      exerciseId.trim().isNotEmpty &&
      setIndex >= 0 &&
      reps > 0 &&
      weight >= 0 &&
      (rpe == null || (rpe! >= 0 && rpe! <= 10));
}
