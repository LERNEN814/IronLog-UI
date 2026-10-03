// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'exercise_set.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ExerciseSet _$ExerciseSetFromJson(Map<String, dynamic> json) => _ExerciseSet(
  id: json['id'] as String,
  exerciseId: json['exerciseId'] as String,
  setIndex: (json['setIndex'] as num).toInt(),
  reps: (json['reps'] as num).toInt(),
  weight: (json['weight'] as num).toDouble(),
  unit: $enumDecodeNullable(_$WeightUnitEnumMap, json['unit']) ?? WeightUnit.kg,
  rpe: (json['rpe'] as num?)?.toDouble(),
  completed: json['completed'] as bool? ?? false,
  note: json['note'] as String?,
);

Map<String, dynamic> _$ExerciseSetToJson(_ExerciseSet instance) =>
    <String, dynamic>{
      'id': instance.id,
      'exerciseId': instance.exerciseId,
      'setIndex': instance.setIndex,
      'reps': instance.reps,
      'weight': instance.weight,
      'unit': _$WeightUnitEnumMap[instance.unit]!,
      'rpe': instance.rpe,
      'completed': instance.completed,
      'note': instance.note,
    };

const _$WeightUnitEnumMap = {WeightUnit.kg: 'kg', WeightUnit.lb: 'lb'};
