import 'package:freezed_annotation/freezed_annotation.dart';

import 'domain_enums.dart';

part 'muscle_heat_score.freezed.dart';
part 'muscle_heat_score.g.dart';

@freezed
abstract class MuscleHeatScore with _$MuscleHeatScore {
  const factory MuscleHeatScore({
    required String muscleId,
    required HeatmapView view,
    @Default(HeatmapSide.bilateral) HeatmapSide side,
    @Default(0) double score,
    @Default(0) double rawLoad,
    @Default(false) bool hasData,
    @Default(0) int sampleCount,
    required DateTime rangeStart,
    required DateTime rangeEnd,
  }) = _MuscleHeatScore;

  const MuscleHeatScore._();

  factory MuscleHeatScore.fromJson(Map<String, dynamic> json) =>
      _$MuscleHeatScoreFromJson(json);

  double get clampedScore => score.clamp(0, 1).toDouble();
}
