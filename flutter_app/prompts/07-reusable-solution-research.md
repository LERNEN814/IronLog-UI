# 可复用热力图方案检索与选型提示词

这份提示词用于阶段 0/阶段 2 开始前，也可以在依赖、Flutter SDK 或参考图发生变化时重新发送。它把“能否复用”变成可审查的工程决策，不代表下面列出的包已经自动获准接入。版本、许可证和仓库内容必须在执行时重新核验。

## 可复制提示词

```text
执行肌肉热力图可复用方案检索和选型，不要直接开始画图。

目标：在严格复刻参考图的前提下，优先嵌入满足条件的现有 Flutter 包、SVG/Rive/Canvas 资产或 GitHub 项目，减少重复开发；如果没有方案同时满足视觉、技术和许可证要求，才进入自研。执行者必须把检索、淘汰和选型证据写入 `docs/reuse_evaluation.md`，并在最终报告引用该文件。

硬门槛：
1. 最终渲染必须保留逐肌群独立的高保真 SVG/Path/Rive mask、曲线边界、描边、肌纤维纹理、前后视层级和可命中区域。严禁用简单多边形、矩形、椭圆、占位块、均匀色块、默认图标或整张静态位图替代肌肉分离效果。临时占位也不能作为阶段完成或验收证据。
2. 候选必须实际支持前视/后视覆盖、稳定肌群 ID、路径级命中测试和按训练负荷改变颜色/透明度；纹理、标签/引线、选中描边和参考图的深色面板仍需可扩展。只有粗粒度 body chart、只有前视单图或只能整体着色的候选不能直接满足目标。
3. 必须分别核对代码许可证和艺术资产许可证。查 LICENSE、LICENSE-ASSET、CREDITS、资产目录和仓库说明；许可证不明、`NOASSERTION`、禁止修改/再分发、只允许付费升级、要求而未完成署名或代码许可证覆盖不到内置资产时，不得纳入生产依赖。不要把 MIT/BSD 代码许可误当成 SVG/Rive 艺术资产许可。
4. 核对当前 Flutter/Dart SDK、Android 构建、依赖版本、传递依赖、维护状态、最近提交和离线打包能力。固定版本或 commit，不能只写 `any`；运行 `flutter pub get`、`flutter analyze` 和最小渲染/命中测试。
5. 采用候选前必须用目标参考图尺寸生成至少一个窄屏和一个宽屏截图，逐项检查人体比例、肌群分离、颜色层次、热度渐变、纹理、描边、标签/引线和响应式缩放。截图不达标就淘汰或只复用底层解析/命中代码，不能为了省事接受近似图。

检索范围和顺序：
- 先检查当前仓库、`pubspec.yaml`/lockfile、已有 SVG/Path/Rive 资源和 git 历史，避免重复引入已经存在的实现。
- 再查 pub.dev 包搜索、包页面、归档源码和 GitHub 上游仓库；可以核对 `flutter_muscle_anatomy`、`muscle_mapper`、`flutter_body_atlas`、`bodychart_heatmap`、`muscle_map`、`rive_muscle_heatmap`，但不得仅凭名称或 README 选型。
- 对每个候选记录：包名/仓库 URL、pub.dev 版本、commit/tag、检索时间、SDK 约束、最近提交、stars（仅作为背景）、代码许可证、艺术资产许可证、署名/商用/修改/再分发条款、前后视和肌群数量、路径/命中/热度/纹理能力、与参考图的差异、构建和截图结果。
- `rive_muscle_heatmap` 的代码许可和 bundled `.riv` 资产许可必须分开审查；若使用免费资源，确认其只有前视 18 肌群，后视/强度/点击能力是否需要另行购买授权，不能把付费能力当成免费 API。
- `flutter_body_atlas` 的代码许可证和内置图资产许可证必须分开记录，并完成其 CC BY 4.0 署名要求；若无法接受署名或资产修改限制，应淘汰。

选型规则：
- 选择“满足参考图视觉、技术和许可证”的候选，优先通过 adapter 接入并复用路径/资产；主题色、热度算法、纹理、引线、标签、数据 provider 和无障碍语义由本项目控制。
- 若候选的路径分离/命中基础合格但视觉风格不合格，可只复用解析器、Path 数据格式或交互层；不要把不匹配的简化人体图当最终视觉。
- 如果所有候选均不合格，记录逐项淘汰理由，然后自研逐肌群 SVG/CustomPainter/Canvas 路径。自研仍需保留来源、绘制工具、许可证、坐标测量和视觉回归证据。严禁退回简单多边形。

`docs/reuse_evaluation.md` 至少包含：
1. 检索时间、查询链接和候选比较表；
2. 代码许可证与每个艺术资产/字体/纹理的独立许可证；
3. SDK/依赖兼容性和固定版本/commit；
4. 窄屏/宽屏截图或 Golden 结果及与参考图的差异；
5. 选中方案、adapter 边界、署名文件位置、未解决风险和淘汰理由；
6. 结论：复用、部分复用后自研，或完全自研。没有证据的项目写“未验证”，不能写“符合”。

完成后先输出候选矩阵和推荐决策，再实施被选方案；运行 `flutter pub get`、`dart format`、`flutter analyze`、热力图路径/命中测试和可执行截图检查。最终报告必须说明：为什么选它、它没有解决什么、许可证如何满足、若后续版本漂移如何复核。
```

## 本次检索的候选快照（执行时必须复核）

检索日期：2026-10-02。以下是用于缩短侦察时间的起点，不是无条件推荐。

| 候选 | 观察到的能力 | 许可证/风险 | 处理建议 |
| --- | --- | --- | --- |
| [`flutter_body_heatmap`](https://pub.dev/packages/flutter_body_heatmap) / [`nyandrianinamamy/flutter_body_heap_map`](https://github.com/nyandrianinamamy/flutter_body_heap_map) | `0.1.0`；纯 Flutter `CustomPainter`；男性/女性前后视；23 个区域；连续强度和点击回调；路径来自 `react-native-body-highlighter` | 仓库 MIT；必须保留上游 MIT 归因并核对发布包内路径；不自带参考图的纹理、标签和引线 | 首个 Flutter 视觉 spike；只有窄屏/宽屏截图达到路径粒度和视觉目标才可采用，否则只复用路径/命中思路 |
| [`flutter_muscle_anatomy`](https://pub.dev/packages/flutter_muscle_anatomy) / [`angujo/flutter_muscle_anatomy`](https://github.com/angujo/flutter_muscle_anatomy) | `1.2.8`；male/female 前后视 SVG；左右肌肉；Path 访问、CustomPainter、缩放和命中测试；可自定义高亮 | 仓库声明 BSD-3-Clause；仍需检查当前版本资产目录和许可证是否覆盖所有 SVG | 优先做目标参考图截图评估，可能作为高保真路径基础或 adapter |
| [`muscle_mapper`](https://pub.dev/packages/muscle_mapper) / [`suryamolly/muscle_mapper`](https://github.com/suryamolly/muscle_mapper) | `1.2.2`；35 个 sub-muscle、3 层分组、BYOA、SVG Path 命中、强度和颜色映射 | 仓库声明 MIT；必须核对 advanced/minimal 内置 SVG 和第三方来源 | 优先评估，尤其适合复用路径解析/命中和改接自有高保真资产 |
| [`flutter_body_atlas`](https://pub.dev/packages/flutter_body_atlas) / [`kit-g/flutter-body-atlas`](https://github.com/kit-g/flutter-body-atlas) | `0.2.1`；SVG body atlas、稳定 ID、前后视、命中和元素着色 | 代码 BSD-3-Clause；内置资产为 CC BY 4.0，需归属 Ryan Graves 和遵守资产条款 | 可作为 atlas/ID 基础，但先验证图形风格与署名是否可接受 |
| [`bodychart_heatmap`](https://pub.dev/packages/bodychart_heatmap) / [`Obaloluwa-Obidoyin/bodychart_heatmap`](https://github.com/Obaloluwa-Obidoyin/bodychart_heatmap) | `1.1.0`；`BodyHeatmap`/`BodyChart`、前后/整体 body chart、强度等级、SVG 依赖 | 仓库声明 MIT；公开 API 的部位较粗（chest/back/arm/leg 等），不保证参考图的逐肌群分离或纹理 | 只作快速基线/对照，除非截图证明能满足逐肌群门槛，否则淘汰或只借鉴接口 |
| [`muscle_map`](https://pub.dev/packages/muscle_map) / [`SquashVash/muscle-map`](https://github.com/SquashVash/muscle-map) | `1.3.0`；前/后/全身 SVG、点选和 light/medium/hard intensity map | 仓库声明 MIT；需检查 SVG 来源和具体边界，存在拼写/分组兼容风险 | 可作对照或部分复用，但必须过路径级视觉截图验收 |
| [`rive_muscle_heatmap`](https://pub.dev/packages/rive_muscle_heatmap) / [`jorgeg922/rive_muscle_heatmap`](https://github.com/jorgeg922/rive_muscle_heatmap) | `0.2.0`；Rive 交互和布尔高亮；免费 bundled asset | Dart 代码 MIT；资产有单独许可；免费资源是 male front、18 muscles，back/intensity/tap 依赖付费 pack，不能假设满足目标 | 仅用于评估 Rive 管线；除非明确取得所需资产许可，否则不作为本项目最终热力图 |

低层辅助包如 `flutter_svg`、`path_drawing`、`xml`、`vector_graphics` 只能解决 SVG 解析/渲染，不能证明已经有符合参考图的解剖资产；它们不能替代候选资产审查，也不能为简单多边形开绿灯。
