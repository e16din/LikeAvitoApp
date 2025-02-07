package me.likeavitoapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.likeavitoapp.ui.theme.primaryLight

@Composable
fun ClosableMessage(
    text: String,
    onCloseClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(8))
                .background(primaryLight)
                .align(Alignment.BottomStart)
                .padding(16.dp),
            color = Color.White,
            text = text
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "close",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .size(21.dp)
                .background(lightColorScheme().primaryContainer)
                .padding(4.dp)
                .clickable {
                    onCloseClick()
                },
        )
    }
}