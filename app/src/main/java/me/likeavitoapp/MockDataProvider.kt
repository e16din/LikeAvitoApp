package me.likeavitoapp

import androidx.compose.runtime.Composable

class MockDataProvider {
    fun dataSources(): DataSources = DataSources(
        app = AppModel().apply { user = getUser() },
        platform = AppPlatform(),
        backend = Backend()
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
}