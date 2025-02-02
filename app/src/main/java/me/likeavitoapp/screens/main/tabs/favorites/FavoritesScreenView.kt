package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.AdView
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun FavoritesScreenProvider(screen: FavoritesScreen) {
    val nextScreen by screen.navigator.screen.collectAsState()

    Box {
        LaunchedEffect(Unit) {
            screen.StartScreenUseCase()
        }

        FavoritesScreenView(screen)

        when (nextScreen) {
            is SearchScreen -> SearchScreenProvider(nextScreen as SearchScreen)
            is ProfileScreen -> ProfileScreenProvider(nextScreen as ProfileScreen)
            is CartScreen -> CartScreenProvider(nextScreen as CartScreen)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreenView(screen: FavoritesScreen) {
    val ads by screen.state.ads.data.collectAsState()
    val moveToAdsEnabled by screen.state2.moveToAdsEnabled.collectAsState()

    val listState = rememberLazyListState()
    val displayButton = !listState.canScrollBackward || listState.lastScrolledBackward

    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures()
            }) {

        if (moveToAdsEnabled) {
            Box {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = buildAnnotatedString {
                        val link =
                            LinkAnnotation.Url(
                                "stub",
                                TextLinkStyles(SpanStyle(color = Color.Blue))
                            ) {
                                screen.ClickToMoveToAdsUseCase()
                            }
                        withLink(link) { append("Перейти к объявлениям") }
                    }
                )
            }

        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {

                stickyHeader {
                    AnimatedVisibility(displayButton) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ElevatedButton(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(horizontal = 16.dp),
                                onClick = {
                                    screen.ClickToClearAllUseCase()
                                }) {
                                Text("Очистить")
                            }
                        }
                    }
                }

                items(items = ads, key = { ad ->
                    ad.id
                }) { ad ->
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
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val screen = FavoritesScreen(
        parentNavigator = mockScreensNavigator(),
        scope = mockCoroutineScope(),
        sources = mockDataSource()
    ).apply {
        state.ads.data.value = MockDataProvider().getFavorites()
    }

    LikeAvitoAppTheme {
        FavoritesScreenView(screen)
    }
}