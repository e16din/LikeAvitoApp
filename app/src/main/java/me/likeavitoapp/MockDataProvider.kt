package me.likeavitoapp

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.AppBackend
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.Contacts
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.User


class MockDataProvider {
    fun dataSources(): DataSources = DataSources(
        app = AppModel().apply { user = getUser() },
        platform = AppPlatform(),
        backend = AppBackend()
    )

    fun getUser(): User {
        return User(
            id = 1, name = "Александр Кундрюков", contacts = Contacts(
                phone = null,
                whatsapp = null,
                telegram = "@alex_ku_san",
                email = null
            ), ownAds = listOf(),
            photoUrl = "https://ybis.ru/wp-content/uploads/2023/09/milye-kotiki-16.webp"
        )
    }

    fun getAds(categoryId: Int, page: Int, query: String): List<Ad> {
        val result = mutableListOf<Ad>()
        repeat(20) {
            result.add(
                getAd(it.toLong())
            )
        }
        return result
    }

    fun getCategories(): List<Category> {
        return listOf(
            Category(
                name = "Квартиры",
                id = 1
            ),
            Category(
                name = "Авто",
                id = 2
            ),
            Category(
                name = "Ноутбуки",
                id = 3
            ),
            Category(
                name = "Мебель",
                id = 4
            ),
            Category(
                name = "Книги",
                id = 5
            ),
            Category(
                name = "Телефоны",
                id = 6
            ),
            Category(
                name = "Мониторы",
                id = 7
            ),
            Category(
                name = "Бытовая техника",
                id = 8
            ),
        )
    }

    fun getAd(id: Long): Ad {
        return Ad(
            id = id.toLong(),
            title = "${id.toLong()}: MacBook Pro 14",
            description = "Ноутбук, который расширяет ваши возможности\n" + "Apple MacBook Pro 14\" 2024 года – это ноутбук, созданный для тех, кто привык к скорости, мощности и комфорту. С процессором M4 он обеспечивает идеальный баланс между автономностью и производительностью, достаточной для очень требовательных задач. В нём есть всё, чтобы работать или отдыхать где угодно, подключать любые дисплеи и аксессуары, а высокий уровень безопасности, в сочетании с идеальной оптимизацией macOS и сервисами Apple, позволяет не загружать себя лишними заботами, фокусируясь на том, что действительно важно.",
            photoUrls = listOf("https://ir-3.ozone.ru/s3/multimedia-1-n/wc1000/6917949671.jpg"),
            contacts = Contacts(
                phone = "8XXXXXX1234",
                whatsapp = null,
                telegram = "@any_contact",
                email = "any@gmail.com"
            ),
            price = 100000,
            isBargainingEnabled = true,
            isPremium = true,
            category = Category("Ноутбуки", 1),
            address = Ad.Address(
                address = "г.Москва, ул.Ленина, д.45"
            ),
            owner = Ad.Owner(
                id = 100500,
                name = "Петр Петрович",
                contacts = Contacts(phone = "8950XXXXX07")
            ),
            isFavorite = MutableStateFlow(false)
        )
    }

    fun getRegions(): List<Region> {
        return listOf(
            Region("Москва", 1),
            Region("Санкт-Петербург", 2),
            Region("Ростов-на-Дону", 3),
            Region("Екатеринбург", 4),
            Region("Омск", 5),
        )
    }

    fun getSuccessOrFail(success: Boolean): Result<Boolean> {
        return if (success)
            Result.success(true)
        else
            Result.failure(Exception("Request failed"))
    }
}