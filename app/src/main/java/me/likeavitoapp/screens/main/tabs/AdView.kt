package me.likeavitoapp.screens.main.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.Ad
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Preview(showBackground = true)
@Composable
fun AdViewPreview() {
    LikeAvitoAppTheme {
        AdView(
            ad = MockDataProvider().getAd(1),
            screen = BaseAdScreen(
                parentNavigator = mockScreensNavigator(),
                scope = mockCoroutineScope(),
                sources = mockDataSource()
            )
        )
    }
}

@Composable
inline fun AdView(
    ad: Ad,
    screen: BaseAdScreen,
    modifier: Modifier = Modifier
) {
    val favoriteSelected by ad.isFavorite
    val timerLabel by ad.timerLabel

    Card(
        modifier = modifier,
        onClick = {
            screen.ClickToAdUseCase(ad)
        }) {

        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = ad.title,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ActualAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    url = ad.photoUrls.first()
                )

                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent),
                    onClick = {
                        screen.ClickToFavoriteUseCase(ad)
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

                if (!timerLabel.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 24.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        color = Color.White,
                        text = "Продолжить оформление заказа $timerLabel"
                    )
                    Button(onClick = {
                        screen.ClickToCloseTimerLabel(ad)
                    }) { }
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

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        screen.ClickToBuyUseCase(ad)
                    }) {
                    Text(
                        text = "Купить за " + "${ad.price}₽",
                        style = AppTypography.bodySmall
                    )
                }

                Spacer(Modifier.weight(1f))

                if (ad.isBargainingEnabled) {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = {
                            screen.ClickToBargainingUseCase(ad)
                        }) {
                        Text(
                            text = stringResource(R.string.bargaining_button),
                            style = AppTypography.labelSmall
                        )
                    }
                }
            }
        }
    }

}
