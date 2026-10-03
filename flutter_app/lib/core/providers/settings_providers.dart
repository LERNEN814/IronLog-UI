import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/domain_enums.dart';
import '../settings/app_settings.dart';
import '../settings/settings_store.dart';
import 'repository_providers.dart';

final settingsStoreProvider = Provider<SettingsStore>(
  (ref) => ref.watch(fitnessRepositoriesProvider).settings,
);

final appSettingsProvider =
    AsyncNotifierProvider<AppSettingsNotifier, AppSettings>(
      AppSettingsNotifier.new,
    );

class AppSettingsNotifier extends AsyncNotifier<AppSettings> {
  late SettingsStore _store;

  @override
  Future<AppSettings> build() async {
    _store = ref.watch(settingsStoreProvider);
    try {
      return await _store.read();
    } on Object {
      return AppSettings.defaults;
    }
  }

  Future<bool> setWeightUnit(AppSettings current, String unit) async {
    final parsed = WeightUnit.values.firstWhere(
      (value) => value.name == unit,
      orElse: () => current.weightUnit,
    );
    return _save(current.copyWith(weightUnit: parsed));
  }

  Future<bool> setReminderEnabled(AppSettings current, bool enabled) async =>
      _save(current.copyWith(remindersEnabled: enabled));

  Future<bool> setThemeMode(AppSettings current, String mode) async =>
      _save(current.copyWith(themeMode: mode));

  Future<bool> setDefaultTemplate(AppSettings current, String? id) async =>
      _save(
        id == null
            ? current.copyWith(clearDefaultTemplate: true)
            : current.copyWith(defaultTemplateId: id),
      );

  Future<bool> setTemplateOrder(
    AppSettings current,
    List<String> order,
  ) async =>
      _save(current.copyWith(templateOrder: List<String>.unmodifiable(order)));

  Future<bool> restore(AppSettings settings) => _save(settings);

  Future<bool> _save(AppSettings next) async {
    try {
      await _store.write(next);
      state = AsyncData(next);
      return true;
    } on Object {
      // Keep the in-memory state usable even if a platform store is read-only.
      state = AsyncData(next);
      return false;
    }
  }
}
