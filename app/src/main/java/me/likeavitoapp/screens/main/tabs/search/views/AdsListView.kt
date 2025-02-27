package me.likeavitoapp.screens.main.tabs.search.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.main.tabs.AdView
import me.likeavitoapp.screens.main.tabs.search.SearchScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
inline fun AdsListView(
    screen: SearchScreen,
    crossinline stickyHeaderContent: @Composable (displayHeader:Boolean) -> Unit
) {

    val listState = rememberLazyListState()
    val displayHeader = !listState.canScrollBackward || listState.lastScrolledBackward
    val isAtTheEndOfList by remember(listState) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    if (isAtTheEndOfList) {
        screen.ScrollToEndUseCase()
    }

    var ads = screen.state.ads.output.collectAsState()

    val adsListenersMap = remember { mutableMapOf<Long, State<Boolean>>() }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp),

        ) {

        stickyHeader {
            stickyHeaderContent(displayHeader)
        }

        items(items = ads.value, key = { ad ->
            ad.id
        }) { ad ->
            adsListenersMap[ad.id] = ad.isFavorite.collectAsState()

            if (ad.isPremium) {
                AdView(
                    isFavorite = ad.isFavorite.collectAsState(),
                    timerLabel = ad.timerLabel.collectAsState(),
                    modifier = Modifier
                        .animateItem()
                        .clickable {
                            screen.ClickToAdUseCase(ad)
                        },
                    ad = ad,
                    screen = screen
                )

            } else {
                MinAdView(
                    modifier = Modifier.animateItem(),
                    isFavorite = ad.isFavorite.collectAsState(),
                    ad = ad,
                    onItemClick = { ad ->
                        screen.ClickToAdUseCase(ad)
                    },
                    onFavoriteClick = { ad ->
                        screen.ClickToFavoriteUseCase(ad)
                    }
                )
            }
        }
    }
}