package me.likeavitoapp.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import me.likeavitoapp.R
import me.likeavitoapp.log
import me.likeavitoapp.logError

@Composable
fun ActualAsyncImage(modifier: Modifier = Modifier, url: String) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp),
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .build(),
        contentScale = ContentScale.Companion.FillWidth,
        contentDescription = "image",
        onLoading = {
        },
        onSuccess = {
        },
        onError = { error ->
            error.result.throwable.log()
        }
    )
}