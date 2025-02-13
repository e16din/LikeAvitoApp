package me.likeavitoapp.screens.main.tabs.search.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.main.tabs.search.SearchScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarView(screen: SearchScreen) {
    val query by screen.searchBar.state.query.collectAsState()
    val searchTips by screen.searchBar.state.searchTips.data.collectAsState()
    val selectedCategory by screen.searchSettingsPanel.state.selectedCategory.collectAsState()
    val categoriesSource = screen.searchSettingsPanel.state.categories.data.collectAsState()
    val categories = remember { categoriesSource.value.toMutableStateList() }
    val searchFilterPanelEnabled by screen.searchSettingsPanel.state.enabled.collectAsState()


    fun hasSelectedCategory(): Boolean = selectedCategory?.id != 0
    fun isExpanded(): Boolean = !searchTips.isEmpty()

    Column(
        modifier = Modifier.Companion
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    onSearch = { text ->
                        screen.searchBar.ChangeSearchQueryUseCase(text)
                    },
                    expanded = isExpanded(),
                    onExpandedChange = {
                    },
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        if (isExpanded()) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back",
                                modifier = Modifier.clickable {
                                    screen.searchBar.ClickToTipsBackUseCase()
                                }
                            )

                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "search_icon"
                            )
                        }
                    },
                    trailingIcon = {
                        if (isExpanded()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "clear",
                                modifier = Modifier.clickable {
                                    screen.searchBar.ClickToTipsClearUseCase()
                                }
                            )

                        } else {
                            Icon(
                                ImageVector.Companion.vectorResource(R.drawable.baseline_tune_24),
                                "searchbar_trailing_icon",
                                modifier = Modifier.Companion.clickable {
                                    screen.searchBar.ClickToFilterButtonUseCase()
                                })
                        }
                    },
                    query = query,
                    onQueryChange = { newQuery ->
                        screen.searchBar.ChangeSearchQueryUseCase(newQuery)
                    }
                )
            },
            expanded = isExpanded(),
            onExpandedChange = { expanded ->
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(searchTips) {

                }
            }
        }

        AnimatedVisibility(visible = !hasSelectedCategory()) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(136.dp)
            ) {
                LazyHorizontalStaggeredGrid(
                    modifier = Modifier.Companion.wrapContentHeight(),
                    rows = StaggeredGridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalItemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(categories.size) { index ->
                        Card(
                            modifier = Modifier.Companion
                                .wrapContentHeight()
                                .clickable {
                                    screen.searchBar.ClickToCategoryUseCase(
                                        categories[index]
                                    )
                                }
                        ) {
                            Text(
                                modifier = Modifier.Companion.padding(16.dp),
                                text = categories[index].name
                            )
                        }
                    }
                }
            }

        }

        AnimatedVisibility(visible = hasSelectedCategory()) {
            Text(
                text = selectedCategory?.name ?: "",
                modifier = Modifier.Companion
                    .background(Color.Companion.Yellow)
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            )
        }
    }
}