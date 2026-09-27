# REST_TIMER — 组间休息计时器技术规格（M3）

> 这是 Phase 1 技术风险最高的部分。严格按本文件实现，不要自由发挥（比如不要写常驻前台 Service）。

## 1. 设计目标
- App 在前台：应用内精确倒计时，结束时应用内提示音 + 震动。
- App 在后台/锁屏：结束时系统通知（声音 + 震动）尽可能准时；等待期间通知栏显示实时倒计时。
- 进程被杀：重启后能恢复倒计时状态。
- **不使用前台服务**（Android 14 前台服务类型限制复杂，且计时不需要持续计算）。

## 2. 组件

```
domain/timer/RestTimerMath.kt          纯函数（DOMAIN_RULES §7）
data/timer/RestTimerRepository.kt      单例：StateFlow<RestTimerUiModel?>，持久化到 workout_session.rest_target_at
platform/timer/RestAlarmScheduler.kt   接口 + AlarmManager 实现（可以在测试中替换为 Fake）
platform/timer/RestAlarmReceiver.kt    BroadcastReceiver：时间到 → 发"完成"通知
platform/timer/RestNotifications.kt    通知渠道与通知构建
platform/timer/ExactAlarmPermission.kt 权限检测与跳转
```

> `platform/` 是新增的顶层包（与 core/data/domain/ui 并列），专门放依赖 Android 系统服务的代码。ARCHITECTURE.md 包结构的补充以本文件为准。

## 3. 时间基准
- 计时用 **两个时间**：
  - `targetElapsedRealtime`：`SystemClock.elapsedRealtime()` 基准，用于 UI 倒计时与 AlarmManager（`ELAPSED_REALTIME_WAKEUP`），不受用户改系统时间影响。
  - `targetWallMillis`：`Clock.nowMillis()` 基准，写入数据库 `rest_target_at`，只用于进程恢复（重启后 elapsedRealtime 基准依然有效，但为了简单统一用墙钟恢复）。
- 为了可测试，提供 `interface ElapsedClock { fun elapsedRealtime(): Long }`（`core/time/` 中），生产实现调用 `android.os.SystemClock.elapsedRealtime()`。**这是 `core/time` 中允许的第二个系统时间来源。**

## 4. 调度逻辑

```kotlin
fun start(sessionId: String, seconds: Int)
  1. 计算 target（两个基准）
  2. 写库 workout_session.rest_target_at = targetWall
  3. scheduler.schedule(targetElapsed)
  4. 发"进行中"通知（仅当 App 在后台时可见，见 §6）
  5. 更新 StateFlow

fun adjust(deltaSeconds)   → RestTimerMath.adjust → 重新 schedule + 更新库 + 更新通知
fun skip() / finish()      → scheduler.cancel()；rest_target_at = null；取消"进行中"通知
```

`AlarmManager` 调度策略（`RestAlarmScheduler` 的实现）：

```kotlin
if (Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()) {
    alarmManager.setExactAndAllowWhileIdle(ELAPSED_REALTIME_WAKEUP, targetElapsed, pendingIntent)
    exact = true
} else {
    alarmManager.setAndAllowWhileIdle(ELAPSED_REALTIME_WAKEUP, targetElapsed, pendingIntent)
    exact = false   // UI 显示"可能延迟"
}
```

- `PendingIntent` 使用 `FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT`，requestCode 固定为 `1001`（同一时间只有一个休息计时）。
- **不要**使用 `setAlarmClock()`（会在状态栏显示闹钟图标，并被系统视为用户闹钟）。

## 5. Manifest

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<receiver android:name=".platform.timer.RestAlarmReceiver" android:exported="false" />
```

禁止：`USE_EXACT_ALARM`、`FOREGROUND_SERVICE*`、`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`。

## 6. 通知
- 渠道 1 `rest_running`（`IMPORTANCE_LOW`，无声）：进行中通知，`setUsesChronometer(true)` + `setChronometerCountDown(true)` + `setWhen(targetWall)`，`setOngoing(true)`，`setOnlyAlertOnce(true)`，点击打开 MainActivity 并导航到当前会话。
- 渠道 2 `rest_done`（`IMPORTANCE_HIGH`，默认通知音 + 震动）：时间到通知，`setAutoCancel(true)`，`CATEGORY_ALARM` 不要用（改用 `CATEGORY_REMINDER`）。
- 通知 id：进行中 = 2001，完成 = 2002。完成时取消 2001。
- App 在前台时：**不发**进行中通知；时间到由应用内处理（`ProcessLifecycleOwner` 判断前后台），Receiver 在前台时只做"应用内提示"。
  - 实现方式：`RestTimerRepository` 暴露 `isAppInForeground`（由 `ProcessLifecycleOwner` 驱动），Receiver 查询它；前台 → 通过 Repository 发出 `TimerEvent.Finished`，由 UI 播放 `RingtoneManager.TYPE_NOTIFICATION` 默认音 + 震动 300ms；后台 → 发渠道 2 通知。
  - App 从前台切到后台时，若计时进行中 → 发进行中通知；切回前台 → 取消进行中通知。

## 7. 进程恢复
- `MainActivity` 启动（或 `RestTimerRepository` 初始化）时：查询进行中会话的 `rest_target_at`：
  - 为空 → 无计时。
  - `> now` → 用 `remaining = target - now` 重新计算 `targetElapsed = elapsedNow + remaining`，重新 schedule，恢复 UI。
  - `≤ now` → 清空字段，不提示。

### 7.1 Receiver 冷启动与前后台接线（M3 审计设计变更 D5）
- **前后台接线**：在 `platform/timer/` 新增 `ForegroundTimerBridge : DefaultLifecycleObserver`，`onStart` → `restTimer.onForegroundChanged(true)`，`onStop` → `onForegroundChanged(false)`；在 `Application.onCreate` 中 `ProcessLifecycleOwner.get().lifecycle.addObserver(bridge)`。
- **串行化**：`RestTimerRepository` 的 `start / adjust / finish / restore / onAlarmFired` 用同一个 `Mutex` 串行执行。
- **`onAlarmFired()` 在内存中没有计时（冷进程）时**：读取进行中会话的 `rest_target_at`：
  - 为空，或没有进行中会话 → 取消 2001，结束（真正的 no-op）。
  - `≤ now + 5_000`（5 秒容差，吸收墙钟与 elapsed 的偏差）→ 视为到点：清空字段、取消 2001，前台发 `TimerEvent.Finished`，后台发 2002 通知。
  - `> now + 5_000` → 按 §7 恢复（重新 schedule），不提示。
- 测试（Robolectric，使用 Fake）：
  - TR6：新建 Repository，库中 `rest_target_at = now − 1_000`，处于后台 → 调用 `onAlarmFired()` → `showFinished` 被调用一次，字段为 null，`cancelRunning` 被调用。
  - TR7：同上，但库中字段为 null → `showFinished` 未被调用。
  - TR8：`ForegroundTimerBridge` 在计时进行中收到 onStop → `showRunning(targetWall)`；收到 onStart → `cancelRunning`。

## 8. 权限引导（R-3.6）
- 第一次点击休息预设时：
  1. Android 13+ 且无通知权限 → 请求 `POST_NOTIFICATIONS`。
  2. Android 12+ 且 `!canScheduleExactAlarms()` → 弹对话框说明原因 → 跳转 `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM`（带 `package:` URI）。
  3. 用户拒绝的结果记录在 DataStore（`exact_alarm_prompted = true`），之后不再主动弹，只在计时条显示提示图标，点击可以再次引导。
- 监听 `AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED` 非必需，每次开始计时时重新检查 `canScheduleExactAlarms()` 即可。

## 9. 测试要求
- 单元测试：`RestTimerMath`（DOMAIN_RULES 向量 T）。
- Robolectric：`RestTimerRepository` 用 `FakeRestAlarmScheduler` + `FixedClock` + 假 `ElapsedClock`：
  - TR1：start(90) → scheduler 收到 `targetElapsed = elapsed + 90_000`，数据库 `rest_target_at = now + 90_000`。
  - TR2：adjust(+15) → 重新 schedule，target +15s。
  - TR3：skip → scheduler.cancel 被调用，数据库字段为 null。
  - TR4：恢复：数据库中 `rest_target_at = now + 30_000` → 初始化后 StateFlow 剩余 30s，scheduler 被调用。
  - TR5：恢复：`rest_target_at = now − 1` → 字段被清空，StateFlow 为 null，scheduler 未被调用。
- Robolectric：`RestAlarmScheduler` 实现：用 `ShadowAlarmManager` 验证 `setExactAndAllowWhileIdle` 在有权限时被调用，无权限时调用 `setAndAllowWhileIdle`（Robolectric `ShadowAlarmManager.setCanScheduleExactAlarms(false)`）。

## 10. 真机验收（由用户执行，写在交接报告中给用户的检查清单里）
在用户自己的手机上：
1. 前台 90 秒：准时响、有震动。
2. 按 Home 回桌面 90 秒：通知栏有倒计时，结束时有声音通知。
3. 锁屏 + 省电模式 150 秒：结束时通知误差 ≤ 5 秒（精确闹钟已授权）。
4. 计时中划掉任务卡（杀进程）→ 重新打开 App：倒计时恢复。
5. 拒绝精确闹钟权限：计时条显示提示图标，通知可能延迟但一定会到。
