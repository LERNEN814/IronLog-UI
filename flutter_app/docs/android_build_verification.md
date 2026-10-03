# Android 构建验证记录

Android debug 构建应在不含 shell 特殊字符的路径中执行。当前开发目录包含 `&`，Windows 下 Gradle wrapper 可能将其拆分，出现 `GradleWrapperMain` 找不到；这不是 Flutter/Dart 源码错误。

推荐使用一次性副本验证，不要修改或回滚开发目录：

```powershell
$verify = 'E:\Project\temp-builds\fitness_record_app_verify'
Remove-Item -LiteralPath $verify -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item -LiteralPath 'C:\Users\Wang Nathan\Documents\ChatGPT\reverse&rebuild\main_builder' -Destination $verify -Recurse
Set-Location $verify
flutter pub get
flutter analyze
flutter test
flutter build apk --debug
```

验收时记录 APK 的绝对路径、文件大小和构建退出码。若没有 Android 设备，仍可报告 APK 构建成功，但不能声称完成真机截图、通知抽屉验证或触控回归；此时应把这些项目列为未验证项。

`android/gradle.properties` 固定 `kotlin.incremental=false`。Flutter 插件的 Kotlin 源码位于
`C:` pub cache，而隔离工程可能位于 `E:`；Kotlin 增量缓存会在这种跨盘场景报
`this and base files have different roots`，禁用增量缓存后仍可使用 Gradle daemon 并保持可重复构建。
