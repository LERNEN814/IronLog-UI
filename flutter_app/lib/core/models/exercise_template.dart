import 'package:freezed_annotation/freezed_annotation.dart';

part 'exercise_template.freezed.dart';
part 'exercise_template.g.dart';

@freezed
abstract class ExerciseTemplate with _$ExerciseTemplate {
  const factory ExerciseTemplate({
    required String id,
    required String name,
    @Default('strength') String category,
    String? equipment,
    @Default(<String>[]) List<String> targetMuscleIds,
    @Default(3) int defaultSets,
    @Default(10) int defaultReps,
    required DateTime createdAt,
    required DateTime updatedAt,
    @Default(1) int schemaVersion,
  }) = _ExerciseTemplate;

  const ExerciseTemplate._();

  factory ExerciseTemplate.fromJson(Map<String, dynamic> json) =>
      _$ExerciseTemplateFromJson(json);

  bool get hasValidTargets =>
      targetMuscleIds.isNotEmpty &&
      defaultSets > 0 &&
      defaultReps > 0 &&
      name.trim().isNotEmpty;
}
