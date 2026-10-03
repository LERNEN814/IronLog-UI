import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../export/platform_export_service.dart';

final platformExportServiceProvider = Provider<PlatformExportService>(
  (ref) => const PlatformExportService(),
);
