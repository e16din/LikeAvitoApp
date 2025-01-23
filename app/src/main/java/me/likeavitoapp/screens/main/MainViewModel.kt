package me.likeavitoapp.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import me.likeavitoapp.Ad
import me.likeavitoapp.AdDetailsScreen
import me.likeavitoapp.Contacts
import me.likeavitoapp.DataSources
import me.likeavitoapp.UserDataSource
import me.likeavitoapp.exceptionHandler


class MainViewModel : ViewModel() {
    val userDataSource = MainDataSource()

    private val sources = DataSources(userDataSource)

    private val getAdsUseCase = GetAdsUseCase(viewModelScope, sources)
    private val changeSearchQueryUseCase = ChangeSearchQueryUseCase(viewModelScope, sources)
    private val adDetailsUseCase = AdDetailsUseCase(sources)

    init {
        getAdsUseCase.runWith("")
        with(sources.user.input) {
            onScrollToEnd = {
                getAdsUseCase.runWith()
            }

            onSearchQuery = { query ->
                changeSearchQueryUseCase.runWith(query)
            }
            onClearClick = {
                changeSearchQueryUseCase.runWith("")
            }

            onTipClick = { tip ->
                getAdsUseCase.runWith(tip)
            }
            onSearchClick = { query ->
                getAdsUseCase.runWith(query)
            }

            onAdClick = { ad ->
                adDetailsUseCase.runWith(ad)
            }
        }
    }
}

class MainDataSource(
    val input: Input = Input(), val state: State = State()
) : UserDataSource {

    class Input(
        var onSearchQuery: (query: String) -> Unit = {},
        var onClearClick: () -> Unit = {},
        var onScrollToEnd: () -> Unit = {},
        var onTipClick: (tip: String) -> Unit = {},
        var onSearchClick: (query: String) -> Unit = {},
        var onAdClick: (ad: Ad) -> Unit = {},
    )

    // ADVICE: set states only from use cases
    class State {
        var searchQuery by mutableStateOf("")
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
                    imageUrl = "https://ir-3.ozone.ru/s3/multimedia-1-n/wc1000/6917949671.jpg",
                    contacts = Contacts(
                        phone = "8XXXXXX1234",
                        whatsapp = null,
                        telegram = "@any_contact",
                        email = "any@gmail.com"
                    ),
                    price = 100000.0
                )
            )
        )
    }
}

class AdDetailsUseCase(
    val sources: DataSources<MainDataSource>
) {
    fun runWith(ad: Ad) {
        sources.app.currentScreenFlow.tryEmit(
            AdDetailsScreen(ad)
        )
    }
}


class GetAdsUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<MainDataSource>
) {
    var page = 0
    var lastQuery = ""

    fun runWith(query: String = lastQuery) = with(sources.user.state) {
        if (sources.user.state.adsLoading) {
            return@with
        }

        if (lastQuery != query) {
            page = 0
        }
        lastQuery = query
        adsLoading = true

        scope.launch(exceptionHandler) {
            val result = sources.backend.adsService.getAds(page = page, query = query)
            val adsList = result.getOrNull()
            adsLoading = false
            if (result.isSuccess && adsList != null) {
                ads = adsList.ads
            } else {
                adsLoadingError = true
            }
        }
    }
}

class ChangeSearchQueryUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<MainDataSource>
) {

    var queryFlow: MutableStateFlow<String>? = null

    fun runWith(newQuery: String) = with(sources.user.state) {
        searchQuery = newQuery

        if (queryFlow == null) {
            queryFlow = MutableStateFlow(newQuery)
            scope.launch {
                queryFlow?.debounce(390)?.collect { lastQuery ->
                    tipsLoading = true
                    val result = sources.backend.adsService.getSearchTips(lastQuery)
                    tipsLoading = false
                    searchTips = result.getOrNull() ?: emptyList()
                }
            }
        } else {
            queryFlow?.tryEmit(newQuery)
        }
    }
}