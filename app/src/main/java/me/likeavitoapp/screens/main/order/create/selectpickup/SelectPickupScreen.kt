package me.likeavitoapp.screens.main.order.create.selectpickup

import androidx.work.WorkQuery
import com.google.android.play.core.integrity.q
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.PickupPoint
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep


class SelectPickupScreen(
    selectedPickupPoint: UpdatableState<PickupPoint?>,
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {


    class State(
        var selectedPickupPoint: UpdatableState<PickupPoint?>,
        var query: UpdatableState<String> = UpdatableState(""),
        var suggestions: UpdatableState<List<String>> = UpdatableState(emptyList())
    )

    val state = State(selectedPickupPoint)

    fun PressBack() {
        recordScenarioStep()

        parentNavigator.backToPrevious()
    }

    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.post(query)
        state.suggestions.post(
            allSuggestions.filter { it.contains(query, ignoreCase = true) }
        )
    }

    fun ClickToClearAddress() {
        state.query.post("")
        state.suggestions.post(allSuggestions)
    }

    fun ClickToSelectSuggestion(address: String) {
        state.query.post(address)
        state.suggestions.post(emptyList())
    }

    private val allSuggestions = listOf("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург")

}
