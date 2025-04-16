package me.likeavitoapp.screens.main.order.create.selectpickup

import com.yandex.mapkit.geometry.Point
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.MapItem
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.PickupPointType
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.recordScenarioStep


class SelectPickupScreen(
    val selectedPickupPoint: UpdatableState<PickupPoint?>,
    val enabledTypes: List<PickupPointType>,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val selectedTypeId = UpdatableState(
            selectedPickupPoint.value?.typeId
                ?: enabledTypes.first().id
        )
        val query = UpdatableState("")
        val areaPoint = UpdatableState(Point())
        val suggestions = Worker<List<MapItem>>(emptyList())
    }

    val state = State()

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.next(query)

        get.scope().launchWithHandler {
            state.suggestions.load(loading = {
                get.sources().backend.mapService.getAddressesBy(query, state.areaPoint.value)
            }, onSuccess = { data ->
                state.suggestions.output.next(data)
            })
        }
    }

    fun ClickToClearAddress() {
        state.query.next("")
        state.suggestions.resetWith(emptyList())
    }

    fun ClickToSelectSuggestion(item: MapItem) {
        state.query.next(item.name)
        state.suggestions.resetWith(emptyList())
        state.areaPoint.next(item.point)
    }

    fun ChangeAreaPointUseCase(point: Point) {
        recordScenarioStep()

        state.areaPoint.next(point)
    }

    fun SelectPickupPointTypeUseCase(typeId: Int) {
        recordScenarioStep()

        state.selectedTypeId.next(typeId)
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

}
