package me.likeavitoapp.screens.order

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Order
import me.likeavitoapp.Screen


class OrderScreen(
    order: Order,
    val state: State = State(order),
    override var prevScreen: Screen?,
    override var innerScreen: MutableStateFlow<Screen>?,
    ) : Screen {
    class Input {}
    class State(val order: Order) {

    }
}
