import 'package:json_annotation/json_annotation.dart';

/// Units used for weight entry and persisted in backups.
enum WeightUnit {
  @JsonValue('kg')
  kg,
  @JsonValue('lb')
  lb,
}

/// Extensible body measurement kinds. `custom` uses the caller-provided unit.
enum BodyMetricType {
  @JsonValue('weight')
  weight,
  @JsonValue('bodyFat')
  bodyFat,
  @JsonValue('waist')
  waist,
  @JsonValue('custom')
  custom,
}

enum HeatmapView {
  @JsonValue('front')
  front,
  @JsonValue('back')
  back,
}

enum HeatmapSide {
  @JsonValue('left')
  left,
  @JsonValue('right')
  right,
  @JsonValue('bilateral')
  bilateral,
}
