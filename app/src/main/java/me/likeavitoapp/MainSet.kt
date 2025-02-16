package me.likeavitoapp

import androidx.compose.runtime.Composable
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import me.likeavitoapp.model.AppBackend
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IAppPlatform
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ISource
import me.likeavitoapp.screens.root.RootScreen
import kotlin.reflect.KClass

const val develop = true
var get = MainSet()

class MainSet {
    private var appModel: AppModel? = null
    private var appBackend: AppBackend? = null
    private var appPlatform: IAppPlatform? = null

    private var actualScope: CoroutineScope? = null
    private var actualDataSources: DataSources? = null

    fun init(platform: AppPlatform, scope: CoroutineScope): AppModel {
        if (appModel != null) {
            return appModel!!
        }

        try {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
            MapKitFactory.initialize(platform)
        } catch (e: RuntimeException) {
            // NOTE: unit-tests workaround
        }

        appPlatform = platform
        actualScope = scope
        appBackend = AppBackend()
        appModel = AppModel().apply {
            rootScreen = RootScreen()
        }

        return appModel!!
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.log()
        if (throwable is UnauthorizedException) {
            appModel?.onLogoutException()
        }
    }

    var defaultContext = SupervisorJob() + exceptionHandler

    // NOTE: Use it after call initApp()
    fun sources() = actualDataSources!!
    fun scope() = actualScope!!
    fun app() = appModel!!
    fun platform() = appPlatform as AppPlatform
}

@Composable
fun isPreviewMode(): Boolean = runCatching { get.app() }.isFailure

fun log(text: String, tag: String = "debug") {
    if (develop) {
        println("tag:$tag | $text")
    }
}

fun logError(text: String, tag: String = "debug", prefix: String = "Error: ") {
    if (develop) {
        println("tag:$tag | $prefix$text")
    }
}

fun Throwable.log() {
    logError(this.message ?: this.className())
}

fun checkState(condition: Boolean) {
    assert(condition)
}

class ScenarioStep(
    val owner: IScreen,
    val useCase: String,
    val delayMs: Long,
    val argument: Any? = null
)

private var lastCallMs = System.currentTimeMillis()
private val scenarioSteps = mutableListOf<ScenarioStep>()
private val scenarioDataSourcesMap = mutableMapOf<KClass<*>, List<ISource>>()


fun runRecordedScenario(scope: CoroutineScope) {
    get.scope().launchWithHandler {
        scenarioSteps.forEach { step ->
            step.owner::class.members.firstOrNull { it.name == step.useCase }?.apply {
                if (step.argument == null) {
                    call()
                } else {
                    if (step.argument is ISource) {
                        val data = scenarioDataSourcesMap[step.argument::class]
                            ?.first { it.id == step.argument.id }
                        call(data)

                    } else {
                        call(step.argument)
                    }
                }
            }
            delay(step.delayMs)
        }
    }
}

fun IScreen.bindScenarioDataSource(key: KClass<*>, list: List<ISource>) {
    scenarioDataSourcesMap[key] = list
}

class NameAndId(val name: String, val id: Long)

fun IScreen.recordScenarioStep(argument: Any? = null) {
    if (develop) {
        var end = "()"
        if (argument != Unit && argument != null) {
            end = "(${argument})"
        }
        val methodName = Thread.currentThread().stackTrace.first {
            it.methodName.first().isUpperCase()
        }.methodName

        val delayMs = System.currentTimeMillis() - lastCallMs
        lastCallMs = System.currentTimeMillis()

        val argumentValue =
            if (argument is ISource) NameAndId(argument.className(), argument.id) else argument
        scenarioSteps.add(ScenarioStep(this, methodName, delayMs, argumentValue))
        log("delay(${Math.round(delayMs / 100f) * 100})")
        log("$methodName$end")
    }
}