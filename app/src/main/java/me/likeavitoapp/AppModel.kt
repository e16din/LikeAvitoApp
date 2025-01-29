package me.likeavitoapp

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.screens.auth.AuthScreen
import me.likeavitoapp.screens.splash.SplashScreen

fun checkState(condition:Boolean) {
    assert(condition)
}

val scenariosEnabled = false

class AppModel(
    val backend: Backend = Backend(),
    val platform: AppPlatform = AppPlatform.platform
) {
    var user: User? = null

    var currentScreen = MutableStateFlow<Screen>(
        SplashScreen(
            sources = DataSources(
                app = this,
                platform = platform,
                backend = backend
            )
        )
    )

    val nav = Navigation()

    class Navigation(val roots: Roots = Roots()) {
        class Roots {
            fun authScreen() = AuthScreen()
        }
    }

    // UseCases:

    fun PressBack(screen: Screen) {
        screen.prevScreen?.let {
            currentScreen.value = it
        }
    }

    suspend fun Logout() {
        currentScreen.value = nav.roots.authScreen()
        platform.authDataStore.clear()
    }
}

interface Screen {
    var prevScreen: Screen?
    var innerScreen: MutableStateFlow<Screen>?
}

data class User(
    val id: Long,
    var name: String,
    var contacts: Contacts,
    var ownAds: List<Ad>,
    var photoUrl: String
)

data class Contacts(
    val phone: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val email: String? = null
)

data class Category(val name: String, val id: Int)

data class Ad(
    val id: Long,
    val title: String,
    val description: String,
    val photoUrls: List<String>,
    val contacts: Contacts,
    val price: Double,
    val isPremium: Boolean,
    val isFavorite: Boolean,
    val category: Category,
    val address: Address,
    val owner: Owner
) {
    data class Address(val address: String)
    data class Owner(
        var id: Long,
        var name: String,
        var contacts: Contacts
    )
}

data class SearchSettings(
    var category: MutableStateFlow<Category>,
    var query: MutableStateFlow<String>,
    var region: MutableStateFlow<Region>,
    var priceRange: MutableStateFlow<PriceRange>
) {
    data class Region(val name: String, val id: Int)
    data class PriceRange(val from: Int, val to: Int)
}

data class Order(val ad: Ad, val buyType: BuyType, val state: State) {
    enum class State {
        New,
        Active,
        Archived
    }

    sealed class BuyType {
        class Pickup(address: Ad.Address)
        class Delivery(delivery: DeliveryType)
    }

    enum class DeliveryType {
        Post,
        Cdek,
        Boxberry,
    }
}
