package me.likeavitoapp

class MockDataProvider {
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
}