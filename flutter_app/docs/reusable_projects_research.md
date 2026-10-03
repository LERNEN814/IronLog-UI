# 可复用项目与依赖检索报告

核查日期：2026-10-02（Asia/Singapore）
目标：为本项目的 Flutter Android 健身记录 App 寻找可以减轻“肌肉热力图”开发工作量的开源项目、Flutter package 和可移植的人体肌肉矢量资产。

## 先决条件：严格复刻不可降级

参考图的肌肉分离效果是本项目的硬约束。任何候选方案只有在能提供真实的、可独立着色的肌肉区域路径时才可能进入生产实现。

- **严禁用简单多边形、矩形、椭圆、粗略包围盒或整块位图覆盖来替代肌肉分离路径。** 这些形状可以作为不可见的触摸容错区域，但不能成为视觉肌肉区域。
- 每个可见肌肉区域必须来自闭合的 SVG path（`M/L/C/Q/A/Z` 等路径命令）或等价的 Dart `Path`（`moveTo`、`lineTo`、`cubicTo`、`quadraticBezierTo`、`close`），并可独立应用颜色、渐变、纹理和选中描边。
- 必须保留前视和后视的独立路径，左右肌群需要独立 `regionId`，同一肌群可以共享 `muscleId`。点击、热度和纹理都必须裁切到实际路径内部。
- 参考 PNG 只能作为比对图，不能作为生产人体主体。生产版本必须是可缩放路径或等价的 `CustomPainter` 路径。
- 复用外部路径不等于复用外部视觉风格。最终仍需重新处理深蓝灰人体、橙黄红热力渐变、肌纤维纹理、引导线、标签和选中光晕，并用截图/Golden Test 证明没有路径越界或区域粘连。

如果候选项目只有粗粒度的 `chest`/`arm`/`leg` 大块，或只提供单张 PNG，它不能被描述为“复刻完成”；应降级为原型参考或直接淘汰。

## 检索方法与限制

本次通过以下公开入口检索并抽查了源代码、README、package metadata 和许可证：

- GitHub repository search/API：`muscle anatomy svg`、`flutter body chart`、`fitness muscle heatmap`。
- GitHub raw 文件：抽查 `README.md`、`pubspec.yaml`、SVG/path 数据和许可证文件。
- pub.dev API：`body chart`、`muscle heatmap`、`body map`，以及候选 package 的版本、Dart/Flutter 约束、下载量和 pub score。
- 关键页面和源代码 URL 都保留在下表，便于执行者复核。

未使用需要登录的商业 API，也没有把远程图像下载进工程。GitHub 未认证 API 在多次查询后触发了当前出口 IP 的 rate limit，后续改用 `raw.githubusercontent.com` 和 pub.dev API 完成文件核验；因此星标和更新时间以检索当日能取得的元数据为准，不能替代执行者在集成前的再次复核。联网搜索也不能证明某个外部人体图与本项目参考图像素级一致，最终判断必须以本地截图验收为准。

## 优先级结论

## 按用户准则执行的选择门槛

执行者发现满足以下条件的现成项目时，应**优先固定版本并嵌入/改造该项目**，不重复自研同一层能力：

1. 有可审计的闭合肌肉 SVG/Dart Path，至少覆盖前后视和本期需要的独立肌群；
2. Android Flutter 能在当前 Dart/Flutter 版本解析，或可以在仓库内以 fork/patch 的方式修复且不改变路径精度；
3. 许可证明确允许当前应用的修改、打包和分发，并能随工程保留署名；
4. 能通过本项目的截图、路径越界、左右命中和性能验收。

如果项目满足第 1、3、4 条但不是 Flutter，优先移植它的路径数据和分组/热力图算法，再复用 Flutter 的渲染层；如果项目只满足交互框架而不满足参考图资产，则嵌入其解析/命中框架并替换为有授权的真实 SVG。只有在没有候选能满足这些门槛时，才从零制作对应层。任何粗粒度多边形、单张 PNG、无许可证或付费 API 方案都不能因为“集成快”而越过门槛。

### A 级：优先复用，但要带着路径和许可证一起审查

#### 1. `muscle_mapper`：Flutter 交互/解析骨架的首选

- 项目：[github.com/suryamolly/muscle_mapper](https://github.com/suryamolly/muscle_mapper)
- Package：[pub.dev/packages/muscle_mapper](https://pub.dev/packages/muscle_mapper)
- 版本/核查数据：`1.2.2`，2026-08-18 发布；pub score `150/160`，近 30 天下载量约 `107`。
- 许可证：仓库标记 MIT。README 说明 `advanced` SVG 来自 Ryan Graves，按 **CC BY 4.0** 使用，公开应用需要署名；`minimal` 资产也必须在集成前核查来源和随包许可。
- 可复用能力：
  - `male front / male back / female front / female back` 四视图；
  - 以 `<g id="...">` 分组的 SVG 资产；
  - `flutter_svg` 负责视觉渲染，`path_drawing` 解析不可见 Path 做精确命中测试；
  - 子肌肉、肌群、主要肌群三级层级；多选、逐肌肉强度、不透明度、颜色覆盖和渐变动画；
  - BYOA（Bring Your Own Asset）接口，可替换为本项目自己制作且授权清楚的 SVG。
- 适配判断：**最适合复用其“资产提供器 + 分组解析 + 路径命中”架构，不能直接承诺复刻参考图。** 自带图形的医学/健身风格与参考图的深色插画、肌纤维纹理和标签布局不同，必须导入重新绘制的真实 SVG path。保留 `<g>` 和 path ID 后，可以复用它的交互逻辑并把绘制层换成本项目的 `CustomPainter`/SVG 样式层。
- 重要兼容性风险：该 package 当前依赖 `path_drawing ^1.0.1`；pub.dev metadata 显示 `path_drawing 1.0.1` 的 SDK 约束仍为 `>=2.12.0 <3.0.0`，而本工程使用 Dart 3.13。**不得未经 `flutter pub get`、分析和 APK 构建验证就直接加入依赖。** 推荐 fork 后替换为 Dart 3 可用的路径解析方案（例如直接使用 `path_parsing 1.1.0`/自有转换脚本，或把 SVG 在构建阶段转成 Dart Path），并固定 commit。
- 许可证动作：若使用 advanced SVG，保留 CC BY 4.0 署名；如果路径被重绘或替换，必须在工程 `THIRD_PARTY_NOTICES` 记录来源和改动。不能把受限资产抽取后单独发布。

#### 2. `melihcolpan/MuscleMap`：最完整的多视图路径和热力图参考

- 项目：[github.com/melihcolpan/MuscleMap](https://github.com/melihcolpan/MuscleMap)
- 许可证：GitHub 标记 MIT；检索时约 `263` stars、`38` forks，GitHub 元数据更新时间 2026-09-28。
- 可复用内容：Swift package 中明确拆分了 `MaleFrontPaths.swift`、`MaleBackPaths.swift`、`FemaleFrontPaths.swift`、`FemaleBackPaths.swift`、`BodyPathData.swift`；另有 `SVGPathParser`、`PathBuilder`、`ColorInterpolation`、`HeatmapColorScale`、`MuscleFill`、路径缓存、缩放容器和可访问性覆盖层。
- 适配判断：**它不是 Flutter package，不能直接嵌入 Android Flutter；但它是非常好的真实 Path 数据和热力图状态设计参考。** 执行者可以在遵守 MIT 的前提下，将路径数据转换成 SVG `d` 或 Dart `Path` 常量，再接入本项目的 `MuscleRegion`/`CustomPainter`。四视图和路径缓存可显著减少从零拆图的工作量，但转换后必须逐视图截图核对，不能把 Swift UI 代码当成最终实现。
- 风险：仓库的人体造型和参考 PNG 不是同一画风；需要重做颜色、纹理、轮廓和标签。转换脚本应产生可审阅的 path 文件，不应在运行时解析 Swift 源码。

#### 3. `vulovix/body-muscles`：可移植的 70+ SVG 路径数据

- 项目：[github.com/vulovix/body-muscles](https://github.com/vulovix/body-muscles)
- 许可证：Apache-2.0；检索时约 `30` stars，最近代码推送 2026-04-16。
- README 声明：70+ 解剖区域、前（anterior）/后（posterior）视图、0-10 强度渐变、选择态和 glow，纯 TypeScript，无运行时依赖。`src/data/muscles.front.ts` 与 `src/data/muscles.back.ts` 直接保存每个肌群的 SVG path 字符串。
- 适配判断：**适合作为路径资产候选或数据映射参考。** 可以把 `MuscleDef` 的 `id/name/view/path` 转成项目自己的 SVG 文件，并用 `flutter_svg` 或构建期转换器渲染；70+ 区域比参考图首版的 12 个热区更细。它没有 Flutter 交互层，需要自行接入 Riverpod 和 hit-test。
- 许可证动作：Apache-2.0 需要保留许可证和适用的 NOTICE/归属信息；不要只复制 path 而丢失第三方声明。使用前审查每条路径是否与参考图的轮廓、分割和视觉层次相容。

#### 4. `react-native-body-parts-anatomy`：317 个可点击 SVG 碎片

- 项目：[github.com/eslamelfateh/react-native-body-parts-anatomy](https://github.com/eslamelfateh/react-native-body-parts-anatomy)
- 许可证：MIT；README 明确写明 `23` 个肌群、男性/女性 × 前/后视、`317` 个分别可寻址的 SVG fragments，支持缩放和命中。
- 适配判断：**适合作为高粒度区域拆分的数据来源，不能直接作为 Flutter widget。** 其实现依赖 React Native SVG、gesture handler 和 Reanimated；执行者需要把生成的区域/路径移植到 Flutter，或只借用 `bodyRegions.generated.ts` 的 ID/分组，再由 Flutter 的 SVG/Path 层渲染。317 碎片有利于严格肌肉分离，但也会增加路径清理、左右侧映射和纹理裁切工作。
- 风险：外部资产的风格、坐标系和本项目参考图不同；迁移前先做单视图 SVG 渲染截图。MIT 归属保留在第三方声明中。

### B 级：可用于 Flutter 原型或直接拿来做基线，但不能未经截图验收就当最终资产

#### 5. `muscle_map_flutter`：CustomPainter 热力图基线

- 项目：[github.com/henrytran1803/muscle-map-flutter](https://github.com/henrytran1803/muscle-map-flutter)
- Package：[pub.dev/packages/muscle_map_flutter](https://pub.dev/packages/muscle_map_flutter)
- 版本/核查数据：`0.1.0`，2026-09-09 发布；pub score `150/160`，近 30 天下载量约 `93`；GitHub 检索时约 `0` stars。
- 许可证：仓库标记 MIT。
- 可复用能力：男性/女性前后视、CustomPainter、23 个左右侧 `MuscleGroup`、逐 surface `partValues`、Load/Frequency/Balance/Recovery Risk 四种色带、选中 tooltip、点击回调、glow 和图例。`surface_ids.dart` 展示了胸、核心、斜方肌、背阔肌、臀、腘绳肌、三角肌前/侧/后束等独立表面 ID。
- 适配判断：**如果先要得到可工作的 Flutter 热力图，它是最省集成代码的候选之一。** 但其图形和参考 PNG 的插画风格、路径粒度、纹理、引导线均不等价；必须审阅 `assets/bodies/` 或 Dart 图形数据并替换/扩展为本项目真实 SVG path。不要因为它叫 `CustomPainter` 就把简单轮廓当作“已复刻”。
- 风险：版本号很新、采用 git/小众包形态，维护和 API 稳定性证据少；发布前应 fork 固定 commit，并补充 golden、命中测试和资产许可证审查。

#### 6. `flutter_body_heatmap`：23 区域、四视图、纯 Flutter

- 项目：[github.com/nyandrianinamamy/flutter_body_heap_map](https://github.com/nyandrianinamamy/flutter_body_heap_map)
- Package：[pub.dev/packages/flutter_body_heatmap](https://pub.dev/packages/flutter_body_heatmap)
- 版本/核查数据：`0.1.0`，2026-03-19 发布；pub score `140/160`，近 30 天下载量约 `153`。
- 许可证：仓库 MIT；README 说明人体 SVG 数据来源于 [react-native-body-highlighter](https://github.com/HichamELBSI/react-native-body-highlighter)，该上游按 MIT 许可。
- 可复用能力：纯 `CustomPaint`、男性/女性前后视、23 个解剖区域、左右侧、连续强度色带和 `onMusclePressed` 回调。它避免了额外 SVG 运行时依赖，适合研究 Path/Canvas 层。
- 适配判断：可做架构基线和路径移植参考；若其源码确实保存了闭合曲线 Path，可以复用命中与着色思路。但它本身没有参考图所需的标签、引导线、肌纤维纹理和分层阴影，仍需大幅改造。
- 风险：版本和社区规模较小；在当前 Dart/Flutter 版本上先跑 `pub get`、analyze、test、debug APK。不要在没有确认源路径的情况下把它的截图或位图当成生产主体。

#### 7. `bodychart_heatmap`：可以快速验证数据流，但粒度太粗

- 项目：[github.com/Obaloluwa-Obidoyin/bodychart_heatmap](https://github.com/Obaloluwa-Obidoyin/bodychart_heatmap)
- Package：[pub.dev/packages/bodychart_heatmap](https://pub.dev/packages/bodychart_heatmap)
- 版本/核查数据：`1.1.0`，2025-09-16 发布；pub score `140/160`，近 30 天下载量约 `1,173`；MIT。
- 可复用能力：`BodyHeatmap`/`BodyChart`、前/后/双视和 intensity levels；实现内嵌 SVG path 并使用 `flutter_svg`。
- 不适配原因：它的公开 API 只有 `neck/chest/arm/leg/butt/back/abs` 等粗粒度分组，源代码把大量 path 统一填成同一分组颜色，没有逐肌肉点击、左右侧独立热区、局部高光、纹理和参考图标签。**这与“严禁简单多边形替代、严格复刻分离效果”不相符。** 只适合快速验证热度数据映射，不应作为最终人体资产。

#### 8. `muscle_map`：有 SVG 路径，但当前版本有许可和 SDK 风险

- 项目：[github.com/SquashVash/muscle-map](https://github.com/SquashVash/muscle-map)
- Package：[pub.dev/packages/muscle_map](https://pub.dev/packages/muscle_map)
- 版本/核查数据：`1.3.0`，2026-09-08 发布；pub score `130/160`，近 30 天下载量约 `188`。
- 可复用能力：`front_body.svg`/`back_body.svg`/`human_body.svg`，每个肌肉 path 有 ID（如 `chest1`、`abs1`、`glutes1`），提供选择和 light/medium/hard 强度图。
- 风险：仓库的 `LICENSE` 仍是带 `[year] [fullname]` 占位符的 MIT 模板，不能视为已完成的资产授权；依赖 `svg_path_parser 1.1.2`，其 pubspec SDK 约束为 `<3.0.0`，与当前 Dart 3 工程不兼容风险高。只有在取得作者许可、修正依赖并审计 SVG 来源后才能考虑借用路径；不建议直接加入生产依赖。

### C 级：不适合作为本项目最终方案

#### 9. `rive_muscle_heatmap`

- 项目：[github.com/jorgeg922/flutter-rive-muscle-heatmap](https://github.com/jorgeg922/flutter-rive-muscle-heatmap)
- Package：[pub.dev/packages/rive_muscle_heatmap](https://pub.dev/packages/rive_muscle_heatmap)
- 版本/核查数据：`0.2.0`，2026-09-16 发布；pub score `160/160`，近 30 天下载量约 `147`。
- 代码按 MIT 许可；但随包的 `human_anatomy_free_basic_v2.0.riv` 有独立 `LICENSE-ASSET.md`：免费资产只有**男性前视 18 个肌肉**，固定设计颜色，不允许修改、改色、重组或反向工程；后视、女性、强度颜色和点按识别在付费 Basic/Advanced 包中。
- 结论：适合做“Rive 渲染管线是否可行”的短期原型，**不适合本项目最终目标**。它缺少免费后视，且禁止对 `.riv` 做达到参考图所需的热力渐变、纹理和样式修改；付费资产也需要另行评估授权，不能把价格当作自动获得衍生/提取权。

#### 10. `human_body_map_selector`

- 项目：[github.com/imeshthana/human_body_map_selector](https://github.com/imeshthana/human_body_map_selector)
- Package：[pub.dev/packages/human_body_map_selector](https://pub.dev/packages/human_body_map_selector)
- 版本/核查数据：`1.0.1`，2026-07-09 发布；pub score `160/160`，近 30 天下载量约 `14`；MIT。
- 主要是前后视 PNG + 手工矩形区域（约 35 个区域）和点标记，适合伤痛/部位选择，不是肌肉路径热力图。矩形可以做触摸辅助层，但不能满足本项目的视觉路径和肌纤维分离要求。

#### 11. `ExerciseDB/muscle-visualizer-api`

- 项目：[github.com/ExerciseDB/muscle-visualizer-api](https://github.com/ExerciseDB/muscle-visualizer-api)
- README 描述 RapidAPI 上的男性/女性前后视肌肉图、热力图和 workout activation，输出 PNG/JPEG/WebP；生产 API 需要 RapidAPI key。仓库没有可确认的开源许可证。
- 结论：不符合本项目的本地优先、离线、矢量和可交互要求。即使生成图像质量较好，也只能作为设计灵感或人工对照，不能作为运行时人体层。

#### 12. 其他应避免直接使用的仓库

- [`snaprepo/muscles`](https://github.com/snaprepo/muscles)：虽然描述为 front/back/full 人体 SVG，但 GitHub metadata 未声明许可证；在取得明确授权前不要复制。
- 只给屏幕截图、单张 PNG 或没有可审计路径 ID 的仓库：不能证明肌肉边界可命中、可裁切，也不能满足本项目的路径验收。

## 基础 Flutter 依赖建议

这些包不能替代人体资产，但可以降低“把真实 SVG path 接入 Flutter”的成本：

| 依赖 | 核查版本 | 用途 | 结论 |
| --- | --- | --- | --- |
| [`flutter_svg`](https://pub.dev/packages/flutter_svg) | `2.3.0`，2026-05-08 | SVG 1.1 渲染、`SvgPicture`、颜色/主题处理 | 推荐；Flutter 官方 packages 仓库维护，适合生产渲染。 |
| [`vector_graphics`](https://pub.dev/packages/vector_graphics) | `1.2.3`，2026-07-31 | 将 SVG 编译为二进制矢量格式，降低解析/绘制开销 | 可选；当路径量和首屏性能成为问题时采用，不能替代可审计的源 SVG。 |
| [`path_parsing`](https://pub.dev/packages/path_parsing) | `1.1.0`，Dart `^3.3.0` | 解析 SVG path 命令为 Flutter Path 相关结构 | 可作为 Dart 3 解析基础；先验证 API，再决定是否构建期转换。 |
| [`path_drawing`](https://pub.dev/packages/path_drawing) | `1.0.1`，旧 SDK `<3.0.0` | 传统 SVG path 到 Canvas Path 工具 | 当前工程不要直接依赖，除非 fork/patch 并固定版本；上面的 `muscle_mapper` 也有该风险。 |
| [`rive`](https://pub.dev/packages/rive) | `0.14.11` | Rive runtime | 仅在接受 `.riv` 资产限制并有明确授权时使用，不能用来绕过路径和改色要求。 |

当前工程已有 `flutter_svg` 之外的状态、存储和动画基础依赖时，先不要为了“有现成热力图”引入多个互相冲突的地图包。优先确定一套资产格式（推荐 SVG source + build-time Path cache）和一套 hit-test 实现，再接入 Riverpod。

## 给执行者的推荐实施顺序

1. **先做资产审计，不先写 UI 替代品。** 从 `muscle_mapper` 的 BYOA 结构、`MuscleMap` 的四视图路径组织、`body-muscles` 的 70+ path 数据中选一个来源，确认每个路径有稳定 ID、闭合且无自交，并确认许可证允许商用/修改。
2. **首选组合：`muscle_mapper` 交互思想 + 自有/明确授权 SVG。** 如果它的 `path_drawing` 依赖无法在 Dart 3 解决，就 fork 包并替换解析器；不得为了省事把肌肉区域改成多边形。
3. **做单视图垂直切片。** 先实现正面：头/颈、左右三角肌、胸大肌、腹直肌、左右股四头肌和冷区基础层。每个 region 用真实 SVG path、局部径向高光和裁切纹理，点击后验证左右不串区。
4. **再移植后视路径。** 加入斜方肌、背阔肌、后束三角肌、臀大肌和腘绳肌，并补齐前/后标签和引导线。不要把前视图水平翻转后直接当成后视图。
5. **最后接训练数据和动画。** Riverpod 只提供归一化 score；painter/renderer 只读 immutable render data。热度、纹理、选中描边都必须由同一组 region path 驱动。
6. **每次候选替换都跑许可证和视觉门槛。** `flutter pub get`、`flutter analyze`、`flutter test`、debug APK、四种视口截图和逐肌群点击测试必须同时通过。任何候选若只能显示粗粒度轮廓、出现 PNG 兜底或不能证明路径来源，立即回退到自有 SVG 路径制作。

## 交付前必须记录的证据

- 资产来源 URL、commit/tag、许可证文本和第三方署名文件；
- 前/后视每个 path 的 `regionId`、`muscleId`、左右侧、包围盒和 SVG `d`/Dart Path 来源；
- 至少 `score = 0 / 0.5 / 1` 三张截图，以及 360×780、412×915、768×1024、1080×1920 视口截图；
- 每个主要肌群点击、24 dp 容错、左右不串区和缩放后的命中结果；
- 路径越界、纹理越界、标签/引导线重叠检查；
- APK 构建日志，尤其是 Dart 3 依赖解析结果；
- 如果使用 `muscle_mapper`、`MuscleMap`、`body-muscles` 或其他外部资产，第三方许可证和改动记录必须随工程提交。

本报告的推荐并不改变 `docs/heatmap_spec.md` 的生产约束：外部项目只能减少路径解析、命中测试或数据映射工作，不能降低人体肌肉路径的分离精度，也不能把参考 PNG 变成运行时人体主体。
