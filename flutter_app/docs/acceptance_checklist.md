# 验收清单

## 基线命令

- [x] `flutter pub get`
- [x] `flutter analyze`
- [x] `flutter test`
- [x] `flutter build apk --debug`（无特殊字符隔离目录证据）
- [x] API 35 模拟器启动并截屏

## 热力图视觉

- [x] 参考图资源可加载，画布保持固定比例
- [x] 深色面板、前后视并排构图、热度色带、选中描边
- [x] 真实 SVG/CustomPainter 基础人体，前后 SVG 分别 126/80 paths
- [x] 独立肌群路径、稳定 ID、纹理 mask、描边、引线、端点、命中测试和无障碍语义
- [x] 日期范围参与聚合，空分数状态有测试和语义
- [x] 窄屏与 768x1664 宽屏截图及人工差异记录
- [x] `docs/reuse_evaluation.md` 记录候选版本、哈希、代码/艺术许可证、署名和淘汰理由
- [ ] 同构像素/SSIM 阈值（environment-limited：参考图与生产面板没有同尺寸、同坐标、同状态基线）
- [ ] 真机/横屏完整矩阵（environment-limited）

## 健身记录

- [x] 动作模板创建、编辑、删除确认、排序逻辑、设置/JSON 持久化测试
- [x] 训练记录支持组数、次数、重量、单位、完成状态、RPE、session 备注和组备注
- [x] Hive 重启读回证据
- [x] 历史日期/动作/肌群筛选、总训练量、PR 规则/展示/空态和体重趋势
- [x] JSON/CSV 导出；JSON 导入 schema 校验和跨 repository 原子替换/回滚
- [x] 分享失败/取消、通知不可用和权限拒绝 fallback
- [ ] 用户选择历史空日期范围的真实设备流程（environment-limited：Material picker 自动化无法可靠导航历史月份）
- [ ] 真实模板拖拽改变顺序后强停重启（environment-limited：已有坐标/语义拖拽尝试未改变顺序）
- [ ] 完整分享 chooser 多版本目标应用矩阵（environment-limited：设备无目标分享应用）

## 质量门槛

- [x] `dart run build_runner build` 无意外生成差异
- [x] 关键 repository、聚合器、PR、表单校验、路径命中和 Widget 语义测试
- [x] 未使用破坏性 Git 命令覆盖用户改动

## 阶段 5 收敛结论

- 状态：部分完成（代码 gate 通过，设备/同构视觉限制未验证）
- P0/P1：无
- P2：同构像素/SSIM、历史空日期范围设备流程、真实拖拽重启、真机/横屏、分享 chooser 矩阵
- P3：中文字体运行时碰撞、更完整设备错误/加载/表单回归
- `code-closed`：日期范围代码、模板排序持久化、真实 SVG/Path、PR、组备注、原子 JSON 导入、fallback
- `device-verified`：API30/API35 概览与热力图、前后视、空数据库空态、768x1664 宽屏、已选日期范围、lb 和 Hive 重启
- `environment-limited`：系统 picker 历史空范围、真实长按拖拽、同构像素阈值、真机/横屏、多分享目标矩阵

## 2026-10-03 恢复收敛复核

- 代码 gate：`dart format --set-exit-if-changed lib test` 通过（47 files, 0 changed）；`flutter analyze` 通过；`flutter test --concurrency=1` 通过（40 tests）；`dart run build_runner build` 通过（0 outputs changed）。
- Android 构建证据：`E:\Project\temp-builds\fitness_record_app_verify_20261003_pathgate\build\app\outputs\flutter-apk\app-debug.apk` 存在，158895317 bytes。
- 热力图静态审查：`assets/heatmap/body_front.svg`、`body_back.svg` 分别为 126/80 path；生产 painter 使用独立 Path、SVG 图层、纹理 clipPath、渐变、描边、引线和命中测试；参考 PNG 未作为生产主体；不宣称像素级复刻。
- 分类未改变：`code-closed` 为日期范围聚合/空态逻辑、模板排序持久化、PR、组备注、原子 JSON 导入、fallback 和真实 SVG/Path；`device-verified` 为 API30/API35、前后视、768x1664、选定日期范围、lb、Hive 重启；`environment-limited` 为用户选择历史空日期范围、真实拖拽重启、同构像素/SSIM、真机/横屏和多目标分享 chooser。
- 阶段 5：部分完成（代码 gate 通过，设备/同构视觉限制未验证）；P0/P1 无。

## 2026-10-04 最终恢复 gate

- 启动资料和目标文件均存在并已读取。
- `flutter analyze`：通过，No issues found。
- `flutter test --concurrency=1`：通过，40 tests passed。
- `dart format --set-exit-if-changed lib test`：通过，47 files unchanged。
- `dart run build_runner build`：通过，0 outputs changed。
- 阶段 5 仍为部分完成（代码 gate 通过，设备/同构视觉限制未验证）；P0/P1 无，环境限制项不变。

## 2026-10-04 代码收敛与 SVG 分层 gate

- 日期范围空状态：使用非空仓库和历史训练 + 选定无训练日期范围的 Widget 回归验证空状态文案、语义标签和日期按钮；归类为 `code-closed`。Material picker 历史月份设备操作仍为 `environment-limited`。
- 模板排序：通过生产 `ReorderableDragStartListener` 的 Widget 拖拽测试，验证两个模板顺序改变、SharedPreferences 写入和重建 App 后顺序保持；归类为 `code-closed`。adb 坐标手势不再重复。
- 热力图分层：真实 SVG 是唯一默认人体视觉主体；SVG alpha `ShaderMask` 提供纹理裁切；旧 cubic Path 只用于命中、语义和选中描边。API35 最新截图为 `docs/evidence/android_api35_heatmap_svg_final.png`。
- 最新源代码 gate：`dart format --set-exit-if-changed lib test` 通过（47 files unchanged）；`flutter analyze` 通过；`flutter test --concurrency=1` 通过（42 tests）；隔离路径 `E:\Project\temp-builds\fitness_record_app_verify_final_2\build\app\outputs\flutter-apk\app-debug.apk` 构建通过。
- 阶段 5 结论仍为：部分完成（代码 gate 通过，设备/同构视觉限制未验证）；P0/P1 无。剩余 P2 为正式同构像素/SSIM、真机/横屏和完整分享目标矩阵；这些项目没有当前环境所需输入，不能通过重复 adb 完成。

## 2026-10-04 最终收敛 gate

- `dart format --set-exit-if-changed lib test`：通过，47 files unchanged。
- `flutter analyze`：通过，No issues found。
- `flutter test --concurrency=1`：通过，43 tests passed；包含日期范围空态、picker 取消、真实模板 Widget 拖拽/重建、SVG 分层和标签矩形测试。
- `dart run build_runner build`：通过，0 outputs changed。
- 最新隔离 APK：`E:\Project\temp-builds\fitness_record_app_verify_20261004_final\build\app\outputs\flutter-apk\app-debug.apk`，`flutter pub get` 与 `flutter build apk --debug` 均通过，大小 158912509 bytes。
- 文档一致性：`docs/architecture.md`、`ENGINEERING_PACKAGE.md` 和热力图视觉差异记录均明确真实 SVG 为生产主体，参考 PNG 不参与生产渲染。
- 阶段 5 仍为部分完成（代码 gate 通过，设备/同构视觉限制未验证）；P0/P1 无。没有新外部证据时停止自动循环。
