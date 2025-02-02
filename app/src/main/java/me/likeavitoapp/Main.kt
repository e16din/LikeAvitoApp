package me.likeavitoapp

import android.util.Log
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import me.likeavitoapp.model.AppBackend
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IAppPlatform
import me.likeavitoapp.screens.RootScreen

val develop = true

private var appModel: AppModel? = null
private var appBackend: AppBackend? = null
private var appPlatform: IAppPlatform? = null
private var actualScope: CoroutineScope? = null
private var actualDataSources: DataSources? = null

fun initApp(platform: IAppPlatform, scope: CoroutineScope): AppModel {
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

@OptIn(DelicateCoroutinesApi::class)
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("Error!")
    println(throwable.message)
    if (throwable is AuthFiledException) {
        appModel?.onLogoutException()

    } else {
        throwable.printStackTrace()
    }
}
val defaultContext = Job() + exceptionHandler

fun log(text: String, tag: String = "debug") {
    if (develop) {
        Log.d(tag, text)
    }
}

fun checkState(condition: Boolean) {
    assert(condition)
}

private var lastCallMs = System.currentTimeMillis()
fun recordScenarioStep(value: Any? = Unit) {
    if (develop) {
        var end = "()"
        if (value != Unit) {
            end = "(${value})"
        }
        val methodName = Thread.currentThread().stackTrace.first {
            it.methodName.first().isUpperCase()
        }.methodName

        val timeMs = System.currentTimeMillis() - lastCallMs
        lastCallMs = System.currentTimeMillis()
        val tag = "record_scenario"
        log("delay(${Math.round(timeMs / 100f) * 100})", tag)
        log("$methodName$end", tag)
    }
}