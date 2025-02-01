package me.likeavitoapp.screens.main.tabs.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.R
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsPanelView(panel: SearchScreen.SearchSettingsPanel) {
    val category by panel.state.selectedCategory.collectAsState()
    val region by panel.state.selectedRegion.collectAsState()
    val priceRange by panel.state.priceRange.collectAsState()
    val textFrom = remember { priceRange.from.toString() }
    val textTo = remember {
        if (priceRange.to < 0)
            ""
        else
            priceRange.to.toString()
    }

    val localFocusManager = LocalFocusManager.current

    Column {
        Text(
            stringResource(R.string.search_settings_title),
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        PreferenceItem(
            title = stringResource(R.string.category_title),
            value = category?.name
                ?: stringResource(R.string.all_categories),
            modifier = Modifier.clickable(onClick = {
                panel.ClickToCategoryUseCase()
            })
        )
        HorizontalDivider()

        PreferenceItem(
            title = stringResource(R.string.region_title),
            value = region?.name
                ?: stringResource(R.string.all_regions),
            modifier = Modifier.clickable(onClick = {
                panel.ClickToRegionUseCase()
            })
        )
        HorizontalDivider()

        PreferenceCustomItem(stringResource(R.string.price_title)) { modifier ->
            OutlinedTextField(
                value = textFrom,
                onValueChange = { value ->
                    panel.ChangePriceFromUseCase(value.toIntOrNull() ?: 0)
                },
                label = {
                    Text(stringResource(R.string.from))
                },
                modifier = modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        localFocusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            )

            OutlinedTextField(
                value = textTo,
                onValueChange = { value ->
                    panel.ChangePriceToUseCase(value.toIntOrNull() ?: 0)
                },
                label = {
                    Text(stringResource(R.string.to))
                },
                placeholder = {
                    if (textTo.isEmpty()) {
                        Text(stringResource(R.string.hint_max))
                    }
                },
                modifier = modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
        }
        Spacer(Modifier.size(128.dp))
    }
}

@Composable
fun PreferenceItem(title: String, value: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column {
            PreferenceCustomItem(title) { modifier ->
                Text(value, fontSize = 14.sp, color = Color.Gray, modifier = modifier)
            }
        }
        Icon(
            Icons.Default.ArrowDropDown,
            "arrow_drop_down",
            Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )
    }
}

@Composable
inline fun PreferenceCustomItem(
    title: String,
    crossinline content: @Composable (modifier: Modifier) -> Unit
) {
    Text(
        title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 0.dp)
            .fillMaxWidth()
    )
    content.invoke(
        Modifier
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
            .fillMaxWidth()
    )
}


@Preview(showBackground = true)
@Composable
fun SearchSettingsViewPreview() {
    LikeAvitoAppTheme {
        SearchSettingsPanelView(
            panel = SearchScreen(
                parentNavigator = ScreensNavigator()
            ).searchSettingsPanel
        )
    }
}