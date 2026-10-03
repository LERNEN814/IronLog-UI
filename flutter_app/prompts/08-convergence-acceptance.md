# 阶段 5 收敛验收提示词

当 `docs/phase_progress.md` 已显示阶段 5 为“部分完成”，或同一设备验证连续失败三次以上时，主 agent 必须使用本文件。目标是把现有工程收敛为可审计的交付状态，禁止无限重复设备操作。

## 执行顺序

1. 读取 `docs/phase_progress.md`、`docs/acceptance_checklist.md`、最近证据目录、实际代码和测试。把每个未验证项分类为：
   - `code-closed`：生产逻辑和单元/Widget 测试已经证明，业务项可关闭；
   - `device-verified`：有新的真实模拟器/设备证据；
   - `environment-limited`：当前环境缺少真机、目标分享应用、可操作的系统控件或同构视觉基线；
   - `blocked`：存在真实 P0/P1 代码问题，必须修复。
2. 每个未验证项最多做一次新的、有明确假设的验证尝试。若账本已有相同命令、相同 adb 操作或相同失败原因，不得重跑。一次尝试仍失败时，保存失败证据并标记 `environment-limited`，继续处理其他项。
3. 对日期范围空状态：核对 provider、聚合器、结束日边界、`scores.isEmpty` 渲染和 Widget/单元测试。设备日期 picker 无法可靠选择历史日期时，不得用空数据库截图替代；保留已有失败 XML/PNG，并关闭为 `code-closed + environment-limited`。
4. 对模板排序：核对 `ReorderableListView`、`onReorder`、SharedPreferences/JSON round-trip 和排序测试。若 UIAutomator/坐标拖拽未改变顺序，停止手势重试；标记业务逻辑 `code-closed`，设备手势为 `environment-limited`。
5. 对像素/SSIM：只有参考图与生产热力图面板在同一坐标、尺寸、裁剪和状态下，才允许计算阈值。整页截图或近似裁剪只能作为诊断，不能生成“通过”结论；没有同构基线时标记 `environment-limited/not-measurable`，保留人工差异、Path、布局和语义证据。
6. 对真机、横屏和多 Android/分享 chooser：当前没有对应硬件或目标应用时只做一次可行性检查；使用已有 API 30/API 35/宽屏和 MethodChannel fallback 测试作为替代证据，缺失矩阵标记 `environment-limited`，不得阻塞核心离线功能。
7. 运行一次最终代码 gate：`dart format --set-exit-if-changed lib test`、`flutter analyze`、`flutter test --concurrency=1`、`dart run build_runner build`，并核对已有隔离目录 Android debug 构建证据。若全部通过，不为设备缺口修改生产代码。
8. 更新 `docs/phase_progress.md` 和 `docs/acceptance_checklist.md`：写明分类、证据绝对路径、一次尝试的结果、环境限制原因和剩余风险。不要把阶段 5 标记为“通过”，除非所有硬门槛都有新证据；可以标记为“部分完成（代码 gate 通过，设备限制未验证）”。

## 停止条件和最终报告

收敛验收在以下任一条件满足时停止：

- 所有剩余项已分类，且 P0/P1 为零；
- 继续操作只会重复已有失败或需要当前环境没有的设备/资产；
- 出现需要用户提供资产、凭据或改变范围的真实阻塞。

最终报告必须包括：实际命令和结果、代码/Widget/设备证据、`environment-limited` 清单、P0-P3 风险、阶段状态和下一步。没有新证据时不要开启下一轮自动执行。
