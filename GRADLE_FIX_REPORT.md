# IronLog Gradle 构建修复报告

> 本报告针对 `E:/Project/fix/GRADLE_BLOCKER_REPORT.md`(Gradle 构建阻塞报告)中反映的问题,
> 记录完整的排查过程、根因分析、修复内容与验证结果。
>
> 修复日期:2026-09-27
> 项目路径:`C:\Users\Wang Nathan\Documents\ChatGPT\IronLog`

---

## 0. 摘要

原报告结论是:Gradle daemon 无法建立回环连接(`Unable to establish loopback connection` /
`SocketException: Invalid argument: connect`),构建卡死在初始化阶段,无法取得真正的编译结果。

本次修复的结果:

| 恢复标准项(取自原报告) | 结果 |
|---|---|
| `:app:compileDebugKotlin` | ✅ BUILD SUCCESSFUL |
| `:app:testDebugUnitTest` | ✅ 280 个测试,0 失败,0 错误 |
| `:app:assembleDebug` | ✅ 产出 `app-debug.apk`(约 21.3 MB) |
| 常规 daemon 模式构建 | ✅ daemon 正常启动并保持 IDLE |

**两件事说在前面:**

1. 原报告的核心阻塞(回环连接失败)**已无法复现**,判定为临时性 Windows 环境问题(详见第 2 节)。
2. 阻塞消失后,构建真正进入 Kotlin/Compose 编译阶段,暴露出 **4 处源码/工程配置缺陷**(环境暂停期间
   UI 继续开发但未做构建验证所致),以及 **1 处 Robolectric 空格路径缺陷**。这些均已修复,详见第 3 节。

**本次修复涉及 IronLog 项目内 4 个文件的更改**(详见第 4 节):
`gradle/libs.versions.toml`、`app/build.gradle.kts`、
`app/src/main/java/com/ironlog/app/ui/feature/exercise/ExercisePickerScreen.kt`、
`app/src/main/java/com/ironlog/app/ui/feature/history/HistoryScreen.kt`。
此外在项目外新建了 Robolectric 缓存目录 `C:\Users\Public\robolectric-m2`(非项目文件)。
所有源码更改均**未提交 git**,留待审计。

---

## 1. 环境核查:发现两个环境疑点

### 1.1 JAVA_HOME 指向不存在的 JDK

系统环境核查结果:

```text
实际安装的 JDK(仅一个):C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot  (17.0.20.1 LTS)
PATH 上的 java:         同上的 17.0.20.1
机器级 JAVA_HOME:       C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot  ← 该目录已不存在
```

机器级 `JAVA_HOME` 指向一个**已被卸载/移除**的 JDK 目录。项目内的 `scripts/env.sh` 已针对此问题
做了兜底(检测 `JAVA_HOME/bin/java.exe` 不存在时自动改用 `jdk-17.0.20.101-hotspot`)。

影响:在 PowerShell 中若不手动设置 `$env:JAVA_HOME` 或先 `source scripts/env.sh`,
`gradlew.bat` 可能报 "JAVA_HOME is set to an invalid directory"。
本次所有验证命令均显式使用有效 JDK:

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot'
```

**建议**(未代为执行,属系统级改动):用管理员 PowerShell 修正机器级变量:

```powershell
setx JAVA_HOME "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
```

### 1.2 系统存在 Hyper-V/WSL 保留端口段

```text
协议 tcp 端口排除范围
开始端口    结束端口
   5357      5357
  49706     49805
  50000     50059   *
  50060     50159
  50260     50359
  ...
动态端口范围:49152 - 65535(共 16384 个)
```

Windows 上这类保留端口段是 Java NIO 临时端口偶发 `Invalid argument: connect` 的典型诱因——
原报告的回环失败很可能与此(或当时的防火墙/安全软件瞬时状态)有关。

**若回环问题复发**,建议:

```powershell
net stop winnat && net start winnat   # 需管理员,通常能释放被错误保留的端口段
netsh interface ipv4 show excludedportrange protocol=tcp   # 复查
```

---

## 2. 原报告核心阻塞(回环连接)排查过程

### 2.1 最小化复现测试

原报告的失败栈在 `sun.nio.ch.PipeImpl$Initializer$LoopbackConnector`,普通 `ServerSocket` 测试
覆盖不到该代码路径,因此分别做了两个最小化测试:

**测试 A — 基础回环(报告第 A 节建议的排查):**

```java
// C:\temp\LoopbackCheck.java
try (ServerSocket s = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
    try (Socket c = new Socket(InetAddress.getLoopbackAddress(), s.getLocalPort())) { ... }
}
```

结果:**通过**。`loopback=localhost/127.0.0.1`,连接建立成功。

**测试 B — NIO Pipe(精确对应失败栈):**

```java
// C:\temp\NioPipeCheck.java
for (int i = 0; i < 20; i++) { Pipe p = Pipe.open(); ... }
```

结果:**连续 20 次全部通过**,`SelectorProvider = sun.nio.ch.WEPollSelectorProvider`。

### 2.2 直接运行原失败任务

在有效 JDK 下直接执行报告中的失败命令:

```powershell
.\gradlew.bat --console=plain --no-daemon help
.\gradlew.bat --console=plain --no-daemon :app:compileDebugKotlin
```

结果:single-use daemon 正常建立本地通信,构建正常进入编译阶段——**回环失败未复现**。
随后又用常规 daemon 模式验证(`:app:compileDebugKotlin :app:assembleDebug`),
daemon 正常启动、执行、保持 IDLE。

### 2.3 对原阻塞的判定

- 基础回环、NIO Pipe、single-use daemon、常驻 daemon 四条路径均正常 → 当前 Java/Windows 网络栈可用。
- 原失败具备"偶发、环境相关"特征(与保留端口段、安全软件瞬时状态吻合),不涉及项目源码或 Gradle 配置。
- **判定:回环阻塞为临时环境问题,当前已不存在;报告第 4 节"建议排查顺序"中的 A–D 项核查结果均正常。**

---

## 3. 真实编译/测试错误的定位与修复

回环阻塞消失后,构建首次进入 Kotlin/Compose 编译,暴露出真正的错误。以下按修复顺序说明。

### 3.1 完整错误清单(修复前)

`compileDebugKotlin` 失败,共 29 个编译错误,集中在 4 个文件:

- `ExercisePickerScreen.kt`:21 个错误(`FitnessCenter`、`CustomExerciseSheet`、`regionLabel`、`size`、
  `Tag`、`DropdownField`、`kindLabel` 未解析;6 处 "Modifier 'private' is not applicable to 'local function'";
  423:2 "Syntax error: Expecting '}'")
- `HistoryScreen.kt`:5 个错误(`CalendarMonth`、`Insights` 未解析;137:14 `weight` 未解析)
- `SessionScreen.kt`:2 个错误(`FitnessCenter` 未解析)
- `IronLogBottomBar.kt`:2 个错误(`FitnessCenter` 未解析)

错误形态显示这是典型的**连锁错误**:一个语法错误(缺括号)引发后续声明全部被错误解析。

### 3.2 修复一:补充 `material-icons-extended` 依赖

**根因**:代码使用的 3 个图标 `FitnessCenter`、`CalendarMonth`、`Insights` 属于
`androidx.compose.material:material-icons-extended`(Compose 的扩展图标库),
但工程只声明了 `material-icons-core`(只含 40 余个基础图标)。

- `SessionScreen.kt`(37、433 行)与 `IronLogBottomBar.kt`(7、21 行)中的 `FitnessCenter` 是历史代码遗留;
- `ExercisePickerScreen.kt` 的 `FitnessCenter` 由 commit `450ce41` 引入;
- `HistoryScreen.kt` 的 `CalendarMonth`/`Insights` 由 commit `47e3e9a` 引入。

**改动**:

1. `gradle/libs.versions.toml` — 新增库声明(版本由 Compose BOM 2026.09.00 统一管理,
   实际解析为 1.7.8,BOM POM 中已确认包含该构件):

   ```toml
   compose-material-icons-extended = { module = "androidx.compose.material:material-icons-extended" }
   ```

2. `app/build.gradle.kts` — 引入依赖:

   ```kotlin
   implementation(libs.compose.material.icons.core)
   implementation(libs.compose.material.icons.extended)
   ```

### 3.3 修复二:`ExercisePickerScreen.kt` 缺闭合括号 + 缺 import

**根因**:commit `450ce41`("unify home and exercise library surfaces")把 `ExerciseRow` 中 Card 内的
顶层 `Column` 改为 `Row`,并新增 `Box`(图标)+ `Spacer` + 内层 `Column` 的嵌套结构,但**没有补充
对应的闭合大括号**,导致 `ExerciseRow` 函数体无法闭合:

- 后续的 `Tag`、`regionLabel`、`CustomExerciseSheet`、`DropdownField`、`kindLabel` 等声明全部被
  解析为 `ExerciseRow` 内部的局部声明 → 报 "Modifier 'private' is not applicable to 'local function'";
- 局部声明晚于调用点 → 报 `Tag`/`regionLabel`/`kindLabel`/`DropdownField` "Unresolved reference";
- 文件末尾报 "Syntax error: Expecting '}'"。

另外该 commit 新增的 `Modifier.size(56.dp)` 使用了 `size`,但文件缺少
`import androidx.compose.foundation.layout.size`。

**改动**(`ExercisePickerScreen.kt`):

1. 补充 import:

   ```kotlin
   import androidx.compose.foundation.layout.size
   ```

2. 在 `ExerciseRow` 的 Card 闭合后补充缺失的 `}`(即 `@Composable private fun Tag` 之前):

   ```kotlin
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tag(text = exercise.equipment)
                if (primaryMuscleName != null) Tag(text = primaryMuscleName)
            }
        }
    }
   }
   }                    // ← 新增:闭合 ExerciseRow 函数体
   ```

### 3.4 修复三:`HistoryScreen.kt` 缺 import + `weight` 用法错误

**根因**(commit `47e3e9a` 引入):

1. `StatTile` 使用了 `Modifier.background(...)`,但文件缺 `import androidx.compose.foundation.background`
   (编译期因 `weight` 先报错、同表达式链上的后续错误被抑制而未显示)。
2. `StatTile` 在自己的函数体内对 `Column` 的 `modifier` 链调用 `Modifier.weight(1f)`。
   `weight` 是 `RowScope`/`ColumnScope` 的成员扩展,只有在 `Row { }`/`Column { }` 内容 lambda 的
   **调用点**作用域内才能解析;写在 `StatTile` 自身函数体内既无法编译、语义上也不生效
   (3 个统计卡片实际上无法等分宽度)。

**改动**(`HistoryScreen.kt`):

1. 补充 import:

   ```kotlin
   import androidx.compose.foundation.background
   ```

2. `StatTile` 改为接受 `modifier` 参数,`weight` 移到 Row 调用点作用域内:

   ```kotlin
   Row(horizontalArrangement = Arrangement.spacedBy(Dimens.CardSpacing)) {
       StatTile(modifier = Modifier.weight(1f), value = sessions.size.toString(), label = ...)
       StatTile(modifier = Modifier.weight(1f), value = totalSets.toString(), label = ...)
       StatTile(modifier = Modifier.weight(1f), value = totalMinutes.toString(), label = ...)
   }

   @Composable
   private fun StatTile(modifier: Modifier = Modifier, value: String, label: String) {
       Column(
           modifier = modifier
               .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Dimens.CardCorner))
               .padding(vertical = 16.dp),
           ...
       ) { ... }
   }
   ```

修复后 `:app:compileDebugKotlin` **BUILD SUCCESSFUL**(仅剩原有非阻塞告警,见 5.3)。

### 3.5 修复四:Robolectric 空格路径崩溃(35 个测试失败)

**现象**:`testDebugUnitTest` 首次运行 280 个测试中 **35 个失败**,全部是
`UnsatisfiedLinkError: 'long android.database.sqlite.SQLiteConnection.$$robo$$nativeOpen$nativeBinding(...)'`。

**根因**(通过 `--info` 日志 + 反编译 `DefaultNativeRuntimeLoader` 确认):

1. Robolectric 4.17 仅当 **测试 SDK ≥ 35** 时加载原生运行时(`AndroidTestEnvironment` 中
   `shouldLoadNativeRuntime() && getApiLevel() >= 35`)。DB/仓库测试均标注 `@Config(sdk = [35])`,
   因此只有这些测试触发原生运行时;其余测试运行于 SDK 26(minSdk 回退),故通过。
2. 原生运行时加载时,`maybeCopyIcuData` → `getResourcesInAndroidAll` 用
   `Resources.getResource("build.prop").toURI().toString()` 得到 android-all jar 的路径,
   再 `new JarFile(path)`。`URI.toString()` 会把路径中的空格编码为 `%20`,于是实际访问
   `C:\Users\Wang%20Nathan\Documents\ChatGPT\IronLog\.robolectric-m2\...jar` → `NoSuchFileException`。
3. 完整因果链:

   ```text
   java.lang.AssertionError: Unable to load Robolectric native runtime library
   Caused by: java.nio.file.NoSuchFileException:
     C:\Users\Wang%20Nathan\Documents\ChatGPT\IronLog\.robolectric-m2\org\robolectric\
     android-all-instrumented\15-robolectric-13954326-i7\android-all-instrumented-15-robolectric-13954326-i7.jar
   ```

4. 项目此前已有规避尝试(`app/build.gradle.kts` 注释:"Robolectric breaks on user-home paths
   containing spaces; keep its cache in-repo",即把 `maven.repo.local` 指向仓库内 `.robolectric-m2`),
   但**仓库本身位于带空格的 `C:\Users\Wang Nathan\...` 下**,规避不彻底。

**改动**(`app/build.gradle.kts` 的 `testOptions`):

```kotlin
testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.all {
        // Robolectric's native runtime (used for SDK 35+ tests) locates the
        // android-all jar via a URL-encoded path and breaks when the path
        // contains spaces (Windows). Point its Maven cache at a space-free
        // directory whenever the repo path itself contains a space.
        val inRepoCache = rootProject.file(".robolectric-m2")
        val cacheDir = if (System.getProperty("os.name").startsWith("Windows") &&
            inRepoCache.absolutePath.contains(' ')
        ) {
            File(System.getenv("PUBLIC") ?: "C:\\Users\\Public", "robolectric-m2")
        } else {
            inRepoCache
        }
        it.systemProperty("maven.repo.local", cacheDir.absolutePath)
    }
}
```

(另需在脚本头部 `import java.io.File`;Kotlin DSL 默认导入不含 `java.io`。)

**说明**:

- 仅在 **Windows 且仓库路径含空格** 时重定向到 `C:\Users\Public\robolectric-m2`
  (`PUBLIC` 目录在所有语言版本 Windows 上均为无空格、用户可写的固定路径);
  其他环境仍使用仓库内 `.robolectric-m2`,保持原有可移植性。
- 已将原 `.robolectric-m2` 缓存复制到新位置,避免重复下载 android-all jar。

修复后 DB 测试通过,进而全量 280 个测试 0 失败。

---

## 4. 本次更改涉及的文件清单

**IronLog 项目内文件(4 个,均修改):**

| 文件 | 修改内容 |
|---|---|
| `gradle/libs.versions.toml` | 新增 `compose-material-icons-extended` 库声明(1 行) |
| `app/build.gradle.kts` | ① 引入 `material-icons-extended` 依赖;② 重写 Robolectric 缓存目录逻辑(空格路径时改用 `C:\Users\Public\robolectric-m2`);③ 头部新增 `import java.io.File` |
| `app/src/main/java/com/ironlog/app/ui/feature/exercise/ExercisePickerScreen.kt` | ① 补 `import androidx.compose.foundation.layout.size`;② 补 `ExerciseRow` 缺失的闭合 `}` |
| `app/src/main/java/com/ironlog/app/ui/feature/history/HistoryScreen.kt` | ① 补 `import androidx.compose.foundation.background`;② `StatTile` 改为 `modifier` 参数,`weight(1f)` 移至 Row 调用点 |

**项目外产物(非项目文件):**

- `C:\Users\Public\robolectric-m2\` — 新建的 Robolectric Maven 缓存(从仓库内 `.robolectric-m2` 复制);
- 仓库内 `.robolectric-m2` 目录**保留未动**(供无空格路径环境使用)。

**git 状态**:上述 4 个项目文件均**未提交**;`app/build/` 下还有大量构建产物变更(历史遗留的
已跟踪构建中间产物被本次构建更新),也未提交。如需提交,可按 AGENTS.md 规范执行。

---

## 5. 验证结果与残余事项

### 5.1 恢复标准验收(原报告要求)

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot'
.\gradlew.bat --console=plain --no-daemon :app:compileDebugKotlin   # ✅ SUCCESS
.\gradlew.bat --console=plain --no-daemon :app:testDebugUnitTest    # ✅ 280 tests, 0 failures
.\gradlew.bat --console=plain --no-daemon :app:assembleDebug        # ✅ SUCCESS
```

- APK:`app/build/outputs/apk/debug/app-debug.apk`,约 21.3 MB。
- 常规 daemon 模式亦验证通过(`--status` 显示 daemon IDLE,回环通信正常)。

### 5.2 测试统计

```text
41 个测试类,280 个测试,failures=0,errors=0
```

(修复前:35 个失败,全部为 Robolectric 原生 SQLite 加载失败;修复后归零。)

### 5.3 残余告警(均非阻塞、为原有问题,未处理)

1. `app/build.gradle.kts:47` `srcDir(...)` 弃用提示(建议改用 `directories`)。
2. `SessionScreen.kt:567` `rememberSwipeToDismissBoxState` 的 `confirmValueChange` 弃用提示。
3. Gradle 输出 "Deprecated Gradle features ... incompatible with Gradle 10"(AGP/Kotlin 插件内部告警)。

### 5.4 遗留建议(供用户决定,均未代为执行)

1. 修正机器级 `JAVA_HOME`(见 1.1)。
2. 若回环问题复发:重启 `winnat` 服务、复查排除端口段、检查安全软件(见 1.2)。
3. 提交本次修复(4 个源文件)并更新 `docs/PROGRESS.md` / `UI_ITERATION_LOG.md`(按 AGENTS.md 工作流)。
4. 原 `GRADLE_BLOCKER_REPORT.md` 仍描述修复前状态,可考虑更新或归档。

---

## 6. 时间线回顾

| 步骤 | 操作 | 结果 |
|---|---|---|
| 1 | 阅读阻塞报告,核查环境 | 发现 JAVA_HOME 指向已删除 JDK;存在 Hyper-V 保留端口段 |
| 2 | 最小化回环测试(LoopbackCheck / NioPipeCheck) | 全部通过 |
| 3 | 直接运行原失败任务 | 回环失败未复现;构建进入编译阶段 |
| 4 | 收集完整编译错误清单 | 29 个错误,集中在 4 个文件 |
| 5 | 修复图标依赖(icons-extended) | `FitnessCenter`/`CalendarMonth`/`Insights` 全部解析 |
| 6 | 修复 ExercisePickerScreen 缺括号/缺 import | 连锁错误消除 |
| 7 | 修复 HistoryScreen import/weight | `compileDebugKotlin` SUCCESS |
| 8 | 运行全量测试 | 35 失败 → 定位 Robolectric 空格路径缺陷 |
| 9 | 修复 Robolectric 缓存路径 | 280/280 通过 |
| 10 | assembleDebug + daemon 模式验证 | APK 产出,daemon 正常 |
