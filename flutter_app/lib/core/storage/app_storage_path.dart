import 'package:flutter/services.dart';

const _storageChannel = MethodChannel('ironlog/storage');

/// Returns the platform-owned writable directory used by Hive on Android.
/// Hosts without the channel return null so startup can keep its in-memory
/// fallback instead of blocking the workout UI.
Future<String?> applicationStoragePath() async {
  try {
    return await _storageChannel.invokeMethod<String>(
      'getApplicationStoragePath',
    );
  } on MissingPluginException {
    return null;
  } on PlatformException {
    return null;
  }
}
