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
bash scripts/verify.sh           # 里程碑门禁
```

要求：JDK 17、Android SDK Platform 37、Build-Tools 36+。
