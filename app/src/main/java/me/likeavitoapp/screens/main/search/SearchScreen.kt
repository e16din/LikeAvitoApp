package me.likeavitoapp.screens.main.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.likeavitoapp.Ad
import me.likeavitoapp.Category
import me.likeavitoapp.Contacts
import me.likeavitoapp.Route
import me.likeavitoapp.Screen
import me.likeavitoapp.SearchSettings

class SearchScreen(
    val input: Input = Input(),
    val state: State = State(),
    override val route: Route = Route()
) : Screen {

    class Input(
        var onPullToRefresh: () -> Unit = {},
        var onReloadClick: () -> Unit = {},
        var onSearchQuery: (query: String) -> Unit = {},
        var onClearClick: () -> Unit = {},
        var onScrollToEnd: () -> Unit = {},
        var onTipClick: (tip: String) -> Unit = {},
        var onSearchClick: (query: String) -> Unit = {},
        var onAdClick: (ad: Ad) -> Unit = {},
    )

    // ADVICE: set states only from use cases
    class State {
        var selectedCategory by mutableStateOf(Category(name = "", id = 0))
        var searchFilter by mutableStateOf(SearchSettings(
            category = selectedCategory,
            query = "",
            region = SearchSettings.Region(name = "Все регионы", id = 0),
            priceRange = SearchSettings.PriceRange(from = 0, to = Int.MAX_VALUE)
        ))
        var categories by mutableStateOf(emptyList<Category>())

        var searchTips by mutableStateOf(emptyList<String>())
        var tipsLoading by mutableStateOf(false)

        var adsLoadingError by mutableStateOf(false)
        var adsLoading by mutableStateOf(false)
        var ads by mutableStateOf(
            listOf(
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
    }
}