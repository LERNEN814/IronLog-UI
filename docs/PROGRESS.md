# PROGRESS — 进度与状态

> **规则**：每完成一个任务就更新本文件。任务状态只改状态列，不要删行。
> 状态：`[ ]` 未开始 / `[~]` 进行中 / `[x]` 已完成 / `[!]` 被阻塞

---

## 当前里程碑：**M6**（Phase 1 功能基线已完成，待审计）

## HM-1：真实男士前后视肌肉热力图（进行中）

| 任务 | 状态 | 提交 | 备注 |
|------|------|------|------|
| HM-1 post-M6 heatmap | [x] | pending | Canvas + PathParser；保留 M6 完成记录 |

| 任务 | 状态 | 提交 | 备注 |
|------|------|------|------|
| M0 骨架（审计官完成） | [x] | — | 工具链已验证：AGP 9.4.1 / Kotlin 2.4.20 / Compose BOM 2026.09.00 / Room 2.8.5 / Hilt 2.60.1 / Gradle 9.8.0 / compileSdk 37 |
| M1-T1.1 core 工具（Clock/Id/UnitConverter） | [x] | 52af97e | 32 tests, 0 skipped |
| M1-T1.2 数字键盘纯逻辑 | [x] | c7cf3f2 | K1-K12 + S1-S4 + 额外边界，22 tests |
| M1-T1.3 领域模型与枚举 | [x] | 244d4ae | 6 enums + 8 models；EnumOrderTest 6 tests |
| M1-T1.4 数据库 v1 + 种子导入 | [x] | 29b6dcb | verify.sh 0 FAIL；种子 16 肌群 + 66 动作；schema 1.json 已提交 |
| M1-T1.5 预填/汇总/1RM/疲劳度 | [x] | ec5808b | P1-P4 + SM1-SM3 + E1-E6 + F1-F12 全融合，含颜色表测试 |
| M1-T1.6 Repository + Hilt 绑定 | [x] | 19faa15 | WorkoutRepositoryImplTest 6 tests；AppModule 转换为 abstract class 以支持 @Binds |
| M1 交接报告 | [x] | 本提交 | docs/handoff/M1.md |
| M1 审计 | [x] | m1-audited | 有条件通过，见 docs/audit/M1.md |
| M2-T2.1 导航与主题基座 | [x] | 51e8bdd | 类型安全路由 + 底部导航 + 深色主题 + 页面骨架（均带 @Preview） |
| M2-T2.2 主页（最小版） | [x] | 18e4852 | startSession 原子化 + D2 + HomeViewModelTest 5 |
| M2-T2.3 动作选择页 | [x] | dc291f8 | upsertCustom 事务 + 椭圆机种子 + Repository/Picker 测试 |
| M2-T2.4 训练页与组编辑 | [x] | 5df4cd3 | D1 + 键盘/汇总 Sheet + input_unit（小修 dda57ce/be6749c/5731fe2） |
| M2-T2.5 历史列表（最小版） | [x] | 2c7b641 | SessionListFormatter + 详情 Sheet |
| M2-T2.6 设置页（最小版） | [x] | 22edffd | SettingsDataStoreTest + 主题接线 |
| M2 交接报告 | [x] | 本提交 | docs/handoff/M2.md（真机自测清单留空待用户填写） |
| M2 审计 | [x] | m2-audited | 有条件通过，见 docs/audit/M2.md |
| M3-T3.0 距离/时长输入（D3/D4） | [x] | b0fb981 | DI1–DI8 + formatDuration + 距离/分钟键盘 |
| M3-T3.1 计时数学与时钟 | [x] | 907249e | T1–T6 + ElapsedClock/FakeElapsedClock |
| M3-T3.2/T3.3 权限通知与 Repository | [x] | 7fef340 | 渠道/通知/调度器/receiver + TR1–TR5（+ bab4ce1 lint 修复） |
| M3-T3.4 训练页 UI 集成 | [x] | bab8a95 | 计时条/选择 Sheet/提示音震动/权限引导/设置帮助项 |
| M3-T3.5 恢复与边界 | [x] | ba830c8 | 恢复 + 边界测试（+ e93c690 finish no-op） |
| M3 交接报告 | [x] | 773e013 | docs/handoff/M3.md（真机验收表留空待用户填写） |
| M3 审计 | [x] | m3-audited | 首审不通过（219f013）→ 返工复审有条件通过，见 docs/audit/M3.md |
| M3 返工（D5） | [x] | b8da2d6 | ForegroundTimerBridge + 冷进程 onAlarmFired 分支 + Mutex 串行化 + 通知点击导航 |
| M3 交接报告（更新） | [x] | 本提交 | docs/handoff/M3.md 已按 D5 更新偏离/风险说明 |
| M4-T4.1 日历数据与纯逻辑 | [x] | 61fb92f | CalendarModelTest 9 + `observeRecentSessionHeaders` 批量查询 + `@Volatile` 修复 |
| M4-T4.2 月历页面 | [x] | 6bfa6fd | 月历/列表切换 + 通知 intent 的 savedInstanceState 防护 |
| M4-T4.3 训练详情与编辑 | [x] | 2c4e94e | SessionDetailViewModelTest 6 + 模板预填（不触发休息计时） |
| M4-T4.4 动作历史与 PR 曲线 | [x] | d36753e | StrengthTrendTest 6 + Canvas 折线与 PR 标记 |
| M4 交接报告 | [x] | 7ebd2b5 | docs/handoff/M4.md（真机自测清单留空待用户填写） |
| M4 审计 | [x] | m4-audited | 有条件通过（D7 模板/D8 分块），见 docs/audit/M4.md |
| M5-T5.0 模板守卫与分块查询（D7/D8） | [x] | 8421405 | `startSessionFromTemplate` 事务 + 900 分块 + ChunkedQueryTest |
| M5-T5.1 热力图数据装配 | [x] | ffcdd10 | FatigueRepositoryImplTest 5 + CalendarViewModelTest 2 + 删死代码 |
| M5-T5.2 人体图与热力图 UI | [x] | fd068a2 | HeatmapColorsTest 9 边界 + 简化分区人体图 + Home 接线 |
| M5-T5.3 分享长图 | [x] | 60c60da | ShareCardView/View.draw 渲染 + MediaStore(29+)/FileProvider + 尺寸测试 |
| M5-T5.4 PR 即时提示 | [x] | 6219031 | SessionViewModelTest PR 两条 + 🏆 + Snackbar |
| M5 交接报告 | [x] | ddf3779 | docs/handoff/M5.md（4 项真机自测留空待用户填写） |
| M5 审计 | [x] | m5-audited | 有条件通过（D9 PR 基线 / D10 不加存储权限），见 docs/audit/M5.md |

| M6-T6.1 导出 / 导入 | [x] | `backup-20260928-phase1-final` | SAF JSON 备份、schema/引用校验、事务导入和导入前确认 |
| M6-T6.2 自动备份 | [x] | bacd259 | Android 11/12+ 备份规则与 Manifest 配置 |
| M6-T6.3 体重打卡 | [x] | `backup-20260928-phase1-final` | 同日覆盖、单位显示、历史删除、最近记录折线 |
| M6-T6.4 设置完善与帮助 | [x] | bacd259 | 单位、主题、计时帮助、关于和备份入口 |
| M6-T6.5 收尾与质量 | [x] | `backup-20260928-phase1-final` | 101 条种子动作、281 项测试、lint、Debug/Release 打包 |
| M6 交接报告 | [x] | `backup-20260928-phase1-final` | docs/handoff/M6.md |

**M6 已完成。** Phase 1 功能基线自评为 **100%**；按里程碑规则停止，等待审计。UI 视觉重构、外部动作媒体和 Phase 2/3 不属于本里程碑。

### 工程连续推进记录（UI 收尾）
- 动作详情已接入 `ExerciseRepository`：导航传入动作 ID 后，ViewModel 查询真实动作、器械和肌群映射；媒体和动作说明仍保留为待接入占位。
- 外部数据导入未纳入 Phase 1 功能基线；候选来源与许可证清单保留在 `docs/EXERCISE_MEDIA_CANDIDATES.md` 和 `docs/EXERCISE_MEDIA_SOURCES.md`，待 UI/资源专门工作流复核。
- APK 测试已完成：Debug APK 已由用户真机验证核心功能；Release 构建成功但未签名，未执行安装测试。
- M6 体重记录已完成闭环：Repository、按日期覆盖、单位显示、历史删除、最近记录折线和设置入口已实现。
- Gradle 复核：根因是 Windows `TEMP` 使用 8.3 短路径，JDK Unix-domain socket 在该路径上连接失败。已通过用户级 `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\\temp` 修复，并将用户级 `JAVA_HOME` 指向有效 JDK 17；Gradle 9.8 daemon 通信恢复。
- M6 数据安全基础已完成：备份 JSON 模型、SAF 导入导出 Repository、schema 校验、事务替换、导入后缺失种子恢复和 Android 自动备份 XML 已加入。
- M6 数据安全 UI 已接线：设置页支持 SAF 导出、导入、覆盖确认和结果反馈；导入增加重复 ID、引用关系、状态和日期/体重校验。
- 备份事务实现已收紧为 Room `withTransaction`，避免导入过程中出现部分写入。
- 外部对话留下的 Gradle 修复已复核：源码级改动仅为 `app/build.gradle.kts` 的 `java.io.File` 显式导入，逻辑不变；已通过 IDE 重启后的有效 JDK 17 环境验证。
- 增加体重 ViewModel 同日覆盖测试，并补齐趋势绘制；已修正测试期望与 `FixedClock` 的 Asia/Shanghai 时区一致。`compileDebugKotlin`、`testDebugUnitTest`（281/281）、`lintDebug`、`assembleDebug` 和 `assembleRelease` 均通过。

---

## 阻塞项（尝试 3 次仍失败的）

（格式）
```
### B1. <一句话描述>
- 任务：M1-T1.4
- 错误（关键 20 行）：
- 已尝试：
- 猜测：
```

（当前无）

---

## 规格疑问 / 偏离记录

（格式）
```
### Q1. <问题>
- 位置：docs/DOMAIN_RULES.md §3.2
- 我选择的做法：
- 理由：
```

### Q1. T1.1 需要 `WeightUnit`，但枚举归 T1.3
- 位置：docs/tasks/M1.md T1.1 / T1.3
- 我选择的做法：T1.1 先创建 `domain/model/WeightUnit.kt`（按 DATA_MODEL §2 原样 `enum class WeightUnit { KG, LB }`）；T1.3 只补其余枚举与顺序测试，不重复创建。
- 理由：`UnitConverter.parseToGrams/formatGrams` 签名引用 `WeightUnit`，不先创建无法编译。

### Q2. Git 提交身份
- 位置：仓库 `.git/config`（本机全局无 Git 身份）
- 我选择的做法：设置仅本仓库的 `IronLog Coder <coder@ironlog.local>`。
- 理由：M0 提交身份为 `IronLog Auditor`，编码代理提交需可区分；未改全局配置。

### Q3. “8 张表”与实际表数
- 位置：docs/tasks/M1.md T1.4
- 我选择的做法：按 DATA_MODEL §3 实现 **7** 张 SQLite 表（user_settings 是 DataStore，不是表）。
- 理由：DATA_MODEL §3.8 明确 user_settings 用 DataStore Preferences。

### Q4. `summarize` 需要“当前时间”但签名只给了 3 个参数
- 位置：docs/DOMAIN_RULES.md §4
- 我选择的做法：函数签名为 `summarize(session, entries, exerciseMeta, nowMillis: Long)`，进行中会话用 `nowMillis` 作为结束时间。
- 理由：§4 要求“进行中则用 `Clock.nowMillis()`”，但红线 5 禁止在 `core/time` 外直接取时间；只能由调用方注入。M2 的 ViewModel 会传 `clock.nowMillis()`。

### Q5. `exerciseCount` 与 `bodyRegions` 的口径
- 位置：docs/DOMAIN_RULES.md §4
- 我选择的做法：`exerciseCount` = “至少有 1 个已完成组（不限类型，含热身）的条目数”；`bodyRegions` = 会话中所有条目对应动作的 PRIMARY 肌群的 `body_region` 集合。
- 理由：规格只定义了 workSetCount/volume/duration，未定义这两项；选最简单口径，已在代码注释说明。
  （**M1 审计已修正**：D2 生效后两者都只统计“至少含 1 个已完成组”的条目，见 SM4/SM5。）

### Q6. M2 `search(query, filter)` 的 filter 语义
- 位置：T1.6 接口 / T2.3 选择页
- 我选择的做法：filter = `muscle_group.body_region`（M1 实现曾是 ExerciseKind.ordinal）。
- 理由：R-1.2 要求按部位筛选；M2 无类型筛选 UI。

### Q7. 有氧距离字段单位
- 位置：PRD R-2.9 / 设置页 distance_unit
- 我选择的做法：训练页距离以米（m）录入与显示，`distance_unit`（km/mi）仅存储；换算留待 M4/M6。
- 理由：M2 任务卡未要求在组行做距离单位换算，存储值 `distance_m` 不受影响。

### Q8. 时长键盘的输入单位
- 位置：PRD R-2.9
- 我选择的做法：键盘输入秒，组行/汇总显示 `mm:ss`。
- 理由：存储单位是秒；R-2.9 只规定显示格式。

### Q9. 预填占位值的存储方式
- 位置：R-2.4 占位样式
- 我选择的做法：占位值保留在 ViewModel 内存，数据库行直到点 ✓ 才写入真实值；重启后按“上次数据”重算。
- 理由：schema v1 已冻结，无法加 `is_placeholder` 列；且“未修改的组不写库”更符合占位语义。

### Q10. 历史详情展示形式
- 位置：T2.5“至少可查看所有组”
- 我选择的做法：BottomSheet 只读展示，不新建详情路由；M4 再做可编辑详情页。
- 理由：任务卡允许“至少可查看”。

### Q11. 训练页单位切换是否持久化
- 位置：R-2.7
- 我选择的做法：只改当前训练页显示，不写 DataStore；全局默认单位仍由设置决定。
- 理由：R-2.7 明确“只改显示，不改存储”。

### Q12. T3.2 与 T3.3 合并提交、T3.4 含 T3.5 边界
- 位置：docs/tasks/M3.md T3.2–T3.5
- 我选择的做法：T3.2 的 `RestAlarmReceiver` 依赖 T3.3 的 `RestTimerRepository`，而后者依赖 T3.2 的 `RestAlarmScheduler` 接口，类型上有环；为保证每个提交都能编译，将 T3.2/T3.3 合并为一个提交（`7fef340`）。
- 理由：宁要可编译的提交，不要按任务拆分的破碎中间态；T3.5 的边界/恢复测试随后单独提交。

### Q13. 进程恢复时的 totalMillis 口径
- 位置：REST_TIMER §7
- 我选择的做法：数据库只存 `rest_target_at`；恢复时把“剩余时间”当作 total，进度环从满开始重新走。
- 理由：不新增列（schema v1 冻结），且规格未要求恢复后保持原始总时长。**M3 审计已以 D6 正式批准。**

### Q14. 去掉 Repository init 里的自动 restore（M3 返工 D5）
- 位置：REST_TIMER §7 / §7.1
- 我选择的做法：`RestTimerRepository` 构造时不再自动 `restore()`；恢复只由 `MainActivity.onStart`、`SessionViewModel.init` 与显式调用触发。
- 理由：冷进程由闹钟启动时，init 的 restore 会抢先把过期的 `rest_target_at` 清掉，导致 §7.1 的“到点提醒”永远看不到目标；去掉后 receiver 与 UI 进程的恢复路径不再竞争（并有 Mutex 串行化）。TR4/TR5 改为显式调用 `restore()`。

---

## 环境备注（不要修改，仅供排查）

### 2026-09-27 集中验证结果

- IDE 重启后 `JAVA_TOOL_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\temp` 生效，Gradle loopback 阻塞已解决。
- `:app:compileDebugKotlin`：成功。
- `:app:testDebugUnitTest --rerun-tasks`：281 tests，0 failures，0 errors。
- `:app:assembleDebug`：成功。
- `:app:assembleRelease`：成功，产物为未签名 APK。
- `:app:lintDebug`：成功。
- 仅有既有 Compose API 弃用和 Gradle 10 兼容性提示；Release native strip 对两个库回退为保留未剥离形式，不影响打包成功。

### M6 / Phase 1 收尾（2026-09-28）

- M6 数据安全、体重、设置和工程收尾已完成，详见 `docs/handoff/M6.md`。
- 内置动作种子已从 67 条补齐到 101 条，ID 唯一且每条包含肌群映射。
- Debug APK 已完成用户真机功能验收；Release 构建成功但当前为未签名 APK，未进行真机安装。
- Phase 1 功能完成度标记为 **100%（功能基线）**。UI 视觉重构、外部动作媒体和精细人体图属于后续独立 UI/资源工作流，不作为本功能基线的未完成项。
- `scripts/verify.sh --quick` 与完整门禁的静态阶段均报告 2 项受保护文件哈希不匹配：`gradle/libs.versions.toml`、`app/build.gradle.kts`。本次未修改或刷新 `scripts/protected.lock.json`；需审计确认锁文件基线后再处理。
- 完整 `scripts/verify.sh` 的 Gradle 阶段在 Windows Git Bash 下成功：Debug 打包、281 项测试（0 失败、0 跳过）和 lint 通过；脚本总体因上述静态阶段失败而返回非零。另行执行的 `assembleRelease` 也成功。

- 本机 `JAVA_HOME` 默认值无效，必须先 `source scripts/env.sh`（指向 `C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot`）。
- Robolectric 需要 `-Dmaven.repo.local=<repo>/.robolectric-m2`（用户名含空格），已在 `app/build.gradle.kts` 配好。
- 首次构建 5–10 分钟；命令超时请设 ≥ 600 秒。
- 静态红线检查：`bash scripts/verify.sh --quick`。
