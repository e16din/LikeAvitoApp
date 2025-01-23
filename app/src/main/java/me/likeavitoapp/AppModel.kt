package me.likeavitoapp

import kotlinx.coroutines.flow.MutableStateFlow


object NavRoutes {
    val Splash = "splash"
    val Auth = "auth"
    val Main = "main"
    val AdDetails = "ad_details"
    val Profile = "profile"
    val EditOwnAdd = "edit_own_add"
}

object AppModel {
    val user = User()

    var screens = mutableListOf<Screen>()
    var currentScreenFlow = MutableStateFlow<Screen>(SplashScreen())
}

// main screen
fun showMainView() {}
fun requestFromUserSearchQuery() {}
fun requestFromServerNewFirstPageAds() {}
fun updateAdsView(){}

fun requestFromServerFirstPageAds() {}
// fun updateAdsView(){}
fun requestFromUserScrollAds() {}
fun requestFromServerNextPageAds() {}
//fun updateAdsView(){}

fun requestFromUserSelectedAd() {}
fun showNextViewErrorOrAdDetails() {}

// ad details
fun showAdDetailsView() {}
fun requestFromUserClickBack() {}
fun popAdDetailsView() {}

// ...

class SplashScreen(
    override val route: String = NavRoutes.Splash,
    override val isRoot: Boolean = true
) : Screen

class AuthScreen(
    override val route: String = NavRoutes.Auth,
    override val isRoot: Boolean = true
) : Screen

class MainScreen(
    override val route: String = NavRoutes.Main,
    override val isRoot: Boolean = true
): Screen {
    val adsList = AdsList(
        ads = emptyList(),
        query = "",
        page = 0
    )
}

class AdDetailsScreen(
    val ad: Ad,
    override val route: String = NavRoutes.AdDetails,
    override val isRoot: Boolean = false
): Screen

interface Screen {
    val route: String
    val isRoot: Boolean
}

data class User(
    var id: Long? = null,
    var name: String? = null,
    var contacts: Contacts? = null,
    var ownAds: List<Ad>? = null
) {
    fun isAuth(): Boolean {
        return id != null
    }
}

data class Contacts(
    val phone: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val email: String? = null
)

data class Ad(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val contacts: Contacts,
    val price: Double
)

data class AdsList(
    var ads: List<Ad>,
    var query: String,
    var page: Int
)
