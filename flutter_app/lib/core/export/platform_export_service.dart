import 'package:flutter/services.dart';

enum PlatformExportStatus { shared, unavailable, failed }

class PlatformExportService {
  const PlatformExportService();

  static const _channel = MethodChannel('ironlog/export');

  Future<PlatformExportStatus> shareText({
    required String fileName,
    required String content,
    required String mimeType,
  }) async {
    try {
      final result = await _channel.invokeMethod<bool>('shareText', {
        'fileName': fileName,
        'content': content,
        'mimeType': mimeType,
      });
      return result == true
          ? PlatformExportStatus.shared
          : PlatformExportStatus.failed;
    } on MissingPluginException {
      return PlatformExportStatus.unavailable;
    } on PlatformException {
      return PlatformExportStatus.failed;
    } on Object {
      // A host without an initialized binary messenger must not block export
      // previews or the core offline logging flow.
      return PlatformExportStatus.unavailable;
    }
  }
}
