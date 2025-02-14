package me.likeavitoapp.model


import com.yandex.mapkit.geometry.Point
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

    fun startScreen(
        screen: IScreen,
        clearAll: Boolean = false,
        clearAfterFirst: Boolean = false
    ) {
        if (screens.last().className() != screen.className()) {
            if (clearAll) {
                screens.clear()
            }
            if (clearAfterFirst) {
                val first = screens.first()
                screens.clear()
                screens.add(first)
            }

            log("$tag.startScreen: ${screen.javaClass.simpleName}")
            screens.add(screen)
            this@ScreensNavigator.screen.post(screen)
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

interface ISource {
    val id: Long
}

data class Ad(
    override val id: Long,
    val title: String,
    val description: String,
    val photoUrls: List<String>,
    val contacts: Contacts,
    val price: Int,
    val isBargainingEnabled: Boolean,
    val isPremium: Boolean,
    val categoryId: Long,
    val address: Address?,
    val isPickupEnabled: Boolean,
    val owner: Owner,
    val isFavorite: UpdatableState<Boolean> = UpdatableState(false),
    val timerLabel: UpdatableState<String> = UpdatableState(""),
    var reservedTimeMs: Long?
) : ISource {
    data class Address(val address: String)
    data class Owner(
        var id: Long,
        var name: String,
        var contacts: Contacts
    )
}

data class Region(val name: String, val id: Int)
data class PriceRange(var from: Int = 0, var to: Int = -1)

data class Order(val ad: Ad, val type: Type, val state: State) {
    enum class State {
        Edit,
        Active,
        Archived
    }

    data class PickupPoint(
        override val id: Long,
        val address: String,
        val openingHoursFrom: Int,
        val openingHoursTo: Int,
        val point: Point
    ) : ISource {
        enum class Type {
            Post,
            Cdek,
            Boxberry,
            OwnerAddress
        }

        class Point(val latitude: Double, val longitude: Double)
    }

    sealed class Type(open val name: String) {
        class Delivery(override val name: String, val address: Ad.Address) : Type(name)
        class Pickup(override val name: String, val type: PickupPoint.Type) : Type(name)
    }
}

interface IMessage : ISource {
    val dateMs: Long
    val isNew: Boolean
    val isMy: Boolean
}

data class TextMessage(
    val text: String,
    override val id: Long,
    override val isNew: Boolean = true,
    override val dateMs: Long,
    override val isMy: Boolean
) : IMessage

data class OfferMessage(
    val newPrice: Int,
    val isConsentReceived: Boolean,
    override val id: Long,
    override val dateMs: Long,
    override val isNew: Boolean = true,
    override val isMy: Boolean
) : IMessage

data class MapItem(val name: String, val point: Point)

data class PaymentData(
    val cardNumber: Worker<String> = Worker(""), // 1111 1111 1111 1111
    val mmYy: Worker<String> = Worker(""), // mm/yy
    val cvvCvc: Worker<String> = Worker("") // 123
)

