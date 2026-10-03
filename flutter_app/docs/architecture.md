# 工程架构与技术栈

## 分层

```text
UI / presentation
  Dashboard, workout editor, history, trends, settings, heatmap widgets
          |
Riverpod state / application services
  Notifier, validation, date range, muscle-load aggregation, export orchestration
          |
Repositories
  WorkoutRepository, ExerciseRepository, BodyMetricRepository, SettingsRepository
          |
Local data
  hive_ce boxes + JSON/CSV codecs + SharedPreferences settings
```

页面不直接调用 Hive，也不在 `build` 中执行聚合或写盘。Repository 只暴露领域对象，Notifier 负责状态和副作用，Widget 负责展示与事件。

## 技术栈清单

| 层 | 选择 | 工程边界 |
| --- | --- | --- |
| UI | Flutter/Dart、Material 3、Cupertino | Android 优先，保留 iOS 风格的导航和反馈 |
| 状态 | `flutter_riverpod: ^2.6.1` | 当前 Goal 保持 Riverpod 2 API；不要无理由升级到 Riverpod 3 |
| 主库 | `hive_ce: ^2.20.1` | 原始 `hive: 2.2.3` 不支持 Dart 3；`hive_ce` 是兼容 Hive API 的维护分支 |
| 设置 | SharedPreferences | 只存主题、kg/lb、默认模板等小型键值 |
| 模型 | Freezed + `json_serializable` | 生成不可变模型、复制方法和备份 JSON；生成文件不得手写 |
| 动效 | `flutter_animate`、Flutter AnimationController | 只给有状态变化的组件加动画，避免持续重绘 |
| 图表 | `fl_chart` | 训练量、力量、体重趋势；空数据要有空状态 |
| 布局 | `gap` | 统一间距，避免散落魔法数字 |
| 热力图 | SVG/CustomPainter + Path | 1000×1080 设计坐标，路径、纹理、标签和命中测试分层 |
| 导出 | `csv` + JSON、path_provider、share_plus | JSON 必须可往返解析；失败/取消不能阻塞训练记录 |
| 通知 | permission_handler、flutter_local_notifications | 仅在用户启用提醒时申请权限，拒绝后核心功能仍可用 |
| 可选 | dio、cached_network_image、sensors_plus | 默认关闭；不能成为离线核心流程的依赖 |

完整的 Android 原生插件依赖放在 `docs/optional_dependencies.yaml`，按 feature 启用。当前可运行基线只保留不触发 Windows 原生 hook 的核心依赖。

## Feature 边界

```text
lib/
  app.dart
  core/
    theme/
    models/                 # Freezed 领域模型（后续创建）
    storage/                # Hive/SharedPreferences 封装（后续创建）
    export/                 # JSON/CSV codec（后续创建）
  features/
    dashboard/
    exercises/
    workout/
    history/
    body_metrics/
    heatmap/
    settings/
```

## 当前基线的明确限制

`MuscleHeatmap` 的生产人体层现在使用 `assets/heatmap/body_front.svg` 和 `body_back.svg` 的逐肌群解剖路径，并由 `ColorMapper` 按稳定肌群 ID 映射训练热度；同一 SVG 的 alpha 还用于裁切斜向纹理。项目自己的 `CustomPainter` cubic Path 只承担命中测试、无障碍语义、选中描边和标签锚点，不再覆盖 SVG 主体。用户提供的 PNG 仅作为只读视觉参考，不能被加载为生产人体层。训练记录通过 `heatmapScoresProvider` 聚合后传入该分层渲染器。

热力图实现还必须遵守 `docs/strict_heatmap_policy.md`。在自研路径前先读取 `docs/reusable_projects_research.md` 并更新 `docs/reuse_evaluation.md`；只有经过路径级视觉截图、SDK 构建和代码/艺术资产许可证审计的候选才能通过 adapter 进入生产。

## 数据流

```text
WorkoutSession / ExerciseSet
        -> MuscleLoadAggregator(dateRange, bodyPartWeights)
        -> Map<MuscleId, MuscleHeatScore>
        -> Riverpod heatmap provider
        -> MuscleHeatmap painter + semantics + hit test
```

热力图绘制不能从标签文字反推数据。标签是展示层，稳定 ID 和训练动作到肌群的映射才是数据契约。
