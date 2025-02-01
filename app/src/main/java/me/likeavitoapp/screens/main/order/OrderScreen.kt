package me.likeavitoapp.screens.main.order

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.IScreen


class OrderScreen(
    order: Order,
    val state: State = State(order),
    ) : IScreen {
    class Input {}
    class State(val order: Order) {

    }
}
