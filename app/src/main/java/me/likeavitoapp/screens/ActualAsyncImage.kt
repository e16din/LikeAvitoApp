package me.likeavitoapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import me.likeavitoapp.R
import me.likeavitoapp.log

@Composable
fun ActualAsyncImage(
    modifier: Modifier = Modifier,
    url: String,
    contentScale:ContentScale = ContentScale.FillWidth) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .build(),
        contentScale = contentScale,
        contentDescription = "photo",
        onLoading = {
        },
        onSuccess = {
        },
        onError = { error ->
            error.result.throwable.log()
        }
    )
}