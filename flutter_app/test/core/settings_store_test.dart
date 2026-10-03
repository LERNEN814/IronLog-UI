import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:hive_ce/hive_ce.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:ironlog/core/models/domain_enums.dart';
import 'package:ironlog/core/settings/app_settings.dart';
import 'package:ironlog/core/settings/settings_store.dart';

void main() {
  test('settings JSON round trip preserves template order', () {
    const settings = AppSettings(templateOrder: ['squat', 'bench']);

    expect(AppSettings.fromJson(settings.toJson()), settings);
  });

  test('in-memory settings round trip preserves preferences', () async {
    final store = InMemorySettingsStore();
    const settings = AppSettings(
      weightUnit: WeightUnit.lb,
      remindersEnabled: true,
      defaultTemplateId: 'push-day',
      templateOrder: ['pull-day', 'push-day'],
      themeMode: 'system',
    );

    await store.write(settings);

    expect(await store.read(), settings);
  });

  test(
    'SharedPreferences settings round trip and malformed fallback',
    () async {
      SharedPreferences.setMockInitialValues({
        'ironlog.settings.v1': '{bad json',
      });
      final preferences = await SharedPreferences.getInstance();
      final store = SharedPreferencesSettingsStore(preferences);

      expect(await store.read(), AppSettings.defaults);

      const settings = AppSettings(
        weightUnit: WeightUnit.lb,
        remindersEnabled: true,
        defaultTemplateId: 'pull-day',
        templateOrder: ['pull-day', 'legs-day'],
      );
      await store.write(settings);
      expect(await store.read(), settings);
    },
  );

  test('Hive settings survive closing and reopening the box', () async {
    final directory = await Directory.systemTemp.createTemp(
      'fitness-settings-',
    );
    addTearDown(() async {
      await Hive.close();
      await directory.delete(recursive: true);
    });

    Hive.init(directory.path);
    final box = await Hive.openBox<dynamic>('settings-test');
    final store = HiveSettingsStore(box);
    const settings = AppSettings(
      weightUnit: WeightUnit.lb,
      remindersEnabled: true,
      templateOrder: ['legs-day', 'push-day'],
      themeMode: 'light',
    );
    await store.write(settings);
    expect(await store.read(), settings);
    await Hive.close();

    final reopened = HiveSettingsStore(
      await Hive.openBox<dynamic>('settings-test'),
    );
    expect(await reopened.read(), settings);
  });
}
