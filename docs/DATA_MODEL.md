# DATA_MODEL — IronLog 数据模型

数据库：Room + SQLite，文件 `ironlog.db`。时间统一存 UTC 毫秒（`Long`），本地日期存 `YYYY-MM-DD`（`String`）。

## 1. 版本策略

| 阶段 | DB version | 状态 |
|------|-----------|------|
| M1 | 1 | 全量 schema，冻结于 M1 审计通过 |
| 之后任何改动 | +1 | 必须同时提供 `Migration(n, n+1)` + 迁移测试 + 更新本文件 |

**禁止** `fallbackToDestructiveMigration`。schema JSON 导出到 `app/schemas/`，必须提交进 git。

## 2. 枚举（存 `Int`，带 `defaultValue`，便于迁移）

```kotlin
enum class ExerciseKind { STRENGTH, CARDIO, BODYWEIGHT, TIMED }   // 0..3
enum class MuscleRole   { PRIMARY, SECONDARY }                     // 0,1
enum class SetType      { WORK, WARMUP, DROP, FAILURE }            // 0,1,2,3
enum class SessionStatus{ IN_PROGRESS, FINISHED }                  // 0,1
enum class WeightUnit   { KG, LB }                                 // 0,1
enum class DistanceUnit { KM, MI }                                 // 0,1
```

**注意：不要把枚举命名为 `Unit`（与 `kotlin.Unit` 冲突）。**所有枚举必须显式赋值（`= 0` 起），**顺序永不变更**，新增值只能追加在末尾。

## 3. 表结构

### 3.1 `muscle_group`（静态表，随种子导入）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | 固定 id，如 `chest`, `front_delts`, `triceps` |
| `display_name_en` | TEXT | 如 `Chest` |
| `display_name_zh` | TEXT | 如 `胸` |
| `body_region` | Int | 0=胸 1=背 2=肩 3=臂 4=腿 5=核心 6=有氧（用于日历配色，见 PRD R-4） |
| `recovery_half_life_hours` | Int | 疲劳半衰期，见 DOMAIN_RULES.md §5 |
| `sort_order` | Int | |

建议的肌群清单（M1 内置，共 16 个）：
`chest`(半衰期48) `upper_back`(48) `lats`(48) `lower_back`(48) `front_delts`(36) `side_delts`(36) `rear_delts`(36)
`biceps`(30) `triceps`(30) `forearms`(24) `quads`(54) `hamstrings`(54) `glutes`(54) `calves`(24) `abs`(30) `cardio`(12)

### 3.2 `exercise`（动作库：内置 + 用户自定义）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | 内置动作用固定字符串 id（如 `barbell_bench_press`），自定义用 UUID |
| `name_zh` | TEXT | 如 `杠铃卧推` |
| `name_en` | TEXT | 如 `Barbell Bench Press` |
| `kind` | Int | `ExerciseKind`，默认 0 |
| `equipment` | TEXT | 如 `barbell` / `dumbbell` / `machine` / `cable` / `bodyweight` / `treadmill` / `stair_climber` |
| `primary_muscle_id` | TEXT | 冗余字段，指向 `muscle_group.id`，便于列表展示（可空） |
| `is_custom` | Int | 0=内置 1=用户创建 |
| `is_archived` | Int | 1 表示不再出现在选择列表（不物理删除） |
| `created_at` | Long | |
| `updated_at` | Long | |

`muscle_group` 用 `body_region` 冗余即可；动作只需一个 `primary_muscle_id` 用于列表图标/分组。

### 3.3 `exercise_muscle`（动作 ↔ 肌群，多对多，带权重）

| 列 | 类型 | 说明 |
|----|------|------|
| `exercise_id` | TEXT | PK 的一部分 |
| `muscle_id` | TEXT | PK 的一部分 |
| `role` | Int | `MuscleRole` |
| `weight` | Real | 主动肌 1.0，协同肌 0.5（种子数据给出，热力图用） |

PK = (`exercise_id`, `muscle_id`)。

### 3.4 `workout_session`（训练会话）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | UUID |
| `status` | Int | `SessionStatus`，默认 0 |
| `started_at` | Long | UTC 毫秒 |
| `ended_at` | Long | 可空 |
| `local_date` | String | `YYYY-MM-DD`，由 `Clock` 在创建时写入，跨午夜不重算 |
| `rating` | Int | 可空，5..10 |
| `note` | TEXT | 可空 |
| `rest_target_at` | Long | 可空：进行中的休息倒计时的目标时间戳（M3 进程恢复用） |
| `created_at` / `updated_at` | Long | |

**不变式：同一时刻最多只能有一个 `status = IN_PROGRESS` 的会话**（由 `WorkoutRepository` 保证）。

### 3.5 `session_exercise`（会话中的动作条目）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | UUID |
| `session_id` | TEXT | FK → `workout_session.id`，`onDelete = CASCADE`，建索引 |
| `exercise_id` | TEXT | FK → `exercise.id` |
| `order_index` | Int | 会话内顺序 |
| `note` | TEXT | 可空 |
| `created_at` / `updated_at` | Long | |

### 3.6 `workout_set`（单组记录）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | UUID |
| `session_exercise_id` | TEXT | FK，`onDelete = CASCADE`，建索引 |
| `order_index` | Int | 条目内顺序（0 起） |
| `set_type` | Int | `SetType`，默认 0 |
| `parent_set_id` | TEXT | 可空，递减组指向主组 |
| `weight_g` | Int | 可空（自重用 null 或 0，约定：自重/有氧为 null） |
| `input_unit` | Int | `WeightUnit`，默认 0：记录用户当时的输入单位 |
| `reps` | Int | 可空（有氧为 null） |
| `rir` | Int | 可空，0..5 |
| `duration_s` | Int | 可空（有氧/计时类） |
| `distance_m` | Int | 可空（跑步机） |
| `level` | Int | 可空（爬楼机挡位） |
| `incline_x10` | Int | 可空（跑步机坡度 ×10） |
| `speed_x10` | Int | 可空（速度 km/h ×10） |
| `completed_at` | Long | 可空：勾选完成的时间 |
| `is_completed` | Int | 默认 0：0=未完成 1=已完成（未完成的组不计入统计） |
| `created_at` / `updated_at` | Long | |

### 3.7 `body_weight`（体重打卡）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | TEXT PK | 同一天只允许一条（`local_date` 唯一索引，重复则覆盖） |
| `local_date` | TEXT | `YYYY-MM-DD`，唯一索引 |
| `weight_g` | Int | 克 |
| `recorded_at` | Long | UTC 毫秒 |
| `created_at` / `updated_at` | Long | |

### 3.8 `user_settings`（DataStore Preferences，不是 SQLite 表）

键名（见 `SettingsRepository`）：

| 键 | 类型 | 默认值 |
|----|------|-------|
| `weight_unit` | `WeightUnit` | `KG` |
| `distance_unit` | `DistanceUnit` | `KM` |
| `default_rest_seconds` | Int | `120` |
| `rest_presets_seconds` | String（逗号分隔） | `"30,90,120,150"` |
| `keep_screen_on_during_workout` | Boolean | `true` |
| `show_rir_field` | Boolean | `false` |
| `theme_mode` | String(`system`/`light`/`dark`) | `system` |
| `seed_version` | Int | `0`（种子导入版本，用于将来增量更新内置动作） |

## 4. DAO 约定

- 查询一律返回 `Flow<...>`（响应式），写操作为 `suspend`。
- 更新一律使用 `@Update` 或明确的 `@Query`，**不要** 用 `@Insert(onConflict = REPLACE)` 覆盖会话（会破坏外键级联）。
- "最近一次该动作的数据"查询必须带索引且限定 `is_completed = 1`，见 DOMAIN_RULES.md §3 的 SQL 契约。

## 5. 种子数据

- 文件：`app/src/main/assets/seed/seed_v1.json`（单文件，包含 `version`、`muscles`、`exercises`）。`muscles` 部分已由审计官提供于 `seed_muscles_reference.json`，请原样合并进去。
- JSON 结构（与 `domain/model` 的 `@Serializable` 类一一对应，字段名用 snake_case 并用 `@SerialName` 映射）：
  ```json
  {"version":1,"muscles":[{"id":"chest","display_name_en":"Chest","display_name_zh":"胸","body_region":0,"recovery_half_life_hours":48,"sort_order":0}],
   "exercises":[{"id":"barbell_bench_press","name_zh":"杠铃卧推","name_en":"Barbell Bench Press","kind":0,
                 "equipment":"barbell","primary_muscle_id":"chest",
                 "muscles":[{"muscle_id":"chest","role":0,"weight":1.0},{"muscle_id":"triceps","role":1,"weight":0.5},{"muscle_id":"front_delts","role":1,"weight":0.5}]}]}
  ```
- 导入时机：M1 起每次 App 启动都在后台执行一次导入（`INSERT OR IGNORE`，幂等，约 100 行，耗时可忽略），**不覆盖用户自定义动作，也不覆盖用户对内置动作的修改**。`seed_version` 键预留给将来需要"更新已有内置动作"时使用，M1 不需要读写它。
- 数量要求：M1 至少 60 个动作（覆盖胸/背/肩/臂/腿/核心 + 有氧 4 种器械）；M2 结束前补到 ≥ 100。

## 6. 导出/导入（M6）

- 导出格式：单个 JSON 文件（`ironlog-backup-v1.json`），包含所有表 + `schema_version` + `exported_at` + `app_version`。
- 导入策略：**先清空再写入**（不做合并），导入前弹出确认。导入后必须重新校验不变式。
- 文件通过 SAF（`ACTION_CREATE_DOCUMENT` / `ACTION_OPEN_DOCUMENT`）读写，不申请存储权限。
