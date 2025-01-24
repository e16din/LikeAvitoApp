package me.likeavitoapp.screens.main.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.likeavitoapp.Ad
import me.likeavitoapp.CollapsingAppBarNestedScrollConnection
import me.likeavitoapp.DataSources
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SearchScreenProvider() {
    val scope = rememberCoroutineScope()

    val sources = DataSources<SearchScreen>()

    val reloadUseCase = ReloadUseCase(scope, sources)
    val getCategoriesUseCase = GetCategoriesUseCase(scope, sources)
    val getAdsUseCase = GetAdsUseCase(scope, sources)
    val changeSearchQueryUseCase = ChangeSearchQueryUseCase(scope, sources)
    val adDetailsUseCase = AdDetailsUseCase(sources)

    SearchScreenView(sources.screen)

    LaunchedEffect(Unit) {
        fun reload() {
            reloadUseCase.runWith {
                getCategoriesUseCase.run()?.join()
                getAdsUseCase.runWith("")?.join()
            }
        }

        reload()

        with(sources.screen.input) {
            onReloadClick = {
                reload()
            }

            onPullToRefresh = {
                reload()
            }

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

@Composable
fun SearchScreenView(screen: SearchScreen) {
    val SEARCH_VIEW_HEIGHT_DP = 64.dp

    val appBarMaxHeight = with(LocalDensity.current) {
        SEARCH_VIEW_HEIGHT_DP.toPx() * 2
    }
    val connection = remember {
        CollapsingAppBarNestedScrollConnection(appBarMaxHeight.toInt())
    }

    Box(
        Modifier
            .systemBarsPadding()
            .nestedScroll(connection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
                ), verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(count = screen.state.ads.size) { index ->
                    AdView(screen.state.ads[index], onClick = { ad ->
                        screen.input.onAdClick(ad)
                    })
                }
            }
        }
        Column(
            modifier = Modifier
                .offset { IntOffset(0, connection.appBarOffset) }) {

            SearchView(
                modifier = Modifier.padding(horizontal = 16.dp),
                height = SEARCH_VIEW_HEIGHT_DP,
                query = screen.state.searchFilter.query,
                tips = screen.state.searchTips,
                clearEnabled = screen.state.searchFilter.query.isNotEmpty(),
                onQueryChanged = { value ->
                    screen.input.onSearchQuery(value)
                },
                onClearClick = {
                    screen.input.onClearClick()
                },
                onSearchClick = { query ->
                    screen.input.onSearchClick(query)
                })
        }
    }
}

@Composable
inline fun AdView(ad: Ad, crossinline onClick: (ad: Ad) -> Unit) {
    val color = if(ad.isPremium) Color(0xff00c000)  else Color(0xffffffff)
    Card(modifier =
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
    crossinline onSearchClick: (query: String) -> Unit
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
                    onSearchClick(query)
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        SearchScreenView(SearchScreen())
    }
}