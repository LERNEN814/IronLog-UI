// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'exercise_template.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ExerciseTemplate _$ExerciseTemplateFromJson(Map<String, dynamic> json) =>
    _ExerciseTemplate(
      id: json['id'] as String,
      name: json['name'] as String,
      category: json['category'] as String? ?? 'strength',
      equipment: json['equipment'] as String?,
      targetMuscleIds:
          (json['targetMuscleIds'] as List<dynamic>?)
              ?.map((e) => e as String)
              .toList() ??
          const <String>[],
      defaultSets: (json['defaultSets'] as num?)?.toInt() ?? 3,
      defaultReps: (json['defaultReps'] as num?)?.toInt() ?? 10,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      schemaVersion: (json['schemaVersion'] as num?)?.toInt() ?? 1,
    );

Map<String, dynamic> _$ExerciseTemplateToJson(_ExerciseTemplate instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'category': instance.category,
      'equipment': instance.equipment,
      'targetMuscleIds': instance.targetMuscleIds,
      'defaultSets': instance.defaultSets,
      'defaultReps': instance.defaultReps,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
      'schemaVersion': instance.schemaVersion,
    };
