package me.likeavitoapp.screens.order

import me.likeavitoapp.Order
import me.likeavitoapp.Screen


class OrderScreen(
    order: Order,
    val input: Input = Input(),
    val state: State = State(order),
    override var prevScreen: Screen?,
    ) : Screen {
    class Input {}
    class State(val order: Order) {

    }
}
