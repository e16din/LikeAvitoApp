package me.likeavitoapp.screens.main.order.create.selectdelivery

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yandex.mapkit.MapKitFactory
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.mocks.mockAds
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.OrderRequest
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDeliveryAddressScreenProvider(screen: SelectDeliveryAddressScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        ActionTopBar(
            title = stringResource(R.string.select_delivery_address_title),
            onClose = {
                screen.ClickToCloseUseCase()
            },
            onDone = {
                screen.ClickToDoneUseCase()
            },
            withDoneButton = true
        ) { innerPadding ->
            SelectDeliveryAddressScreenView(screen, Modifier.padding(innerPadding))
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun SelectDeliveryAddressScreenView(screen: SelectDeliveryAddressScreen, modifier: Modifier) = with(screen) {
    val query by screen.state.query.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        val addressText by screen.state.query.collectAsState()
        val addresses by screen.state.addresses.output.collectAsState()

        Column {
            TextField(
                value = query,
                onValueChange = { newText ->
                    screen.ChangeQueryUseCase(newText)
                },
                label = { Text(stringResource(R.string.enter_address_label)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (!addressText.isNotEmpty()) {
                        IconButton(onClick = {
                            screen.ClickToClearAddress()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            )

            LazyColumn {
                items(addresses) { address ->
                    Text(
                        text = address,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                screen.ClickToAddressUseCase(address)
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SelectDeliveryAddressScreenPreview() {
    get = mockMainSet()
    get.sources().app.activeOrderRequest = OrderRequest(
        ad = mockAds().first(),
        type = Order.Type.Delivery,
        pickupPoint = PickupPoint(
            id = 0,
            typeId = 1,
            address = "г.Москва, пр-т.Ленина, д.48",
            openingHoursFrom = 8,
            openingHoursTo = 21,
            point = PickupPoint.Point(0.0, 0.0),
            isInPlace = true
        )
    )
    LikeAvitoAppTheme {
        SelectDeliveryAddressScreenProvider(
            screen = SelectDeliveryAddressScreen(
                navigator = mockScreensNavigator(),
            )
        )
    }
}