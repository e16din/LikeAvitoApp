package me.likeavitoapp.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OutlinedCardLabel(
    modifier: Modifier = Modifier,
    label: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    content: @Composable () -> Unit
) {
    var focusEnabled by remember { mutableStateOf(false) }

    Box(
        modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                focusEnabled = it.isFocused
            }
            .focusTarget()

    ) {
        OutlinedCard(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(2)
        ) {
            content()
        }

        Text(
            text = label,
            color = if (focusEnabled)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 36.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}