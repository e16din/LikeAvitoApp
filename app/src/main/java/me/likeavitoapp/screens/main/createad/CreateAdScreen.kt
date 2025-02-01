package me.likeavitoapp.screens.main.createad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.IScreen


class CreateAdScreen(
    val input: Input = Input(),
    val state: State = State(),
    ) : IScreen {

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
        var adCreated by mutableStateOf(Loadable(false))
        var exitDialog by mutableStateOf(false)
    }

    // UseCases:

    fun PressBackUseCase() {
        if (state.exitDialog != true) {
            state.exitDialog = true
        }
    }
}