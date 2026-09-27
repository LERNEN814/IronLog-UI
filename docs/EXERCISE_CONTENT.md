# 动作内容与媒体策略

## 数据归属

动作名称、器械、肌群映射、动作说明和媒体引用属于动作内容域；训练重量、次数和完成状态属于训练记录域。两者不能混为一张训练记录表。

当前实现将动作结构化元数据保存在 Room，将媒体文件放在 `assets/exercises/<exercise-id>/`，由 `ExerciseMedia` 按固定约定解析。

## 媒体格式

- 缩略图：WebP，建议 256px，供动作库和训练侧边栏使用。
- 演示图：WebP 动图或短视频，只有进入动作详情时加载。
- 缺失资源：必须显示静态占位，不影响训练记录。
- Room 不保存二进制媒体；只在未来 schema 中保存可选的资源键、来源和许可证元数据。

## 外部资源准入

联网搜索只能用于筛选可合法再分发的资源。每个媒体资源必须记录：来源、作者或组织、许可证、原始链接和本地文件名。禁止把搜索结果或第三方 App 的素材直接打包进应用。

优先顺序：自制动作演示 > 明确允许再分发的开放许可证资源 > 仅用于开发测试的远程引用。正式发布前必须完成许可证复核。

## 当前来源清单

当前仓库没有引入任何外部动作图片、动图、视频或远程媒体 URL。动作媒体目录尚未添加，因此目前 UI 显示的是占位图标，不存在需要复核的外来媒体版权来源。

已使用的图标来源：

- AndroidX Compose Material Icons Core / Extended
- 依赖坐标：`androidx.compose.material:material-icons-core`、`androidx.compose.material:material-icons-extended`
- 版本由 Compose BOM `2026.09.00` 管理
- 许可证：Apache License 2.0
- 官方项目：https://developer.android.com/develop/ui/compose/graphics/images/material

未来候选动作媒体来源只会在完成许可证核对后加入。目前没有从候选来源下载或复制动作媒体文件。

## UI 图标

系统和动作语义图标优先使用 Material Icons Extended；品牌图形和动作媒体单独管理。避免为常见操作引入重复图标库，以控制 APK 体积和视觉一致性。
