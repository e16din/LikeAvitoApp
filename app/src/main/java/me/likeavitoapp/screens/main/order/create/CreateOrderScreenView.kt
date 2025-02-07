package me.likeavitoapp.screens.main.order.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import me.likeavitoapp.collectAsState
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen.OrderType


@Composable
fun CreateOrderScreenProvider(screen: CreateOrderScreen) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        CreateOrderScreenView(screen)
    }

    BackHandler {
        screen.PressBack()
    }
}

@Composable
fun CreateOrderScreenView(screen: CreateOrderScreen) = with(screen) {
    val selectedOrderType = screen.state.orderType.collectAsState()

    Column(Modifier.selectableGroup()) {
        listOf(OrderType.Delivery, OrderType.Pickup).forEach { orderType ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (orderType == selectedOrderType.value),
                        onClick = { screen.ClickToOrderTypeUseCase(orderType) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (orderType == selectedOrderType.value),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = orderType.text,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    when(selectedOrderType.value) {
        OrderType.Delivery -> {

        }
        OrderType.Pickup -> {

        }
    }
    // самовывоз
    //     адрес
    //     время
    //     посмотреть на карте
    // доставка
    //    компания доставки
    // оплатить
    // card number Номер карты
    // mm/yy Действует до
    // cvv/cvc три цифры с обратной стороны карты

}
