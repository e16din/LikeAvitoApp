package me.likeavitoapp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.AppBackend
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.IAppPlatform

val develop = true
val scenariosEnabled = false

lateinit var appModel: AppModel
lateinit var appBackend: AppBackend
lateinit var appPlatform: IAppPlatform
lateinit var actualScope: CoroutineScope

fun initMain(platform: IAppPlatform, scope: CoroutineScope) {
    appPlatform = platform
    actualScope = scope
    appBackend = AppBackend()
    appModel = AppModel()
}

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
        log("delay(${Math.round(timeMs/100f)*100})", tag)
        log("$methodName$end", tag)
    }
}