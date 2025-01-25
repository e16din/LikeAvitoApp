package me.likeavitoapp.screens.main.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.Ad
import me.likeavitoapp.Category
import me.likeavitoapp.Loadable
import me.likeavitoapp.Route
import me.likeavitoapp.RouteTabStub
import me.likeavitoapp.Screen
import me.likeavitoapp.SearchSettings
import kotlin.collections.emptyList

class SearchScreen(
    val input: Input = Input(),
    val state: State = State(),
    override val route: Route = RouteTabStub
) : Screen {

    class Input(
        var onPullToRefresh: () -> Unit = {},
        var onReloadClick: () -> Unit = {},
        var onScrollToEnd: () -> Unit = {},
        var onAdClick: (ad: Ad) -> Unit = {},

        // search
        var onSearchQuery: (query: String) -> Unit = {},
        var onClearSearchClick: () -> Unit = {},
        var onSearchTipClick: (tip: String) -> Unit = {},
        var onSearchClick: (query: String) -> Unit = {},
        )

    class State {
        val categories = Loadable(emptyList<Category>())
        val ads = Loadable(emptyList<Ad>())
        var adsPage by mutableStateOf(0)
        var searchTips = Loadable(emptyList<String>())
        var selectedCategory by mutableStateOf(Category(name = "", id = 0))
        var searchFilter by mutableStateOf(
            SearchSettings(
                category = selectedCategory,
                query = "",
                region = SearchSettings.Region(name = "Все регионы", id = 0),
                priceRange = SearchSettings.PriceRange(from = 0, to = Int.MAX_VALUE)
            )
        )
    }
}