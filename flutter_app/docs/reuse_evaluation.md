# 热力图复用方案评估记录

状态：**已完成阶段性选型；视觉仍需持续回归**
初始检索日期：2026-10-02（Asia/Singapore）
工程基线：Flutter 3.47.6 / Dart 3.13.5 / Android 优先

本文件是执行者接入任何第三方热力图包或解剖资产前的决策记录。候选项目的版本、许可证和仓库内容会变化；下次 Goal 必须在实际接入前重新打开来源并记录当前版本或完整 commit。`docs/reusable_projects_research.md` 保存了本次更完整的搜索报告。

## 当前结论

当前工程采用“部分复用基础解剖 SVG、热度和交互自研”的生产实现。参考 PNG 只用于只读比对，未进入 painter；热区使用项目维护的独立 cubic Path、纹理裁切、渐变、描边、引线和命中测试。

该选择尚未等同于像素级复刻通过：Android API 35 已有前视/后视和有数据截图，API 30 也有概览截图；API 35/API 30 均已有 `768x1664` 宽屏截图和 UIAutomator XML。仍未完成自动像素差异阈值、横屏和真机验证。后续视觉变更仍需复核 `docs/evidence/` 和本文件的许可证记录。

## 候选矩阵

| 候选 | 来源/版本（检索快照） | 路径和视图能力 | 许可证审计 | Dart 3 / 构建风险 | 当前决策 |
| --- | --- | --- | --- | --- | --- |
| `flutter_body_heatmap` | [pub.dev](https://pub.dev/packages/flutter_body_heatmap)，`0.1.0` | 纯 Flutter `CustomPainter`；男性/女性前后视；23 个区域；连续强度和点击回调；路径来自 `react-native-body-highlighter` | 代码 MIT；必须随集成保留上游 MIT 归因并核对发布包内路径来源 | pub metadata 为 Dart `^3.11.1`，与当前 SDK 相容性较好；仍需运行本工程 `pub get/analyze/test/apk` | **首个 Flutter 视觉 spike**；只有截图达到参考图粒度才可采用 |
| `muscle_mapper` | [pub.dev](https://pub.dev/packages/muscle_mapper)，`1.2.2` | 四视图、分组/子肌肉、SVG、BYOA、强度和 Path 命中 | 代码 MIT；advanced 图资产声明来自 Ryan Graves，需 CC BY 4.0 署名；minimal 资产需单独核对 | 依赖 `path_drawing 1.0.1`，其 SDK `<3.0.0` 与当前 Dart 3 冲突；需要 fork/替换解析器 | **条件采用**：优先复用架构或路径 ID，不得未经改造加入依赖 |
| `flutter_muscle_anatomy` | [pub.dev](https://pub.dev/packages/flutter_muscle_anatomy)，`1.2.8` | 男女前后视 SVG、左右肌肉、Path、CustomPainter、命中测试 | 仓库声明 BSD-3-Clause；集成前逐项核对 SVG/资产是否覆盖 | 依赖旧版 `path_drawing`；需验证或 fork | **条件评估**，先检查路径和资产许可 |
| `flutter_body_atlas` | [pub.dev](https://pub.dev/packages/flutter_body_atlas)，`0.2.1` | 高细节 SVG、稳定 ID、前后视、碎片级命中和动态着色 | 代码 BSD-3-Clause；内置图资产 CC BY 4.0，需 Ryan Graves 署名 | 依赖 `path_drawing 1.0.1`；需 fork/替换解析器 | **条件评估**，需接受 CC BY 归因 |
| `muscle_map_flutter` | [pub.dev](https://pub.dev/packages/muscle_map_flutter)，`0.1.0` | CustomPainter、男女前后视、约 23 个双侧表面、强度/选中/glow | 仓库声明 MIT；须审计 `assets/bodies` 的来源和许可证 | 新包，维护和 API 稳定性证据少 | **Flutter 基线候选**，必须先审查真实路径 |
| `bodychart_heatmap` | [pub.dev](https://pub.dev/packages/bodychart_heatmap)，`1.1.0` | 前后视和 intensity API，但主要是 chest/arm/leg 等大区 | 仓库声明 MIT；许可证不代表其路径能满足本项目视觉粒度 | 路径/功能粒度不足，不是主要 SDK 问题 | **拒绝作为最终资产**；仅可作数据流参考 |
| `muscle_map` | [pub.dev](https://pub.dev/packages/muscle_map)，`1.3.0` | 前后 SVG、选择和强度等级 | MIT 模板含 `[year] [fullname]` 占位符；SVG 来源需补审计 | `svg_path_parser` 旧 SDK `<3.0.0` 风险 | **拒绝直接依赖**，除非取得授权并修复依赖 |
| `rive_muscle_heatmap` | [pub.dev](https://pub.dev/packages/rive_muscle_heatmap)，`0.2.0` | Rive 交互；免费资产只有男性前视约 18 肌群 | 代码 MIT；`.riv` 资产有独立许可，禁止改色/重组，后视和高级能力需另行授权 | 需要单独 Rive 资产许可，无法满足本项目双视和纹理要求 | **拒绝最终方案** |
| `melihcolpan/MuscleMap` | [GitHub](https://github.com/melihcolpan/MuscleMap) | MIT Swift 四视图真实 Path 数据、路径缓存和色阶逻辑 | MIT；转换后的路径仍须保留许可证和改动记录 | 非 Flutter；需要离线转换脚本和 Flutter 命中层 | **路径数据候选**，可在 Flutter spike 失败时转移 |
| `vulovix/body-muscles` | [GitHub](https://github.com/vulovix/body-muscles) | Apache-2.0；70+ 前后视 SVG path、强度和选择数据 | 需保留 Apache LICENSE/NOTICE | 非 Flutter；需要转换和逐路径视觉审查 | **路径数据候选** |

## 接入前必须补齐的证据

- [ ] 记录实际采用候选的 pub.dev 版本或完整 commit、下载源和文件哈希。
- [ ] 记录代码许可证与每一个 SVG/Rive/纹理资产的独立许可证、署名和再分发要求。
- [ ] 以参考图坐标生成 360x780、412x915、768x1024 或等价目标尺寸的前视/后视截图。
- [ ] 对 `score=0`、`score=0.5`、`score=1` 验证热度只影响目标路径，纹理被真实 mask 裁切，左右区域不会串色。
- [ ] 为每个 `regionId` 记录 `muscleId`、view、side、path 来源、包围盒和命中测试结果。
- [ ] 运行 `flutter pub get`、`dart run build_runner build`、`flutter analyze`、`flutter test` 和 Android debug APK 构建。
- [ ] 将采用包的许可证和归因文本放到工程内的 `THIRD_PARTY_NOTICES` 或等价文件。
- [ ] 若候选被拒绝，写出路径粒度、视觉差异、SDK、维护或许可证的具体证据；没有证据的结论写“未验证”。

## 2026-10-03 实时复核与最终阶段性决策

研究 agent 在隔离目录 `E:\Project\temp-builds\heatmap-research` 重新下载并验证了候选。参考图 SHA-256 为
`2560FD42D4F97B347FA8DC957B174358993AD4CB4445AFB83EB275ABEB4CADD2`，尺寸为 `794 x 855`。当前环境为 Flutter `3.47.6` / Dart `3.13.5`。

| 候选 | 固定 pub archive SHA / Git HEAD | 实际复核 | 决策 |
| --- | --- | --- | --- |
| `flutter_body_atlas 0.2.1` | `3b8243c7d06c29699c9c3bb9f41deae5325496d4884c00e49ac8187ddaada57e` / `d28a0848218757f1d43025f3a98c28faa9b3fffe` | pub get、analyze、tests 通过；前 126/后 80 paths，真实 ID 和前后视完整。BSD-3-Clause 代码；SVG 是 Ryan Graves 的 CC BY 4.0，需署名。内置几何是扁平灰色，没有本项目参考图的纹理、渐变、引线和标签；依赖旧 `path_drawing`。 | 只作为 geometry/ID 参考，不直接依赖或复制资产。 |
| `flutter_body_heatmap 0.1.0` | `fab77295cee4a9f65967e825957be7dac116598f11e263e4d4d1ba4bbaf65d94` / `c6ee0398e1afff915305b48a50e96a495d3ed738` | pub get/analyze 通过；自带两个 golden 均失败（约 468 px 差异）。MIT 代码与上游 `react-native-body-highlighter` MIT 路径；缺标签、引线、斜纹和目标插画风格。 | 仅作 Canvas 架构参考，拒绝最终视觉。 |
| `flutter_muscle_anatomy 1.2.8` | `43d4c0fcb3e7b82e5dd62d79e4dbbe59b13d587b4f0b681d5abb38626e22d46` / `6213a238dec891c5375187a3311c19156895c234` | 前后 SVG/Path 测试通过；BSD-3-Clause 代码，但存档未找到覆盖所有 SVG 的独立艺术授权；依赖旧 path parser。 | 未确认艺术资产授权，拒绝直接纳入。 |
| `muscle_mapper 1.2.2` | `d2d7272709469b713db3b2d1e3c2ce545ac02a1a9e1365366bafa6e157cd57ee` / `8350dcaea79cd1013a140887ae4f0a3369bc4b73` | advanced 资产前后 126/80 paths，MIT 代码 + CC BY 4.0 资产；BYOA/hit-test 有价值，但旧 path_drawing、只有男体 advanced 资产，默认风格不匹配。 | 只借鉴 ID/adapter 结构，拒绝直接依赖。 |
| `muscle_map_flutter 0.1.0` | `6714837ad80178d9c66b9de16b76c356c619dc67ca20109cb35884b36d0a6b18` / `f316a3edba48fc907f29f7cdca1ec6adbee26a99` | 38 surface IDs、前后视 CustomPainter；MIT，但新项目无 tests，艺术来源未单独声明。 | 仅作 surface ID 对照，拒绝复制资产。 |
| `bodychart_heatmap 1.1.0` | archive `f19d55cf0f54b4d110dc285de1cbca0e2741212c4766b725a6e4170808570b2d` | pub get 可解析；区域仅 neck/chest/arm/leg/butt/back/abs，粒度不满足独立肌群门槛。 | 拒绝最终方案。 |
| `muscle_map 1.3.0` | archive `122fd73e559ed8a693be59890baa1d8515419b0706307e4a0a6e6e065a6e81d2` | LICENSE 仍是 `[year] [fullname]` 模板；`svg_path_parser` SDK `<3.0.0`。 | 许可证和 SDK 风险阻塞，拒绝。 |
| `rive_muscle_heatmap 0.2.0` | archive `f6d3536100021dfe55a56879487d974555c3188cb5d899b7b8ad90409313ed91` / `de1a1b6f349171391b7f1ba423963c529dcbbc94` | MIT Dart 代码；免费 `.riv` 是男性前视 18 肌群，资产许可禁止改色/重组，后视和动态热度需付费资产。 | 许可和覆盖范围不满足，硬性拒绝。 |

### 阶段性选择

本工程采用**部分复用后自研 adapter**：固定 `flutter_body_atlas 0.2.1` 的前/后 SVG 作为基础解剖层，并在 `lib/features/heatmap/domain/muscle_region.dart` 保留项目自己的稳定 region ID、命中路径和 `1000 x 1080` 设计坐标；热度、渐变、高光、斜纹、描边、标签和引线完全由本项目 `CustomPainter` 控制。候选 package 没有作为 Dart 依赖加入，避免旧 `path_drawing` SDK 约束。参考 PNG 只保留在 `assets/reference` 作为只读比对资料，没有被生产 painter 加载。

该选择承担独立的 BSD-3-Clause 代码和 CC BY 4.0 艺术资产署名义务，完整文本和固定版本证据见 `docs/THIRD_PARTY_NOTICES.md`。窄屏、API 30 和 `768x1664` 宽屏截图已经归档；仍需完成参考图的逐项像素对比、横屏和真机验证。若路径、比例或标签不达标，不能把 adapter 视为最终视觉通过。

## 决策记录模板

```text
项目：
来源：
检索日期：
版本/commit：
代码许可证：
艺术资产许可证：
覆盖范围：
采用方式：直接使用 / adapter / 转换路径 / 部分采用 / 拒绝
修改：
归因和再分发义务：
SDK/构建结果：
视觉截图和 Golden 结果：
路径审计结果：
决策：
拒绝或未解决风险：
```
