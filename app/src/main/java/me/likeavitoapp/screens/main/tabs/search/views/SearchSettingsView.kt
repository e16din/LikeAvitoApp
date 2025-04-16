package me.likeavitoapp.screens.main.tabs.search.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
inline fun SearchSettingsPanelView(
    panel: SearchScreen.SearchSettingsPanel,
    crossinline onFocus: () -> Unit = {}
) {
    val categoryMenuEnabled by panel.state.categoryMenuEnabled.collectAsState()
    val regionMenuEnabled by panel.state.regionMenuEnabled.collectAsState()

    val categories = panel.state.categories
    val regions = panel.state.regions

    val category by panel.state.selectedCategory.collectAsState()
    val region by panel.state.selectedRegion.collectAsState()
    val priceFrom by panel.state.priceFrom.collectAsState()
    val priceTo by panel.state.priceTo.collectAsState()

    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Box {
        Column(modifier = Modifier) {
            Text(
                stringResource(R.string.search_settings_title),
                style = AppTypography.headlineLarge,
                //            fontWeight = FontWeight.Medium,
                //            fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            PreferenceItem(
                title = stringResource(R.string.category_title),
                value = category?.name ?: "",
                modifier = Modifier.clickable(onClick = {
                    panel.ClickToCategoryUseCase()
                })
            )
            HorizontalDivider()

            PreferenceItem(
                title = stringResource(R.string.region_title),
                value = region?.name ?: "",
                modifier = Modifier.clickable(onClick = {
                    panel.ClickToRegionUseCase()
                })
            )
            HorizontalDivider()

            PreferenceCustomItem(stringResource(R.string.price_title)) { modifier ->
                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                log("isFocused: ${it.isFocused}")
                                onFocus()
                            }
                        },
                    value = priceFrom,
                    onValueChange = { value ->
                        panel.ChangePriceFromUseCase(value)
                    },
                    label = {
                        Text(stringResource(R.string.from))
                    },
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
                    modifier = modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                onFocus()
                            }
                        },
                    value = priceTo,
                    onValueChange = { value ->
                        panel.ChangePriceToUseCase(value)
                    },
                    label = {
                        Text(stringResource(R.string.to))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                )
            }
            Spacer(Modifier.size(128.dp))
        }

        DropdownMenu(
            expanded = categoryMenuEnabled,
            onDismissRequest = { panel.DismissCategoryMenuUseCase() },
            offset = DpOffset(x = 100.dp, y = 100.dp)
        ) {
            Text(
                stringResource(R.string.select_category_lable),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        panel.ChangeCategoryUseCase(category)
                    }
                )
            }
        }

        DropdownMenu(
            expanded = regionMenuEnabled,
            onDismissRequest = { panel.DismissRegionMenuUseCase() },
            offset = DpOffset(x = 100.dp, y = 100.dp)
        ) {
            Text(
                stringResource(R.string.select_region_lable),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            regions.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.name) },
                    onClick = {
                        panel.ChangeRegionUseCase(region)
                    }
                )
            }
        }
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
    get = mockMainSet()
    LikeAvitoAppTheme {
        SearchSettingsPanelView(
            panel = SearchScreen(
                navigator = mockScreensNavigator(),
            ).searchSettingsPanel,
            onFocus = {}
        )
    }
}