package me.likeavitoapp.screens.main.order.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.get
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun OrderDetailsScreenProvider(screen: OrderDetailsScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        OrderDetailsScreenView(screen)
    }

    BackHandler {
        screen.PressBackUseCase()
    }

    DisposableEffect(Unit) {
        onDispose {
            screen.CloseScreenUseCase()
        }
    }
}

@Composable
fun OrderDetailsScreenView(screen: OrderDetailsScreen) = with(screen.state) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        OrderDetailsScreenView(
            OrderDetailsScreen(
                order = MockDataProvider().createOrder(0, Order.Type.Delivery),
                navigator = mockScreensNavigator(),
            )
        )
    }
}