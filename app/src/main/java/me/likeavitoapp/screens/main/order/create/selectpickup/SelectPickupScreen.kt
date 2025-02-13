package me.likeavitoapp.screens.main.order.create.selectpickup

import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.load
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.MapItem
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep


class SelectPickupScreen(
    selectedPickupPoint: UpdatableState<PickupPoint?>,
    val navigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    class State(val selectedPickupPoint: UpdatableState<PickupPoint?>) {
        val pickupPointType = UpdatableState(PickupPoint.Type.OwnerAddress)
        val query = UpdatableState("")
        val areaPoint = UpdatableState(Point())
        val suggestions = Loadable<List<MapItem>>(emptyList())
    }

    val state = State(selectedPickupPoint)

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ChangeQueryUseCase(query: String) {
        recordScenarioStep(query)

        state.query.post(query)

        scope.launchWithHandler {
            state.suggestions.load(loading = {
                sources.backend.mapService.getAddressesBy(query, state.areaPoint.value)
            }, onSuccess = { data ->
                state.suggestions.data.post(data)
            })
        }
    }

    fun ClickToClearAddress() {
        state.query.post("")
        state.suggestions.resetWith(emptyList())
    }

    fun ClickToSelectSuggestion(item: MapItem) {
        state.query.post(item.name)
        state.suggestions.resetWith(emptyList())
        state.areaPoint.post(item.point)
    }

    fun ChangeAreaPointUseCase(point: Point) {
        recordScenarioStep()

        state.areaPoint.post(point)
    }

    fun SelectPickupPointTypeUseCase(type: PickupPoint.Type) {
        recordScenarioStep()

        state.pickupPointType.post(type)
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
