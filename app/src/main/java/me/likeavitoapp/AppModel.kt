package me.likeavitoapp

import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.screens.splash.SplashScreen


object NavRoutes {
    val Splash = "splash"
    val Auth = "auth"
    val Main = "main"
    val SearchFilter = "search_filter"
    val AdDetails = "ad_details"
    val OwnAdDetails = "own_ad_details"
    val EditOwnAd = "edit_own_ad"

}

class AppModel {
    var user: User? = null

    private val initialScreen = SplashScreen()
    var screens = mutableListOf<Screen>(initialScreen)
    var currentScreenFlow = MutableStateFlow<Screen>(initialScreen)
}

interface Screen {
    val route: Route
}

val RouteTabStub = Route("tab_stub")
class Route(
    val path: String,
    val isRoot: Boolean = false
)

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

data class AdsList(
    var ads: List<Ad>,
    var query: String,
    var page: Int
)

data class SearchSettings(
    var category: Category,
    var query: String,
    var region: Region,
    var priceRange: PriceRange
) {
    data class Region(val name: String, val id: Int)
    data class PriceRange(val from: Int, val to: Int)
}
