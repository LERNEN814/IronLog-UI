import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class NotificationService {
  static const workoutReminderId = 1001;
  static const workoutReminderChannelId = 'workout_reminders';

  final FlutterLocalNotificationsPlugin _plugin =
      FlutterLocalNotificationsPlugin();
  bool _initialized = false;

  Future<bool> _ensureInitialized() async {
    if (_initialized) return true;
    try {
      const settings = InitializationSettings(
        android: AndroidInitializationSettings('ic_launcher'),
      );
      _initialized = await _plugin.initialize(settings: settings) ?? false;
      if (_initialized) {
        final android = _plugin
            .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin
            >();
        await android?.createNotificationChannel(
          const AndroidNotificationChannel(
            workoutReminderChannelId,
            '训练提醒',
            description: '本地训练提醒通知',
            importance: Importance.defaultImportance,
          ),
        );
      }
      return _initialized;
    } on Object {
      return false;
    }
  }

  Future<bool> requestPermission() async {
    if (!await _ensureInitialized()) return false;
    try {
      final android = _plugin
          .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin
          >();
      return await android?.requestNotificationsPermission() ?? false;
    } on Object {
      return false;
    }
  }

  Future<bool> showWorkoutReminder() async {
    if (!await _ensureInitialized()) return false;
    try {
      await _plugin.show(
        id: workoutReminderId,
        title: '训练提醒',
        body: '准备好记录今天的训练了吗？',
        notificationDetails: const NotificationDetails(
          android: AndroidNotificationDetails(
            workoutReminderChannelId,
            '训练提醒',
            channelDescription: '本地训练提醒通知',
            importance: Importance.defaultImportance,
            priority: Priority.defaultPriority,
          ),
        ),
      );
      return true;
    } on Object {
      return false;
    }
  }

  /// Starts an inexact daily reminder from the current time.
  ///
  /// The app deliberately uses an inexact schedule, so it does not need the
  /// exact-alarm permission. Unsupported hosts return false and leave workout
  /// logging unaffected.
  Future<bool> scheduleDailyWorkoutReminder() async {
    if (!await _ensureInitialized()) return false;
    try {
      await _plugin.periodicallyShow(
        id: workoutReminderId,
        title: '训练提醒',
        body: '准备好记录今天的训练了吗？',
        repeatInterval: RepeatInterval.daily,
        notificationDetails: const NotificationDetails(
          android: AndroidNotificationDetails(
            workoutReminderChannelId,
            '训练提醒',
            channelDescription: '本地训练提醒通知',
            importance: Importance.defaultImportance,
            priority: Priority.defaultPriority,
          ),
        ),
        androidScheduleMode: AndroidScheduleMode.inexactAllowWhileIdle,
      );
      return true;
    } on Object {
      return false;
    }
  }

  Future<bool> cancelWorkoutReminder() async {
    if (!await _ensureInitialized()) return false;
    try {
      await _plugin.cancel(id: workoutReminderId);
      return true;
    } on Object {
      return false;
    }
  }

  Future<bool> areNotificationsEnabled() async {
    if (!await _ensureInitialized()) return false;
    try {
      final android = _plugin
          .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin
          >();
      return await android?.areNotificationsEnabled() ?? false;
    } on Object {
      return false;
    }
  }
}
