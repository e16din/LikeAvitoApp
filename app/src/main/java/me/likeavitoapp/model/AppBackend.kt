package me.likeavitoapp.model


import androidx.compose.runtime.toMutableStateList
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import me.likeavitoapp.mocks.MockDataProvider


class AppBackend(val client: HttpClient = HttpClient()) {

    var token: String? = null

    var userService = UserService()
    var adsService = AdsService()
    var orderService = OrderService()
    var mapService = MapService()
    var messagesService = MessagesService()


    private var mockDataProvider = MockDataProvider()

    data class LoginResult(val user: User, val token: String)

    inner class MessagesService {
        suspend fun listenChatUpdates(
            addId: Long,
            onUpdate: suspend (List<TextMessage>) -> Unit
        ) {
            delay(3000)
            val chatWithUserId = mockDataProvider.chats.first { it.adId == addId }.userId
            if (addId == 28L) {
                onUpdate(
                    listOf(
                        mockDataProvider.createMessage(chatWithUserId, "Прицениваюсь пока", true),
                    )
                )

                delay(12000)
                onUpdate(
                    listOf(
                        mockDataProvider.createMessage(
                            chatWithUserId,
                            "Скинь 500р. - сразу заберу",
                            true
                        )
                    )
                )

                delay(9000)
                onUpdate(
                    listOf(
                        mockDataProvider.createMessage(chatWithUserId, "Договорились", true),
                        mockDataProvider.createMessage(chatWithUserId, "Покупаю", true),
                    )
                )

            } else {
                onUpdate(
                    listOf(
                        mockDataProvider.createMessage(chatWithUserId, "Бла бла бла", true),
                    )
                )
            }
        }

        suspend fun sendMessage(
            chatId: Long,
            message: String
        ): Result<TextMessage> {
            delay(300)
            return Result.success(
                mockDataProvider.createMessage(
                    mockDataProvider.activeUser!!.id,
                    message
                )
            )
        }

        suspend fun loadChat(adId: Long): Result<Chat> {
            delay(500)
            val ad = mockDataProvider.ads.first { it.id == adId }
            return Result.success(
                mockDataProvider.chats.firstOrNull { it.adId == adId }
                    ?: Chat(
                        id = 99,
                        adId = ad.id,
                        userId = ad.owner.id,
                        userName = ad.owner.name,
                        title = ad.title,
                        messages = listOf()
                    ).apply {
                        mockDataProvider.chats.add(this)
                    }
            )
        }

        suspend fun getActiveChats(): Result<List<Chat>> {
            delay(460)
            return Result.success(mockDataProvider.chats)
        }

        suspend fun getAllNewMessagesCount(): Result<List<Pair<Long, Int>>> {
            delay(200)

            val result = mutableListOf<Pair<Long, Int>>()
            mockDataProvider.chats.forEach { chat ->
                val count = chat.messages.count { it.isNew }
                result.add(Pair(chat.adId, count))
            }
            return Result.success(result)
        }
    }

    inner class MapService {
        val searchManager by lazy {
            SearchFactory.getInstance().createSearchManager(SearchManagerType.OFFLINE)
        }

        suspend fun getPickupPoints(centerPoint: Order.PickupPoint.Point): Result<List<Order.PickupPoint>> {
            delay(800)
            return Result.success(mockDataProvider.pickupPoints.filter {
                return@filter it.point.latitude < centerPoint.latitude + 0.5
                        && it.point.latitude > centerPoint.latitude - 0.5
                        && it.point.longitude < centerPoint.longitude + 0.5
                        && it.point.longitude > centerPoint.longitude - 0.5
            })
        }

        suspend fun getAddressesBy(query: String): Result<List<String>> {
            delay(800)
            return Result.success(mockDataProvider.addresses.filter {
                it.lowercase().contains(query.lowercase())
            })
        }

        suspend fun getPickupPointsBy(
            query: String,
            typeId: Int,
            centerPoint: Point
        ): Result<List<Order.PickupPoint>> {
            delay(1100)
            val centerPoint = Point(55.7, 37.6) // test
            return Result.success(mockDataProvider.pickupPoints.filter {
                it.typeId == typeId
                        && it.address.lowercase().contains(query.lowercase())
                        && (it.point.latitude < centerPoint.latitude + 0.5
                        && it.point.latitude > centerPoint.latitude - 0.5
                        && it.point.longitude < centerPoint.longitude + 0.5
                        && it.point.longitude > centerPoint.longitude - 0.5)
            })
        }
    }

    // NOTE: this is mock for an example
    inner class UserService {

        suspend fun login(username: String, password: String): Result<LoginResult> {
            delay(1500)
            if (username == "ss@ss.ss" && password == "123456") {
                val userId = 0L
                val user = mockDataProvider.users.first { it.id == userId }
                mockDataProvider.activeUser = user

                return Result.success(
                    LoginResult(
                        user = user,
                        token = mockDataProvider.token
                    )
                )
            } else {
                return Result.failure(UnauthorizedException())
            }
        }

        suspend fun logout(): Result<Boolean> {
            delay(300)
            mockDataProvider.activeUser = null

            return Result.success(true)
        }

        suspend fun getUser(userId: Long): Result<User> {
            delay(1000)
            val user = mockDataProvider.users.first { it.id == userId }
            mockDataProvider.activeUser = user

            return Result.success(user)
        }

        suspend fun postPhoto(photoBase64: String): Result<String> { //todo: return url on prod
            delay(2000)
            return Result.success(photoBase64)
        }

        suspend fun updateUser(
            userId: Long,
            name: String,
            phone: String?,
            telegram: String?,
            whatsapp: String?,
            email: String?
        ): Result<User> {
            delay(2000)
            return Result.success(mockDataProvider.users.first { it.id == userId }
                .copy(
                    name = name,
                    contacts = Contacts(
                        email = email,
                        whatsapp = whatsapp,
                        telegram = telegram,
                        phone = phone
                    )
                )
            )
        }
    }

    // NOTE: this is mock for an example
    inner class AdsService {
        suspend fun getCategories(): Result<List<Category>> {
            delay(600)
            return Result.success(mockDataProvider.categories)
        }

        suspend fun getRegions(): Result<List<Region>> {
            delay(500)
            return Result.success(mockDataProvider.regions)
        }

        suspend fun getAds(
            range: PriceRange,
            regionId: Int?,
            categoryId: Int?,
            query: String?,
            resetPage: Boolean
        ): Result<List<Ad>> {
            delay(2000)

            val ads =
                mockDataProvider.getNextAdsPage(range, regionId, categoryId, query, resetPage)
            return Result.success(ads)
        }

        suspend fun getSearchTips(query: String): Result<List<String>> {
            delay(500)
            return Result.success(mockDataProvider.searchTips.filter {
                it.contains(
                    query,
                    ignoreCase = true
                )
            })
        }

        suspend fun updateFavoriteState(ad: Ad): Result<Boolean> {
            delay(500)
            return Result.success(true)
        }

        suspend fun getFavorites(): Result<List<Ad>> {
            delay(1500)
            return Result.success(mockDataProvider.getFavorites())
        }

        suspend fun getNewMessagesCount(adId:Long): Result<Int> {
            delay(200)
//            val count = Random(5).nextInt()
            val count = mockDataProvider.ads.firstOrNull{
                it.id == adId
            }?.newMessagesCount?.data() ?: mockDataProvider.ownAds.firstOrNull{
                it.id == adId
            }?.newMessagesCount?.data() ?: 0
            return Result.success(count)
        }

        suspend fun deleteAllFavorites(): Result<Boolean> {
            delay(300)
            mockDataProvider.ads = mockDataProvider.ads.apply {
                forEach { it.isFavorite.next(false) }
            }
            return mockDataProvider.getSuccessOrFail(true)
        }

        suspend fun postTip(tip: String) {
            delay(400)
            mockDataProvider.searchTips.add(0, tip)
        }

        suspend fun createAd(data: OwnAd): Result<Boolean> {
            delay(300)

            mockDataProvider.ownAds.add(data)

            return Result.success(true)
        }

    }

    // NOTE: this is mock for an example
    inner class OrderService {
        suspend fun getLastDeliveryAddresses(): Result<List<String>> {
            delay(700)
            return Result.success(mockDataProvider.lastDeliveryAddresses)
        }

        suspend fun reserve(adId: Long): Result<Boolean> {
            delay(400)
            val testFailId = 2L
            val testFailId2 = 11L

            if (adId != testFailId && adId != testFailId2) {
                mockDataProvider.ads = mockDataProvider.ads.toMutableStateList().apply {
                    firstOrNull { ad -> ad.id == adId }?.let {
                        it.reservedTimeMs = System.currentTimeMillis()
                    }
                }
            }

            return mockDataProvider.getSuccessOrFail(
                adId != testFailId
                        && adId != testFailId2
            )
        }

        suspend fun order(
            adId: Long,
            type: Order.Type,
            cardNumber: String,
            mmYy: String,
            cvvCvc: String
        ): Result<Order> {
            delay(700)
            return Result.success(
                mockDataProvider.createOrder(adId, type).also {
                    mockDataProvider.orders.add(it)
                }
            )
        }

        suspend fun getPickupPointTypes(): Result<List<PickupPointType>> {
            delay(320)
            val types = mockDataProvider.pickupPointTypes
            return Result.success(types)
        }

        suspend fun getActiveOrders(): Result<List<Order>> {
            delay(300)
            val orders = mockDataProvider.orders.filter { it.state == Order.State.Active }
            return Result.success(orders)
        }

        suspend fun getArchivedOrders(): Result<List<Order>> {
            delay(900)
            val orders = mockDataProvider.orders.filter { it.state == Order.State.Archived }
            return Result.success(orders)
        }

        suspend fun getOwnAds(): Result<List<OwnAd>> {
            delay(900)
            val orders = mockDataProvider.ownAds
            return Result.success(orders)
        }

    }
}

//fun main() {
//    val mockDataProvider = MockDataProvider()
//    mockDataProvider.getNextAdsPage(
//        range = PriceRange(),
//        regionId = null,
//        categoryId = 4,
//        query = "диван",
//        resetPage = true,
//    ) // ожидаю 1 объявление с диваном
//
//    mockDataProvider.getNextAdsPage(
//        range = PriceRange(),
//        regionId = null,
//        categoryId = null,
//        query = null,
//        resetPage = false,
//    ) // ожидаю 1-ю страницу
//
//    mockDataProvider.getNextAdsPage(
//        range = PriceRange(),
//        regionId = null,
//        categoryId = null,
//        query = null,
//        resetPage = false,
//    ) // ожидаю 2-ю страницу
//}