package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryStepScreenView(screen: CategoryStepScreen, modifier: Modifier) = with(screen) {
    val selectedCategoryId by screen.state.selectedCategoryId.collectAsState()
    val categories by screen.state.categories.collectAsState()
    val localFocusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        val query by screen.state.query.collectAsState()

        TextField(
            value = query,
            onValueChange = { newText ->
                screen.ChangeQueryUseCase(newText)
            },
            label = { Text(stringResource(R.string.category_label)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        screen.ClickToClearQueryUseCase()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus()
                }
            )
        )

        Column {
            categories.forEach { category ->
                Row(Modifier.clickable {
                    screen.SelectCategoryIdUseCase(category.id)
                }) {
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )
                    AnimatedVisibility(category.id == selectedCategoryId) {
                        Icon(
                            Icons.Filled.Check,
                            "selected",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }

}