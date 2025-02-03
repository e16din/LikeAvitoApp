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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.likeavitoapp.model.Ad
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Preview(showBackground = true)
@Composable
fun AdViewPreview() {
    LikeAvitoAppTheme {
        AdView(
            ad = MockDataProvider().getAd(1),
            onItemClick = {},
            onFavoriteClick = {},
            onBuyClick = {},
            onBargainingClick = { }
        )
    }
}

@Composable
inline fun AdView(
    ad: Ad,
    crossinline onItemClick: (ad: Ad) -> Unit,
    crossinline onFavoriteClick: (ad: Ad) -> Unit,
    crossinline onBuyClick: (ad: Ad) -> Unit,
    crossinline onBargainingClick: (ad: Ad) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteSelected by ad.isFavorite.collectAsState()
    Card(
        modifier = modifier,
        onClick = {
            onItemClick(ad)
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
            Box(modifier = Modifier
                .fillMaxWidth()) {
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
                        onFavoriteClick(ad)
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

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        onBuyClick(ad)
                    }) {
                    Text(text = "Купить за " + "${ad.price}₽",
                        style = AppTypography.bodySmall)
                }

                Spacer(Modifier.weight(1f))

                if (ad.isBargainingEnabled) {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = {
                            onBargainingClick(ad)
                        }) {
                        Text(text = stringResource(R.string.bargaining_button)
                        , style = AppTypography.labelSmall
                        )
                    }
                }
            }
        }
    }

}
