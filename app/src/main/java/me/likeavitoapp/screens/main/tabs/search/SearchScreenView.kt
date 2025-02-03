package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.AdView
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreenProvider
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SearchScreenProvider(screen: SearchScreen) {
    val nextScreen by screen.navigator.screen.collectAsState()

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }
    Box {
        SearchScreenView(screen)

        when (nextScreen) {
            is FavoritesScreen -> FavoritesScreenProvider(nextScreen as FavoritesScreen)
            is ProfileScreen -> ProfileScreenProvider(nextScreen as ProfileScreen)
            is CartScreen -> CartScreenProvider(nextScreen as CartScreen)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreenView(screen: SearchScreen) {
    val listState = rememberLazyListState()
    val isAtTheEndOfList by remember(listState) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }
    val displayTopBar = !listState.canScrollBackward || listState.lastScrolledBackward

    if (isAtTheEndOfList) {
        LaunchedEffect(Unit) {
            screen.ScrollToEndUseCase()
        }
    }

    val ads by screen.state.ads.data.collectAsState()
    val query by screen.searchBar.state.query.collectAsState()
    val searchTips by screen.searchBar.state.searchTips.data.collectAsState()
    val selectedCategory by screen.searchSettingsPanel.state.selectedCategory.collectAsState()
    val categories by screen.searchSettingsPanel.state.categories.data.collectAsState()
    var searchBarExpanded by remember { mutableStateOf(false) }
    val searchFilterPanelEnabled by screen.searchSettingsPanel.state.enabled.collectAsState()
    val searchSettingsSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    fun hasSelectedCategory(): Boolean = selectedCategory?.id != 0

    Box(
        Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),

            ) {

            stickyHeader {
                AnimatedVisibility(
                    visible = displayTopBar,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        SearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    onSearch = { searchBarExpanded = false },
                                    expanded = searchBarExpanded,
                                    onExpandedChange = { searchBarExpanded = it },
                                    placeholder = { Text(stringResource(R.string.search_hint)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "searchbar_leading_icon"
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            ImageVector.vectorResource(R.drawable.baseline_tune_24),
                                            "searchbar_trailing_icon",
                                            modifier = Modifier.clickable {
                                                screen.searchBar.ClickToFilterButtonUseCase()
                                            })
                                    },
                                    query = query,
                                    onQueryChange = { newQuery ->
                                        screen.searchBar.ChangeSearchQueryUseCase(newQuery)
                                    },

                                    )
                            },
                            expanded = searchBarExpanded,
                            onExpandedChange = { expanded ->
                                searchBarExpanded = expanded
                            }
                        ) {
                            // onExpanded
                        }

                        AnimatedVisibility(visible = !hasSelectedCategory()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(136.dp)
                            ) {
                                LazyHorizontalStaggeredGrid(
                                    modifier = Modifier.wrapContentHeight(),
                                    rows = StaggeredGridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalItemSpacing = 8.dp,
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(categories.size) { index ->
                                        Card(
                                            modifier = Modifier
                                                .wrapContentHeight()
                                                .clickable {
                                                    screen.searchBar.ClickToCategoryUseCase(
                                                        categories[index]
                                                    )
                                                }
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(16.dp),
                                                text = categories[index].name
                                            )
                                        }
                                    }
                                }
                            }

                        }

                        AnimatedVisibility(visible = hasSelectedCategory()) {
                            Text(
                                text = selectedCategory?.name ?: "",
                                modifier = Modifier
                                    .background(Color.Yellow)
                                    .padding(vertical = 4.dp, horizontal = 16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }


            }

            items(items = ads, key = { ad ->
                ad.id
            }) { ad ->
                if (ad.isPremium) {
                    AdView(
                        modifier = Modifier.animateItem(),
                        ad = ad,
                        onItemClick = { ad ->
                            screen.ClickToAdUseCase(ad)
                        },
                        onFavoriteClick = { ad ->
                            screen.ClickToFavoriteUseCase(ad)
                        },
                        onBuyClick = {
                            screen.ClickToBuyUseCase()
                        },
                        onBargainingClick = {
                            screen.ClickToBargainingUseCase()
                        }
                    )

                } else {
                    MinAdView(
                        modifier = Modifier.animateItem(),
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

        if (searchFilterPanelEnabled) {
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
    val screen = SearchScreen(
        parentNavigator = mockScreensNavigator(),
        scope = mockCoroutineScope(),
        sources = mockDataSource()
    ).apply {
        state.ads.data.value = MockDataProvider().getAds(0, 0, "")
    }

    LikeAvitoAppTheme {
        SearchScreenView(
            screen
        )
    }
}