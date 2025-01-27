package me.likeavitoapp.scenario

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.screens.splash.SplashScreen

class SplashScenarios(
    val scope: CoroutineScope,
    val sources: DataSources<SplashScreen>
) {
    fun ClearDataScenario() {
        Log.i("scenario", "Scenario: Clear data!")
        scope.launch {
            sources.platform.authDataStore.clear()
            Log.i("scenario", "Scenario complete.")
        }
    }
}