package me.likeavitoapp.screens.main.tabs.search.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen.SearchBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarView(screen: SearchScreen) {
    val query by screen.searchBar.state.query.collectAsState()
    val searchTips by screen.searchBar.state.searchTips.output.collectAsState()
    val selectedQuery by screen.searchBar.state.selectedQuery.collectAsState()
    val selectedCategory by screen.searchSettingsPanel.state.selectedCategory.collectAsState()
    val isCategoriesVisible by screen.state.isCategoriesVisible.collectAsState()
    val categories by screen.searchSettingsPanel.state.categories.output.collectAsState()

    fun hasSelectedCategory(): Boolean = selectedCategory != null
    fun isExpanded(): Boolean = !searchTips.isEmpty()

    Column(

    ) {
        AnimatedVisibility(
            selectedQuery == null, enter = fadeIn(), exit = fadeOut()
        ) {
            SearchBar(inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onSearch = { text ->
                        screen.searchBar.ClickToSearchActionUseCase(text)
                    },
                    expanded = isExpanded(),
                    onExpandedChange = {},
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        if (isExpanded()) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back",
                                modifier = Modifier.clickable {
                                    screen.searchBar.ClickToTipsBackUseCase()
                                })

                        } else {
                            Icon(
                                Icons.Default.Search, contentDescription = "search_icon"
                            )
                        }
                    },
                    trailingIcon = {
                        if (isExpanded()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "clear",
                                modifier = Modifier.clickable {
                                    screen.searchBar.ClickToClearUseCase()
                                })

                        } else {
                            Icon(
                                ImageVector.vectorResource(R.drawable.baseline_tune_24),
                                "searchbar_trailing_icon",
                                modifier = Modifier.clickable {
                                    screen.searchBar.ClickToFilterButtonUseCase()
                                })
                        }
                    },
                    query = query,
                    onQueryChange = { newQuery ->
                        screen.searchBar.ChangeSearchQueryUseCase(newQuery)
                    })
            }, expanded = isExpanded(), onExpandedChange = { expanded ->
            }) {
                TipsView(screen.searchBar, searchTips)
            }
        }

        AnimatedVisibility(isCategoriesVisible && !hasSelectedCategory()) {
            LazyHorizontalStaggeredGrid(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(136.dp)
                    .fillMaxWidth(),
                rows = StaggeredGridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalItemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories.toMutableStateList()) { category ->
                    Card(
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.clickable {
                            screen.searchBar.ClickToCategoryUseCase(category)
                        }) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = category.name,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        AnimatedVisibility(selectedQuery != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        screen.searchBar.ClickToSelectedQueryUseCase()
                    }) {

                    Text(
                        text = selectedQuery ?: "",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                    )
                }

                Icon(
                    Icons.Default.Close,
                    "clear",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .height(24.dp)
                )

            }
        }

        AnimatedVisibility(isCategoriesVisible && hasSelectedCategory()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        screen.searchBar.ClickToSelectedCategoryUseCase()
                    }) {

                    Text(
                        text = selectedCategory?.name ?: "",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                    )
                }

                Icon(
                    Icons.Default.Close,
                    "clear",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .height(24.dp)
                )

            }
        }
    }
}

@Composable
fun TipsView(searchBar: SearchBar, searchTips: List<String>) {
    val query by searchBar.state.query.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val tips =
            if (query.isEmpty())
                searchTips
            else
                listOf(query) + searchTips
        items(tips) {
            TipView(it, searchBar)
            Spacer(
                Modifier
                    .height(0.36.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
private fun TipView(
    tip: String,
    searchBar: SearchBar
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                searchBar.ClickToSearchTipUseCase(tip)
            }
    ) {
        Text(tip, modifier = Modifier, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    get = mockMainSet()
    val screen = SearchScreen(
        navigator = mockScreensNavigator(),
    ).apply {
        val mockDataProvider = MockDataProvider()
        searchBar.state.query.post("Query")
        searchBar.state.searchTips.output.post(mockDataProvider.searchTips)
        searchSettingsPanel.state.categories.output.post(mockDataProvider.categories.toMutableStateList())
    }

    LikeAvitoAppTheme {
        SearchBarView(screen)
    }
}