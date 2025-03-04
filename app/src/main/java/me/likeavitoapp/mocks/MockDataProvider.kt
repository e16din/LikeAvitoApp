package me.likeavitoapp.mocks

import me.likeavitoapp.log
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.AppModel
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.Contacts
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.PriceRange
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User
import kotlin.math.min

class MockDataProvider {
    var token = "dsdgHIHKE#U&HpFJN@ASDsADDASSASADASDadsgfff"
    var user = User(
        id = 0,
        name = "Кундрюков Александр",
        contacts = Contacts(
            telegram = "@alex_ku_san",
            email = "a.kundryukov@gmail.com"
        ),
        ownAds = emptyList(),
        photoUrl = UpdatableState("https://ybis.ru/wp-content/uploads/2023/09/milye-kotiki-16.webp")
    )

    var categories = createCategories()
    var searchTips = mutableListOf(
        "Mac Book",
        "Mac Book Pro",
        "Mac Book Pro 14",
        "Mac Book Pro 16",
        "Mac Book Pro 16 2025"
    )
    var ads = mockAds()
    var orders = mutableListOf<Order>(
        createOrder(12, Order.Type.Delivery, Order.State.Active),
        createOrder(16, Order.Type.Pickup, Order.State.Archived),
    )
    var lastDeliveryAddresses = mutableListOf<String>()
    var pickupPoints = mockPickupPoints()

    init {
        repeat(5) {
            lastDeliveryAddresses.add("г.Москва, ул.Ленина, д.45, к.$it")
        }
    }

    fun createCategories(): List<Category> {
        return listOf(
            Category(name = "Все", id = 0),
            Category(name = "Квартиры", id = 1),
            Category(name = "Авто", id = 2),
            Category(name = "Ноутбуки", id = 3),
            Category(name = "Мебель", id = 4),
            Category(name = "Книги", id = 5),
            Category(name = "Телефоны", id = 6),
            Category(name = "Мониторы", id = 7),
            Category(name = "Бытовая техника", id = 8),
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

    fun getFavorites(): List<Ad> {
        return ads.filter { it.isFavorite.value }
    }


    fun createOrder(adId: Long, type: Order.Type, state: Order.State = Order.State.Active): Order {
        return Order(
            ad = ads.first { it.id == adId },
            type = type,
            state = state,
            id = 0,
            number = "123-1234-${adId}",
            createdMs = System.currentTimeMillis(),
            expectedArrivalMs = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 60 * 1000,
            pickupPoint = null
        )
    }

    private val paged = mutableSetOf<Long>()
    private var pageCounter = 0

    fun getNextAdsPage(
        range: PriceRange,
        regionId: Int?,
        categoryId: Int?,
        query: String?,
        resetPage: Boolean = false,
        pageSize: Int = AppModel.adsPageSize
    ): List<Ad> {
        log("query: $query")
        log("categoryId: $categoryId")
        log("regionId: $regionId")
        log("getNextAdsPage")
        val filterCondition : (Ad) -> Boolean =  { it ->
            ((categoryId == null || categoryId == 0) || it.categoryId == categoryId)
                    && ((regionId == null || regionId == 0) || it.regionId == regionId)
                    &&  ((query == null || query.isEmpty()) || it.title.contains(query, ignoreCase = true))
        }

        val filtered = ads.filter(filterCondition)
        log("filtered: $filtered")

        val maxPages = 20
        if (resetPage || pageCounter > maxPages) {
            paged.clear()
            pageCounter = 0
        }

        pageCounter += 1

        if (filtered.size < pageSize) {
            return filtered
        }

        val result = mutableListOf<Ad>()
        log("pageCounter: $pageCounter")
        for (i in (pageCounter-1) * pageSize until min((pageCounter-1) * pageSize + pageSize, filtered.size)) {
            log("i: $i")
            val ad = filtered[i]
            if (!paged.contains(ad.id)) {
                log("ad.id: ${ad.id}")
                paged.add(ad.id)
                result.add(ad)
            }
        }
        log("result: $result")
        return result
    }

}