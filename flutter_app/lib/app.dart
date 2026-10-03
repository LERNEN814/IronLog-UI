import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/theme/app_theme.dart';
import 'core/providers/repository_providers.dart';
import 'core/storage/repositories.dart';
import 'features/dashboard/presentation/dashboard_page.dart';

class FitnessRecordApp extends StatelessWidget {
  const FitnessRecordApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'IronLog',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.dark(),
      home: const DashboardPage(),
    );
  }
}

Widget buildApp({FitnessRepositories? repositories}) => ProviderScope(
  overrides: repositories == null
      ? const []
      : [fitnessRepositoriesProvider.overrideWithValue(repositories)],
  child: const FitnessRecordApp(),
);
