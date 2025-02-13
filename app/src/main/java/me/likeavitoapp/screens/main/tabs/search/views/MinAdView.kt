package me.likeavitoapp.screens.main.tabs.search.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.model.Ad
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
inline fun MinAdView(
    isFavorite: State<Boolean>,
    ad: Ad,
    crossinline onItemClick: (ad: Ad) -> Unit,
    crossinline onFavoriteClick: (ad: Ad) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        onClick = {
            onItemClick(ad)
        }) {

        Box {
            ActualAsyncImage(
                modifier = Modifier
                    .fillMaxWidth(),
                url = ad.photoUrls.first()
            )
            Text(
                text = ad.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis
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
                    imageVector = if (isFavorite.value)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "favorite",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Red
                )
            }
            Text(
                text = "${ad.price.toInt()}₽",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .align(Alignment.BottomEnd),
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MinAdViewPreview() {
    LikeAvitoAppTheme {
        MinAdView(
            isFavorite = remember { mutableStateOf(true) },
            ad = MockDataProvider().ads[1],
            onItemClick = {},
            onFavoriteClick = {}
        )
    }
}
