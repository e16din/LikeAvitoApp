package me.likeavitoapp.screens.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SearchScreenProvider(screen: SearchScreen) {

    LaunchedEffect(Unit) {
        screen.listenLoadAdsCalls()
    }
    LaunchedEffect(Unit) {
        screen.listenLoadCategoriesCalls()
    }
    LaunchedEffect(Unit) {
        screen.searchBar.listenChangeSearchQueryCalls()
    }
    SearchScreenView(screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenView(screen: SearchScreen) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    val SEARCH_VIEW_HEIGHT_DP = 64.dp

    val appBarMaxHeight = with(LocalDensity.current) {
        SEARCH_VIEW_HEIGHT_DP.toPx() * 2
    }
    val connection = remember {
        CollapsingAppBarNestedScrollConnection(appBarMaxHeight.toInt())
    }

    val ads by screen.state.ads.data.collectAsState()
    val query by screen.state.searchSettings.query.collectAsState()
    val searchTips by screen.state.searchTips.data.collectAsState()
    val selectedCategory by screen.state.searchSettings.selectedCategory.collectAsState()
    val categories by screen.state.searchSettings.categories.data.collectAsState()
    var searchBarExpanded by remember { mutableStateOf(false) }
    val searchFilterPanelEnabled by screen.state.searchSettingsPanelEnabled.collectAsState()

    fun hasSelectedCategory(): Boolean = selectedCategory?.id != 0

Box {
    Box(modifier = Modifier
        .background(Color.Black)
        .nestedScroll(connection)) {

        Column(
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp),

                ) {

                item {
                    val spacerSize = (36 + if (hasSelectedCategory()) 32 else 0).dp
                    Spacer(
                        modifier = Modifier
                            .size(spacerSize)
                            .systemBarsPadding()
                            .background(Color.Black)
                    )
                }

                items(count = ads.size) { index ->
                    val ad = ads[index]
                    if (ad.isPremium) {
                        AdView(
                            ad = ad,
                            onItemClick = { ad ->
                                screen.adsList.ClickToAdUseCase(ad)
                            },
                            onFavoriteClick = {
                                screen.adsList.ClickToFavoriteUseCase()
                            },
                            onBuyClick = {
                                screen.adsList.ClickToBuyUseCase()
                            },
                            onBargainingClick = {
                                screen.adsList.ClickToBargainingUseCase()
                            }
                        )

                    } else {
                        MinAdView(
                            ad = ad,
                            onItemClick = { ad ->
                                screen.adsList.ClickToAdUseCase(ad)
                            },
                            onFavoriteClick = {
                                screen.adsList.ClickToFavoriteUseCase()
                            }
                        )
                    }
                }

                if (!screen.state.ads.data.value.isEmpty()) {
                    item {
                        LaunchedEffect(Unit) {
                            screen.adsList.ScrollToEndUseCase()
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .systemBarsPadding()
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

        if (hasSelectedCategory()) {
            Text(
                text = selectedCategory?.name ?: "",
                modifier = Modifier
                    .background(Color.Yellow)
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            )
        } else {
            if (!hasSelectedCategory()) {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
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
                                        screen.searchBar.ClickToCategoryUseCase(categories[index])
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
        }

        if(searchFilterPanelEnabled) {
            SearchSettingsPanelView(
                settings = screen.state.searchSettings,
                onDismiss = {
                    screen.DismissSearchSettingsPanelUseCase()
                }
            )
        }
    }

    }

//    Box(
//        Modifier
//            .systemBarsPadding()
//            .nestedScroll(connection)
//    ) {
//
//        Column(modifier = Modifier.fillMaxSize()) {
//
//            LazyColumn(
//                contentPadding = PaddingValues(
//                    start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
//                ), verticalArrangement = Arrangement.spacedBy(24.dp)
//            ) {
//
//                items(count = ads.size) { index ->
//                    val ad = ads[index]
//                    AdView(ad, onClick = { ad ->
//                        screen.ClickToAdUseCase(ad)
//                    })
//
//                }
//                item {
//                    LaunchedEffect(Unit) {
//                        screen.ScrollToEndUseCase()
//                    }
//                }
//            }
//        }
//        Column(
//            modifier = Modifier
//                .offset { IntOffset(0, connection.appBarOffset) }) {
//
//            SearchView(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                height = SEARCH_VIEW_HEIGHT_DP,
//                query = query,
//                tips = searchTips,
//                clearEnabled = query.isNotEmpty(),
//                onQueryChanged = { value ->
//                    scope.launch {
//                        screen.ChangeSearchQueryUseCase(value)
//                    }
//                },
//                onClearClick = {
//                    scope.launch {
//                        screen.ClickToClearQueryUseCase()
//                    }
//                },
//                onImeSearchClick = {
//                    scope.launch {
//                        screen.ClickToSearchUseCase()
//                    }
//                })
//        }
//    }
}

class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Int
) : NestedScrollConnection {

    var appBarOffset: Int by mutableIntStateOf(0)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y.toInt()
        val newOffset = appBarOffset + delta
        val previousOffset = appBarOffset
        appBarOffset = newOffset.coerceIn(-appBarMaxHeight, 0)
        val consumed = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    LikeAvitoAppTheme {
        SearchScreenView(
            SearchScreen(prevScreen = null)
        )
    }
}