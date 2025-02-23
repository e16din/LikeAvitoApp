package me.likeavitoapp.model


import androidx.compose.runtime.toMutableStateList
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import io.ktor.client.*
import kotlinx.coroutines.delay
import me.likeavitoapp.UnauthorizedException
import me.likeavitoapp.mocks.MockDataProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AppBackend(val client: HttpClient = HttpClient()) {

    var token: String? = null

    var userService = UserService()
    var adsService = AdsService()
    var orderService = OrderService()
    var mapService = MapService()


    var mockDataProvider = MockDataProvider()

    data class LoginResult(val user: User, val token: String)

    inner class MapService {
        val searchManager by lazy {
            SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
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

        suspend fun getAddressesBy(
            query: String,
            areaPoint: Point
        ): Result<List<MapItem>> {
//            val suggestSession = searchManager.createSuggestSession()
//            val suggestOptions = SuggestOptions().setSuggestTypes(SuggestType.GEO.value)
//
//            suggestSession.suggest("кафе", BoundingBox(map.visibleRegion.bottomLeft, map.visibleRegion.topRight), suggestOptions, object : SuggestSession.SuggestListener {
//                override fun onResponse(items: MutableList<SuggestItem>) {
//
//                }
//
//                override fun onResponse(response: SuggestResponse) {
//                    response.items
//                    TODO("Not yet implemented")
//                }
//
//                override fun onError(error: Error) {
//                    showMessage("Ошибка получения подсказок")
//                }
//            })

            val searchOptions = SearchOptions().apply {
                searchTypes = SearchType.GEO.value
                resultPageSize = 32
            }

            val geometry = Geometry.fromPoint(areaPoint)

            return suspendCoroutine { continuation ->
                searchManager.submit(
                    query,
                    geometry,
                    searchOptions,
                    object : SearchListener {
                        override fun onSearchResponse(response: Response) {
                            val resultData = mutableListOf<MapItem>()
                            response.collection.children.forEach { item ->
                                val name = item.obj?.name
                                val point = item.obj?.geometry?.first()?.point
                                if (name != null && point != null) {
                                    resultData.add(
                                        MapItem(name, point)
                                    )
                                }
                            }
                            continuation.resume(
                                Result.success(resultData)
                            )
                        }

                        override fun onSearchError(fail: Error) {
                            continuation.resumeWith(
                                Result.failure(IllegalStateException("see: onSearchError()"))
                            )
                        }
                    })
            }
        }
    }

    // NOTE: this is mock for an example
    inner class UserService {

        suspend fun login(username: String, password: String): Result<LoginResult> {
            delay(1500)
            if (username == "ss@ss.ss" && password == "123456") {
                return Result.success(
                    LoginResult(
                        user = mockDataProvider.user,
                        token = mockDataProvider.token
                    )
                )
            } else {
                return Result.failure(UnauthorizedException())
            }
        }

        suspend fun logout(): Result<Boolean> {
            delay(300)
            return Result.success(true)
        }

        suspend fun getUser(userId: Long): Result<User> {
            delay(1000)
            return Result.success(mockDataProvider.user)
        }

        suspend fun postPhoto(photoBase64: String): Result<String> { //todo: return url on prod
            delay(2000)
            return Result.success(photoBase64)
        }

        suspend fun updateUser(
            name: String,
            phone: String?,
            telegram: String?,
            whatsapp: String?,
            email: String?
        ): Result<User> {
            delay(2000)
            return Result.success(mockDataProvider.user.apply {
                this.name = name

                this.contacts.email = email
                this.contacts.whatsapp = whatsapp
                this.contacts.telegram = telegram
                this.contacts.phone = phone
            })
        }
    }

    // NOTE: this is mock for an example
    inner class AdsService {
        suspend fun getCategories(): Result<List<Category>> {
            return Result.success(mockDataProvider.categories)
        }

        suspend fun getAds(
            range: PriceRange,
            regionId: Int,
            categoryId: Int,
            query: String,
            page: Int,
        ): Result<List<Ad>> {
            delay(2000)
            return Result.success(mockDataProvider.ads)
        }

        suspend fun getAdDetails(ad: Ad): Result<Ad> {
            TODO("Not yet implemented")
        }

        suspend fun getSearchTips(categoryId: Int, query: String): Result<List<String>> {
            return Result.success(
                listOf(
                    "Mac Book",
                    "Mac Book Pro",
                    "Mac Book Pro 14",
                    "Mac Book Pro 16",
                    "Mac Book Pro 16 2025",
                )
            )
        }

        suspend fun updateFavoriteState(ad: Ad): Result<Boolean> {


            return Result.success(true)
        }

        suspend fun getFavorites(): Result<List<Ad>> {
            return Result.success(mockDataProvider.getFavorites())
        }

        fun deleteAllFavorites(): Result<Boolean> {
            mockDataProvider.ads = mockDataProvider.ads.apply {
                forEach { it.isFavorite.post(false) }
            }
            return mockDataProvider.getSuccessOrFail(true)
        }
    }

    // NOTE: this is mock for an example
    inner class OrderService {
        suspend fun getLastDeliveryAddresses(): Result<List<String>> {
            delay(700)
            return Result.success(mockDataProvider.lastDeliveryAddresses)
        }

        suspend fun reserve(adId: Long): Result<Boolean> {
            val testFailId = 2L

            if (adId != testFailId) {
                mockDataProvider.ads = mockDataProvider.ads.toMutableStateList().apply {
                    firstOrNull { ad -> ad.id == adId }?.let {
                        it.reservedTimeMs = System.currentTimeMillis()
                    }
                }
            }

            return mockDataProvider.getSuccessOrFail(adId != testFailId)
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

    }
}