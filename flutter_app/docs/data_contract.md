# 数据契约与热力聚合

所有时间以 UTC ISO-8601 存储，展示时按设备时区格式化。每个可持久化对象带 `schemaVersion`，导入备份时先校验版本再迁移。

## 领域对象

```text
ExerciseTemplate {
  id: String
  name: String
  category: String
  equipment: String?
  targetMuscleIds: List<String>
  defaultSets: int
  defaultReps: int
  createdAt: DateTime
  updatedAt: DateTime
  schemaVersion: int
}

WorkoutSession {
  id: String
  startedAt: DateTime
  endedAt: DateTime?
  templateId: String?
  notes: String?
  sets: List<ExerciseSet>
  schemaVersion: int
}

ExerciseSet {
  id: String
  exerciseId: String
  setIndex: int
  reps: int
  weight: double
  unit: kg | lb
  rpe: double?
  completed: bool
  note: String?
}

BodyMetric {
  id: String
  measuredAt: DateTime
  type: weight | bodyFat | waist | custom
  value: double
  unit: String
  note: String?
  schemaVersion: int
}
```

Freezed/JSON 生成文件是派生物；不能把 `.g.dart` 或 `.freezed.dart` 当作手写源文件。Hive box 名称、JSON 字段名和肌群 ID 一旦发布，迁移前不得随意改名。

## 稳定肌群 ID

初始集合：`deltoids`、`pectorals`、`biceps`、`triceps`、`abs`、`quadriceps`、`calves`、`trapezius`、`lats`、`glutes`、`hamstrings`。前/后视、左右侧和本地化文案是 ID 的属性，不是 ID 本身。

## 热力分数

1. 按用户选择的日期范围读取已完成训练组。
2. 每组基础负荷为 `weight × reps`；自重动作使用动作配置的估算体重系数，缺少系数时退化为有效组数。
3. 将动作的负荷按 `targetMuscleIds` 和可选的 `bodyPartWeights` 分摊到肌群。
4. 在同一日期范围内用 `p95` 作为高值基准，计算 `clamp(load / p95, 0, 1)`；没有数据时返回 `hasData=false`，不能把缺失和真实 0 混为一谈。
5. 左右对称区域可以共享总分，也可以在动作映射提供侧别时分别计算；规则必须在 UI 图例或详情中保持一致。
6. painter 使用冷色 `#607A9A`、暖色 `#FFAE4B`、热色 `#F04436` 的分段插值，并在路径内部绘制径向光晕和肌纤维纹理。

建议的传输对象：

```text
MuscleHeatScore {
  muscleId: String
  view: front | back
  side: left | right | bilateral
  score: double        // 0..1
  rawLoad: double
  hasData: bool
  sampleCount: int
  rangeStart: DateTime
  rangeEnd: DateTime
}
```

## 导入导出

JSON 备份包含 `backupVersion`、`exportedAt`、`settings`、`exerciseTemplates`、`workoutSessions`、`bodyMetrics`。导入必须先解析到临时对象，完成校验和迁移后再原子替换；解析失败不能清空已有数据。CSV 是给人读取的扁平视图，不作为唯一恢复格式。
