package me.likeavitoapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.log
import me.likeavitoapp.screens.RootScreen
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import kotlin.reflect.KClass


class AppModel {

    var user: User? = null

    lateinit var rootScreen: RootScreen

    fun onLogoutException() {
        if (rootScreen.navigator.screen.value !is AuthScreen) {
            rootScreen.LogoutUseCase()
        }
    }
}

class StubScreen() : IScreen

class ScreensNavigator(initialScreen: IScreen = StubScreen(), val tag: String = "") {
    val screens = mutableListOf(initialScreen)
    val screen = MutableStateFlow(initialScreen)

    fun startScreen(nextScreen: IScreen, clearStack: Boolean = false) {
        if (screens.last().javaClass.simpleName != nextScreen.javaClass.simpleName) {
            if (clearStack) {
                screens.clear()
            }
            log("$tag.startScreen: ${nextScreen.javaClass.simpleName}")
            screens.add(nextScreen)
            this@ScreensNavigator.screen.value = nextScreen
        }
    }

    fun backToPrevious() {
        screens.removeAt(screens.lastIndex)
        screen.value = screens.last()
        log("$tag.backToPrevious: ${screen.value.javaClass.simpleName}")
    }

    inline fun <reified T: IScreen> getScreenOrNull(klass: KClass<T>): T? {
        return screens.firstOrNull { it.javaClass.simpleName == klass.simpleName } as T?
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

