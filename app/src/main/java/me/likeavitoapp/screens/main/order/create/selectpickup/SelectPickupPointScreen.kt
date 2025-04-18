package me.likeavitoapp.screens.main.order.create.selectpickup

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.yandex.mapkit.geometry.Point
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.PickupPointType
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.load
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.order.create.payment.PaymentScreen


class SelectPickupPointScreen(
    val enabledTypes: List<PickupPointType>,
    val navigator: ScreensNavigator
) : IScreen {

    inner class State {
        val typeId = UpdatableState(
            get.sources().app.activeOrderRequest?.pickupPoint?.typeId
                ?: enabledTypes.first().id
        )
        val query = UpdatableState(TextFieldValue(""))
        val areaPoint = UpdatableState(Point())
        val points = Worker<List<Order.PickupPoint>>(emptyList())

        val tabIndex = UpdatableState(0)
    }

    val state = State()

    init {
        get.sources().app.activeOrderRequest!!.pickupPoint?.address?.let {
            ChangeQueryUseCase(
                TextFieldValue(it, TextRange(it.length))
            )
        }
    }

    fun PressBackUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ChangeQueryUseCase(query: TextFieldValue) {
        recordScenarioStep(query)

        state.query.next(query)

        loadPickupPoints()
    }

    private fun loadPickupPoints() {
        state.points.load {
            val result = get.sources().backend.mapService.getPickupPointsBy(
                state.query.value.text,
                state.typeId.value,
                state.areaPoint.value
            )
            return@load Pair(result.getOrNull(), result.isSuccess)
        }
    }

    fun ClickToClearAddressUseCase() {
        state.query.next(TextFieldValue(""))
        val orderRequest = get.sources().app.activeOrderRequest!!
        orderRequest.pickupPoint = null
        state.points.resetWith(emptyList())
        loadPickupPoints()
    }

    fun ClickToPickupPointUseCase(point: Order.PickupPoint) {
        state.query.next(
            TextFieldValue(point.address, TextRange(point.address.length))
        )
        state.areaPoint.next(Point(point.point.latitude, point.point.longitude))
        val orderRequest = get.sources().app.activeOrderRequest!!
        orderRequest.pickupPoint = point
    }

    fun ChangeAreaPointUseCase(point: Point) {
        recordScenarioStep()

        state.areaPoint.next(point)
    }

    fun SelectPickupPointTypeUseCase(typeId: Int) {
        recordScenarioStep()

        state.typeId.next(typeId)
        loadPickupPoints()
    }

    fun ClickToCloseUseCase() {
        recordScenarioStep()

        navigator.backToPrevious()
    }

    fun ClickToTabUseCase(tabIndex: Int) {
        recordScenarioStep(tabIndex)

        state.tabIndex.next(tabIndex)
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        val orderRequest = get.sources().app.activeOrderRequest!!
        if (orderRequest.pickupPoint != null) {
            navigator.startScreen(
                PaymentScreen(navigator)
            )
        } else {
            get.sources().app.message.next(
                get.sources().platform.getString(R.string.select_pickup_point_to_next_message)
            )
        }
    }
}
