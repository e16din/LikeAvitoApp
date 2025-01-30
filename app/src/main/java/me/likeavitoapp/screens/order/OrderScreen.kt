package me.likeavitoapp.screens.order

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.IScreen


class OrderScreen(
    order: Order,
    val state: State = State(order),
    override var prevScreen: IScreen?,
    override var innerScreen: MutableStateFlow<IScreen>?,
    ) : IScreen {
    class Input {}
    class State(val order: Order) {

    }
}
