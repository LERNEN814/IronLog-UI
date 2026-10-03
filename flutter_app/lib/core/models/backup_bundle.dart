import 'package:freezed_annotation/freezed_annotation.dart';

import 'body_metric.dart';
import 'exercise_template.dart';
import 'workout_session.dart';

part 'backup_bundle.freezed.dart';
part 'backup_bundle.g.dart';

/// Versioned, self-contained JSON backup payload.
@freezed
abstract class BackupBundle with _$BackupBundle {
  const factory BackupBundle({
    @Default(1) int backupVersion,
    required DateTime exportedAt,
    @Default(<String, dynamic>{}) Map<String, dynamic> settings,
    @Default(<ExerciseTemplate>[]) List<ExerciseTemplate> exerciseTemplates,
    @Default(<WorkoutSession>[]) List<WorkoutSession> workoutSessions,
    @Default(<BodyMetric>[]) List<BodyMetric> bodyMetrics,
  }) = _BackupBundle;

  const BackupBundle._();

  factory BackupBundle.fromJson(Map<String, dynamic> json) =>
      _$BackupBundleFromJson(json);
}
