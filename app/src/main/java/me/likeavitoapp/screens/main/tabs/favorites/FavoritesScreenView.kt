package me.likeavitoapp.screens.main.tabs.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.AdView
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun FavoritesScreenProvider(screen: FavoritesScreen, tabsNavigator: ScreensNavigator) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            FavoritesScreenView(screen)
        }

        NextTabProvider(screen, tabsNavigator)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreenView(screen: FavoritesScreen) {
    val ads = screen.state.favorites.output.collectAsState()

    val moveToAdsEnabled by screen.state.moveToAdsEnabled.collectAsState()

    val listState = rememberLazyListState()
    val displayButton = !listState.canScrollBackward || listState.lastScrolledBackward

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
                    withLink(link) { append(stringResource(R.string.moe_to_ads_button)) }
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
                            Text(stringResource(R.string.clear))
                        }
                    }
                }
            }

            items(items = ads.value, key = { ad ->
                ad.id
            }) { ad ->
                AdView(
                    ad = ad,
                    screen = screen,
                    modifier = Modifier.animateItem().clickable {
                        screen.ClickToAdUseCase(ad)
                    },
                    isFavorite = ad.isFavorite.collectAsState(),
                    timerLabel = ad.timerLabel.collectAsState()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    get = mockMainSet()
    val screen = FavoritesScreen(
        navigator = mockScreensNavigator(),
    ).apply {
        state.favorites.output.post(MockDataProvider().getFavorites().toMutableStateList())
    }

    LikeAvitoAppTheme {
        FavoritesScreenView(screen)
    }
}