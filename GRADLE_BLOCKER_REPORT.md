# Gradle 构建阻塞报告

## 当前结论

### 2026-09-27 复现更新

本报告在前一次“已恢复”的修复报告之后再次验证。当前阻塞已重新出现，且可稳定复现：在本项目目录执行 Gradle wrapper 时，仍停在 Gradle 启动阶段，尚未进入项目配置、依赖解析或 Kotlin 编译。

复现环境：

- 工作区：`C:\Users\Wang Nathan\Documents\ChatGPT\IronLog`
- PowerShell
- `JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot`
- Gradle Wrapper 9.8.0

复现命令：

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot'
.\gradlew.bat --console=plain --no-daemon :app:compileDebugKotlin
```

实际结果：

```text
FAILURE: Build failed with an exception.
java.io.IOException: Unable to establish loopback connection
Caused by: java.net.SocketException: Invalid argument: connect
```

本次还尝试了以下变体，结果相同：

```text
--stacktrace
--max-workers=1
--offline
-Dorg.gradle.daemon=false
-Dorg.gradle.parallel=false
-Djava.net.preferIPv4Stack=true
GRADLE_USER_HOME=C:\gradle-home
```

切换到独立 `GRADLE_USER_HOME=C:\gradle-home` 后，wrapper 可以下载并解压 Gradle 9.8.0，但 daemon 随后仍在 loopback 初始化处失败。因此问题不太可能只是项目 `.gradle` 缓存或用户目录空格导致。

这次没有运行 `assembleDebug`、`testDebugUnitTest` 或 `assembleRelease`，因为它们都会在同一个 daemon 初始化点失败。

项目源码尚未进入 Kotlin/Compose 编译阶段。Gradle 在启动构建 daemon 或 single-use daemon 时，初始化本地通信通道失败，因此无法取得真正的编译结果。

核心错误：

```text
java.io.IOException: Unable to establish loopback connection
Caused by: java.net.SocketException: Invalid argument: connect
```

错误发生在：

```text
org.gradle.internal.remote.internal.inet.SocketConnection
org.gradle.launcher.daemon.client.DefaultDaemonConnector
java.nio.channels.SocketChannel.open
sun.nio.ch.PipeImpl$Initializer$LoopbackConnector
```

这表示 Gradle 使用 Java NIO 建立进程内/本机回环通信时失败。当前没有证据表明是项目 Kotlin 源码、Compose API、依赖解析或 Android SDK 编译错误。

## 已确认环境

- 操作系统：Windows
- Java：Microsoft OpenJDK 17.0.20.1 LTS
- Gradle Wrapper：仓库自带 Gradle 9.8.0
- Android Gradle Plugin：9.4.1
- Kotlin：2.4.20
- compileSdk：37
- 构建目标：`:app:compileDebugKotlin`
- 当前工作目录：`C:\Users\Wang Nathan\Documents\ChatGPT\IronLog`

Java 可执行文件验证通过：

```text
openjdk version "17.0.20.1" 2026-08-18 LTS
```

## 已尝试命令

### 1. 普通编译

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot'
.\gradlew.bat --console=plain :app:compileDebugKotlin
```

结果：loopback connection 失败。

### 2. 停止 daemon 后重新编译

```powershell
.\gradlew.bat --stop
.\gradlew.bat --no-daemon --console=plain :app:compileDebugKotlin
```

结果：single-use daemon 仍需建立本地通信，失败。

### 3. 强制 IPv4

设置过：

```text
-Djava.net.preferIPv4Stack=true
-Djava.net.preferIPv6Addresses=false
```

结果不变。说明问题不是 IPv6 地址选择导致的普通 TCP 回环失败。

### 4. 限制 Gradle 并行和 worker

```text
--max-workers=1
-Dorg.gradle.daemon=false
-Dorg.gradle.parallel=false
```

结果不变。

### 5. 离线模式与禁用 instrumentation agent

```text
--offline
-Dorg.gradle.internal.instrumentation.agent=false
```

结果不变。

### 6. 修改 Java 临时目录

尝试将临时目录指定到本地目录：

```text
-Djava.io.tmpdir=C:\temp
```

结果不变。

## 重要判断

Gradle 即使使用 `--no-daemon` 也会创建 single-use daemon，因为当前项目 JVM 参数和 Gradle 启动机制要求隔离的构建进程。因此“禁用 daemon”没有绕过本地通信初始化。

异常发生在 Gradle 初始化阶段，早于：

- settings.gradle.kts 的完整配置
- 依赖下载/解析
- KSP
- Hilt 代码生成
- Room schema 处理
- Kotlin 编译
- Compose 编译

## 建议排查顺序

### 给第三方修复人员的优先检查项

1. 使用同一 JDK 17 直接运行一个最小 `Pipe.open()` 和 `ServerSocket` 回环测试，并记录是否失败。Gradle 堆栈显示失败点是 `sun.nio.ch.PipeImpl$Initializer$LoopbackConnector`，不是普通项目任务。
2. 检查 Windows 网络栈、WFP/防火墙、杀毒软件、VPN/代理和企业安全策略是否阻止 Java 进程的本地回环/Unix domain socket 通信。
3. 检查系统临时目录、用户目录、Gradle daemon 目录权限和路径重定向；但独立 `C:\gradle-home` 已复现失败，不能只按缓存损坏处理。
4. 对比另一个 JDK 17 发行版或同版本 Java 的 `java.nio` 行为，确认不是当前 Microsoft OpenJDK 安装或 Windows 补丁环境问题。
5. 检查是否存在残留 Gradle/Java 进程、daemon registry 锁或安全软件注入；保留失败时的完整 `--stacktrace --info` 日志。
6. 修复后必须在本项目目录实际执行恢复标准中的三个任务，不能只验证 `gradle --version` 或 `help`。

### A. 检查 Java 回环能力

在 PowerShell 中执行：

```powershell
@"
import java.net.InetAddress;
import java.net.ServerSocket;
public class LoopbackCheck {
  public static void main(String[] args) throws Exception {
    System.out.println(InetAddress.getLoopbackAddress());
    try (ServerSocket s = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
      System.out.println("port=" + s.getLocalPort());
    }
  }
}
"@ | Set-Content C:\temp\LoopbackCheck.java
javac C:\temp\LoopbackCheck.java
java -cp C:\temp LoopbackCheck
```

如果该程序也失败，问题位于 Java/Windows 网络栈或安全软件，而不是 Gradle。

### B. 检查 Windows 回环和安全软件

重点检查：

- Windows Defender Firewall 出站/入站规则
- 企业安全软件、杀毒软件或应用隔离策略
- VPN、代理和网络过滤器
- Windows 网络堆栈是否被策略限制
- `127.0.0.1` 和 `::1` 是否可用

可执行：

```powershell
Test-NetConnection 127.0.0.1 -Port 135
Test-NetConnection ::1 -Port 135
netsh interface ipv4 show excludedportrange protocol=tcp
```

### C. 检查 Java 版本和发行版

确认系统中不存在失效的 `JAVA_HOME`、多个 Java 版本冲突或 wrapper 实际使用了不同 Java：

```powershell
where.exe java
where.exe javac
java -version
javac -version
Get-ChildItem Env:JAVA_HOME
.\gradlew.bat --version
```

### D. 检查 Gradle 用户目录和 daemon 缓存

在确认没有其他 Gradle 构建运行后，可以清理当前用户 Gradle daemon 状态：

```powershell
.\gradlew.bat --stop
Get-ChildItem "$env:USERPROFILE\.gradle\daemon"
```

建议优先重命名对应版本目录，而不是直接删除，以便保留现场：

```powershell
Rename-Item "$env:USERPROFILE\.gradle\daemon\9.8" "9.8.bak"
```

### E. 检查用户目录路径和临时目录权限

当前用户目录包含空格：

```text
C:\Users\Wang Nathan\
```

项目本身已为 Robolectric 配置独立缓存，但 Gradle daemon 仍可能使用用户目录下的通信/锁文件。可尝试临时指定：

```powershell
$env:GRADLE_USER_HOME='C:\gradle-user-home'
$env:TEMP='C:\temp'
$env:TMP='C:\temp'
```

然后重新执行 wrapper。

### F. 从 Git Bash 执行

项目原始说明要求 Git Bash 环境。PowerShell 理论上可以执行 wrapper，但可以排除 shell 环境因素：

```bash
source scripts/env.sh
./gradlew --no-daemon --console=plain :app:compileDebugKotlin --stacktrace
```

## 暂不建议的操作

- 不建议修改 Gradle、AGP、Kotlin 或 Compose 版本来绕过当前错误。
- 不建议删除 `gradle/wrapper` 或替换 wrapper。
- 不建议关闭所有防火墙作为长期解决方案。
- 不建议删除整个 `.gradle` 目录，除非已保留日志和确认依赖可重新下载。

## 当前影响

受影响：

- `compileDebugKotlin`
- `assembleDebug`
- `testDebugUnitTest`
- `assembleRelease`
- APK 生成和安装验证

不受影响：

- 源码编辑
- Git 提交和标签回退
- 静态代码检查和文档维护
- UI 结构继续开发

## 恢复标准

问题解决后，至少执行：

```powershell
.\gradlew.bat --no-daemon --console=plain :app:compileDebugKotlin
.\gradlew.bat --no-daemon --console=plain :app:testDebugUnitTest
.\gradlew.bat --no-daemon --console=plain :app:assembleDebug
```

随后记录：

- `gradlew --version`
- 编译结果
- 测试数量和失败数量
- APK 路径和文件大小
- 是否仍有 loopback、daemon 或权限警告

## 与历史修复报告的关系

`GRADLE_FIX_REPORT.md` 记录了 2026-09-27 早些时候一次成功构建（Kotlin 编译、280 个测试和 Debug APK 均成功）。本报告不是对该记录的否定，而是记录后续在同一工作区再次出现的可复现阻塞。两份报告说明该问题具有环境/时段不稳定性：曾经成功，之后再次在相同 Java NIO loopback 初始化点失败。

本次没有修改 Gradle wrapper、AGP、Kotlin 版本或根构建配置，也没有把 loopback 错误误判为当前源码的编译错误。
