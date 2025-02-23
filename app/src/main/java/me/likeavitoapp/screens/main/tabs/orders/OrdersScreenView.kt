package me.likeavitoapp.screens.main.tabs.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.get
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun OrdersScreenProvider(screen: OrdersScreen, tabsNavigator: ScreensNavigator) {

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            OrdersScreenView(screen)
        }

        NextTabProvider(screen, tabsNavigator)
    }

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }
}

@Composable
fun OrdersScreenView(screen: OrdersScreen) = with(screen) {
    val activeOrders by state.activeOrders.output.collectAsState()
    val newMessagesCount = state.newMessagesCount.output.collectAsState()
    LazyColumn {
        items(activeOrders.toMutableStateList()) {
            OrderView(
                screen = screen,
                order = it,
                newMessagesCount = newMessagesCount
            )
        }
    }
}

@Preview
@Composable
fun OrdersScreenPreview() {
    get = mockMainSet()
    val screen = OrdersScreen(
        navigator = mockScreensNavigator(),
    ).apply {
        state.activeOrders.output.post(
            listOf(MockDataProvider().createOrder(0, Order.Type.Delivery))
        )
    }
    LikeAvitoAppTheme {
        OrdersScreenProvider(
            screen = screen,
            tabsNavigator = mockScreensNavigator()
        )
    }
}