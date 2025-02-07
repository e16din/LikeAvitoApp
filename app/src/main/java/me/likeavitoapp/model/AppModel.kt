package me.likeavitoapp.model

import me.likeavitoapp.className
import me.likeavitoapp.log
import me.likeavitoapp.screens.RootScreen
import me.likeavitoapp.screens.auth.AuthScreen
import kotlin.reflect.KClass


class AppModel {

    val user = UpdatableState<User?>(null)

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
    val screen = UpdatableState(initialScreen)

    fun startScreen(nextScreen: IScreen, clearStack: Boolean = false) {
        if (screens.last().className() != nextScreen.className()) {
            if (clearStack) {
                screens.clear()
            }
            log("$tag.startScreen: ${nextScreen.javaClass.simpleName}")
            screens.add(nextScreen)
            this@ScreensNavigator.screen.post(nextScreen)
        }
    }

    fun backToPrevious() {
        screens.removeAt(screens.lastIndex)
        screen.post(screens.last())
        log("$tag.backToPrevious: ${screen.value.javaClass.simpleName}")
    }

    inline fun <reified T : IScreen> getScreenOrNull(klass: KClass<T>): T? {
        return screens.firstOrNull { it.javaClass.simpleName == klass.simpleName } as T?
    }

    fun hasScreen(): Boolean {
        val last = screens.lastOrNull()
        return last != null && last !is StubScreen
    }
}

interface IScreen

data class User(
    val id: Long,
    var name: String,
    var contacts: Contacts,
    var ownAds: List<Ad>,
    var photoUrl: UpdatableState<String>
)

data class Contacts(
    var phone: String? = null,
    var whatsapp: String? = null,
    var telegram: String? = null,
    var email: String? = null
)

data class Category(val name: String, val id: Int)

data class Ad(
    val id: Long,
    var title: String,
    val description: String,
    val photoUrls: List<String>,
    val contacts: Contacts,
    val price: Int,
    val isBargainingEnabled: Boolean,
    val isPremium: Boolean,
    var isFavorite: UpdatableState<Boolean> = UpdatableState(false),
    var reservedTimeMs: Long?,
    val timerLabel: UpdatableState<String> = UpdatableState(""),
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

