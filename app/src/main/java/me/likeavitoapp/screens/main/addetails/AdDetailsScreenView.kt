package me.likeavitoapp.screens.main.addetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun AdDetailsScreenProvider(screen: AdDetailsScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        AdDetailsScreenView(screen)
    }

    BackHandler {
        screen.PressBack()
    }
}

@Composable
fun AdDetailsScreenView(screen: AdDetailsScreen) = with(screen.state) {
    val favoriteSelected by screen.state.ad.isFavorite.collectAsState()


    Column {
        Text(
            text = ad.title,
            style = AppTypography.titleLarge,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis
        )
        Box {
            val pagerState = rememberPagerState(pageCount = {
                ad.photoUrls.size
            })
            HorizontalPager(state = pagerState) { page ->
                ActualAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    url = ad.photoUrls[page]
                )
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                onClick = {
                    screen.ClickToFavoriteUseCase()
                }
            ) {
                Icon(
                    imageVector = if (favoriteSelected)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "favorite",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Red
                )
            }
        }

        Text(
            text = ad.description,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
            maxLines = 3,
            overflow = TextOverflow.Companion.Ellipsis
        )

        Row(modifier = Modifier) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    screen.ClickToBuyUseCase(ad)
                }) {
                Text(text = "Купить за " + "${ad.price}₽")
            }

            Spacer(Modifier.weight(1f))

            if (ad.isBargainingEnabled) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        screen.ClickToBargaining()
                    }) {
                    Text(text = stringResource(R.string.bargaining_button))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdDetailsScreenPreview() {
    LikeAvitoAppTheme {
        AdDetailsScreenView(
            AdDetailsScreen(
                ad = MockDataProvider().getAd(0L),
                parentNavigator = mockScreensNavigator(),
                scope = mockCoroutineScope(),
                sources = mockDataSource()
            )
        )
    }
}