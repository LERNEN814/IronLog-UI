# IronLog

## Flutter v0.1.0 更新

本仓库保留原有 Kotlin/Compose Phase 1 实现，同时新增 `flutter_app/` 目录作为 IronLog 的 Flutter Android 更新版本。该版本采用 Flutter + Riverpod + Hive CE + Freezed/json_serializable，加入本地优先训练记录、模板、身体数据、趋势、JSON/CSV 备份和基于真实 SVG 肌群路径的训练热力图。

Flutter 工程入口：[`flutter_app/`](flutter_app/)。在该目录执行 `flutter pub get`、`flutter analyze`、`flutter test`，Android 构建请在不含 shell 特殊字符的路径中执行。品牌资源和 Android 包标识已经更新为 `IronLog` / `com.ironlog.app`，版本为 `0.1.0+1`。

Flutter 测试 APK：[下载 IronLog-Flutter-v0.1.0-debug.apk](https://github.com/LERNEN814/IronLog-UI/releases/download/v0.1.0-flutter/IronLog-Flutter-v0.1.0-debug.apk)；补充 Release：[v0.1.0-flutter](https://github.com/LERNEN814/IronLog-UI/releases/tag/v0.1.0-flutter)。测试步骤见 [`flutter_app/docs/phone_test_guide.md`](flutter_app/docs/phone_test_guide.md)。该 APK 用于个人手机测试；正式商店发布前仍需使用独立 production keystore 完成 release 签名。

IronLog 是一款仅面向 Android 的离线优先力量训练记录 App。它面向已经有稳定训练习惯、希望在组间快速记录并在训练后复盘的人，重点解决“记录不能打断训练”和“数据不能丢失”两个问题。

Phase 1 的目标是提供一个不依赖账号和网络的训练记录闭环：用户可以在训练中单手录入数据，每次交互立即保存，进程被系统回收后继续训练，并随时导出自己的数据。

## 开发目的

- 把重量、次数、组类型和有氧数据压缩到适合组间操作的输入流程中。
- 通过预填、最近使用动作和模板减少重复输入，同时保留用户修改的自由度。
- 让历史、日历、趋势、1RM 和疲劳热力图服务于训练复盘，而不是增加训练中的干扰。
- 让数据所有权留在设备和用户手中：Phase 1 不需要账号、云同步或联网权限。
- 用可测试的领域规则和明确的分层架构支撑后续迭代，而不是把业务逻辑散落在 Composable 中。

## Phase 1 范围

当前版本包含以下能力：

- 主页、训练、历史、动作和设置五个主要入口。
- 101 个内置动作、16 个肌群，以及搜索、筛选、最近使用、自定义和归档动作。
- 力量、自重、有氧和计时动作；支持重量、次数、组类型、RIR、时长、速度、坡度、挡位和距离。
- 上次数据预填、单位切换、休息计时、通知提醒，以及精确闹钟权限被拒绝时的降级提示。
- 训练总结、评分、备注、PR 提示、训练模板和分享长图。
- 历史日历、训练详情、动作趋势、估算 1RM 和 7 日疲劳热力图。
- 体重记录、主题、屏幕常亮、JSON 导入导出和 Android Auto Backup。

以下内容明确不属于当前 Phase 1：账号、云同步、社交、AI 训练建议、饮食记录、Wear OS、Health Connect 和桌面小组件。外部动作演示媒体尚未导入，动作详情页目前保留占位内容；媒体只有在完成来源、作者和许可证复核后才会进入仓库。

## 当前发布

- 版本：[`v0.1.0`](https://github.com/LERNEN814/IronLog-UI/releases/tag/v0.1.0)
- APK：[`IronLog-Phase1.1-release-signed.apk`](https://github.com/LERNEN814/IronLog-UI/releases/download/v0.1.0/IronLog-Phase1.1-release-signed.apk)
- Application ID：`com.ironlog.app`
- 版本号：`versionCode 1`、`versionName 0.1.0`
- APK SHA-256：`5A13DFC34F2BF41AE39852AE0D5351D29331DBF44F7F039F320BA7B8B26CFEBA`
- 签名：本地 Phase 1.1 阶段分发 key，已通过 `apksigner verify` 的 APK Signature Scheme v2 验证。

该 APK 用于阶段性评估，不是 Google Play production key 签名的商店版本。当前记录包含构建和签名验证结果；发布前仍应在目标设备上重新安装并验证 Release 版的完整训练流程。

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 语言与构建 | Kotlin `2.4.20`、Java/JVM `17`、Gradle Wrapper `9.8.0`、Android Gradle Plugin `9.4.1` |
| Android 基线 | `minSdk 26`、`targetSdk 36`、`compileSdk 37` |
| UI | Jetpack Compose、Material 3、Compose BOM `2026.09.00` |
| 导航 | Navigation Compose `2.10.2`、Kotlin Serialization 类型安全路由 |
| 数据 | Room `2.8.5`、SQLite schema v1、DataStore Preferences |
| 依赖注入 | Hilt `2.60.1`、KSP `2.3.12` |
| 并发与状态 | Kotlin Coroutines/Flow |
| 测试 | JUnit、Truth、Turbine、Robolectric `4.17` |

Release 构建启用 R8 混淆和资源压缩。物理量在存储层使用整数定点表示：重量为克、距离为米、速度和坡度为十分之一单位、时长为秒，避免浮点误差进入数据库。

## 架构

项目采用单 Activity 和 Compose UI，依赖方向保持为：

```text
ui/feature  ->  domain  <-  data
                   ^
                platform
```

- `ui/feature` 只依赖 domain 的 Repository 接口和纯逻辑，Composable 不直接访问 DAO。
- `domain` 保存模型、Repository 接口和可独立测试的规则，例如预填、疲劳度、1RM、计时和汇总。
- `data` 实现 Room、DataStore、种子数据导入和 Repository。
- `platform` 封装通知、精确闹钟、震动、唤醒锁和系统分享等 Android 服务。
- 训练中的写操作在每次交互后立即落库，以支持进程恢复和本地数据导出。

## 开发流程

1. 开始任务前阅读 `AGENTS.md`、`docs/PROGRESS.md`、对应任务卡、PRD 和架构文档。
2. 先为领域规则写测试，再实现 domain、data 和 UI；跨层行为通过 Repository 接口连接。
3. 运行定向测试、Debug 构建、完整单元测试和 lint，修复失败后再继续。
4. 更新进度和交接文档，提交小而明确的变更。
5. 里程碑结束时执行 `scripts/verify.sh`，确认静态规则、构建、测试和 lint 都通过。
6. Release 阶段执行 `assembleRelease` 和 `apksigner verify`，将 APK 作为 GitHub Release asset 上传；签名密码只从环境变量或 CI secret 读取。

## 本地构建

开发环境需要 Android SDK 37、JDK 17 和 Git Bash。项目脚本会修正本机的 `JAVA_HOME`，因此执行 Gradle 前先加载环境脚本：

```bash
source scripts/env.sh
./gradlew --console=plain assembleDebug
./gradlew --console=plain testDebugUnitTest
./gradlew --console=plain lintDebug
bash scripts/verify.sh
```

只做静态规则检查时可以运行：

```bash
bash scripts/verify.sh --quick
```

阶段 Release 签名需要本地私有 keystore 和当前 shell 中的变量。密码不要写入仓库、脚本或 issue：

```bash
source scripts/env.sh
export IRONLOG_STAGE_STORE_PASSWORD='<local secret>'
export IRONLOG_STAGE_KEY_ALIAS='ironlog-stage'
export IRONLOG_STAGE_KEY_PASSWORD='<local secret>'
./gradlew --console=plain assembleRelease
```

`.stage-signing/`、`app/build/`、`artifacts/stage-release/`、APK、JKS、密码和 `local.properties` 均被 Git 忽略，不应强行加入提交。

## 测试与质量检查

当前 Release 准备阶段已验证 `assembleRelease` 成功、`apksigner verify` 通过，并完成 289 项 JVM/Robolectric 单元测试（0 failures、0 errors、0 skipped）。Debug 核心流程已做真机验收；Release APK 的完整真机安装验收仍应在更广泛分发前单独执行。

## 数据与权限

Phase 1 不申请 `INTERNET`，训练、历史和设置默认保存在设备本地。用户可以通过系统文件选择器导出或导入 JSON，也可以使用 Android Auto Backup。计时器功能按系统版本使用通知、精确闹钟、震动和唤醒锁权限；用户拒绝精确闹钟权限时，计时仍可使用，但后台提醒可能延迟。

## 项目文档

- [贡献指南](CONTRIBUTING.md)：开发约定、验证命令和提交边界。
- [产品需求](docs/PRD.md)：Phase 1 需求和明确不做的范围。
- [架构说明](docs/ARCHITECTURE.md)：分层、包结构、状态流和存储约定。
- [数据模型](docs/DATA_MODEL.md)：Room schema、备份格式和导入校验。
- [领域规则](docs/DOMAIN_RULES.md)：预填、汇总、疲劳度、1RM 和计时规则。
- [开发进度](docs/PROGRESS.md)：里程碑、审计和已知偏离记录。
- [动作内容与媒体边界](docs/EXERCISE_CONTENT.md)：种子数据、占位媒体和许可证复核流程。
- [第三方归属](NOTICE)：MuscleMap-derived 身体路径数据的来源和 MIT 条款。

## 许可证与资源边界

当前仓库尚未选择 IronLog 自身的开源许可证，因此公开可见不等于自动授予代码再分发、修改或商用权利。项目维护者确定许可证后，应在根目录加入对应的 `LICENSE` 文件。依赖项仍受各自上游许可证约束。

`app/src/main/java/com/ironlog/app/ui/feature/home/MusclePathTable.kt` 中的身体路径几何数据来自 [app-happy/MuscleMap](https://github.com/app-happy/MuscleMap) 的指定提交，归属和完整 MIT 条款见 [`NOTICE`](NOTICE)。该第三方许可只覆盖对应的派生数据，不代表 IronLog 整个仓库已经采用 MIT 许可证。
