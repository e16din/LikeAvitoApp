package me.likeavitoapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    startIcon: () -> ImageVector? = { null },
    isStartIconEnabled: Boolean = false,
    startIconTint: Color = Color.Companion.Unspecified,
    onStartIconClicked: () -> Unit = { },
    endIcon: () -> ImageVector? = { null },
    isEndIconEnabled: Boolean = false,
    endIconTint: Color = Color.Companion.Unspecified,
    onEndIconClicked: () -> Unit = { },
    color: Color = MaterialTheme.colorScheme.surface,
    contentDescription: String,
    label: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = { }
) {
    Surface(
        modifier = Modifier.Companion.clickable(
            enabled = isClickable,
            onClick = { onClick() }
        ),
        shadowElevation = 8.dp,
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.small,
        color = color
    ) {
        Row(verticalAlignment = Alignment.Companion.CenterVertically) {
            val leader = startIcon()
            val trailer = endIcon()

            if (leader != null) {
                Icon(
                    leader,
                    contentDescription = contentDescription,
                    tint = startIconTint,
                    modifier = Modifier.Companion
                        .clickable(enabled = isStartIconEnabled, onClick = onStartIconClicked)
                        .padding(horizontal = 4.dp)
                )
            }

            Text(
                label,
                modifier = Modifier.Companion.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Companion.Black)
            )

            if (trailer != null) {
                Icon(
                    trailer,
                    contentDescription = contentDescription,
                    tint = endIconTint,
                    modifier = Modifier.Companion
                        .clickable(enabled = isEndIconEnabled, onClick = onEndIconClicked)
                        .padding(horizontal = 4.dp)
                )
            }

        }
    }
}