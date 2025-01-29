package me.likeavitoapp


import io.ktor.client.*
import kotlinx.coroutines.delay


class Backend(val client: HttpClient = HttpClient()) {

    interface UserServiceContract {
        suspend fun login(username: String, password: String): Result<LoginResult>
        suspend fun logout(userId: Long): Result<Boolean>
        suspend fun getUser(userId: Long): Result<User>
    }

    interface AdsServiceContract {
        suspend fun getCategories(): Result<List<Category>>
        suspend fun getAds(categoryId: Int = 0, page: Int = 0, query: String = ""): Result<List<Ad>>
        suspend fun getAdDetails(adId: Long): Result<Ad>
        suspend fun getSearchTips(categoryId: Int, query: String): Result<List<String>>
    }

    interface CartServiceContract {
        suspend fun reserve(adId: Long): Result<Boolean>
        suspend fun order(adId: Long, buyType: Order.BuyType): Result<Boolean>

        suspend fun getOrders(userId: Long): Result<List<Order>>
    }

    var token: String? = null

    var userService = UserService()
    var adsService = AdsService()
    var cartService = CartService()

    var mockDataProvider = MockDataProvider()

    data class LoginResult(val user: User, val token: String)

    // NOTE: this is mock for an example
    inner class UserService : UserServiceContract {

        override suspend fun login(username: String, password: String): Result<LoginResult> {
            delay(1500)
            if(username == "ss@ss.ss" && password == "123456") {
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

        override suspend fun logout(userId: Long): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getUser(userId: Long): Result<User> {
            return Result.success(mockDataProvider.getUser())
        }

    }

    // NOTE: this is mock for an example
    inner class AdsService : AdsServiceContract {
        override suspend fun getCategories(): Result<List<Category>> {
            return Result.success(
                mockDataProvider.getCategories()
            )
        }

        override suspend fun getAds(categoryId: Int, page: Int, query: String): Result<List<Ad>> {
            return Result.success(
                mockDataProvider.getAds(categoryId, page, query)
            )
        }

        override suspend fun getAdDetails(adId: Long): Result<Ad> {
            TODO("Not yet implemented")
        }

        override suspend fun getSearchTips(categoryId: Int, query: String): Result<List<String>> {
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
    }

    // NOTE: this is mock for an example
    inner class CartService : CartServiceContract {
        override suspend fun reserve(adId: Long): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun order(
            adId: Long,
            buyType: Order.BuyType
        ): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getOrders(userId: Long): Result<List<Order>> {
            TODO("Not yet implemented")
        }

    }
}