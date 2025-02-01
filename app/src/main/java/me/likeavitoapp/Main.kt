package me.likeavitoapp

import android.util.Log
import me.likeavitoapp.model.AppBackend
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.IAppPlatform

val develop = true
val scenariosEnabled = false

lateinit var appModel: AppModel
lateinit var appBackend: AppBackend
lateinit var appPlatform: IAppPlatform

fun initMain(platform: IAppPlatform) {
    appPlatform = platform
    appBackend = AppBackend()
    appModel = AppModel()
}

fun println(key: String = "debug", text: String) {
    if (develop) {
        Log.d(key, text)
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
        println("record_scenario", "delay(${Math.round(timeMs/100f)*100})")
        println("record_scenario", "$methodName$end")
    }
}