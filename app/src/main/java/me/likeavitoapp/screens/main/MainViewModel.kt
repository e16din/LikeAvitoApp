package me.likeavitoapp.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.likeavitoapp.Ad
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.Backend
import me.likeavitoapp.Contacts

class MainViewModel(
    val uiState: UiState = UiState(),
    val modelState: ModelState = ModelState(),
    val getAdsUseCase: GetAdsUseCase = GetAdsUseCase(uiState),
    val changeSearchQueryUseCase: ChangeSearchQueryUseCase = ChangeSearchQueryUseCase(uiState)
) : ViewModel() {

    class UiState {
        val adsLoadingErrorFlow = MutableStateFlow(false)
        val adsState = MutableStateFlow(
            listOf(
                Ad(
                    id = 1,
                    title = "MacBook Pro 14",
                    description = "Ноутбук, который расширяет ваши возможности\n" +
                            "Apple MacBook Pro 14\" 2024 года – это ноутбук, созданный для тех, кто привык к скорости, мощности и комфорту. С процессором M4 он обеспечивает идеальный баланс между автономностью и производительностью, достаточной для очень требовательных задач. В нём есть всё, чтобы работать или отдыхать где угодно, подключать любые дисплеи и аксессуары, а высокий уровень безопасности, в сочетании с идеальной оптимизацией macOS и сервисами Apple, позволяет не загружать себя лишними заботами, фокусируясь на том, что действительно важно.",
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

    // NOTE: emit only from ui classes
    class ModelState {}

    fun onStart() {
        viewModelScope.launch {
            getAdsUseCase.runWith("")
        }
    }

    fun onScrollToEnd() {

    }

    fun onTipClick(tip: String) {
        viewModelScope.launch {
            getAdsUseCase.runWith(tip)
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            changeSearchQueryUseCase.runWith("")
        }
    }

    fun onClearQueryClick() {
        viewModelScope.launch {
            changeSearchQueryUseCase.runWith("")
        }
    }

}

class GetAdsUseCase(
    val uiState: MainViewModel.UiState,
    val backend: Backend = AppPlatform.get.backend
) {
    var page = 0
    var lastQuery = ""

    suspend fun runWith(query: String) {
        if (lastQuery != query) {
            page = 0
        }
        lastQuery = query

        val result = backend.adsService.getAds(page = page, query = query)
        val adsList = result.getOrNull()
        if (result.isSuccess && adsList != null) {
            uiState.adsState.emit(adsList.ads)
        } else {
            uiState.adsLoadingErrorFlow.emit(true)
        }
    }
}

class ChangeSearchQueryUseCase(
    val uiState: MainViewModel.UiState,
    val backend: Backend = AppPlatform.get.backend,
) {
    suspend fun runWith(query: String) {
        // show loading
        // show tips
        // show clear button
        // show text
        val result = backend.getSearchTips(query)
    }
}