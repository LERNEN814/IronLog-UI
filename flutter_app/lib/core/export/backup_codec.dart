import 'dart:convert';

import 'package:csv/csv.dart';

import '../models/backup_bundle.dart';
import '../models/body_metric.dart';
import '../models/exercise_template.dart';
import '../models/workout_session.dart';

class BackupCodec {
  const BackupCodec._();

  static String encodeJson(BackupBundle bundle) =>
      const JsonEncoder.withIndent('  ').convert(bundle.toJson());

  static BackupBundle decodeJson(String source) {
    final decoded = jsonDecode(source);
    if (decoded is! Map) {
      throw const FormatException('备份必须是 JSON 对象');
    }
    final map = Map<String, dynamic>.from(decoded);
    if (map['backupVersion'] is! num ||
        (map['backupVersion'] as num).toInt() != 1) {
      throw const FormatException('不支持的备份版本');
    }
    return BackupBundle.fromJson(map);
  }

  static String workoutsCsv(Iterable<WorkoutSession> sessions) {
    final rows = <List<Object?>>[
      [
        'session_id',
        'started_at',
        'ended_at',
        'exercise_id',
        'set',
        'reps',
        'weight',
        'unit',
        'rpe',
        'completed',
        'note',
      ],
    ];
    for (final session in sessions) {
      if (session.sets.isEmpty) {
        rows.add([
          session.id,
          session.startedAt.toIso8601String(),
          session.endedAt?.toIso8601String() ?? '',
          '',
          '',
          '',
          '',
          '',
          '',
          '',
          session.notes ?? '',
        ]);
      } else {
        for (final set in session.sets) {
          rows.add([
            session.id,
            session.startedAt.toIso8601String(),
            session.endedAt?.toIso8601String() ?? '',
            set.exerciseId,
            set.setIndex + 1,
            set.reps,
            set.weight,
            set.unit.name,
            set.rpe ?? '',
            set.completed,
            set.note ?? session.notes ?? '',
          ]);
        }
      }
    }
    return csv.encode(rows);
  }

  static BackupBundle createBundle({
    required Iterable<ExerciseTemplate> templates,
    required Iterable<WorkoutSession> sessions,
    required Iterable<BodyMetric> metrics,
    Map<String, dynamic> settings = const {},
    DateTime? now,
  }) => BackupBundle(
    exportedAt: (now ?? DateTime.now()).toUtc(),
    settings: settings,
    exerciseTemplates: templates.toList(growable: false),
    workoutSessions: sessions.toList(growable: false),
    bodyMetrics: metrics.toList(growable: false),
  );
}
