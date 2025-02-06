package me.likeavitoapp.screens.main.order

import me.likeavitoapp.model.Order
import me.likeavitoapp.model.BaseScreen


class OrderScreen(
    order: Order,
    val state: State = State(order),
    ) : BaseScreen() {
    class Input {}
    class State(val order: Order) {

    }
}
