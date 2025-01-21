package me.likeavitoapp


import io.ktor.client.*
import kotlinx.coroutines.delay


class Backend(val client: HttpClient) {
    fun getSearchTips(query: String) {
        TODO("Not yet implemented")
    }

    interface UserServiceContract {
        suspend fun login(username: String, password: String): Result<User>
        suspend fun logout(userId: Long): Result<Boolean>
        suspend fun getUser(userId: Long): Result<User>
    }

    interface AdsServiceContract {
        suspend fun getAds(page: Int = 0, query: String = ""): Result<AdsList>
        suspend fun getAdDetails(adId: Long): Result<Ad>
        suspend fun getSearchTips(query: String): Result<List<String>>
    }

    var userService = UserService()
    var adsService = AdsService()


    // NOTE: this is mock for an example
    inner class UserService: UserServiceContract {

        override suspend fun login(username: String, password: String): Result<User> {
            delay(1500)
            return Result.success(User(
                id = 1,
                name = "Александр Кундрюков",
                contacts = Contacts(
                    phone = null,
                    whatsapp = null,
                    telegram = "@alex_ku_san",
                    email = null
                ),
                ownAds = listOf()
            ))
        }

        override suspend fun logout(userId: Long): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getUser(userId: Long): Result<User> {
            TODO("Not yet implemented")
        }

    }

    // NOTE: this is mock for an example
    inner class AdsService: AdsServiceContract {
        override suspend fun getAds(page: Int, query: String): Result<AdsList> {
            TODO("Not yet implemented")
        }

        override suspend fun getAdDetails(adId: Long): Result<Ad> {
            TODO("Not yet implemented")
        }

        override suspend fun getSearchTips(query: String): Result<List<String>> {
            TODO("Not yet implemented")
        }
    }

}