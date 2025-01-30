package me.likeavitoapp.screens.main.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.model.SearchSettings
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Preview
@Composable
fun SearchSettingsPanelViewPreview() {
    LikeAvitoAppTheme {
        val categories = MockDataProvider().getCategories()
        val regions = MockDataProvider().getRegions()
        SearchSettingsPanelView(
            settings = SearchSettings(
                categories = Loadable(categories),
                    selectedCategory = MutableStateFlow(null),
                    query =  MutableStateFlow(""),
                    region = MutableStateFlow(regions.first()),
                    priceRange = MutableStateFlow(SearchSettings.PriceRange())
                ),
                onDismiss = {}
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsPanelView(settings: SearchSettings, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Text(
            "Swipe up to open sheet. Swipe down to dismiss.",
            modifier = Modifier.padding(16.dp)
        )
    }
}