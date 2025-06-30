package me.likeavitoapp.model


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.Order.Type
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.main.MainScreen
import me.likeavitoapp.screens.main.payment.PaymentScreen
import me.likeavitoapp.screens.root.RootScreen


class AppModel {

    companion object {
        const val adsPageSize = 10
    }

    var user = UpdatableState<User?>(null)
    var categories = listOf<Category>()
    var regions = listOf<Region>()
    var pickupPointTypes = listOf<PickupPointType>()


    var activeOrderRequest: OrderRequest? = null
    var activeCreateAdRequest: OwnAd? = null

    val loading = UpdatableState(false)
    val message = UpdatableState<String?>(null)

    val totalNewMessagesCount = UpdatableState(listOf<Pair<Long, Int>>()) // <adId, count>

    lateinit var rootScreen: RootScreen
    lateinit var mainScreen: MainScreen
    var paymentScreen: PaymentScreen? = null

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
                get.sources().app.totalNewMessagesCount.next(pairs)
            }
        }
    }

    fun pay(onPay: (cardNumber: String, mmYy: String, cvvCvc: String) -> Unit) {

        val navigator = mainScreen.navigator
        paymentScreen = PaymentScreen(navigator, onPay)
        navigator.startScreen(paymentScreen!!)
    }
}

data class User(
    val id: Long,
    var name: String,
    var contacts: Contacts,
    var photoUrl: String,
    var ownAds: SnapshotStateList<OwnAd> = mutableStateListOf<OwnAd>()
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
    var newMessagesCount: Worker<Int> = Worker(0),
    var state: Int
) : ISource {
    data class Address(val data: String)
    data class Owner(
        var id: Long,
        var name: String,
        var contacts: Contacts
    )

    fun isActive() = state == 0
    fun isOrdered() = state == 1
    fun isArchived() = state == 2
}

data class Region(val name: String, val id: Int)
data class PriceRange(var from: Int = 0, var to: Int = -1)

data class PickupPointType(val name: String, val id: Int)


data class OrderRequest(
    var ad: Ad,
    val type: Type,
    var pickupPoint: PickupPoint? = null,
    var deliveryAddress: String? = null,
    var completed: Boolean = false
)

data class Order(
    override val id: Long,
    val ad: Ad,
    val number: String,
    val type: Type,
    val state: State,
    val createdMs: Long,
    val expectedArrivalMs: Long,
    val pickupPoint: PickupPoint?
) : ISource {
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
    val adId: Long,
    val userId: Long,
    val userName: String,
    val title: String,
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

// todo: it is possible to use Ad class instead of AdOwn?
data class OwnAdRequest(
    override val id: Long,
    var categoryId: Int? = null,
    var price: Int? = null,
    var selectedPickupPointTypes: MutableList<Int> = mutableListOf(),
    var address: String? = null,
    var title: String? = null,
    var description: String? = null,
    var photos: MutableList<ByteArray> = mutableListOf(),
    var isBargainingEnabled: Boolean = false,
    var isPremiumEnabled: Boolean = false,
    var isAutoupdateEnabled: Boolean = false,
) : ISource

data class OwnAd(
    override val id: Long,
    var categoryId: Int? = null,
    var price: Int? = null,
    var selectedPickupPointTypes: SnapshotStateList<Int> = mutableStateListOf(),
    var address: String? = null,
    var title: String? = null,
    var description: String? = null,
    var photoUrls: List<String> = listOf(),
    var isBargainingEnabled: Boolean = false,
    var isPremiumEnabled: Boolean = false,
    var isAutoupdateEnabled: Boolean = false,
    var newMessagesCount: Worker<Int> = Worker(0),
    val createdMs: Long,
    val updatedMs: Long,
    var state: Int = 0
) : ISource {
    var photoBytes: SnapshotStateList<ByteArray> = mutableStateListOf()

    fun isActive() = state == 0
    fun isOrdered() = state == 1
    fun isArchived() = state == 2
}


