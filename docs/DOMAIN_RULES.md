# DOMAIN_RULES — 领域规则与测试向量

> 本文件中每个 **测试向量表** 都必须原样写成 JUnit 单元测试（每行一个断言或一个参数化用例）。
> 数值由审计官用独立的参考实现计算得出。**如果你的实现与表格不一致，错的是你的实现。**
> 所有代码位于 `domain/**` 或 `core/units/**`，纯 Kotlin，禁止 import `android.*`。

---

## 1. 单位换算 `core/units/UnitConverter.kt`

### 1.1 常量
- `GRAMS_PER_KG = 1000`
- `GRAMS_PER_LB = 453.59237`（精确定义值）
- 计算一律用 `java.math.BigDecimal`，舍入模式 `RoundingMode.HALF_UP`。**禁止**用 `Double` 做中间运算后再 `roundToInt`。

### 1.2 API 契约

```kotlin
object UnitConverter {
    /** 用户输入的文本（如 "62.5"）+ 单位 → 克。非法输入返回 null。四舍五入到整数克。 */
    fun parseToGrams(input: String, unit: WeightUnit): Int?
    /** 克 → 显示文本：最多 2 位小数，去掉末尾的 0 和多余的小数点。 */
    fun formatGrams(grams: Int, unit: WeightUnit): String
}
```

`parseToGrams` 规则：
- 接受 `"."` 和 `","` 作为小数点（统一视为 `.`）。
- 去除首尾空白。空串、`"."`、负数、超过 2 位小数、> 1000 kg 或 > 2200 lb → `null`。
- `"0"` → `0`（合法，用于"空杆/自重记为 0"场景）。
- **末尾小数点合法**（M1 审计设计变更 D1）：键盘会产生 `"62."`、`"0."` 这样的中间状态，用户可能直接点"完成"，因此按去掉末尾 `.` 处理。没有整数部分的 `".5"` 仍为 `null`（键盘不会产生这种输入）。

### 1.3 测试向量 U（parseToGrams / formatGrams）

| # | 输入 | 单位 | parseToGrams | formatGrams(结果, KG) | formatGrams(结果, LB) |
|---|------|------|-------------|---------------------|---------------------|
| U1 | `"60"` | KG | 60000 | `60` | `132.28` |
| U2 | `"62.5"` | KG | 62500 | `62.5` | `137.79` |
| U3 | `"0.25"` | KG | 250 | `0.25` | `0.55` |
| U4 | `"1.25"` | KG | 1250 | `1.25` | `2.76` |
| U5 | `"20.4"` | KG | 20400 | `20.4` | `44.97` |
| U6 | `"45"` | LB | 20412 | `20.41` | `45` |
| U7 | `"2.5"` | LB | 1134 | `1.13` | `2.5` |
| U8 | `"135"` | LB | 61235 | `61.24` | `135` |
| U9 | `"100"` | LB | 45359 | `45.36` | `100` |
| U10 | `"225"` | LB | 102058 | `102.06` | `225` |
| U11 | `"0"` | KG | 0 | `0` | `0` |
| U12 | `" 62,5 "` | KG | 62500 | — | — |
| U13 | `""` | KG | null | — | — |
| U14 | `"."` | KG | null | — | — |
| U15 | `"-5"` | KG | null | — | — |
| U16 | `"62.555"` | KG | null | — | — |
| U17 | `"1000.01"` | KG | null | — | — |
| U18 | `"abc"` | LB | null | — | — |
| U19 | `"2200"` | LB | 997903 | — | — |
| U20 | `"2200.5"` | LB | null | — | — |
| U23 | `"62."` | KG | 62000 | — | — |
| U24 | `"0."` | KG | 0 | — | — |
| U25 | `".5"` | KG | null | — | — |

另外 formatGrams 单独向量：

| # | grams | KG | LB |
|---|-------|----|----|
| U21 | 1000 | `1` | `2.2` |
| U22 | 100000 | `100` | `220.46` |

> 关键性质（写成测试）：对任意 U6–U10 的 lb 输入 x，`formatGrams(parseToGrams(x, LB)!!, LB) == x`。这就是"45 lb 不会显示成 44.99"的保证。

### 1.4 距离换算（M2 审计设计变更 D3）

存储始终为整数米（`distance_m`）；输入与显示用设置中的 `distance_unit`。
- 常量：`METERS_PER_KM = 1000`，`METERS_PER_MI = 1609.344`（精确定义值），BigDecimal + HALF_UP。
- `fun parseDistanceToMeters(input: String, unit: DistanceUnit): Int?`：规则与 `parseToGrams` 相同（`,` 视为 `.`、最多 2 位小数、允许末尾 `.`、`".5"` 为 null）；`0` 或 > 999.99 → null。
- `fun formatMeters(meters: Int, unit: DistanceUnit): String`：最多 2 位小数，去掉末尾的 0。
- 距离键盘：`allowDecimal=true, maxDecimals=2, maxIntDigits=3`。

| # | 输入 | 单位 | 米 | format KM | format MI |
|---|------|------|----|-----------|-----------|
| DI1 | `"5"` | KM | 5000 | `5` | `3.11` |
| DI2 | `"5.25"` | KM | 5250 | `5.25` | `3.26` |
| DI3 | `"0.4"` | KM | 400 | `0.4` | `0.25` |
| DI4 | `"3.1"` | MI | 4989 | `4.99` | `3.1` |
| DI5 | `"0.5"` | MI | 805 | `0.81` | `0.5` |
| DI6 | `"26.2"` | MI | 42165 | `42.17` | `26.2` |
| DI7 | `"0"` | KM | null | — | — |
| DI8 | `"1000"` | KM | null | — | — |

### 1.5 时长输入（M2 审计设计变更 D4）

存储始终为秒（`duration_s`），显示为 `m:ss`（分钟可超过 59，如 `90:00`）。
- **有氧（CARDIO）**：键盘输入**整分钟**（`allowDecimal=false, maxIntDigits=3`），`"20"` → 1200 秒。
- **计时（TIMED，如平板支撑）**：键盘输入**秒**（`maxIntDigits=4`），`"45"` → 45 秒。
- `0` → null（无效）。编辑已有值时，有氧键盘初始文本为 `duration_s / 60`（向下取整）。
- `fun formatDuration(seconds: Int): String`：`1200` → `"20:00"`，`45` → `"0:45"`，`5400` → `"90:00"`，`61` → `"1:01"`。

---

## 2. 数字键盘输入状态机 `domain/model` 或 `ui/feature/session/keypad`（纯 Kotlin 部分放 domain）

```kotlin
data class KeypadState(val text: String, val allowDecimal: Boolean, val maxDecimals: Int, val maxIntDigits: Int)
fun KeypadState.press(key: KeypadKey): KeypadState
sealed interface KeypadKey { data class Digit(val d: Int): KeypadKey; data object Dot; data object Backspace; data object Clear }
```

- 重量字段：`allowDecimal=true, maxDecimals=2, maxIntDigits=4`；次数字段：`allowDecimal=false, maxIntDigits=3`。
- 规则：前导零折叠（`"0"` 后按 `5` → `"5"`；`"0"` 后按 `.` → `"0."`）；空串按 `.` → `"0."`；已有 `.` 再按 `.` 无效；超过位数限制的按键无效；Backspace 删最后一个字符；Clear 变空串。

### 测试向量 K

| # | 字段 | 初始 | 按键序列 | 结果 |
|---|------|-----|---------|-----|
| K1 | 重量 | `""` | 6 0 | `"60"` |
| K2 | 重量 | `""` | . 5 | `"0.5"` |
| K3 | 重量 | `"0"` | 5 | `"5"` |
| K4 | 重量 | `"62."` | . | `"62."` |
| K5 | 重量 | `"62.5"` | 5 5 | `"62.55"` |
| K6 | 重量 | `"1000"` | 0 | `"1000"` |
| K7 | 重量 | `"60"` | ⌫ ⌫ ⌫ | `""` |
| K8 | 次数 | `""` | 1 2 | `"12"` |
| K9 | 次数 | `"12"` | . | `"12"` |
| K10 | 次数 | `"100"` | 1 | `"100"` |
| K11 | 次数 | `"8"` | C | `""` |
| K12 | 次数 | `"0"` | 0 | `"0"` |

**±步进**（在 domain 中实现 `fun stepWeight(grams: Int, unit: WeightUnit, direction: Int, stepText: String): Int`）：
- 默认步长 KG=`"2.5"`，LB=`"5"`；步长用 `parseToGrams(stepText, unit)` 转为克。
- 结果 = 在"用户单位"下的显示值 ± 步长后再转回克，**下限为 0**。
- 向量：

| # | grams | unit | dir | step | 结果 grams |
|---|-------|------|-----|------|-----------|
| S1 | 60000 | KG | +1 | 2.5 | 62500 |
| S2 | 1000 | KG | -1 | 2.5 | 0 |
| S3 | 20412 | LB | +1 | 5 | 22680 |
| S4 | 61235 | LB | -1 | 5 | 58967 |

（S3：45 lb + 5 lb = 50 lb = 22679.6185 → 22680；S4：135 − 5 = 130 lb = 58967.0081 → 58967）

---

## 3. "上次数据"预填 `domain/prefill/PrefillPlanner.kt`

### 3.1 取数（Repository 层）

"上次" = **该 `exercise_id` 在其他会话中最近一次出现的 `session_exercise`**，判定依据是该会话的 `started_at` 最大，并且：
- 会话状态为 `FINISHED`，**或**会话是 IN_PROGRESS 但不是当前会话 → 实际上由于"唯一进行中会话"不变式，只需要 `status = FINISHED`。
- 只取 `is_completed = 1` 的组，按 `order_index` 排序。
- 若该条目下没有已完成的组，则继续往更早的会话找（跳过空记录）。

SQL 契约（DAO 必须等价实现，可用子查询）：

```sql
SELECT ws.* FROM workout_set ws
JOIN session_exercise se ON ws.session_exercise_id = se.id
WHERE se.id = (
  SELECT se2.id FROM session_exercise se2
  JOIN workout_session s ON se2.session_id = s.id
  WHERE se2.exercise_id = :exerciseId AND s.status = 1
    AND EXISTS (SELECT 1 FROM workout_set x WHERE x.session_exercise_id = se2.id AND x.is_completed = 1)
  ORDER BY s.started_at DESC, se2.order_index DESC
  LIMIT 1
)
AND ws.is_completed = 1
ORDER BY ws.order_index ASC
```

### 3.2 规划（纯函数）

```kotlin
data class PrefillSet(val setType: SetType, val weightGrams: Int?, val reps: Int?, val durationS: Int?,
                      val level: Int?, val inclineX10: Int?, val speedX10: Int?, val distanceM: Int?)
object PrefillPlanner {
    /** 返回新条目应创建的"占位组"列表。 */
    fun plan(lastSets: List<WorkoutSet>, kind: ExerciseKind): List<PrefillSet>
}
```

规则：
1. `lastSets` 为空 → 返回 **1 个**空占位组（`WORK`，全部字段 null）。
2. 否则逐组复制：`setType`、`weightGrams`、`reps` 及有氧字段原样复制；`rir` **不复制**（每次主观感受不同）。
3. 递减组（`DROP`）的 `parent_set_id` 不复制；新会话中递减组按顺序紧跟在前一个非递减组后面（位置保持即可）。
4. 预填的组 **`is_completed = 0`**，UI 用"占位/灰色"样式展示（见 PRD R-2.4），用户点勾时若未修改则采用占位值。

### 测试向量 P

| # | 上次的组（type/重量g/次数） | 结果 |
|---|---------------------------|-----|
| P1 | （无） | `[WORK/null/null]` |
| P2 | `WARMUP/20000/10, WORK/60000/8, WORK/60000/8, WORK/60000/7` | 同样 4 组，顺序与类型不变 |
| P3 | `WORK/100000/5 (rir=2), DROP/80000/8` | `[WORK/100000/5 (rir=null), DROP/80000/8]` |
| P4 | CARDIO：`WORK duration 1200 level 8` | `[WORK duration 1200 level 8, weight null, reps null]` |

Repository 集成测试（Robolectric + 内存 Room）必须覆盖：
- R1：同一动作在 3 次已结束会话中出现，返回 `started_at` 最新那次。
- R2：最新一次该动作的组全部未完成 → 回退到更早那次。
- R3：进行中的会话中的记录不会被当作"上次"。
- R4：同一会话中出现两次同一动作 → 取 `order_index` 较大的那个条目。

---

## 4. 训练汇总 `domain/summary/SessionSummary.kt`

```kotlin
data class SessionSummary(val durationS: Long, val exerciseCount: Int, val workSetCount: Int,
                          val totalVolumeGrams: Long, val bodyRegions: Set<Int>)
fun summarize(session: WorkoutSession, entries: List<SessionExerciseWithSets>, exerciseMeta: Map<String, ExerciseMeta>,
              nowMillis: Long): SessionSummary   // nowMillis 由调用方从 Clock 注入（M1 审计批准）
```

- 只统计 `is_completed = 1` 的组。
- **"已训练的条目"** = 至少含 1 个已完成组（任何类型，含热身）的条目（M1 审计设计变更 D2）。
- `exerciseCount` = 已训练的条目数。
- `workSetCount` = `WORK` + `DROP` + `FAILURE` 的组数（不含 `WARMUP`）。
- `totalVolumeGrams` = Σ(`weight_g` × `reps`)，**只含非热身力量组**，使用 `Long`。
- `durationS` = (`ended_at` − `started_at`) / 1000；进行中则用 `Clock.nowMillis()`。
- `bodyRegions` = **已训练的条目**中 `role = PRIMARY` 肌群的 `body_region` 集合（日历圆点用）。一组都没完成的条目不贡献颜色。

### 测试向量 SM
- SM1：热身 20kg×10，正式 60kg×8 ×3 → `workSetCount=3`，`totalVolumeGrams = 1_440_000`。
- SM2：包含一个未完成组 100kg×5 → 不计入。
- SM3：跑步机 20 分钟 → `workSetCount=1`，`totalVolumeGrams=0`。
- SM4：卧推（胸，region 0）完成 1 组 + 深蹲（腿，region 4）只有 1 个未完成组 → `exerciseCount=1`，`bodyRegions={0}`。
- SM5：卧推只完成了 1 个热身组 → `exerciseCount=1`，`workSetCount=0`，`bodyRegions={0}`。

---

## 5. 七日肌肉疲劳度 `domain/fatigue/FatigueModel.kt`

### 5.1 模型

对过去 168 小时（7 天）内每一个 **已完成** 的组 `s`，对该组动作映射到的每个肌群 `m`：

```
age_h         = max(0, (now - s.completed_at) / 3_600_000.0)    // completed_at 为空时用会话 started_at
若 age_h > 168 → 忽略
stimulus(s)   =
    力量类（STRENGTH/BODYWEIGHT/TIMED）: typeFactor(s.set_type) × rirFactor(s.rir)
    有氧类（CARDIO）:                    min(duration_s / 600.0, 3.0)      // 每 10 分钟 = 1 个单位，封顶 3
typeFactor    : WORK=1.0, FAILURE=1.0, DROP=0.5, WARMUP=0.0
rirFactor     : rir 为空或 ≤1 → 1.0；2 → 0.9；3 → 0.8；≥4 → 0.6
fatigue[m]   += stimulus(s) × weight(exercise, m) × 0.5^(age_h / halfLife[m])
```

显示值：`score[m] = min(100, round_half_up(fatigue[m] / 10.0 × 100))`（10 个"等效满强度组"= 100）。
未出现的肌群 score = 0（返回的 Map 中可以缺省，但 UI 按 0 处理）。

```kotlin
data class FatigueInputSet(val completedAtMillis: Long, val kind: ExerciseKind, val setType: SetType,
                           val rir: Int?, val durationS: Int?, val muscles: List<Pair<String, Double>>)
object FatigueModel {
    const val WINDOW_HOURS = 168.0
    const val FULL_SCALE = 10.0
    fun compute(sets: List<FatigueInputSet>, nowMillis: Long, halfLifeHours: Map<String, Int>): Map<String, Int>
    /** 用于测试，返回未取整的原始值 */
    fun computeRaw(sets: List<FatigueInputSet>, nowMillis: Long, halfLifeHours: Map<String, Int>): Map<String, Double>
}
```

### 5.2 测试向量 F

半衰期：chest 48, triceps 30, front_delts 36, quads 54, glutes 54, hamstrings 54, cardio 12。
卧推映射：chest 1.0, triceps 0.5, front_delts 0.5。深蹲映射：quads 1.0, glutes 1.0, hamstrings 0.5。有氧映射：cardio 1.0。
"Nh" = 该组 `completed_at = now − N 小时`。raw 值断言精度 `1e-3`。

| # | 输入 | 期望 score（raw） |
|---|------|------------------|
| F1 | 卧推 5×WORK @0h | chest 50 (5.0), triceps 25 (2.5), front_delts 25 (2.5) |
| F2 | 卧推 5×WORK @48h | chest 25 (2.5), triceps 8 (0.8247), front_delts 10 (0.9921) |
| F3 | 卧推 3×WARMUP + 3×WORK @0h | chest 30, triceps 15, front_delts 15 |
| F4 | 卧推 4×WORK @72h + 3×WORK @0h | chest 44 (4.4142), triceps 19 (1.8789), front_delts 20 (2.0) |
| F5 | 卧推 @0h：2×WORK rir=1, 2×WORK rir=3, 1×WORK rir=5 | chest 42 (4.2), triceps 21 (2.1), front_delts 21 (2.1) |
| F6 | 卧推 2×DROP @0h | chest 10, triceps 5, front_delts 5 |
| F7 | 深蹲 5×WORK @167h | quads 6 (0.5861), glutes 6 (0.5861), hamstrings 3 (0.2931) |
| F8 | 深蹲 5×WORK @169h | 全部为 0（空 map） |
| F9 | 卧推 12×WORK @0h | chest 100 (12.0，封顶), triceps 60, front_delts 60 |
| F10 | 有氧 duration 1200s @3h | cardio 17 (1.6818) |
| F11 | 有氧 duration 3600s @0h | cardio 30 (3.0，刺激封顶) |
| F12 | 卧推 2×WORK，completed_at = now **+1h**（时钟回拨） | chest 20, triceps 10, front_delts 10（age 取 0） |

### 5.3 可视化分档（M5 用）

| score | 档位 | 颜色（亮色主题，单色渐变，避免红绿色盲问题） |
|-------|-----|------|
| 0 | 无 | `#E0E0E0` |
| 1–24 | 低 | `#F9D5D3` |
| 25–49 | 中 | `#F28B82` |
| 50–74 | 高 | `#D93025` |
| 75–100 | 极高 | `#8C1D18` |

---

## 6. 估算 1RM 与 PR `domain/strength/OneRepMax.kt`

- Epley：`e1rm_g = weight_g × (1 + reps / 30.0)`，结果 `round_half_up` 为整数克；`reps == 1` 时 `e1rm = weight_g`；`reps == 0` 或 `reps > 12` → 返回 null（不可靠）。
- PR 判定（M5 使用）：某个组的 e1rm **严格大于**该动作此前所有已完成组的最大 e1rm → 标记为 PR。热身组不参与。
- **PR 需要基线**（M5 审计设计变更 D9）：该动作在本组之前没有任何有效组（历史 + 本会话更早的组，排除热身组，且 e1rm 不为 null）时，**不算 PR**（首次记录只作为基线）。动作历史曲线的第一个有效日同样不标 PR。

| # | 此前有效组的最大 e1rm | 本组 | 是否 PR |
|---|---------------------|------|--------|
| PR1 | （无有效组） | 100000 g × 5 | 否（基线） |
| PR2 | （只有热身组 60000 × 10） | 100000 g × 5 | 否（热身组不算基线） |
| PR3 | 116667（100 kg × 5） | 100000 g × 5 | 否（相等） |
| PR4 | 116667 | 102500 g × 5（e1rm 119583） | 是 |


### 测试向量 E

| # | weight_g | reps | e1rm_g |
|---|---------|------|--------|
| E1 | 100000 | 5 | 116667 |
| E2 | 60000 | 10 | 80000 |
| E3 | 102058 | 8 | 129273 |
| E4 | 140000 | 1 | 140000 |
| E5 | 50000 | 0 | null |
| E6 | 50000 | 13 | null |

---

## 7. 组间休息计时的纯逻辑 `domain/timer/RestTimerMath.kt`（M3）

```kotlin
data class RestTimerState(val targetElapsedRealtime: Long, val totalMillis: Long)
fun remainingMillis(state: RestTimerState, nowElapsed: Long): Long = max(0, target - now)
fun progress(state, nowElapsed): Float  // 1.0 → 0.0
fun adjust(state, deltaSeconds: Int, nowElapsed: Long): RestTimerState
// 规则：newRemaining = max(0, remaining + delta)；实际变化量 applied = newRemaining - remaining；
//       target += applied；total += applied（total 下限 0）
```

测试向量 T（`total = 90s`，`target = 190_000`，即计时从 `now = 100_000` 开始）：

| # | 操作 | now | 期望 |
|---|------|-----|-----|
| T1 | remaining | 100_000 | 90_000 |
| T2 | remaining | 145_000 | 45_000 |
| T3 | remaining | 200_000 | 0 |
| T4 | progress | 145_000 | 0.5 |
| T5 | adjust +15 | 145_000 | target 205_000, total 105_000 |
| T6 | adjust −15 ×4 | 145_000 | target 145_000（剩余 0），total 45_000 |
