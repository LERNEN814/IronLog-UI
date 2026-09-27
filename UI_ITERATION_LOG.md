# UI 迭代日志

## 2026-09-27：项目启动

- 读取并确认产品回答文档 `demand_doc.txt`。
- 确认产品定位：面向有训练经验用户的 Android 高级力量训练记录 App。
- 确认主导航：主页、训练、历史、动作、设置。
- 确认训练页为首要设计对象：动作侧边栏 + 当前动作大卡片 + 组记录。
- 确认技术方向：Kotlin、Jetpack Compose、Material 3、Room、DataStore、Hilt。
- 确认旧工程仅作为功能和数据层参考，不直接覆盖新项目工作区。

## 当前阶段

Phase 1 / UI Foundation：设计系统、页面地图和训练页原型准备。

## 下一步

1. 建立 Android 项目骨架。
2. 实现主题和基础组件。
3. 使用假数据实现训练页静态交互原型。

## 2026-09-27：功能基线导入与主题第一轮

- 导入 `E:\preparation\IronLog-devtest` 作为可运行的功能基线。
- 创建 Git 回退点 `backup-20260927-functional-baseline`。
- 将默认红色 Material 主题替换为 IronLog 紫色语义色，保留系统亮/暗主题切换。
- 将主导航扩展为主页、历史、动作、设置，动作页复用现有动作选择能力。
- 将基础卡片圆角调整为 16dp，开始向高级、沉稳的视觉方向收敛。
- Gradle 编译暂未完成：本机 Gradle 无法建立 loopback connection，待环境网络/进程限制解除后重试。
