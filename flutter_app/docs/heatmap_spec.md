# 肌肉热力图实现规范

## 1. 目标与边界

本规范定义健身记录 App 的“肌肉热力图”模块，用于展示训练后各肌群的刺激/训练量分布，并在前视、后视两个人体区域上复刻参考图的视觉效果。实现必须离线可用，不依赖图片识别、远程接口或云端渲染；训练记录、肌群得分和热力图配置全部来自本地数据。

参考图的核心特征如下：

- 深色蓝黑背景，四周有细描边和 8 px 左右圆角外框。
- 同一画布内并排放置正面和背面人体半身/全身矢量插画。
- 人体主体为深灰蓝色，肌肉区域使用橙、黄、红的热力渐变；未训练区域保留蓝灰冷色。
- 每个重点肌群有白色小圆点、细白色引导线和浅灰标签，标签避让人体轮廓。
- 肌肉纹理沿肌纤维方向绘制，热区中央更亮，边缘保留橙红过渡。
- 正面至少标注 `Deltoids`、`Abs`、`Quadriceps`；背面至少标注 `Trapezius`、`Lats`、`Glutes`、`Hamstrings`。

本模块不负责训练计划推荐、动作识别或 3D 姿态；它只接收已聚合的肌群分数并负责渲染、交互、动效和可访问性。

## 2. 页面与布局契约

### 2.1 画布结构

使用一个可缩放的 `MuscleHeatmapView`，内部坐标固定为 `1000 x 1080` logical units（以下简称设计坐标）。所有人体路径、引导线和标签都以该坐标记录，运行时通过统一 `FittedBox`/矩阵缩放到可用区域，禁止给每个路径单独按屏幕宽度计算比例。

```text
设计坐标：0 <= x <= 1000，0 <= y <= 1080
外框：Rect.fromLTWH(4, 4, 992, 1072)，圆角半径 28
内边距：左右 28，上 24，下 24
前视人体包围盒：x=55..440，y=32..1035
后视人体包围盒：x=558..945，y=32..1035
前/后视中心：约 x=248、752
```

在宽屏设备上保持左右并排；在宽度不足时仍保持整张画布的宽高比，不将前后视拆为两页，也不改变标注的相对位置。推荐 `AspectRatio(aspectRatio: 1000 / 1080)`，最小可交互尺寸为 280 x 302；低于该尺寸时只缩放而不隐藏肌群路径和触点。

### 2.2 背景与层级

从底到顶固定为以下绘制层，层顺序是验收的一部分：

1. 背景：`#11131D` 到 `#1B1D29` 的低对比纵向渐变，禁止明显彩色光晕。
2. 外框：`#3A3F52`，1.5 logical px，圆角 28；外框与画布边缘保持 4 px。
3. 人体基础层：深蓝灰填充 `#3C465D`，轮廓 `#202533`，轮廓宽 2.5。
4. 非热区阴影/肌肉分隔：`#52647D`、`#283044`，透明度 0.65 到 0.9。
5. 肌群热力层：按肌群分数绘制，路径不能超出该肌群 mask。
6. 肌纤维纹理：细线或弧线，使用热区颜色的亮色变体，透明度 0.25 到 0.55。
7. 引导线和目标点：线宽 1.2，颜色 `#D9DCE5`；目标点直径 7，填充 `#F2F4F8`。
8. 标签文字：`#C9CBD5`，默认 20 design px，字重 400；深色背景上满足 WCAG AA 对比度。

不使用外部位图作为人体主体。允许将参考图作为设计审核对照图，但生产渲染必须是可缩放矢量路径或等价的 `CustomPainter` 绘制。

## 3. 人体资产与矢量实现

### 3.1 推荐方案：SVG 路径资产 + Flutter 统一着色

将前视和后视各拆成一个 SVG 资产，放在 `assets/muscle_map/front.svg`、`assets/muscle_map/back.svg`。SVG 只包含稳定的轮廓、基础人体和肌群路径，不包含训练数据、标签和背景。每个肌群必须使用稳定、可读的 `id`，例如：

```xml
<path id="front_deltoids_left" d="..."/>
<path id="front_deltoids_right" d="..."/>
<path id="front_abs_upper" d="..."/>
<path id="front_quadriceps_left" d="..."/>
<path id="back_trapezius" d="..."/>
<path id="back_lats_left" d="..."/>
<path id="back_glutes_right" d="..."/>
<path id="back_hamstrings_left" d="..."/>
```

Flutter 端在构建时将 SVG 路径解析为 `Path` 并缓存；不要在每一帧重新解析 XML。可用 `path_parsing`/`flutter_svg` 等本地包，或者将 SVG 路径在构建阶段转为 Dart 常量。渲染层仍由一个 `CustomPainter` 负责，以便将路径、mask、纹理、触点和标签放入同一坐标系。

### 3.2 备选方案：纯 `CustomPainter`

如果不希望引入 SVG 依赖，使用 `Path.moveTo/lineTo/cubicTo/quadraticBezierTo/close` 直接保存同一组路径。路径数据必须放在独立的 `front_paths.dart`、`back_paths.dart` 常量文件，不能散落在 painter 中。每条路径的 key、视图面、肌群、左右侧和包围盒仍须遵循第 4 节模型。

### 3.3 路径制作与质量要求

- 以参考图为底图进行人工描摹或在矢量编辑器中绘制，导出前视、后视各一张等比例 SVG。
- 路径数量可以先按主要肌群拆分，再逐步补齐小肌群；首个可用版本至少覆盖 12 个交互热区（左右三角肌、胸大肌、腹直肌、背阔肌、斜方肌、臀大肌、股四头肌、腘绳肌、小腿）。
- 左右对称路径优先通过水平镜像生成，确认肩胛、手臂和腿部轮廓后再做小幅人工修正，避免左右热区漂移。
- 每个肌群路径必须闭合、无自交、无零面积子路径；路径填充规则统一为 `nonZero`。
- 基础人体轮廓和热区路径分离，热区不能依赖绘制顺序“盖住”轮廓来形成边界；需要 clipping 时显式使用 `saveLayer + clipPath`。
- 允许在热区上额外绘制纹理，但纹理必须被该肌群路径裁切，且不影响点击命中。

## 4. 肌群目录、区域与数据模型

### 4.1 肌群 ID

`MuscleId` 使用稳定字符串（建议 Dart `enum` + `jsonValue`），首版目录如下：

| id | 中文名 | 视图 | 参考标签 | 默认热点 |
| --- | --- | --- | --- | --- |
| `deltoids` | 三角肌 | front | Deltoids | 肩部左右 |
| `chest` | 胸大肌 | front | Chest（可选） | 胸部左右 |
| `abs` | 腹直肌 | front | Abs | 腹部中线 |
| `obliques` | 腹外斜肌 | front | Obliques（可选） | 腰侧 |
| `quadriceps` | 股四头肌 | front | Quadriceps | 大腿前侧左右 |
| `calves` | 小腿 | front/back | Calves（可选） | 小腿 |
| `trapezius` | 斜方肌 | back | Trapezius | 颈肩至肩胛 |
| `lats` | 背阔肌 | back | Lats | 腋下至腰 |
| `rear_deltoids` | 后束三角肌 | back | Rear deltoids（可选） | 后肩 |
| `glutes` | 臀大肌 | back | Glutes | 臀部左右 |
| `hamstrings` | 腘绳肌 | back | Hamstrings | 大腿后侧左右 |

同一肌群的左右路径共享一个 `muscleId`，但每一侧保留独立 `regionId`，以便呈现左右差异和单独命中。

### 4.2 Dart 数据结构

模型可用 Freezed + json_serializable 生成，结构应等价于：

```dart
enum MuscleView { front, back }

@freezed
class MuscleRegion with _$MuscleRegion {
  const factory MuscleRegion({
    required String regionId,       // 如 front_deltoids_left
    required String muscleId,       // 如 deltoids
    required MuscleView view,
    required String pathKey,        // 与 SVG id 或 Path 常量一致
    required Rect designBounds,
    @Default(0.0) double score,
    @Default(0.0) double volume,
    @Default([]) List<String> aliases,
  }) = _MuscleRegion;
}

@freezed
class MuscleHeatmapState with _$MuscleHeatmapState {
  const factory MuscleHeatmapState({
    required DateTimeRange range,
    required Map<String, double> scoreByMuscle,
    @Default({}) Map<String, double> scoreByRegion,
    @Default(MuscleView.front) MuscleView focusedView,
    String? selectedRegionId,
    @Default(true) bool showLabels,
    @Default(true) bool animateChanges,
  }) = _MuscleHeatmapState;
}
```

`score` 统一归一化到 `[0, 1]`。聚合层可以从训练记录计算总组数、负重、RPE 或动作权重，但 painter 只读取最终分数，不读取 Hive 对象。没有记录的肌群为 `0`，不得使用 `null` 代表“未加载”和“零训练”；加载状态由 Riverpod provider 单独表达。

建议的离线聚合公式：

```text
raw = Σ (有效组数 × 动作肌群系数 × 强度系数 × 新鲜度衰减)
score = clamp(raw / max(历史窗口内 P95(raw), 1), 0, 1)
```

默认新鲜度衰减按 28 天半衰期计算；单位切换 kg/lb 不应改变 score。导出 JSON 时保存原始训练记录与聚合参数，保证同一数据可复算。

## 5. 热力颜色映射

### 5.1 基础色带

采用离散节点 + 线性插值，避免纯红色覆盖全部热区：

| score 区间/节点 | 颜色 | 用途 |
| --- | --- | --- |
| 0.00 | `#53677F` | 无训练/冷区 |
| 0.15 | `#3D8EAA` | 轻微刺激，保留蓝灰 |
| 0.35 | `#F39A3D` | 中等刺激，橙色 |
| 0.60 | `#FFB52E` | 较高刺激，黄橙色 |
| 0.80 | `#FF6A24` | 高刺激，橙红色 |
| 1.00 | `#E92F2F` | 峰值，红色 |

插值在 `HSL` 或 `HSV` 中进行，但最终颜色必须转换为 sRGB `Color`；禁止在透明度上编码 score。区域填充不透明度保持 0.92 左右，纹理和阴影单独控制。

### 5.2 肌群内高光与纹理

每个热区使用两层填充：

1. 基色：由 `score` 映射出的颜色。
2. 局部高光：从肌群路径包围盒中心向外的径向渐变，中心颜色为基色亮度 + 12%，边缘颜色为基色亮度 - 8%，透明度 0.7 到 0.95。

肌纤维纹理沿肌群主轴绘制：胸/三角肌使用横向弧线，腹直肌使用近似水平短线，背阔肌使用从肩胛向腰的斜线，股四头肌和腘绳肌使用纵向弧线，臀大肌使用扇形弧线。纹理数量按路径包围盒短边决定（建议每 10 design px 一条，限制在 5 到 22 条）；纹理线宽 1.0 到 1.8，圆端点，透明度 0.25 到 0.48。纹理必须 deterministic，不使用随机数。

### 5.3 低分与缺失状态

- `score == 0`：使用冷色基础填充，不显示高光，只保留非常淡的纹理。
- `0 < score < 0.15`：允许轻微蓝青高光，但不出现橙色标签。
- provider 加载中：人体显示基础层和 skeleton shimmer，禁止将加载态误显示为高热。
- 数据错误：热图维持基础层，顶部显示可重试的本地错误状态；不要绘制半成品渐变。

## 6. 绘制与交互实现

### 6.1 Painter 结构

`MuscleHeatmapPainter extends CustomPainter` 接收不可变的 `HeatmapRenderData`，至少包含：路径表、当前颜色表、纹理参数、标签布局、选中 region、动画进度。`paint(Canvas canvas, Size size)` 内先将设计坐标矩阵映射到实际尺寸，再按第 2.2 节层级绘制。

用 `shouldRepaint` 比较 render data 的值对象版本或 hash；路径解析和静态标签布局不应因 score 更新而重复计算。Riverpod provider 负责把训练聚合结果转换成 render data，Widget 只负责手势和无障碍语义。

### 6.2 命中测试

- 使用同一设计坐标矩阵把触点转换回设计坐标。
- 按 z-order 对 region 路径执行 `Path.contains`，先检测选中/热区，再检测人体基础层。
- 手指半径小于 24 dp 时，为每个 region 额外维护一个不可见的命中扩展路径（`PathMetrics` 或包围盒膨胀 12 design px），但视觉路径不能膨胀。
- 点击热区后：该区域描边变为 `#FFFFFF`，外加 2 px 的低透明度光晕；底部或旁侧显示肌群名、score、最近训练量和最后训练日期。
- 长按/键盘聚焦显示 tooltip；点击画布空白处取消选择。

### 6.3 视图切换、缩放和滚动

默认前后视同时可见，保持参考图的并排构图。提供 `前视 / 后视 / 双视` segmented control 作为可选辅助入口；切换不销毁 painter 状态。双指缩放限制在 `1.0..2.5x`，平移边界不能露出画布外的透明区域；桌面/平板支持鼠标滚轮缩放和拖拽。手机窄屏默认不自动旋转，也不强制横屏。

### 6.4 动画

- 首次进入：人体基础层先出现（120 ms），热区颜色从 score 0 插值到目标（360 ms，`Curves.easeOutCubic`），纹理和标签随后淡入（160 ms）。
- 数据刷新：仅对 score 变化的 region 做颜色/高光插值，默认 280 ms；不闪烁未变化区域。
- 选中态：描边和光晕 180 ms，取消选中 120 ms。
- 动画由 `AnimationController` 或 `flutter_animate` 驱动，但 painter 的 `animationValue` 必须可测试；`disableAnimations` 或系统“减少动态效果”时直接跳到终态。
- 不使用持续循环的呼吸/闪烁动画，避免干扰读数和耗电。

## 7. 标签与引导线布局

标签布局是静态设计资产，不能每帧自动漂移。每个标签记录 `anchor`（肌肉热点）、`elbow`（折点）和 `textOrigin`（文字起点），全部使用设计坐标：

| 标签 | anchor 约值 | textOrigin 约值 | 对齐 |
| --- | --- | --- | --- |
| Deltoids | (307, 227) | (375, 155) | left |
| Abs | (270, 390) | (350, 505) | left |
| Quadriceps | (285, 710) | (365, 745) | left |
| Trapezius | (688, 235) | (584, 155) | right |
| Lats | (692, 365) | (552, 308) | right |
| Glutes | (735, 575) | (570, 668) | right |
| Hamstrings | (725, 718) | (548, 735) | right |

以上数值是初始布局，最终以 SVG 资产和截图对齐为准。引导线至少包含一段水平线和一个 45° 左右折点，避免穿过其他标签或人体脸部。文字超出内框时优先向内收缩标签位置，不允许裁剪。支持中英文显示时，中文标签使用同等视觉高度的字体，标签宽度由实际字形测量得到。

## 8. Riverpod、Hive 与备份边界

建议 provider 分层：

```text
exerciseLogRepositoryProvider
  -> muscleAggregationProvider(dateRange)
  -> muscleHeatmapStateProvider
  -> heatmapRenderDataProvider
```

Hive 只保存训练记录、动作到肌群的映射、用户偏好和聚合缓存；不要把 `Path`、`Color` 或渲染对象写入 Hive。SVG/Dart 路径是应用版本资产，若未来更新资产，需保持旧 `pathKey` 的兼容映射。导出 JSON 至少包含：schemaVersion、记录时间、动作、组数、负重、RPE、目标肌群及系数、聚合窗口和 score。

## 9. 无障碍与性能

- 为每个可交互 region 提供语义节点：`三角肌，正面左侧，训练刺激 0.72`，而不是只读颜色。
- 颜色不是唯一信息；选中态必须有描边、文本和数值。高对比模式下使用更亮的轮廓和标签。
- 目标设备为中端 Android，60 fps 为验收目标。静态路径、mask、纹理参数和文字布局都应缓存；避免在 `paint` 中分配大量对象。
- 画布从屏幕移出时暂停动画 ticker；页面销毁时释放 controller。
- 首屏热图（冷启动后首次显示）在中端 Android 上目标小于 500 ms；单次 score 更新不超过 16 ms 的绘制预算。

## 10. 验收标准

### 10.1 视觉验收

- 在 360 x 780、412 x 915、768 x 1024 和 1080 x 1920 视口截图中，前后视人体比例和相对位置与参考图误差不超过 2% 设计坐标。
- 外框、背景、人体轮廓、主要肌群路径、标签和引导线全部可见；无裁切、重叠或文字出框。
- 热力色带节点的 sRGB 值与第 5.1 节一致，`score=0/0.5/1` 三张基准图可做像素差回归。
- 各肌群纹理方向符合第 5.2 节，且纹理不越过肌群边界。
- 参考标签至少出现并位置稳定：Deltoids、Abs、Quadriceps、Trapezius、Lats、Glutes、Hamstrings。

### 10.2 交互验收

- 点击或触摸每个主要肌群，在 24 dp 触点容错内都能选中正确 region；左右区域不能误选。
- 选中后展示肌群名称、当前 score 和最近训练信息，点击空白取消。
- 前视/后视/双视切换、缩放、平移在 Android 真机可用；不出现透明空洞或状态丢失。
- 从 Hive 更新训练记录后，只更新受影响肌群颜色，动画在 280 ms 左右完成；关闭系统动画后立即到终态。

### 10.3 数据与可靠性验收

- 无训练数据、单侧训练、极端大值、空记录和损坏记录均有确定性显示，不崩溃。
- 同一 JSON 在不同设备和 kg/lb 单位下得到相同 score 与颜色。
- 导出并重新导入后，训练记录和热图结果一致；schemaVersion 可迁移。
- 离线启动、无权限、Hive 首次初始化失败时，热图仍能显示基础人体并给出可理解的错误状态。

### 10.4 自动化测试建议

- `HeatmapColorScaleTest`：验证节点颜色、边界 clamp 和单调亮度变化。
- `MuscleAggregationTest`：验证组数、系数、衰减、P95 归一化和单位不变性。
- `MuscleHitTest`：为每个 region 取路径内部/边缘/外部点，验证命中和左右隔离。
- `HeatmapGoldenTest`：固定 1000 x 1080 设计尺寸，生成冷/中/高三套 golden；阈值按平台字体差异单独配置。
- `SemanticsTest`：验证所有主要肌群有中文/英文名称、面板值和可聚焦节点。

## 11. 实施顺序

1. 锁定 1000 x 1080 坐标系，完成前/后 SVG 路径和 `pathKey` 清单。
2. 写路径加载器、静态人体层和基础 painter，先用固定 mock score 对齐参考截图。
3. 接入颜色映射、局部高光和确定性纹理，完成 golden 基线。
4. 接入 Freezed 模型、Riverpod 聚合 provider 和 Hive 读取，补齐空态/错误态。
5. 增加命中测试、标签详情、缩放和前后视切换。
6. 加入首次进入/数据刷新动效、无障碍语义和导出回归。
7. 在四种目标尺寸和至少一台中端 Android 真机上执行第 10 节验收，记录截图和性能指标。
