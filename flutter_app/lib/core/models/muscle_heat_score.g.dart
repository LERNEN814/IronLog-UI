// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'muscle_heat_score.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MuscleHeatScore _$MuscleHeatScoreFromJson(Map<String, dynamic> json) =>
    _MuscleHeatScore(
      muscleId: json['muscleId'] as String,
      view: $enumDecode(_$HeatmapViewEnumMap, json['view']),
      side:
          $enumDecodeNullable(_$HeatmapSideEnumMap, json['side']) ??
          HeatmapSide.bilateral,
      score: (json['score'] as num?)?.toDouble() ?? 0,
      rawLoad: (json['rawLoad'] as num?)?.toDouble() ?? 0,
      hasData: json['hasData'] as bool? ?? false,
      sampleCount: (json['sampleCount'] as num?)?.toInt() ?? 0,
      rangeStart: DateTime.parse(json['rangeStart'] as String),
      rangeEnd: DateTime.parse(json['rangeEnd'] as String),
    );

Map<String, dynamic> _$MuscleHeatScoreToJson(_MuscleHeatScore instance) =>
    <String, dynamic>{
      'muscleId': instance.muscleId,
      'view': _$HeatmapViewEnumMap[instance.view]!,
      'side': _$HeatmapSideEnumMap[instance.side]!,
      'score': instance.score,
      'rawLoad': instance.rawLoad,
      'hasData': instance.hasData,
      'sampleCount': instance.sampleCount,
      'rangeStart': instance.rangeStart.toIso8601String(),
      'rangeEnd': instance.rangeEnd.toIso8601String(),
    };

const _$HeatmapViewEnumMap = {
  HeatmapView.front: 'front',
  HeatmapView.back: 'back',
};

const _$HeatmapSideEnumMap = {
  HeatmapSide.left: 'left',
  HeatmapSide.right: 'right',
  HeatmapSide.bilateral: 'bilateral',
};
