# 进度快照模板

在每个阶段结束或准备换对话时，让 Codex 按下面模板输出。将生成的内容原样粘贴到 `04-resume-context.md` 的占位符中。

```text
项目：Flutter 健身记录 App
仓库：<绝对路径>
记录时间：<YYYY-MM-DD HH:mm TZ>
当前阶段：<阶段编号和名称>

仓库状态：
- 当前分支：<branch>
- 工作区：干净 / 有未提交改动
- Flutter/Dart/Android 版本：<versions>
- 参考图：可用 <path> / 不可用，原因 <reason>
- 复用方案审计：<docs/reuse_evaluation.md 或等价路径>

已验证完成：
- [ ] 工程可启动
- [ ] 领域模型与生成文件
- [ ] Hive/SharedPreferences 持久化
- [ ] 动作库和模板
- [ ] 训练记录和历史
- [ ] 身体数据和图表
- [ ] JSON/CSV 导入导出
- [ ] 通知和权限降级
- [ ] 热力图前视/后视与数据映射
- [ ] 热力图截图/视觉对比
- [ ] 肌肉路径不是简单多边形/占位形状
- [ ] 依赖与艺术资产许可证、署名和版本已核验
- [ ] 无障碍和响应式检查

本轮修改文件：
- <absolute path>：<behavioral change>

最后一次验证：
- `<command>` -> 通过/失败，退出码 <code>
- 关键输出：<short output>
- 测试/截图路径：<paths>

数据与热力图状态：
- schema/version：<value>
- 热度聚合规则：<summary>
- 已实现肌群 ID/视图：<summary>
- 复用方案与许可证：<package/repository, version/commit, code license, asset license, attribution, decision>
- 已知视觉差异：<summary>

未完成和风险：
- P0/P1/P2/P3：<issue, impact, reproduction>

下一步最小任务：
1. <specific command or file-level task>
2. <verification command>
```
