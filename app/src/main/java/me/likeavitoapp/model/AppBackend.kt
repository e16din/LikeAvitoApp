package me.likeavitoapp.model


import androidx.compose.runtime.toMutableStateList
import io.ktor.client.*
import kotlinx.coroutines.delay
import me.likeavitoapp.AuthFiledException
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.develop


class AppBackend(val client: HttpClient = HttpClient()) {

    var token: String? = null

    var userService = UserService()
    var adsService = AdsService()
    var cartService = CartService()

    var mockDataProvider = MockDataProvider()

    data class LoginResult(val user: User, val token: String)

    // NOTE: this is mock for an example
    inner class UserService {

        suspend fun login(username: String, password: String): Result<LoginResult> {
            delay(1500)
            if (username == "ss@ss.ss" && password == "123456") {
                return Result.success(
                    LoginResult(
                        user = mockDataProvider.getUser(),
                        token = "dsdgHIHKE#U&HpFJN@ASDsADDASSASADASDadsgfff"
                    )
                )
            } else {
                return Result.failure(AuthFiledException())
            }
        }

        suspend fun logout(userId: Long): Result<Boolean> {
            TODO("Not yet implemented")
        }

        suspend fun getUser(userId: Long): Result<User> {
            return Result.success(mockDataProvider.getUser())
        }

    }

    // NOTE: this is mock for an example
    inner class AdsService {
        suspend fun getCategories(): Result<List<Category>> {
            return Result.success(
                mockDataProvider.getCategories()
            )
        }

        suspend fun getAds(
            range: PriceRange,
            regionId: Int,
            categoryId: Int,
            query: String,
            page: Int,
        ): Result<List<Ad>> {
            return Result.success(
                mockDataProvider.getAds(categoryId, page, query)
            )
        }

        suspend fun getAdDetails(adId: Long): Result<Ad> {
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
            if (develop) {
                mockDataProvider.ads = mockDataProvider.ads.toMutableList().apply {
                    if (ad.isFavorite.value) {
                        add(ad)
                    } else {
                        remove(ad)
                    }
                }
            }

            return Result.success(true)
        }

        suspend fun getFavorites(): Result<List<Ad>> {
            return Result.success(mockDataProvider.getFavorites())
        }

        fun deleteAllFavorites(): Result<Boolean> {
            mockDataProvider.ads = mockDataProvider.ads.toMutableList().apply {
                forEach { it.isFavorite.value = false }
            }
            return mockDataProvider.getSuccessOrFail(true)
        }
    }

    // NOTE: this is mock for an example
    inner class CartService {
        suspend fun reserve(adId: Long): Result<Boolean> {
            mockDataProvider.ads = mockDataProvider.ads.toMutableStateList().apply {
                firstOrNull { ad -> ad.id == adId }?.let {
                    it.reservedTimeMs = System.currentTimeMillis()
                }
            }
            return mockDataProvider.getSuccessOrFail(adId != 2L)
        }

        suspend fun order(
            adId: Long,
            buyType: Order.BuyType
        ): Result<Boolean> {
            TODO("Not yet implemented")
        }

        suspend fun getOrders(userId: Long): Result<List<Order>> {
            TODO("Not yet implemented")
        }

    }
}