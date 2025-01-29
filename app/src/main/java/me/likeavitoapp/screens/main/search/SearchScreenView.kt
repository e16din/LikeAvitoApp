package me.likeavitoapp.screens.main.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.likeavitoapp.Ad
import me.likeavitoapp.Loadable
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.screens.main.search.SearchScreen.State
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SearchScreenProvider(screen: SearchScreen) {

    LaunchedEffect(Unit) {
        screen.ListenGetAdsUseCase()
    }
    LaunchedEffect(Unit) {
        screen.ListenChangeSearchQueryUseCase()
    }
    SearchScreenView(screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenView(screen: SearchScreen) {
    LaunchedEffect(Unit) {
        screen.ReloadDataUseCase()
    }

    val SEARCH_VIEW_HEIGHT_DP = 64.dp

    val appBarMaxHeight = with(LocalDensity.current) {
        SEARCH_VIEW_HEIGHT_DP.toPx() * 2
    }
    val connection = remember {
        CollapsingAppBarNestedScrollConnection(appBarMaxHeight.toInt())
    }

    val ads by screen.state.ads.data.collectAsState()
    val query by screen.state.searchFilter.query.collectAsState()
    val searchTips by screen.state.searchTips.data.collectAsState()
    val selectedCategory by screen.state.searchFilter.category.collectAsState()
    val categories by screen.state.categories.data.collectAsState()
    var searchBarExpanded by remember { mutableStateOf(false) }

    fun hasSelectedCategory(): Boolean = selectedCategory.id != 0

    Column(
        modifier = Modifier
            .systemBarsPadding()
    ) {
        androidx.compose.material3.SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    onSearch = { searchBarExpanded = false },
                    expanded = searchBarExpanded,
                    onExpandedChange = { searchBarExpanded = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                    query = query,
                    onQueryChange = { newQuery ->
                        screen.ChangeSearchQueryUseCase(newQuery)
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
            Column(
                modifier = Modifier
                    .systemBarsPadding()
            ) {
                Text(
                    text = selectedCategory.name,
                    modifier = Modifier
                        .background(Color.Yellow)
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }


    Box {

        Column(
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp),

                ) {

                item {
                    val spacerSize = (36 + if(hasSelectedCategory()) 32 else 0).dp
                    Spacer(modifier = Modifier.size(spacerSize).systemBarsPadding())
                }

                if (!hasSelectedCategory()) {
                    item {
                        Column(
                            modifier = Modifier
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
                                                screen.ClickToCategoryUseCase(categories[index])
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

                items(count = ads.size) { index ->
                    val ad = ads[index]
                    AdView(ad, onClick = { ad ->
                        screen.ClickToAdUseCase(ad)
                    })

                }
                item {
                    LaunchedEffect(Unit) {
                        screen.ScrollToEndUseCase()
                    }
                }
            }
        }
    }


//    SearchView(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        height = SEARCH_VIEW_HEIGHT_DP,
//        query = query,
//        tips = searchTips,
//        clearEnabled = query.isNotEmpty(),
//        onQueryChanged = { value ->
//            scope.launch {
//                screen.ChangeSearchQueryUseCase(value)
//            }
//        },
//        onClearClick = {
//            scope.launch {
//                screen.ClickToClearQueryUseCase()
//            }
//        },
//        onImeSearchClick = {
//            scope.launch {
//                screen.ClickToSearchUseCase()
//            }
//        })

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

@Composable
inline fun AdView(ad: Ad, crossinline onClick: (ad: Ad) -> Unit) {
    val color = if (ad.isPremium) Color(0xff00c000) else Color(0xffffffff)
    Card(
        modifier =
            Modifier.background(color = color),
        onClick = {
            onClick(ad)
        }) {
        Text(
            text = ad.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = null,
        )

        Text(
            text = ad.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
inline fun SearchView(
    height: Dp = 80.dp,
    query: String,
    tips: List<String>,
    modifier: Modifier = Modifier,
    clearEnabled: Boolean,
    crossinline onQueryChanged: (query: String) -> Unit,
    crossinline onClearClick: () -> Unit,
    crossinline onImeSearchClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        if (tips.isNotEmpty()) Color.Green else Color.Blue, label = "color"
    )

    Column(
        modifier = modifier
            .clip(CircleShape)
            .drawBehind {
                drawRect(animatedColor)
            }
            .animateContentSize()) {
        TextField(
            modifier = Modifier
                .height(height)
                .fillMaxWidth(),
            value = query,
            onValueChange = { query -> onQueryChanged(query) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color(0XFF101921),
                focusedPlaceholderColor = Color(0XFF888D91),
                focusedLeadingIconColor = Color(0XFF888D91),
                focusedTrailingIconColor = Color(0XFF888D91),
                focusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0XFF070E14)
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
            trailingIcon = {
                if (clearEnabled) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "",
                        modifier = Modifier.clickable(onClick = {
                            onClearClick()
                        })
                    )
                }

            },
            placeholder = { Text(text = "Search") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onImeSearchClick()
                }
            ))

        repeat(tips.size) { idx ->
            val resultText = "Suggestion $idx"
            ListItem(
                headlineContent = { Text(resultText) },
                supportingContent = { Text("Additional info") },
                leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable {
                        onQueryChanged(resultText)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp))
        }

    }
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
            SearchScreen(
                state = State(
                    ads = Loadable<List<Ad>>(
                        initial = MockDataProvider().getAds(1, 0, "")
                    )
                ),
                prevScreen = null
            )
        )
    }
}