package me.likeavitoapp.screens.main.order.create

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.DetailsTopBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun CreateOrderScreenProvider(screen: CreateOrderScreen) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        DetailsTopBar(
            title = stringResource(R.string.order_title, screen.ad.title),
            onBack = {
                screen.PressBackUseCase()
            }
        ) { innerPadding ->
            CreateOrderScreenView(screen, Modifier.padding(innerPadding))
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun CreateOrderScreenView(screen: CreateOrderScreen, modifier: Modifier) = with(screen) {
    val selectedOrderType by state.orderType.collectAsState()

    fun getTextBy(type: Order.Type): String {
        return when (type) {
            Order.Type.Pickup -> "Самовывоз"
            Order.Type.Delivery -> "Доставка"
        }
    }
    Column(modifier.selectableGroup()) {
        Order.Type.entries.forEach { orderType ->
            if (
                (orderType == Order.Type.Pickup && ad.isPickupEnabled)
                || (orderType == Order.Type.Delivery && ad.isDeliveryEnabled)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (orderType == selectedOrderType),
                            onClick = { screen.ClickToOrderTypeUseCase(orderType) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (orderType == selectedOrderType),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = getTextBy(orderType),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        AnimatedVisibility(selectedOrderType == Order.Type.Delivery) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        screen.ClickToSelectDeliveryAddressUseCase()
                    }, modifier = Modifier.wrapContentWidth()
                ) {
                    Text(stringResource(R.string.select_delivery_address_button))
                }
            }
        }

        AnimatedVisibility(selectedOrderType == Order.Type.Pickup) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        screen.ClickToSelectPickupPointUseCase()
                    }, modifier = Modifier.wrapContentWidth()
                ) {
                    Text(stringResource(R.string.select_pickup_point_button))
                }
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
                state.orderType.next(Order.Type.Pickup)
            }
        )
    }
}

