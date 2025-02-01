package me.likeavitoapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.appBackend
import me.likeavitoapp.appPlatform
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.splash.SplashScreen


class AppModel(
    val backend: AppBackend = appBackend,
    val platform: IAppPlatform = appPlatform
) {
    var user: User? = null

    val navigator = ScreensNavigator(
        SplashScreen(
            sources = DataSources(
                app = this,
                platform = platform,
                backend = backend
            )
        )
    )

    val nav = Navigation()

    class Navigation(val roots: Roots = Roots()) {
        class Roots {
            fun authScreen() = AuthScreen()
        }
    }

    // UseCases:

    fun PressBack() {
        navigator.backToPrevious()
    }

    suspend fun Logout() {
        navigator.startScreen(nav.roots.authScreen())
        platform.appDataStore.clear()
    }
}

class StubScreen() : IScreen
class ScreensNavigator(initialScreen: IScreen = StubScreen()) {
    val screens = mutableListOf(initialScreen)
    val nextScreen = MutableStateFlow(initialScreen)

    fun startScreen(screen: IScreen) {
        screens.add(screen)
        nextScreen.value = screen
    }

    fun backToPrevious() {
        screens.removeAt(screens.lastIndex)
        nextScreen.value = screens.last()
    }
}

interface IScreen

data class User(
    val id: Long,
    var name: String,
    var contacts: Contacts,
    var ownAds: List<Ad>,
    var photoUrl: String
)

data class Contacts(
    val phone: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val email: String? = null
)

data class Category(val name: String, val id: Int)

data class Ad(
    val id: Long,
    val title: String,
    val description: String,
    val photoUrls: List<String>,
    val contacts: Contacts,
    val price: Int,
    val isBargainingEnabled: Boolean,
    val isPremium: Boolean,
    val isFavorite: MutableStateFlow<Boolean>,
    val category: Category,
    val address: Address,
    val owner: Owner
) {
    data class Address(val address: String)
    data class Owner(
        var id: Long,
        var name: String,
        var contacts: Contacts
    )
}


data class Region(val name: String, val id: Int)
data class PriceRange(var from: Int = 0, var to: Int = -1)


data class Order(val ad: Ad, val buyType: BuyType, val state: State) {
    enum class State {
        New,
        Active,
        Archived
    }

    sealed class BuyType {
        class Pickup(address: Ad.Address)
        class Delivery(delivery: DeliveryType)
    }

    enum class DeliveryType {
        Post,
        Cdek,
        Boxberry,
    }
}

interface IMessage {
    val dateMs: Long
    val isNew: Boolean
}

data class TextMessage(
    val text: String,
    override val dateMs: Long,
    override val isNew: Boolean = true
) : IMessage

data class OfferMessage(
    val newPrice: Int,
    val isConsentReceived: Boolean,
    override val dateMs: Long,
    override val isNew: Boolean = true
) : IMessage

