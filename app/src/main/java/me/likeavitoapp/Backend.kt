package me.likeavitoapp


import io.ktor.client.*
import kotlinx.coroutines.delay


class Backend(val client: HttpClient) {

    interface UserServiceContract {
        suspend fun login(username: String, password: String): Result<LoginResult>
        suspend fun logout(userId: Long): Result<Boolean>
        suspend fun getUser(userId: Long): Result<User>
    }

    interface AdsServiceContract {
        suspend fun getCategories(): Result<List<Category>>
        suspend fun getAds(categoryId: Int = 0, page: Int = 0, query: String = ""): Result<AdsList>
        suspend fun getAdDetails(adId: Long): Result<Ad>
        suspend fun getSearchTips(categoryId: Int, query: String): Result<List<String>>
    }

    var token: String? = null

    var userService = UserService()
    var adsService = AdsService()

    data class LoginResult(val user: User, val token: String)

    // NOTE: this is mock for an example
    inner class UserService : UserServiceContract {

        override suspend fun login(username: String, password: String): Result<LoginResult> {
            delay(1500)
            return Result.success(
                LoginResult(
                    user =
                        User(
                            id = 1, name = "Александр Кундрюков", contacts = Contacts(
                                phone = null,
                                whatsapp = null,
                                telegram = "@alex_ku_san",
                                email = null
                            ), ownAds = listOf(),
                            photoUrl = "https://ybis.ru/wp-content/uploads/2023/09/milye-kotiki-16.webp"
                        ),
                    token = "dsdgHIHKE#U&HpFJN@ASDsADDASSASADASDadsgfff"
                )
            )
        }

        override suspend fun logout(userId: Long): Result<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun getUser(userId: Long): Result<User> {
            TODO("Not yet implemented")
        }

    }

    // NOTE: this is mock for an example
    inner class AdsService : AdsServiceContract {
        override suspend fun getCategories(): Result<List<Category>> {
            return Result.success(listOf())
        }

        override suspend fun getAds(categoryId: Int, page: Int, query: String): Result<AdsList> {
            return Result.success(
                AdsList(
                    page = page, query = query, ads = listOf(
                        Ad(
                            id = 1,
                            title = "MacBook Pro 14",
                            description = "Ноутбук, который расширяет ваши возможности\n" + "Apple MacBook Pro 14\" 2024 года – это ноутбук, созданный для тех, кто привык к скорости, мощности и комфорту. С процессором M4 он обеспечивает идеальный баланс между автономностью и производительностью, достаточной для очень требовательных задач. В нём есть всё, чтобы работать или отдыхать где угодно, подключать любые дисплеи и аксессуары, а высокий уровень безопасности, в сочетании с идеальной оптимизацией macOS и сервисами Apple, позволяет не загружать себя лишними заботами, фокусируясь на том, что действительно важно.",
                            photoUrls = listOf("https://ir-3.ozone.ru/s3/multimedia-1-n/wc1000/6917949671.jpg"),
                            contacts = Contacts(
                                phone = "8XXXXXX1234",
                                whatsapp = null,
                                telegram = "@any_contact",
                                email = "any@gmail.com"
                            ),
                            price = 100000.0,
                            isPremium = true,
                            category = Category("Ноутбуки", 1),
                            address = Ad.Address(
                                address = "г.Москва, ул.Ленина, д.45"
                            ),
                            owner = Ad.Owner(
                                id = 100500,
                                name = "Петр Петрович",
                                contacts = Contacts(phone = "8950XXXXX07")
                            )
                        )
                    )
                )
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
}