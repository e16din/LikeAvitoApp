package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.res.painterResource
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
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Preview(showBackground = true)
@Composable
fun MinAdViewPreview() {
    LikeAvitoAppTheme {
        MinAdView(
            ad = MockDataProvider().getAd(1),
            onItemClick = {},
            onFavoriteClick = {}
        )
    }
}

@Composable
inline fun MinAdView(
    ad: Ad,
    crossinline onItemClick: (ad: Ad) -> Unit,
    crossinline onFavoriteClick: (ad: Ad) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteSelected by ad.isFavorite
    Card(
        modifier = modifier,
        onClick = {
            onItemClick(ad)
        }) {

        Column {
            Text(
                text = ad.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis
            )
            Box {
                ActualAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "${ad.price.toInt()}₽",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }
            }
        }
    }

}
