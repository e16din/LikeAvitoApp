package me.likeavitoapp.screens.main.addetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.likeavitoapp.R
import me.likeavitoapp.model.dataSourcesWithScreen


@Composable
fun AdDetailsScreenProvider(screen: AdDetailsScreen) {

    AdDetailsScreenView(screen)

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
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis
            )
            Box {
                AsyncImage(
                    model = ad.photoUrls.first(),
                    contentDescription = null,
                    error = painterResource(R.drawable.placeholder)
                )

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
                    val size = 32.dp
                    if (favoriteSelected) {
                        Icon(Icons.Default.Favorite, "favorite_selected", Modifier.size(size))
                    } else {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            "favorite_unselected",
                            Modifier.size(size)
                        )
                    }
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
                        screen.ClickToBuyUseCase()
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