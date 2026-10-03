# Flutter 健身记录 App 环境与工程骨架

本文档记录本工作区在 **2026-10-02（Asia/Singapore）** 的 Flutter/Android 工具链检查结果，并给出后续自动推进时应遵循的工程边界。文档只描述环境和骨架，不替代具体功能实现。

## 1. 检查结果

检查工作区：以当前 `main_builder` 工程根目录为准；不要依赖生成该压缩包时的父目录路径。

| 项目 | 结果 |
| --- | --- |
| Flutter | **3.47.6 stable**，framework revision `5fc346839b`，engine revision `692136cb65` |
| Dart | **3.13.5 stable**，Windows x64 |
| DevTools | 2.60.0 |
| Windows | Windows 11 25H2（build 26200.9457） |
| Android SDK | **36.0.0**，位置 `C:\AndroidSdk` |
| Android platform/build-tools | `android-37.0` / `36.0.0` |
| Java | Microsoft OpenJDK **17.0.20.1** |
| ADB | 可执行，来自 `C:\Users\Wang Nathan\AppData\Local\Android\Sdk\platform-tools\adb.exe` |
| Android licenses | 已全部接受 |
| Android emulator | `IronLog_API30`、`IronLog_API35` |
| 当前连接设备 | 只有 Windows desktop；没有已连接的 Android 设备 |
| Git | 2.55.0.5；当前仓库尚未有 commit |

Android 端构建前置条件已经满足。`flutter doctor -v` 的两个提示不阻塞 Android：

1. Flutter/Dart 的 PATH 解析到 Scoop 的版本目录 `...\flutter\3.47.6\bin`，而 Flutter 当前 checkout 是 `...\flutter\current`。如果后续同时安装多个版本，应将 `...\flutter\current\bin` 放在 PATH 前面，并用下面的命令确认最终解析路径；Scoop 单版本场景下可暂时保留现状。
2. 找不到 Chrome，只影响 Flutter Web 调试，不影响本项目的 Android APK/AAB。

可复查环境的命令：

```powershell
flutter --version
dart --version
flutter doctor -v
flutter config --list
flutter devices
flutter emulators
adb devices
Get-Command flutter, dart, adb, java | Format-List Name, Source, Version
```

启动已有模拟器并运行 Android：

```powershell
flutter emulators --launch IronLog_API35
flutter devices
flutter run -d <android-device-id>
```

建议首选 API 35 模拟器进行日常验证；API 30 用于检查低版本兼容性。不要把 Windows desktop 运行结果当作 Android 验收结果。

## 2. SDK 与版本约束

当前 `pubspec.yaml` 可以使用以下约束：

```yaml
environment:
  sdk: ">=3.13.5 <4.0.0"
  flutter: ">=3.47.6"
```

Flutter 项目通常无法在 `pubspec.yaml` 中强制 SDK 的精确 patch 版本；需要可复现的 Flutter 版本时，在开发机/CI 中固定 Flutter 3.47.6（例如用 FVM 或固定 Scoop 版本），并在 CI 运行 `flutter --version` 记录实际版本。

以下版本是 2026-10-02 查询 pub.dev 得到、且与 Dart 3.13.5 / Flutter 3.47.6 的 SDK 约束相容的起始值。小版本升级应通过 `flutter pub outdated` 审核后再提交 `pubspec.lock`。

| 用途 | 包 | 建议约束 | 备注 |
| --- | --- | --- | --- |
| 状态管理 | `flutter_riverpod` | `^3.4.3` | Riverpod 运行时 |
| Riverpod 注解 | `riverpod_annotation` | `^4.0.7` | 与 generator 配套 |
| Riverpod 代码生成 | `riverpod_generator` | `^4.0.9`（dev） | 与 annotation 同步升级 |
| 代码生成 | `build_runner` | `^2.16.1`（dev） | 统一运行 Freezed/JSON/Riverpod 生成 |
| 模型注解 | `freezed_annotation` | `^3.1.0` | 不把生成文件手写进业务目录 |
| 模型生成 | `freezed` | `^4.0.2`（dev） | Dart 3.13 兼容 |
| JSON 注解 | `json_annotation` | `^4.12.0` | 备份/导入格式 |
| JSON 生成 | `json_serializable` | `^6.14.1`（dev） | 与 Freezed 配合 |
| 本地数据库 | `hive_ce` | `^2.20.1` | Hive API 的维护分支 |
| Flutter 数据库初始化 | `hive_ce_flutter` | `^2.4.0` | `path_provider` 集成 |
| 动效 | `flutter_animate` | `^4.5.2` | 页面/卡片/热力图过渡动画 |
| 图表 | `fl_chart` | `^1.2.0` | 训练量、体重、力量曲线 |
| 布局 | `gap` | `^3.0.1` | 可选，减少重复间距代码 |
| 路径 | `path_provider` | `^2.1.6` | 导出/备份文件位置 |
| 分享 | `share_plus` | `^13.3.1` | 分享 JSON/CSV 备份 |
| 权限 | `permission_handler` | `^13.0.2` | 仅在实际需要时申请权限 |
| 通知 | `flutter_local_notifications` | `^22.3.1` | 训练提醒/计划通知 |
| CSV | `csv` | `^8.0.0` | 表格导出 |
| ID | `uuid` | `^4.6.0` | 记录、模板、动作稳定 ID |
| 日期 | `intl` | `^0.20.3` | 本地化日期/单位格式 |
| SVG 资产 | `flutter_svg` | `^2.3.0`（可选） | 肌肉图使用 SVG 源文件时启用 |
| 路由 | `go_router` | `^18.0.2`（可选） | 多页面导航稳定后再加入 |

**Hive 选择说明：** 原始 `hive` 最新版是 `2.2.3`，其 SDK 约束为 `<3.0.0`，不能与当前 Dart 3.13.5 一起解析。`hive_ce` 保留 Hive 的主要 API，并提供 Dart 3 兼容的维护版本；本项目应优先使用 `hive_ce`，不要把原始 `hive: ^2.2.3` 放进当前 `pubspec.yaml`。如果未来改用 Isar，应作为一次独立的数据层迁移，不要在同一版本同时维护两套主数据库。

生成代码与基础检查命令：

```powershell
flutter pub get
dart run build_runner build
flutter analyze
flutter test
flutter build apk --debug
```

不要在每次开发启动时自动运行 `build_runner`; 生成文件有变化时手动运行，CI 再执行一次生成并检查工作区无未提交差异。

## 3. 推荐工程包骨架

功能按领域拆分，保持 UI、业务状态、持久化和导出边界清晰。下面是建议的 `lib/` 布局；目录可以随功能逐步创建，不要求一次生成所有空文件。

```text
lib/
  main.dart                         # 只负责 bootstrap 与 runApp
  app/
    app.dart                        # Material/Cupertino 主题与根路由
    router.dart                     # 页面路由（可先用 Navigator）
    theme/
      app_theme.dart
      app_colors.dart
      app_typography.dart
  core/
    constants/
    errors/
    localization/
    units/                           # kg/lb、cm/in 等单位转换
    utils/
  data/
    local/
      hive_boxes.dart                # box 名称、初始化、迁移版本
      adapters/                      # Freezed/Hive adapter 注册
    models/
      exercise.dart
      workout_session.dart
      workout_set.dart
      body_measurement.dart
      muscle_load.dart
    repositories/
      exercise_repository.dart
      workout_repository.dart
      measurement_repository.dart
  features/
    dashboard/
    workouts/                        # 训练中记录、组数、重量、计时
    templates/                       # 训练模板与动作库
    body/                            # 体重/围度/肌肉热力图
      presentation/
      domain/
    history/                          # 历史训练与筛选
    insights/                         # fl_chart 数据与趋势分析
    settings/                         # 单位、主题、通知、备份
  services/
    backup_service.dart              # JSON/CSV 导入导出
    notification_service.dart
  shared/
    widgets/
    formatters/
  muscle_map/
    domain/
      muscle_region.dart              # 语义 ID、视图、路径/命中区域
      muscle_activation.dart          # 训练量到颜色/强度的映射
    presentation/
      muscle_heatmap.dart             # 前/后视图切换与图例
      muscle_map_painter.dart         # 需要交互时使用 CustomPainter
    assets/
      body_front.svg                  # 设计/逆向得到的原始矢量资产
      body_back.svg
      body_regions.json               # region ID、标签、路径或命中框
```

Riverpod provider 按领域放在对应 `features/*/application` 或 `providers` 文件中；repository 只处理数据，不直接依赖 Widget。Freezed 模型的 `.freezed.dart`、`.g.dart` 由生成器维护，禁止手改。

## 4. 肌肉热力图的实现边界

截图中的前/后人体是固定比例的插画，带有深色背景、描边、渐变填充、标签引线和高亮区域。为了尽可能复刻效果，热力图不应通过普通图表库或运行时拼接多个圆形来绘制。建议流程如下：

1. 把逆向得到的前视/后视矢量素材作为唯一几何来源，保留原始 viewBox、路径顺序、描边宽度和渐变。素材放在 `assets/body_front.svg`、`assets/body_back.svg`；如果只有位图，先固定分辨率和裁切规则，再评估是否需要矢量化。
2. 给每个可训练肌群分配稳定语义 ID，例如 `chest`、`front_deltoid`、`biceps`、`abs`、`quads`、`trapezius`、`lats`、`glutes`、`hamstrings`。ID 与动作库/训练记录解耦，不能用屏幕坐标代替。
3. `MuscleRegion` 保存 `id`、视图（front/back）、原始路径或命中区域、标签锚点、默认色与高亮色。训练量先归一化为 0..1，再由固定色阶映射到填充颜色；相同训练数据在不同屏幕尺寸必须得到相同强度。
4. 静态展示优先使用 `flutter_svg`。需要点选、无障碍语义或动态局部高亮时，使用同一套路径数据驱动 `CustomPainter`/命中测试，禁止复制第二套几何数据。
5. 标签引线、字体大小、背景圆角和前/后间距属于视觉回归范围。把设计基准尺寸（例如截图宽高）记录在组件常量中，再通过 `FittedBox`/约束等比例缩放；不要让文字溢出或因热力值变化改变布局。
6. 用 golden test 或固定 viewport 截图比较前视/后视、无数据、低强度和高强度四种状态。热力图是核心验收项，不能只用 Widget 的存在性测试代替。

热力图数据流应保持单向：`WorkoutSet -> MuscleLoadCalculator -> muscleActivationProvider -> MuscleHeatmap`。`MuscleHeatmap` 不读取 Hive、不计算训练量，也不在 Widget 中硬编码动作到肌群的映射。

## 5. 缺失环境时的处理

如果另一台机器没有 Flutter/Dart：

1. 安装 Flutter stable 3.47.6（Windows 使用官方 SDK 或 Scoop；CI 使用固定版本缓存）。
2. 把 `<flutter-sdk>\bin` 放入 PATH，重新打开终端，并确认 `Get-Command flutter,dart` 指向同一 SDK。
3. 安装 Android Studio/Command-line Tools、Android SDK platform 36、build-tools 36.0.0、JDK 17；设置 `ANDROID_HOME`/`ANDROID_SDK_ROOT` 和 `JAVA_HOME`。
4. 运行 `flutter doctor --android-licenses` 接受许可，再运行 `flutter doctor -v`。
5. 创建或启动 Android API 35 模拟器，确认 `flutter devices` 能看到 Android 设备后再运行项目。

不要为解决 Chrome 缺失而安装 Web 依赖；本项目验收目标是 Android。若 `flutter doctor` 报 PATH 指向旧 SDK，先修正 PATH，再清理/重新获取依赖：

```powershell
flutter clean
flutter pub get
```

## 6. Android 构建约定

开发构建：

```powershell
flutter build apk --debug
```

发布构建：

```powershell
flutter build apk --release
flutter build appbundle --release
```

发布前必须在本机安全位置配置 keystore，不能把 `.jks`、密码或 `key.properties` 提交到仓库。通知权限、存储权限和 Android 版本差异应在真实 Android 设备/API 35 模拟器上验证；无权限时导出功能要给出可理解的错误状态。
