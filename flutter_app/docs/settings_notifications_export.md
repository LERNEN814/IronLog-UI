# 设置、提醒与导出边界

## 设置持久化

`lib/core/settings/settings_store.dart` 定义了小型用户偏好的持久化边界。

- Android 正常启动优先使用 `SharedPreferencesSettingsStore`，键为
  `ironlog.settings.v1`。
- `AppSettings` 当前保存重量单位、提醒开关、默认模板 ID、模板排序 ID 和主题模式；新增字段应保持旧 JSON 可读，并提供默认值。
- SharedPreferences 插件不可用、设置 JSON 损坏或写入失败时，读取回退到 `AppSettings.defaults`，写入只报告失败而不影响训练记录。
- `FitnessRepositories.openHive` 同时提供 `HiveSettingsStore` 作为本地 fallback；测试和预览使用 `InMemorySettingsStore`。

设置页接入 `appSettingsProvider`，不要直接把设置写进训练 repository：

```dart
final settings = ref.watch(appSettingsProvider).valueOrNull ?? AppSettings.defaults;
final saved = await ref.read(appSettingsProvider.notifier).setWeightUnit(
  settings,
  WeightUnit.lb.name,
);
```

`saved == false` 时应保留当前页面可操作，并显示短暂的失败提示。

## 通知权限和提醒

`NotificationService` 将所有插件调用包在 `try/catch` 中，并以 `false` 表示不可用。Android 需要 `POST_NOTIFICATIONS`；每日提醒还声明了 `RECEIVE_BOOT_COMPLETED` 和插件的调度 receiver。

- `requestPermission()` 只在用户打开提醒开关时调用。
- `showWorkoutReminder()` 发送一条即时测试通知，用于确认通道可用。
- `scheduleDailyWorkoutReminder()` 从启用时刻开始安排每日**非精确**提醒，不要求 exact-alarm 权限。
- `cancelWorkoutReminder()` 在关闭开关时取消计划。
- 权限被拒绝、插件不可用或宿主不支持调度时，设置状态可以回滚为关闭；训练记录和本地数据继续工作。

目前没有时区数据库或固定时刻选择器，因此产品文案不得暗示“每天某个精确时间”已实现。若未来需要精确时刻，应单独引入 timezone 和 exact-alarm 权限审查。

## 导出与分享

`BackupCodec` 负责 JSON 往返解析和 CSV 人读格式。设置页提供预览、Android 系统分享和剪贴板复制：

- JSON 是恢复数据的唯一完整格式，包含版本号、模板、模板排序、训练记录、身体数据和设置。
- CSV 是训练组扁平视图，适合查看或粘贴到表格，不作为恢复源。
- 用户取消预览对话框时不改变本地数据。
- Android 分享通过 `ironlog/export` MethodChannel 将内容写入应用缓存，并通过 `FileProvider` 打开 chooser；没有目标应用或平台不支持时返回失败提示，不阻塞记录流程。
- 用户取消系统 chooser 不删除本地数据；复制按钮仍作为明确降级路径。
- 剪贴板写入失败必须作为提示处理，不阻塞记录流程。

没有引入 `path_provider` 或 `share_plus`，避免 Windows 原生 hook 破坏离线测试链；Android 分享使用项目自有 FileProvider/MethodChannel。桌面和 iOS 没有该通道时明确回退到复制预览。APK 构建必须在无特殊字符路径中复核。
