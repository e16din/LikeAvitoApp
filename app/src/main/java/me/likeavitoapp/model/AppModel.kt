package me.likeavitoapp.model


import com.yandex.mapkit.geometry.Point
import me.likeavitoapp.className
import me.likeavitoapp.log
import me.likeavitoapp.screens.root.RootScreen
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen
import kotlin.reflect.KClass


class AppModel {

    val user = UpdatableState<User?>(null)

    lateinit var rootScreen: RootScreen
    lateinit var mainScreen: MainScreen

    fun onLogoutException() {
        if (rootScreen.navigator.screen.value !is AuthScreen) {
            rootScreen.LogoutUseCase()
        }
    }
}

object InitialScreen : IScreen

class ScreensNavigator(initialScreen: IScreen = InitialScreen, val tag: String = "") {
    val screens = mutableListOf(initialScreen)
    val screen = UpdatableState(initialScreen)

    fun startScreen(
        screen: IScreen,
        clearAll: Boolean = false,
        clearAfterFirst: Boolean = false
    ) {
        if (clearAll) {
            screens.clear()
        }
        if (screens.size > 1 && clearAfterFirst) {
            val first = screens[1]
            screens.clear()
            screens.add(InitialScreen)
            screens.add(first)
        }

        log("$tag.startScreen: ${screen.className()}")
        screens.add(screen)

        log("screens: $screens")
        this@ScreensNavigator.screen.post(screen)
    }

    fun backToPrevious() {
        log("$tag.screens: ${screens}")
        screens.removeAt(screens.lastIndex)
        screen.post(screens.last())
        log("$tag.backToPrevious: ${screen.value.javaClass.simpleName}")
    }

    inline fun <reified T : IScreen> getScreenOrNull(klass: KClass<T>): T? {
        return screens.firstOrNull { it.javaClass.simpleName == klass.simpleName } as T?
    }

    fun hasScreen(): Boolean {
        val last = screens.lastOrNull()
        return last != null && last !is InitialScreen
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
    var reservedTimeMs: Long?,
    var isOrdered: Boolean = false,
    var newMessagesCount : Worker<Int> = Worker<Int>(0)
) : ISource {
    data class Address(val data: String)
    data class Owner(
        var id: Long,
        var name: String,
        var contacts: Contacts
    )
}

data class Region(val name: String, val id: Int)
data class PriceRange(var from: Int = 0, var to: Int = -1)

data class Order(
    val ad: Ad,
    val id: Long,
    val number: String,
    val type: Type,
    val state: State,
    val createdMs: Long,
    val expectedArrivalMs: Long,
    val pickupPoint: PickupPoint?
) {
    enum class Type {
        Pickup,
        Delivery
    }

    enum class State {
        Init,
        Active,
        Archived
    }

    data class PickupPoint(
        override val id: Long,
        val address: String,
        val openingHoursFrom: Int,
        val openingHoursTo: Int,
        val point: Point,
        val isInPlace: Boolean
    ) : ISource {
        enum class Type {
            Post,
            Cdek,
            Boxberry,
            OwnerAddress
        }

        class Point(val latitude: Double, val longitude: Double)
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


