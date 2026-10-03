// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'body_metric.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BodyMetric _$BodyMetricFromJson(Map<String, dynamic> json) => _BodyMetric(
  id: json['id'] as String,
  measuredAt: DateTime.parse(json['measuredAt'] as String),
  type: $enumDecode(_$BodyMetricTypeEnumMap, json['type']),
  value: (json['value'] as num).toDouble(),
  unit: json['unit'] as String,
  note: json['note'] as String?,
  schemaVersion: (json['schemaVersion'] as num?)?.toInt() ?? 1,
);

Map<String, dynamic> _$BodyMetricToJson(_BodyMetric instance) =>
    <String, dynamic>{
      'id': instance.id,
      'measuredAt': instance.measuredAt.toIso8601String(),
      'type': _$BodyMetricTypeEnumMap[instance.type]!,
      'value': instance.value,
      'unit': instance.unit,
      'note': instance.note,
      'schemaVersion': instance.schemaVersion,
    };

const _$BodyMetricTypeEnumMap = {
  BodyMetricType.weight: 'weight',
  BodyMetricType.bodyFat: 'bodyFat',
  BodyMetricType.waist: 'waist',
  BodyMetricType.custom: 'custom',
};
