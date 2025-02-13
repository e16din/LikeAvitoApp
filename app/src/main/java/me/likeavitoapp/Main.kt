package me.likeavitoapp

import android.util.Log
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
import me.likeavitoapp.screens.RootScreen
import kotlin.reflect.KClass

const val develop = true

private var appModel: AppModel? = null
private var appBackend: AppBackend? = null
private var appPlatform: IAppPlatform? = null
private var actualScope: CoroutineScope? = null
private var actualDataSources: DataSources? = null


fun initApp(platform: AppPlatform, scope: CoroutineScope): AppModel {
    if (appModel != null) {
        return appModel!!
    }

    MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    MapKitFactory.initialize(platform)

    appPlatform = platform
    actualScope = scope
    appBackend = AppBackend()
    appModel = AppModel().apply {
        rootScreen = RootScreen(
            scope = scope,
            sources = DataSources(
                app = this,
                platform = appPlatform!!,
                backend = appBackend!!,
            ).apply {
                actualDataSources = this
            }
        )
    }

    return appModel!!
}

// NOTE: Use it after call initApp()
fun provideDataSources() = actualDataSources!!
fun provideCoroutineScope() = actualScope!!
fun provideRootScreen() = appModel!!.rootScreen
fun provideApp() = appModel!!
fun provideAndroidAppContext() = appPlatform as AppPlatform

@Composable
fun isPreviewMode(): Boolean = runCatching { provideApp() }.isFailure

@OptIn(DelicateCoroutinesApi::class)
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.log()
    if (throwable is UnauthorizedException) {
        appModel?.onLogoutException()
    }
}
val defaultContext = SupervisorJob() + exceptionHandler

fun log(text: String, tag: String = "debug") {
    if (develop) {
        Log.i(tag, text)
    }
}

fun logError(text: String, tag: String = "debug") {
    if (develop) {
        Log.e(tag, "Error: $text")
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
    scope.launchWithHandler {
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