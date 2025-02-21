package me.likeavitoapp

import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.Category
import me.likeavitoapp.model.Contacts
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.Region
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.User


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
    var ads = createAds()
    var orders = mutableListOf<Order>()
    var lastDeliveryAddresses = mutableListOf<String>()
    var pickupPoints = createPickupPoints()

    init {
        repeat(5) {
            lastDeliveryAddresses.add("г.Москва, ул.Ленина, д.45, к.$it")
        }
    }

    fun createCategories(): List<Category> {
        return listOf(
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

    fun createAds() = listOf(
        Ad(
            id = 1,
            title = "Квартира в центре города",
            description = "Уютная квартира с ремонтом.",
            photoUrls = listOf("url1.jpg", "url2.jpg"),
            contacts = Contacts(phone = "123456789", email = "owner1@example.com"),
            price = 50000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 1,
            address = Ad.Address("Центральная улица, 1"),
            isPickupEnabled = false,
            owner = Ad.Owner(id = 1, name = "Иван", contacts = Contacts(phone = "123456789")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 2,
            title = "Новый автомобиль",
            description = "Продается новый автомобиль с гарантией.",
            photoUrls = listOf("car1.jpg", "car2.jpg"),
            contacts = Contacts(phone = "987654321", whatsapp = "987654321"),
            price = 1500000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 2,
            address = Ad.Address("Автозаводская улица, 10"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 2, name = "Петр", contacts = Contacts(phone = "987654321")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 3,
            title = "Игровой ноутбук",
            description = "Мощный игровой ноутбук с видеокартой RTX.",
            photoUrls = listOf(
                "https://ir-3.ozone.ru/s3/multimedia-1-n/wc1000/6917949671.jpg",
                "https://ir-3.ozone.ru/s3/multimedia-s/wc1000/6898199320.jpg",
                "https://ir-3.ozone.ru/s3/multimedia-1-9/wc1000/7265953233.jpg",
                "https://ir-3.ozone.ru/s3/multimedia-1-8/wc1000/7265953232.jpg",
            ),
            contacts = Contacts(phone = "555555555", telegram = "gamer"),
            price = 80000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 3,
            address = Ad.Address("Компьютерная улица, 5"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 3, name = "Сергей", contacts = Contacts(phone = "555555555")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 4,
            title = "Диван в отличном состоянии",
            description = "Продается диван, почти новый.",
            photoUrls = listOf("sofa1.jpg", "sofa2.jpg"),
            contacts = Contacts(phone = "444444444", email = "owner4@example.com"),
            price = 20000,
            isBargainingEnabled = false,
            isPremium = false,
            categoryId = 4,
            address = Ad.Address("Улица Мебельная, 3"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 4, name = "Анна Андреева", contacts = Contacts(phone = "444444444")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 5,
            title = "Книга по программированию",
            description = "Учебник по Kotlin для начинающих.",
            photoUrls = listOf("book1.jpg"),
            contacts = Contacts(phone = "333333333", email = "owner5@example.com"),
            price = 500,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 5,
            address = Ad.Address("Улица Книжная, 7"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 5, name = "Олег Петров", contacts = Contacts(phone = "333333333")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 6,
            title = "Смартфон Samsung Galaxy",
            description = "Продается новый смартфон.",
            photoUrls = listOf("phone1.jpg", "phone2.jpg"),
            contacts = Contacts(phone = "222222222", whatsapp = "222222222"),
            price = 30000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 6,
            address = Ad.Address("Улица Телефонная, 8"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 6, name = "Афанасьев Алексей", contacts = Contacts(phone = "222222222")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 7,
            title = "Монитор 24 дюйма",
            description = "Отличный монитор для работы и игр.",
            photoUrls = listOf("monitor1.jpg", "monitor2.jpg"),
            contacts = Contacts(phone = "111111111", email = "owner7@example.com"),
            price = 15000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 7,
            address = Ad.Address("Улица Мониторная, 2"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 7, name = "Дмитрий Коргин Простакович", contacts = Contacts(phone = "111111111")),
            isFavorite = UpdatableState(true),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 8,
            title = "Холодильник Samsung",
            description = "Новый холодильник с гарантией.",
            photoUrls = listOf("fridge1.jpg", "fridge2.jpg"),
            contacts = Contacts(phone = "666666666", whatsapp = "666666666"),
            price = 40000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 8,
            address = Ad.Address("Улица Техники, 4"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 8, name = "Мария", contacts = Contacts(phone = "666666666")),
            isFavorite = UpdatableState(true),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 9,
            title = "Квартира с видом на море",
            description = "Продается квартира с прекрасным видом.",
            photoUrls = listOf("sea_view1.jpg", "sea_view2.jpg"),
            contacts = Contacts(phone = "777777777", email = "owner9@example.com"),
            price = 120000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 1,
            address = Ad.Address("Улица Морская, 9"),
            isPickupEnabled = false,
            owner = Ad.Owner(id = 9, name = "Елена", contacts = Contacts(phone = "777777777")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 10,
            title = "Электромобиль",
            description = "Продается электромобиль с зарядной станцией.",
            photoUrls = listOf("electric_car1.jpg", "electric_car2.jpg"),
            contacts = Contacts(phone = "888888888", whatsapp = "888888888"),
            price = 2500000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 2,
            address = Ad.Address("Улица Эко, 6"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 10, name = "Светлана", contacts = Contacts(phone = "888888888")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 11,
            title = "Ноутбук для учебы",
            description = "Ноутбук в хорошем состоянии, подходит для учебы.",
            photoUrls = listOf("student_laptop1.jpg", "student_laptop2.jpg"),
            contacts = Contacts(phone = "999999999", email = "owner11@example.com"),
            price = 45000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 3,
            address = Ad.Address("Улица Учебная, 11"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 11, name = "Анастасия", contacts = Contacts(phone = "999999999")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 12,
            title = "Кресло для офиса",
            description = "Удобное офисное кресло, почти новое.",
            photoUrls = listOf("chair1.jpg", "chair2.jpg"),
            contacts = Contacts(phone = "101010101", email = "owner12@example.com"),
            price = 8000,
            isBargainingEnabled = false,
            isPremium = false,
            categoryId = 4,
            address = Ad.Address("Улица О фисная, 12"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 12, name = "Игорь", contacts = Contacts(phone = "101010101")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 13,
            title = "Книга по истории",
            description = "Интересная книга о мировой истории.",
            photoUrls = listOf("history_book1.jpg"),
            contacts = Contacts(phone = "202020202", email = "owner13@example.com"),
            price = 700,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 5,
            address = Ad.Address("Улица Историческая, 13"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 13, name = "Светлана", contacts = Contacts(phone = "202020202")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 14,
            title = "Смартфон iPhone",
            description = "Продается iPhone в идеальном состоянии.",
            photoUrls = listOf("iphone1.jpg", "iphone2.jpg"),
            contacts = Contacts(phone = "303030303", whatsapp = "303030303"),
            price = 60000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 6,
            address = Ad.Address("Улица Яблочная, 14"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 14, name = "Александр", contacts = Contacts(phone = "303030303")),
            isFavorite = UpdatableState(true),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 15,
            title = "Игровой монитор",
            description = "Монитор с высокой частотой обновления.",
            photoUrls = listOf("gaming_monitor1.jpg", "gaming_monitor2.jpg"),
            contacts = Contacts(phone = "404040404", email = "owner15@example.com"),
            price = 25000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 7,
            address = Ad.Address("Улица Игровая, 15"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 15, name = "Денис", contacts = Contacts(phone = "404040404")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 16,
            title = "Стиральная машина",
            description = "Стиральная машина в хорошем состоянии.",
            photoUrls = listOf("washing_machine1.jpg", "washing_machine2.jpg"),
            contacts = Contacts(phone = "505050505", whatsapp = "505050505"),
            price = 30000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 8,
            address = Ad.Address("Улица Техники, 16"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 16, name = "Марина", contacts = Contacts(phone = "505050505")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 17,
            title = "Квартира с ремонтом",
            description = "Продается квартира с новым ремонтом.",
            photoUrls = listOf("renovated_apartment1.jpg", "renovated_apartment2.jpg"),
            contacts = Contacts(phone = "606060606", email = "owner17@example.com"),
            price = 90000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 1,
            address = Ad.Address("Улица Ремонтная, 17"),
            isPickupEnabled = false,
            owner = Ad.Owner(id = 17, name = "Ольга", contacts = Contacts(phone = "606060606")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 18,
            title = "Мотоцикл",
            description = "Продается мотоцикл в отличном состоянии.",
            photoUrls = listOf("motorcycle1.jpg", "motorcycle2.jpg"),
            contacts = Contacts(phone = "707070707", whatsapp = "707070707"),
            price = 300000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 2,
            address = Ad.Address("Улица Мотоциклетная, 18"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 18, name = "Виктор", contacts = Contacts(phone = "707070707")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 19,
            title = "Портативный ноутбук",
            description = "Легкий и мощный ноутбук для работы.",
            photoUrls = listOf("portable_laptop1.jpg", "portable_laptop2.jpg"),
            contacts = Contacts(phone = "808080808", email = "owner19@example.com"),
            price = 60000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 3,
            address = Ad.Address("Улица Портативная, 19"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 19, name = "Наталья", contacts = Contacts(phone = "808080808")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 20,
            title = "Стол для компьютера",
            description = "Удобный стол для работы за компьютером.",
            photoUrls = listOf("desk1.jpg", "desk2.jpg"),
            contacts = Contacts(phone = "909090909", email = "owner20@example.com"),
            price = 12000,
            isBargainingEnabled = false,
            isPremium = false,
            categoryId = 4,
            address = Ad.Address("Улица Офисная, 20"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 20, name = "Светлана", contacts = Contacts(phone = "909090909")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 21,
            title = "Книга по математике",
            description = "Учебник по математике для студентов.",
            photoUrls = listOf("math_book1.jpg"),
            contacts = Contacts(phone = "111111112", email = "owner21@example.com"),
            price = 800,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 5,
            address = Ad.Address("Улица Математическая, 21"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 21, name = "Алексей", contacts = Contacts(phone = "111111112")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 22,
            title = "Смартфон Xiaomi",
            description = "Продается новый смартфон Xiaomi.",
            photoUrls = listOf("xiaomi_phone1.jpg", "xiaomi_phone2.jpg"),
            contacts = Contacts(phone = "222222223", whatsapp = "222222223"),
            price = 25000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 6,
            address = Ad.Address("Улица Сяоми, 22"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 22, name = "Ирина", contacts = Contacts(phone = "222222223")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 23,
            title = "Игровая клавиатура",
            description = "Клавиатура с подсветкой для геймеров.",
            photoUrls = listOf("gaming_keyboard1.jpg", "gaming_keyboard2.jpg"),
            contacts = Contacts(phone = "333333334", email = "owner23@example.com"),
            price = 5000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 7,
            address = Ad.Address("Улица Игровая, 23"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 23, name = "Дмитрий", contacts = Contacts(phone = "333333334")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 24,
            title = "Микроволновая печь",
            description = "Микроволновка в отличном состоянии.",
            photoUrls = listOf("microwave1.jpg", "microwave2.jpg"),
            contacts = Contacts(phone = "444444445", whatsapp = "444444445"),
            price = 15000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 8,
            address = Ad.Address("Улица Кулинарная, 24"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 24, name = "Екатерина", contacts = Contacts(phone = "444444445")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 25,
            title = "Квартира с балконом",
            description = "Продается квартира с балконом и видом на парк.",
            photoUrls = listOf("balcony_apartment1.jpg", "balcony_apartment2.jpg"),
            contacts = Contacts(phone = "555555556", email = "owner25@example.com"),
            price = 110000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 1,
            address = Ad.Address("Улица Балконная, 25"),
            isPickupEnabled = false,
            owner = Ad.Owner(id = 25, name = "Александр", contacts = Contacts(phone = "555555556")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 26,
            title = "Кроссовер",
            description = "Продается кроссовер в отличном состоянии.",
            photoUrls = listOf("crossover1.jpg", "crossover2.jpg"),
            contacts = Contacts(phone = "666666667", whatsapp = "666666667"),
            price = 1800000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 2,
            address = Ad.Address("Улица Автомобильная, 26"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 26, name = "Сергей", contacts = Contacts(phone = "666666667")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 27,
            title = "Планшет Apple",
            description = "Продается новый iPad с гарантией.",
            photoUrls = listOf("ipad1.jpg", "ipad2.jpg"),
            contacts = Contacts(phone = "777777778", email = "owner27@example.com"),
            price = 50000,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 3,
            address = Ad.Address("Улица Планшетная, 27"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 27, name = "Мария", contacts = Contacts(phone = "777777778")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 28,
            title = "Кровать с матрасом",
            description = "Продается кровать с новым матрасом.",
            photoUrls = listOf("bed1.jpg", "bed2.jpg"),
            contacts = Contacts(phone = "888888889", email = "owner28@example.com"),
            price = 25000,
            isBargainingEnabled = false,
            isPremium = false,
            categoryId = 4,
            address = Ad.Address("Улица Спальня, 28"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 28, name = "Игорь Моржович Пет", contacts = Contacts(phone = "888888889")),
            isFavorite = UpdatableState(true),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 29,
            title = "Книга по физике",
            description = "Учебник по физике для студентов.",
            photoUrls = listOf("physics_book1.jpg"),
            contacts = Contacts(phone = "999999990", email = "owner29@example.com"),
            price = 900,
            isBargainingEnabled = true,
            isPremium = false,
            categoryId = 5,
            address = Ad.Address("Улица Физическая, 29"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 29, name = "Дмитрий", contacts = Contacts(phone = "999999990")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        ),
        Ad(
            id = 30,
            title = "Смартфон Huawei",
            description = "Продается новый смартфон Huawei с отличной камерой.",
            photoUrls = listOf("huawei_phone1.jpg", "huawei_phone2.jpg"),
            contacts = Contacts(phone = "101010102", whatsapp = "101010102"),
            price = 35000,
            isBargainingEnabled = false,
            isPremium = true,
            categoryId = 6,
            address = Ad.Address("Улица Хуавей, 30"),
            isPickupEnabled = true,
            owner = Ad.Owner(id = 30, name = "Артем", contacts = Contacts(phone = "101010102")),
            isFavorite = UpdatableState(false),
            timerLabel = UpdatableState(""),
            reservedTimeMs = null
        )
    )

    private fun createPickupPoints(): List<PickupPoint> = listOf(
        PickupPoint(1, "Улица Ленина, 1", 9, 21, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(2, "Улица Пушкина, 2", 10, 20, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(3, "Улица Гоголя, 3", 8, 22, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(4, "Улица Чехова, 4", 9, 18, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(5, "Улица Толстого, 5", 10, 19, PickupPoint.Point(55.7539, 37.6208)),
        PickupPoint(6, "Улица Достоевского, 6", 11, 20, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(7, "Улица Суворова, 7", 9, 21, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(8, "Улица Тверская, 8", 10, 22, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(9, "Улица Невского, 9", 8, 20, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(10, "Улица Кутузова, 10", 9, 21, PickupPoint.Point(55.7539, 37.6208)),
        PickupPoint(11, "Улица Маяковского, 11", 10, 19, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(12, "Улица Лермонтова, 12", 11, 20, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(13, "Улица Блока, 13", 9, 21, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(14, "Улица Цветкова, 14", 10, 22, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(15, "Улица Станиславского, 15", 8, 20, PickupPoint.Point(55.7539, 37.6208)),
        PickupPoint(16, "Улица Крылатская, 16", 9, 21, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(17, "Улица Кутузова, 17", 10, 19, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(18, "Улица Пушкина, 18", 11, 20, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(19, "Улица Гоголя, 19", 9, 21, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(20, "Улица Чехова, 20", 10, 22, PickupPoint.Point(55.7539, 37.6208)),
        PickupPoint(21, "Улица Толстого, 21", 8, 20, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(22, "Улица Достоевского, 22", 9, 21, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(23, "Улица Суворова, 23", 10, 19, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(24, "Улица Тверская, 24", 11, 20, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(25, "Улица Невского, 25", 9, 21, PickupPoint.Point(55.7539, 37.6208)),
        PickupPoint(26, "Улица Маяковского, 26", 10, 22, PickupPoint.Point(55.7558, 37.6173)),
        PickupPoint(27, "Улица Лермонтова, 27", 8, 20, PickupPoint.Point(59.9343, 30.3351)),
        PickupPoint(28, "Улица Блока, 28", 9, 21, PickupPoint.Point(54.7388, 55.9721)),
        PickupPoint(29, "Улица Цветкова, 29", 10, 19, PickupPoint.Point(56.3269, 44.0059)),
        PickupPoint(30, "Улица Станиславского, 30", 11, 20, PickupPoint.Point(55.7539, 37.6208))
    )

    fun createOrder(adId:Long, type: Order.Type): Order {
        val order = Order(
            ad = ads.first { it.id == adId },
            type = type,
            state = Order.State.Init
        )
        orders.add(order)
        return order
    }

}