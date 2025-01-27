package me.likeavitoapp.screens.main.createad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Ad
import me.likeavitoapp.Category
import me.likeavitoapp.DataSources
import me.likeavitoapp.Loadable
import me.likeavitoapp.Screen


class CreateAdScreen(
    val input: Input = Input(),
    val state: State = State(),
    override var prevScreen: Screen?,
    override var innerScreen: MutableStateFlow<Screen>?,
    ) : Screen {

    class Input {
        var onTitleChanged: (title: String) -> Unit = {}
        var onDescriptionChanged: (title: String) -> Unit = {}
        var onPriceChanged: (price: Int) -> Unit = {}
        var onAddPhotoClick: () -> Unit = {}
        var onContactsChanged: (price: Int) -> Unit = {}
        var onPremiumStatusChanged: (isPremium: Boolean) -> Unit = {}
        var onAddressChanged: (address: Ad.Address) -> Unit = {}
        var onCategoryChanged: (category: Category) -> Unit = {}
        var onDeliveryEnableChanged: (enable: Boolean) -> Unit = {}
        var onPickupEnableChanged: (enable: Boolean) -> Unit = {}

        var onBackClick: () -> Unit = {}
        var onDoneClick: () -> Unit = {}
    }

    class State {
        var ad by mutableStateOf(
            Loadable(
                Ad(
                    id = TODO(),
                    title = TODO(),
                    description = TODO(),
                    photoUrls = TODO(),
                    contacts = TODO(),
                    price = TODO(),
                    isPremium = TODO(),
                    isFavorite = TODO(),
                    category = TODO(),
                    address = TODO(),
                    owner = TODO()
                )
            )
        )
        var exitDialog by mutableStateOf(false)
    }

    // UseCases:

    fun PressBackUseCase() {
        if (state.exitDialog != true) {
            state.exitDialog = true
        }
    }
}