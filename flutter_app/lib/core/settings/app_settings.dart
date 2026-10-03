import '../models/domain_enums.dart';

/// Small, version-tolerant preferences used by the settings screen.
///
/// Settings are intentionally separate from workout data. A failed settings
/// plugin must never prevent the local workout repositories from opening.
class AppSettings {
  const AppSettings({
    this.weightUnit = WeightUnit.kg,
    this.remindersEnabled = false,
    this.defaultTemplateId,
    this.templateOrder = const <String>[],
    this.themeMode = 'dark',
  });

  final WeightUnit weightUnit;
  final bool remindersEnabled;
  final String? defaultTemplateId;
  final List<String> templateOrder;
  final String themeMode;

  static const defaults = AppSettings();

  AppSettings copyWith({
    WeightUnit? weightUnit,
    bool? remindersEnabled,
    String? defaultTemplateId,
    bool clearDefaultTemplate = false,
    List<String>? templateOrder,
    String? themeMode,
  }) => AppSettings(
    weightUnit: weightUnit ?? this.weightUnit,
    remindersEnabled: remindersEnabled ?? this.remindersEnabled,
    defaultTemplateId: clearDefaultTemplate
        ? null
        : (defaultTemplateId ?? this.defaultTemplateId),
    templateOrder: templateOrder ?? this.templateOrder,
    themeMode: themeMode ?? this.themeMode,
  );

  Map<String, Object?> toJson() => {
    'weightUnit': weightUnit.name,
    'remindersEnabled': remindersEnabled,
    'defaultTemplateId': defaultTemplateId,
    'templateOrder': templateOrder,
    'themeMode': themeMode,
  };

  factory AppSettings.fromJson(Map<String, dynamic> json) {
    final rawUnit = json['weightUnit'];
    final unit = WeightUnit.values.firstWhere(
      (value) => value.name == rawUnit,
      orElse: () => WeightUnit.kg,
    );
    final rawTemplate = json['defaultTemplateId'];
    final rawOrder = json['templateOrder'];
    final templateOrder = rawOrder is List
        ? rawOrder.whereType<String>().where((id) => id.isNotEmpty).toList()
        : const <String>[];
    return AppSettings(
      weightUnit: unit,
      remindersEnabled: json['remindersEnabled'] == true,
      defaultTemplateId: rawTemplate is String && rawTemplate.isNotEmpty
          ? rawTemplate
          : null,
      templateOrder: templateOrder,
      themeMode:
          json['themeMode'] is String &&
              (json['themeMode'] as String).isNotEmpty
          ? json['themeMode'] as String
          : 'dark',
    );
  }

  @override
  bool operator ==(Object other) =>
      other is AppSettings &&
      other.weightUnit == weightUnit &&
      other.remindersEnabled == remindersEnabled &&
      other.defaultTemplateId == defaultTemplateId &&
      _listEquals(other.templateOrder, templateOrder) &&
      other.themeMode == themeMode;

  @override
  int get hashCode => Object.hash(
    weightUnit,
    remindersEnabled,
    defaultTemplateId,
    Object.hashAll(templateOrder),
    themeMode,
  );

  static bool _listEquals(List<String> left, List<String> right) {
    if (left.length != right.length) return false;
    for (var index = 0; index < left.length; index++) {
      if (left[index] != right[index]) return false;
    }
    return true;
  }
}
