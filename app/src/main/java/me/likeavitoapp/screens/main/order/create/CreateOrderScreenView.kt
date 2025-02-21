package me.likeavitoapp.screens.main.order.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.get
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun CreateOrderScreenProvider(screen: CreateOrderScreen) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        CreateOrderScreenView(screen)
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun CreateOrderScreenView(screen: CreateOrderScreen) = with(screen) {
    val selectedOrderType = state.orderType.collectAsState()

    fun getTextBy(type: Order.Type): String {
        return when(type) {
            Order.Type.Pickup -> "Самовывоз"
            Order.Type.Delivery -> "Доставка"
        }
    }
    Column(Modifier.selectableGroup()) {
        Order.Type.entries.forEach { orderType ->
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
                    text = getTextBy(orderType),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        when (selectedOrderType.value) {
            Order.Type.Delivery -> {
                DeliveryModeView(screen)
            }

            Order.Type.Pickup -> {
                PickupModeView(screen)
            }
        }

        Button(onClick = {
            screen.ClickToOrderUseCase()
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Заказать")
        }
    }
}

@Composable
private fun DeliveryModeView(screen: CreateOrderScreen) = with(screen) {

}

@Composable
private fun PickupModeView(screen: CreateOrderScreen) = with(screen) {

    @Composable
    fun BoxScope.SelectedIcon() {
        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Default.Done,
            contentDescription = "selected"
        )
    }

    val selectedPickupPoint = state.selectedPickupPoint.collectAsState()

    if (ad.isPickupEnabled) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable {
                    screen.ClickToPickupUseCase()
                }
        ) {
            Column {
                Text(text = "Пункт выдачи", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = selectedPickupPoint.value?.address
                        ?: "Выбрать", style = MaterialTheme.typography.bodyMedium
                )
            }
            if (selectedPickupPoint.value != null) {
                SelectedIcon()
            }
        }
    }
}

@Preview
@Composable
fun CreateOrderScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        CreateOrderScreenProvider(
            screen = CreateOrderScreen(
                ad = MockDataProvider().ads.first(),
                navigator = mockScreensNavigator(),
            ).apply {
                state.orderType.post(Order.Type.Pickup, get.scope())
            }
        )
    }
}

