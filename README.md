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
./gradlew assembleRelease        # 无本地密钥时为未签名；配置阶段密钥后生成已签名 APK
bash scripts/verify.sh --quick   # 静态红线检查
bash scripts/verify.sh           # 里程碑门禁
```

要求：JDK 17、Android SDK Platform 37、Build-Tools 36+。

Windows 环境如果 Gradle daemon 报 `Unable to establish loopback connection`，确保 IDE 重启后继承修复环境变量 `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\\temp`，再执行上述命令。Robolectric 在用户路径含空格时使用 `C:\\Users\\Public\\robolectric-m2` 缓存。

Debug APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。Release 构建输出为 `app/build/outputs/apk/release/app-release.apk`。

### 阶段 Release 签名

本地阶段签名只用于测试分发，不是 Google Play production key。keystore 保存在被 Git 忽略的 `.stage-signing/ironlog-phase1.jks`，密码不写入仓库。构建前在当前 shell 设置凭据：

```bash
export IRONLOG_STAGE_STORE_PASSWORD='<local secret>'
export IRONLOG_STAGE_KEY_ALIAS='ironlog-stage'
export IRONLOG_STAGE_KEY_PASSWORD='<local secret>'
./gradlew --console=plain assembleRelease
```

签名 APK 可复制到 `artifacts/stage-release/`，并用 Android SDK 的 `apksigner verify --verbose --print-certs` 校验。GitHub 发布时应把 APK 作为 Release asset 上传，不要提交 keystore、密码或 `app/build/` 内容。
