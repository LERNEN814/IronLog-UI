import 'package:freezed_annotation/freezed_annotation.dart';

import 'domain_enums.dart';

part 'body_metric.freezed.dart';
part 'body_metric.g.dart';

@freezed
abstract class BodyMetric with _$BodyMetric {
  const factory BodyMetric({
    required String id,
    required DateTime measuredAt,
    required BodyMetricType type,
    required double value,
    required String unit,
    String? note,
    @Default(1) int schemaVersion,
  }) = _BodyMetric;

  const BodyMetric._();

  factory BodyMetric.fromJson(Map<String, dynamic> json) =>
      _$BodyMetricFromJson(json);

  bool get hasValidInput =>
      value.isFinite && value >= 0 && unit.trim().isNotEmpty;
}
