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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import me.likeavitoapp.screens.ClosableMessage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Preview(showBackground = true)
@Composable
fun AdViewPreview() {
    LikeAvitoAppTheme {
        val screen = BaseAdScreen(
            parentNavigator = mockScreensNavigator(),
            scope = mockCoroutineScope(),
            sources = mockDataSource()
        )

        AdView(
            ad = MockDataProvider().getAd(1),
            screen = screen,
            isFavorite = remember { mutableStateOf(true) },
            timerLabel = remember { mutableStateOf("12:12") }
        )
    }
}

@Composable
fun AdView(
    ad: Ad,
    screen: BaseAdScreen,
    modifier: Modifier = Modifier,
    isFavorite: State<Boolean>,
    timerLabel: State<String>
) {
    Card(modifier = modifier) {
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
                        imageVector = if (isFavorite.value)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = "favorite",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Red
                    )
                }

                if (!timerLabel.value.isEmpty()) {
                    ClosableMessage(
                        text = stringResource(R.string.continue_order_label, timerLabel.value),
                        onCloseClick = {
                            screen.ClickToCloseTimerLabel(ad)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(vertical = 24.dp, horizontal = 24.dp)
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

