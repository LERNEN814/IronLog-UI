# ARCHITECTURE — IronLog 架构与包结构

## 1. 分层

```
ui/feature/<feature>   Composable + ViewModel + UiState        ← 只依赖 domain 接口与纯函数
        ↓
domain/<area>          模型(data class) + 纯逻辑 + Repository 接口
        ↑
data/<area>            Room Entity/DAO、DataStore、Repository 实现、种子导入
```

依赖方向：`ui → domain ← data`。`data` 实现 `domain` 的接口，在 Hilt 模块中绑定。
**不引入 use-case 层**（过度设计）；需要复用或需要测试的复杂逻辑直接写成 `domain/**` 中的纯对象/纯函数。

## 2. 包结构（新建文件的唯一合法位置）

```
com.ironlog.app
├── IronLogApp.kt                    @HiltAndroidApp
├── MainActivity.kt                  @AndroidEntryPoint，单 Activity
├── core/
│   ├── time/Clock.kt                Clock / SystemClock / FixedClock（M3 增加 ElapsedClock）
│   ├── id/IdGenerator.kt            IdGenerator（UUID）/ FixedIdGenerator（测试）
│   ├── units/UnitConverter.kt       显示单位（kg/lb）与存储整数之间的转换
│   └── di/                          AppModule.kt, DatabaseModule.kt（@Module @InstallIn(SingletonComponent)）
├── domain/
│   ├── model/                       Exercise, ExerciseKind, MuscleGroup, WorkoutSession, SessionExercise,
│   │                                WorkoutSet, SetType, BodyWeight, SessionStatus, UserSettings, WeightUnit, DistanceUnit
│   ├── exercise/                    ExerciseRepository(接口)
│   ├── workout/                     WorkoutRepository(接口)
│   ├── settings/                    SettingsRepository(接口)
│   ├── fatigue/                     FatigueModel.kt（纯函数）
│   ├── strength/                    OneRepMax.kt（Epley，纯函数）
│   ├── prefill/                     PrefillPlanner.kt（"上次数据"预填策略，纯函数）
│   ├── keypad/                      Keypad.kt（数字键盘状态机）、WeightStepper.kt（±步进）
│   ├── timer/                       RestTimerMath.kt（M3）
│   └── summary/                     SessionSummary.kt、BodyRegionColors.kt（纯函数）
├── data/
│   ├── db/
│   │   ├── IronLogDatabase.kt       @Database，version 见 DATA_MODEL.md
│   │   ├── Migration.kt             ALL_MIGRATIONS
│   │   ├── entity/                  *Entity.kt（@Entity）
│   │   ├── dao/                     *Dao.kt（@Dao）
│   │   └── converter/               枚举 ↔ Int 映射
│   ├── repository/                  ExerciseRepositoryImpl, WorkoutRepositoryImpl, SettingsRepositoryImpl, Mappers.kt
│   ├── seed/                        SeedImporter.kt、SeedModels.kt（从 assets 导入内置动作）
│   ├── settings/                    SettingsDataStore.kt
│   └── timer/                       RestTimerRepository.kt（M3）
├── platform/                        依赖 Android 系统服务的代码（M3 起）
│   └── timer/                       RestAlarmScheduler / RestAlarmReceiver / RestNotifications / ExactAlarmPermission
└── ui/
    ├── theme/                       Theme.kt, Type.kt, Color.kt, Dimens.kt
    ├── components/                  跨页面复用的 Composable（纯展示，不含业务）
    └── feature/
        ├── home/                    主页（M2）
        ├── exercise/                动作库/选择动作（M2）
        ├── session/                 训练中（M2/M3）：动作列表、组编辑、数字键盘、休息设置
        ├── timer/                   休息倒计时（M3）
        └── summary/                 训练结束与评价（M2）
```

## 3. UI 模式

```kotlin
data class SomeUiState(
    val loading: Boolean = true,
    val items: List<X> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class SomeViewModel @Inject constructor(
    private val repo: SomeRepository,   // domain 接口，不是 DAO
    private val clock: Clock,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SomeUiState())
    val uiState: StateFlow<SomeUiState> = _uiState.asStateFlow()
    private val _events = Channel<SomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
}

@Composable
fun SomeRoute(viewModel: SomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SomeScreen(state = state, onAction = viewModel::onAction)
}

@Composable
private fun SomeScreen(state: SomeUiState, onAction: (SomeAction) -> Unit) { /* 无状态 */ }
```

规则：
- `*Screen` 必须是无状态的（只吃 `state` + lambda），便于 `@Preview`。
- 每个功能页至少写一个 `@Preview`（使用假数据，不访问数据库）。
- 训练中页面的所有"写"操作在 **每次交互后立即落库**（不缓存到结束再写），这是"进程被杀可恢复"的前提。
- 长按/滑动等手势之外，所有可点元素最小触控尺寸 48dp。

## 4. 类型安全的导航

使用 Navigation Compose + Kotlin Serialization 的类型安全路由（已在本项目配置好）：

```kotlin
@Serializable data object HomeRoute
@Serializable data class SessionRoute(val sessionId: String)
```

`NavHost(startDestination = HomeRoute) { composable<HomeRoute> { ... } }`。

## 5. 依赖注入

- `core/di/DatabaseModule.kt`：提供 `IronLogDatabase`（`Room.databaseBuilder(...)`，**不加** `fallbackToDestructiveMigration`）、各 DAO。
- `core/di/AppModule.kt`：`@Binds` Repository 实现；提供 `Clock`（`SystemClock`）、`IdGenerator`、`DataStore<Preferences>`。
- 所有依赖注入对象都是单例作用域；**不要**用 `@Reusable` 等高级特性。

## 6. 货币/物理量的存储约定（重要）

| 概念 | 存储类型 | 单位 | 说明 |
|------|---------|------|------|
| 重量 | `Int` | 克（g） | 45 lb → `20412`（45 × 453.59237，四舍五入） |
| 距离 | `Int` | 米（m） | |
| 速度 | `Int` | 0.1 km/h | 8.0 km/h → `80` |
| 坡度 | `Int` | 0.1 % | 6.5% → `65` |
| 时长 | `Int` | 秒（s） | |
| 挡位 | `Int` | 无量纲 | |
| 时间点 | `Long` | UTC 毫秒 | 显示时用 `Clock.zone()` 转换 |
| 本地日期 | `String` | `YYYY-MM-DD` | 由 `Clock.localDateOf()` 计算，用于日历与热力图分组 |

**单位换算只允许在 `core/units/UnitConverter.kt` 和 `domain/**` 中发生，UI 不得自己做和 `453.59237` 有关的运算。**

## 7. 出错处理

- 数据库操作在 `data` 层用 `Result` 或直接抛异常由 ViewModel 捕获为空状态；UI 显示可重试的错误文案（字符串在 `strings.xml`）。
- 不做全局异常吞没（不要写 `runCatching {}` 忽略结果）。

## 8. 性能与线程

- 全部数据库访问通过 suspend DAO 或 `Flow`；Room 自动在后台线程执行。
- 疲劳度计算在 `Dispatchers.Default` 上用纯函数跑（数据量小，通常 < 5ms）。
- 训练中页面保持屏幕常亮：`WindowCompat`/`FLAG_KEEP_SCREEN_ON` 在 M3 处理。

## 9. 本机工具链已知坑（必须遵守，否则编译/测试失败）

1. **compileSdk 必须 ≥ 37**（SDK 37.0 已安装）。AGP 9.4.1 / Kotlin 2.4.20 / KSP 2.3.12 / Compose BOM 2026.09.00 / Room 2.8.5 / Hilt 2.60.1 已经验证可编译。
2. **Hilt 的 ViewModel 扩展包已迁移**：`hiltViewModel` 在 `androidx.hilt.lifecycle.viewmodel.compose`，**不是** `androidx.hilt.navigation.compose`（后者已废弃）。
3. **Robolectric 在本机需要 `maven.repo.local` 指向仓库内目录**（用户名含空格会导致原生库加载失败）。已在 `app/build.gradle.kts` 配置好，**不要删掉 `testOptions.unitTests.all { ... }` 那段**。
4. **Room schema 目录 `app/schemas` 同时被注册为 debug 资源目录**，这样 Robolectric 才能给 `MigrationTestHelper` 读取 JSON。**不要删除** `sourceSets { getByName("debug").assets.srcDir("$projectDir/schemas") }`。
5. **迁移测试必须用新的 driver 版 API**（旧的 `createDatabase("name", version)` 在 Room 2.8 + Robolectric 下会因为临时路径报 `IllegalArgumentException`）。照抄模板：
   ```kotlin
   private val instrumentation = InstrumentationRegistry.getInstrumentation()
   @get:Rule val helper = MigrationTestHelper(
       instrumentation = instrumentation,
       file = instrumentation.targetContext.getDatabasePath("ironlog-test.db"),
       driver = AndroidSQLiteDriver(),
       databaseClass = IronLogDatabase::class,
   )
   // helper.createDatabase(1).apply { ... ; close() }
   // helper.runMigrationsAndValidate(2, listOf(MIGRATION_1_2)).close()
   ```
6. **`JAVA_HOME` 在本机默认值无效**，必须先 `source scripts/env.sh`。
7. 首次执行依赖解析/构建可能需要 5–10 分钟；`--console=plain` 便于解析日志。
8. **不要**开启 `org.gradle.configuration-cache=true`（AGP 9 + Hilt/KSP 组合下会报错）。已在 `gradle.properties` 中关闭。
9. Robolectric 测试用 `@Config(sdk = [35])`（本机 Robolectric 4.17 已验证可用的 SDK 级别）。
10. 数据库枚举用 `Int` 存 `@ColumnInfo(defaultValue = ...)`，避免迁移时 NOT NULL 无默认值导致 `validateMigrations` 失败。
