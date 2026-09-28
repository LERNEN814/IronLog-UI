# IronLog

Android 力量训练记录 App（离线优先）。Kotlin + Jetpack Compose + Room + Hilt。

- 开发代理请先读 [`AGENTS.md`](AGENTS.md)。
- 当前进度：[`docs/PROGRESS.md`](docs/PROGRESS.md)
- 产品需求：[`docs/PRD.md`](docs/PRD.md)

## 构建

```bash
source scripts/env.sh
./gradlew assembleDebug          # APK: app/build/outputs/apk/debug/
./gradlew testDebugUnitTest      # JVM + Robolectric 单元测试
./gradlew assembleRelease        # 未签名 Release APK
bash scripts/verify.sh --quick   # 静态红线检查
bash scripts/verify.sh           # 里程碑门禁
```

要求：JDK 17、Android SDK Platform 37、Build-Tools 36+。

Windows 环境如果 Gradle daemon 报 `Unable to establish loopback connection`，确保 IDE 重启后继承修复环境变量 `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\\temp`，再执行上述命令。Robolectric 在用户路径含空格时使用 `C:\\Users\\Public\\robolectric-m2` 缓存。

Debug APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。Release 构建当前输出未签名 APK，位于 `app/build/outputs/apk/release/app-release-unsigned.apk`；安装到真机前需要配置签名。
