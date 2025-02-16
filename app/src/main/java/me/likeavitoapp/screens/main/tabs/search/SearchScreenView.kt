package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.get
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.screens.main.tabs.TabsRootScreen
import me.likeavitoapp.screens.main.tabs.search.views.AdsListView
import me.likeavitoapp.screens.main.tabs.search.views.SearchBarView
import me.likeavitoapp.screens.main.tabs.search.views.SearchSettingsPanelView
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SearchScreenProvider(
    screen: SearchScreen,
    tabsRootScreen: TabsRootScreen,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Surface(modifier = modifier.fillMaxSize()) {
            SearchScreenView(screen)
        }

        NextTabProvider(screen, tabsRootScreen)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreenView(screen: SearchScreen) {
    val searchFilterPanelEnabled by screen.searchSettingsPanel.state.enabled.collectAsState()
    var displayHeader by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = { },
            modifier = Modifier
        ) {
            Column {
                AnimatedVisibility(
                    visible = displayHeader,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    SearchBarView(screen)
                }
                AdsListView(
                    screen,
                    stickyHeaderContent = { isHeaderVisible ->
                        displayHeader = isHeaderVisible
                    }
                )
            }
        }

        if (searchFilterPanelEnabled) {
            val searchSettingsSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            )
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                contentWindowInsets = { WindowInsets.ime },
                sheetState = searchSettingsSheetState,
                onDismissRequest = {
                    screen.CloseSearchSettingsPanelUseCase()
                }
            ) {
                SearchSettingsPanelView(
                    panel = screen.searchSettingsPanel
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    get = mockMainSet()
    val screen = SearchScreen(
        navigator = mockScreensNavigator(),
    ).apply {
        state.ads.output.post(MockDataProvider().ads.toMutableStateList())
    }

    LikeAvitoAppTheme {
        SearchScreenView(
            screen
        )
    }
}