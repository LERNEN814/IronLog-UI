# PROGRESS — 进度与状态

> **规则**：每完成一个任务就更新本文件。任务状态只改状态列，不要删行。
> 状态：`[ ]` 未开始 / `[~]` 进行中 / `[x]` 已完成 / `[!]` 被阻塞

---

## 当前里程碑：**M6**（M5 已审计有条件通过；M2–M5 真机验收待用户填写）

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

**M6**：未开始。任务卡见 `docs/tasks/M6.md`。**不要提前开始下一个里程碑。**

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

- 本机 `JAVA_HOME` 默认值无效，必须先 `source scripts/env.sh`（指向 `C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot`）。
- Robolectric 需要 `-Dmaven.repo.local=<repo>/.robolectric-m2`（用户名含空格），已在 `app/build.gradle.kts` 配好。
- 首次构建 5–10 分钟；命令超时请设 ≥ 600 秒。
- 静态红线检查：`bash scripts/verify.sh --quick`。
