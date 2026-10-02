# 贡献指南

感谢关注 IronLog。当前项目仍处于 Phase 1 收尾和审计阶段，提交变更前请先确认它属于现有范围，并保持离线优先和可恢复记录这两个产品约束。

## 开始前

先阅读以下文件：

- `AGENTS.md`
- `docs/PROGRESS.md`
- `docs/PRD.md`
- `docs/ARCHITECTURE.md`
- 与任务相关的 `docs/tasks/M<n>.md`

不要在没有对应任务或需求记录的情况下顺手加入 Phase 2/3 功能。新增资源、媒体或第三方数据前，先记录来源、作者、许可证、原始链接和再分发条件。

## 实现约定

- 保持 `ui/feature -> domain <- data` 的依赖方向。Composable 不直接访问 DAO，ViewModel 依赖 domain Repository 接口。
- 领域逻辑先写单元测试；时间、ID 和系统服务通过接口注入，避免在 domain 直接读取系统时间或 Android API。
- 训练中的每次写操作都要立即持久化，不能等到结束训练时批量保存。
- 物理量按项目约定保存为整数定点值：克、米、十分之一速度/坡度和秒。
- UI 文案放入资源文件；新页面保持无状态 `Screen` 与可预览结构。
- 不添加 `INTERNET` 或云服务依赖到 Phase 1，也不要用破坏性迁移掩盖 schema 变化。

## 本地验证

在 Git Bash 中执行：

```bash
source scripts/env.sh
./gradlew --console=plain assembleDebug
./gradlew --console=plain testDebugUnitTest
./gradlew --console=plain lintDebug
bash scripts/verify.sh
```

提交前至少运行与改动相关的定向测试；涉及跨层行为、数据库、导航或系统服务时运行完整 `testDebugUnitTest` 和 `lintDebug`。失败信息应保留在本地日志中并在提交说明或 PR 中解释。

## 提交与 Pull Request

- 一次提交只解决一个清晰的问题，提交标题使用简短英文动词，例如 `docs: explain release workflow`。
- PR 描述应说明行为变化、测试命令和仍存在的限制；涉及 UI 时附真机或截图验证结果。
- 如果加入图片、字体、动作媒体或其他外部资源，请在 PR 中列出来源、作者、许可证和包体影响，并同步更新资源清单。
- 不要修改历史进度记录来伪造当前测试数量；当前状态应追加到 `docs/PROGRESS.md`。

## 密钥和生成物

以下内容不能提交：`.stage-signing/`、JKS/keystore、签名密码、`app/build/`、`artifacts/stage-release/`、APK、`local.properties` 和本地 IDE 配置。Release 签名密码只能通过环境变量或 CI secret 提供。阶段 key 仅用于评估，商店发布必须使用单独管理的 production key 或签名服务。

## 许可证

IronLog 当前尚未在仓库根目录声明自身的开源许可证。公开仓库不代表自动授予代码再分发权；在项目维护者选定许可证前，请不要把 IronLog 源码当作 MIT、Apache-2.0 或其他许可证发布。第三方身体路径数据的归属和 MIT 条款见 [`NOTICE`](NOTICE)。
