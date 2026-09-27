# AGENTS.md — IronLog 开发代理守则

> 你（编码代理）每次启动都必须先完整阅读本文件。本文件优先级高于你的任何默认习惯。
> 项目发起人/审计官会在每个里程碑结束后审计你的产出。违反"红线"的提交会被整体打回。

---

## 1. 项目一句话

IronLog 是一个 **仅 Android**、**离线优先** 的力量训练记录 App。技术栈：Kotlin + Jetpack Compose + Room + Hilt + DataStore。
当前处于 **Phase 1（训练记录）**，按里程碑 M1 → M6 推进。Phase 2（AI）和 Phase 3（饮食）**现在不做**，但数据模型已为其预留。

## 2. 文档地图（按需阅读，不要一次全读）

| 文档 | 何时读 |
|------|-------|
| `docs/PROGRESS.md` | **每次开始工作时第一个读**，每完成一个任务就更新它 |
| `docs/tasks/M<n>.md` | 当前里程碑的任务卡，你只做当前里程碑 |
| `docs/PRD.md` | 需要理解功能/交互时 |
| `docs/ARCHITECTURE.md` | 写任何代码之前至少读一次；新建文件时对照包结构 |
| `docs/DATA_MODEL.md` | 涉及数据库、DAO、Repository、导出时 |
| `docs/DOMAIN_RULES.md` | 涉及单位换算、预填、疲劳度、1RM、键盘逻辑时 —— **里面的测试向量必须原样写成单元测试** |
| `docs/REST_TIMER.md` | M3 组间休息计时器 |
| `docs/handoff/TEMPLATE.md` | 里程碑结束时写交接报告 |

## 3. 工作循环（每个任务都照做）

1. 读 `docs/PROGRESS.md` → 找到下一个未完成任务。
2. 读该任务卡及其引用的文档章节。
3. 先写/改测试（领域逻辑必须测试先行），再写实现。
4. 运行该任务的验收命令（任务卡中给出）。失败就修，**不许跳过**。
5. `git add -A && git commit -m "M<n>-T<k>: <简短英文描述>"`。
6. 更新 `docs/PROGRESS.md`（任务状态、遇到的问题、偏离规格之处），并单独提交或与上一步合并提交。
7. 回到第 1 步。

里程碑内所有任务完成后：
1. 运行 `bash scripts/verify.sh`，必须 **0 FAIL**。
2. 按 `docs/handoff/TEMPLATE.md` 写 `docs/handoff/M<n>.md`，提交。
3. **停止工作**，告诉用户："M<n> 已完成，请发起审计"。不要自行开始下一个里程碑。

## 4. 环境与命令

- Windows 11 + Git Bash。项目根目录：本文件所在目录。
- 执行任何 gradle 命令前先 `source scripts/env.sh`（修正本机无效的 JAVA_HOME）。
- 常用命令（首次构建可能 5–10 分钟，请把命令超时设为 ≥ 600 秒）：
  ```bash
  source scripts/env.sh
  ./gradlew --console=plain assembleDebug
  ./gradlew --console=plain testDebugUnitTest
  ./gradlew --console=plain testDebugUnitTest --tests "com.ironlog.app.domain.*"
  ./gradlew --console=plain lintDebug
  bash scripts/verify.sh          # 里程碑门禁（含上面全部 + 静态规则检查）
  bash scripts/verify.sh --quick  # 只跑静态规则检查，几秒钟
  ```
- 单元测试使用 JVM + Robolectric（`app/src/test`）。**本项目不写 androidTest 仪器测试**（没有保证可用的模拟器）。
- 测试失败时，读 `app/build/test-results/testDebugUnitTest/*.xml` 或 `app/build/reports/tests/` 获取堆栈。

## 5. 红线（任何一条违反 = 里程碑审计不通过）

1. **不许修改** `gradle/libs.versions.toml` 中已有版本号、`gradle/wrapper/*`、`settings.gradle.kts`、根 `build.gradle.kts`。新增依赖必须先在交接报告"待审批"里申请，未批准前不得添加。
2. **不许** 用 `@Ignore`、删除断言、放宽断言、`try/catch` 吞异常等方式让测试"通过"。测试失败说明实现错了（或规格有问题 → 写进 PROGRESS 的"规格疑问"，并停止该任务）。
3. **不许** 修改 `docs/` 下除 `PROGRESS.md`、`handoff/M<n>.md` 以外的文件。发现规格错误，写进 PROGRESS 的"规格疑问"。
4. **不许** 使用 `fallbackToDestructiveMigration`。M1 审计通过后数据库 schema v1 冻结，之后任何 schema 变更 = 版本号 +1 + `Migration` + 迁移测试。
5. **不许** 在 `core/time/Clock.kt` 之外调用 `System.currentTimeMillis()`、`Instant.now()`、`LocalDate.now()`、`LocalDateTime.now()`、`ZonedDateTime.now()`。一律注入 `Clock`。
6. **不许** 用 `Float`/`Double` 存储重量、距离、速度等物理量到数据库（用整数：克、米、×10 定点）。
7. **不许** 在 `domain/` 包里 import 任何 `android.*` / `androidx.*`（纯 Kotlin，方便测试）。
8. **不许** 在 Kotlin 源码中硬编码中文 UI 字符串；UI 文案一律放 `res/values/strings.xml`。（种子数据 JSON 里的动作名除外。）
9. **不许** 使用 `GlobalScope`、`runBlocking`（测试代码除外）、主线程数据库访问（`allowMainThreadQueries` 仅限测试）。
10. **不许** 申请 `USE_EXACT_ALARM`、`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`、`INTERNET`（Phase 1 无联网）权限。
11. **不许** 一次性提交整个里程碑。每个任务至少一个提交。
12. **不许** 实现当前里程碑之外的功能（"顺手做了"也算违规，会增加审计负担）。

## 6. 编码约定（细节见 ARCHITECTURE.md）

- 包根：`com.ironlog.app`。分层：`ui/feature → domain(接口+模型+纯逻辑) ← data(实现)`。
- ViewModel 只依赖 domain 层的 Repository 接口与纯函数，**不直接依赖 DAO**。Composable **不依赖 Repository**。
- 屏幕状态：每个页面一个 `UiState` data class，ViewModel 暴露 `StateFlow<UiState>`，用户操作是 ViewModel 的普通函数，一次性事件用 `Channel` → `receiveAsFlow()`。
- 所有 ID 为 UUID 字符串，通过注入的 `IdGenerator` 生成（内置动作使用种子文件中的固定 id）。
- 注释用英文，简洁；只在"为什么"不显然时写注释。
- 不要使用 `!!`；需要时用 `requireNotNull(x) { "reason" }` 或 `checkNotNull`。
- 新文件、新类的命名与位置严格按 ARCHITECTURE.md 的包结构表。

## 7. 卡住时怎么办

- 同一个错误尝试修复 **3 次**仍失败：停止该任务，在 `docs/PROGRESS.md` 的"阻塞项"记录：错误信息（关键 20 行）、你尝试过的方法、你的猜测。然后继续做不依赖它的下一个任务；若都依赖，则停止并告知用户。
- 规格有歧义：选择**最简单且不违反红线**的解释，实现后在 PROGRESS "偏离/解释"中记录。
- 不确定某个 API 在当前库版本中是否存在：写一个最小的编译验证，而不是凭记忆堆代码。已知坑见 ARCHITECTURE.md 第 9 节。
