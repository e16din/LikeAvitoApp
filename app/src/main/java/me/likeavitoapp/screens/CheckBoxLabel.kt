package me.likeavitoapp.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxLabel(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckChanged: (Boolean) -> Unit = {}
) {
    var checkedState by remember { mutableStateOf(checked) }

    Row(
        modifier
            .clip(CircleShape)
            .clickable {
                if (enabled) {
                    checkedState = !checkedState
                    onCheckChanged(checkedState)
                }
            }

    ) {
        Checkbox(
            checked = checkedState,
            enabled = enabled,
            onCheckedChange = {
                checkedState = it
                onCheckChanged(it)
            }
        )
        Text(
            label,
            Modifier
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterVertically)
        )
    }
}