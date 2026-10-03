// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'backup_bundle.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BackupBundle _$BackupBundleFromJson(Map<String, dynamic> json) =>
    _BackupBundle(
      backupVersion: (json['backupVersion'] as num?)?.toInt() ?? 1,
      exportedAt: DateTime.parse(json['exportedAt'] as String),
      settings:
          json['settings'] as Map<String, dynamic>? ??
          const <String, dynamic>{},
      exerciseTemplates:
          (json['exerciseTemplates'] as List<dynamic>?)
              ?.map((e) => ExerciseTemplate.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <ExerciseTemplate>[],
      workoutSessions:
          (json['workoutSessions'] as List<dynamic>?)
              ?.map((e) => WorkoutSession.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <WorkoutSession>[],
      bodyMetrics:
          (json['bodyMetrics'] as List<dynamic>?)
              ?.map((e) => BodyMetric.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <BodyMetric>[],
    );

Map<String, dynamic> _$BackupBundleToJson(
  _BackupBundle instance,
) => <String, dynamic>{
  'backupVersion': instance.backupVersion,
  'exportedAt': instance.exportedAt.toIso8601String(),
  'settings': instance.settings,
  'exerciseTemplates': instance.exerciseTemplates
      .map((e) => e.toJson())
      .toList(),
  'workoutSessions': instance.workoutSessions.map((e) => e.toJson()).toList(),
  'bodyMetrics': instance.bodyMetrics.map((e) => e.toJson()).toList(),
};
