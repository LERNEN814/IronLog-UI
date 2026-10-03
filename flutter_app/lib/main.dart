import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'app.dart';
import 'core/settings/settings_store.dart';
import 'core/storage/app_storage_path.dart';
import 'core/storage/repositories.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  FitnessRepositories repositories;
  try {
    SettingsStore? settings;
    try {
      settings = SharedPreferencesSettingsStore(
        await SharedPreferences.getInstance(),
      );
    } on Object {
      // Desktop/test hosts may not register the preferences plugin. Hive is
      // still a local fallback and is opened by FitnessRepositories below.
    }
    final storagePath = await applicationStoragePath();
    repositories = await FitnessRepositories.openHive(
      path: storagePath ?? 'ironlog_data',
      settings: settings,
    );
  } on Object {
    // A storage/plugin failure must not block recording. The in-memory
    // repository keeps the UI usable and the user can retry on next launch.
    repositories = FitnessRepositories.inMemory();
  }
  runApp(buildApp(repositories: repositories));
}
