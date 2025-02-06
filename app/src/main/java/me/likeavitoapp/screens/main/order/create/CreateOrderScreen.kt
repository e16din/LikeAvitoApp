package me.likeavitoapp.screens.main.order.create

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.BaseScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources


class CreateOrderScreen(
    ad: Ad,
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : BaseScreen() {

    fun PressBack() {
        parentNavigator.backToPrevious()
    }

    val state = State(ad)

    class State(val ad: Ad)

}
