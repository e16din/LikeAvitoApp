package me.likeavitoapp.model


import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.className
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.log
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.Order.Type
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen
import me.likeavitoapp.screens.root.RootScreen
import kotlin.reflect.KClass


class AppModel {

    companion object {
        const val adsPageSize = 10
    }

    var user = UpdatableState<User?>(null)
    var categories = listOf<Category>()
    var regions = listOf<Region>()
    var pickupPointTypes = listOf<PickupPointType>()

    var activeOrderRequest: OrderRequest? = null
    var activeCreateAdRequest: CreateAdRequest? = null

    val loading = UpdatableState(false)
    val message = UpdatableState<String?>(null)

    val newMessagesCount = UpdatableState(listOf<Pair<Long, Int>>()) // <adId, count>

    lateinit var rootScreen: RootScreen
    lateinit var mainScreen: MainScreen

    fun onLogoutException() {
        if (rootScreen.navigator.screen.value !is AuthScreen) {
            rootScreen.LogoutUseCase()
        }
    }

    fun updateNewMessagesIndicator() {
        work {
            val result = get.sources().backend.messagesService.getAllNewMessagesCount()
            val pairs = result.getOrNull() ?: emptyList()
            withContext(Dispatchers.Main) {
                get.sources().app.newMessagesCount.next(pairs)
            }
        }
    }

    fun pay(onDone: (success:Boolean)->Unit){
        onDone(false)
    }
}


class ScreensNavigator(val tag: String = "", initialScreen: IScreen? = null) {
    val screens = if (initialScreen != null)
        mutableListOf(initialScreen)
    else
        mutableListOf()
    val screen = UpdatableState(initialScreen)
    var onResume: (() -> Unit)? = null

    fun startScreen(
        screen: IScreen,
        clearAll: Boolean = false,
        clearAfterFirst: Boolean = false,
        onResume: (() -> Unit)? = null
    ) {
        this.onResume = onResume

        if (clearAll) {
            screens.clear()
        }
        if (screens.size > 0 && clearAfterFirst) {
            val first = screens[0]
            screens.clear()
            screens.add(first)
        }

        log("$tag.startScreen: ${screen.className()}")
        screens.add(screen)

        log("screens: $screens")
        this@ScreensNavigator.screen.next(screen)
    }

    fun backToPrevious() {
        val last = screens.removeAt(screens.lastIndex) // pop

        if (screens.size == 0) {
            screen.next(null)
            log("$tag.backToPrevious: null")

        } else {
            val prev = screens.last()
            screen.next(prev)
            log("$tag.backToPrevious: ${prev.javaClass.simpleName}")
        }

        onResume?.invoke()
    }

    inline fun <reified T : IScreen> getScreenOrNull(klass: KClass<T>): T? {
        return screens.firstOrNull { it.javaClass.simpleName == klass.simpleName } as T?
    }

    fun hasScreen(): Boolean {
        val last = screens.lastOrNull()
        return last != null
    }

    fun reset() {
        screens.clear()
        screen.next(null)
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
    val categoryId: Int,
    val regionId: Int,
    val address: Address?,
    val isPickupEnabled: Boolean,
    val isDeliveryEnabled: Boolean,
    val enabledPickupPointTypes: List<Int>,
    val owner: Owner,
    val isFavorite: UpdatableState<Boolean> = UpdatableState(false),
    val timerLabel: UpdatableState<String> = UpdatableState(""),
    var reservedTimeMs: Long?,
    var isOrdered: Boolean = false,
    var newMessagesCount: Worker<Int> = Worker<Int>(0)
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

data class PickupPointType(val name: String, val id: Int)

data class CreateAdRequest(
    var categoryId: Int?,
    var selectedTypes: List<Int>,
    var address: String?,
    var title: String?,
    var description: String?,
    var photos: List<ByteArray>,
    var isBargainingEnabled: Boolean,
    var completed: Boolean
)

data class OrderRequest(
    var ad: Ad,
    val type: Type,
    var pickupPoint: PickupPoint? = null,
    var deliveryAddress: String? = null,
    var completed: Boolean = false
)

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
        val typeId: Int,
        val address: String,
        val openingHoursFrom: Int,
        val openingHoursTo: Int,
        val point: Point,
        val isInPlace: Boolean
    ) : ISource {
        class Point(val latitude: Double, val longitude: Double)
    }
}

data class Chat(
    override val id: Long,
    val ad: Ad,
    val messages: List<TextMessage>
) : ISource

interface IMessage : ISource {
    val text: String
    val userId: Long
}

var previewCount = -1L

data class PreviewTextMessage(
    override val text: String,
    override val userId: Long,
    var loading: Boolean,
    override val id: Long = previewCount,
) : IMessage {
    init {
        previewCount--
    }
}

data class TextMessage(
    override val text: String,
    override val id: Long,
    override val userId: Long,
    var isNew: Boolean = true,
    val dateMs: Long
) : IMessage


data class MapItem(val name: String, val point: Point)


