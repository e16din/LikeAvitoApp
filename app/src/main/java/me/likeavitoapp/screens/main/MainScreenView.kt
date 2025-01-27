package me.likeavitoapp.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.Screen
import me.likeavitoapp.dataSources
import me.likeavitoapp.screens.main.MainScreen.Tabs
import me.likeavitoapp.screens.main.cart.CartScreen
import me.likeavitoapp.screens.main.cart.CartScreenProvider
import me.likeavitoapp.screens.main.createad.CreateAdScreen
import me.likeavitoapp.screens.main.createad.CreateAdScreenProvider
import me.likeavitoapp.screens.main.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.profile.ProfileScreen
import me.likeavitoapp.screens.main.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.search.SearchScreen
import me.likeavitoapp.screens.main.search.SearchScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun MainScreenProvider(screen: MainScreen) {
    val dataSources = remember { dataSources() }
    val tabScreen = screen.innerScreen?.collectAsState()

    MainScreenView(
        screen = screen,
        tabScreen = tabScreen!!.value
    )

    BackHandler {
        dataSources.app.PressBack(screen)
    }
}

@Composable
fun MainScreenView(screen: MainScreen, tabScreen: Screen) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Content:
        when (tabScreen) {
            is SearchScreen -> SearchScreenProvider(tabScreen)
            is FavoritesScreen -> FavoritesScreenProvider(tabScreen)
            is ProfileScreen -> ProfileScreenProvider(tabScreen)
            is CreateAdScreen -> CreateAdScreenProvider(tabScreen)
            is CartScreen -> CartScreenProvider(tabScreen)
        }

        // Tabs:
        val TABS_COUNT = Tabs.entries.size

        val INDICATOR_PADDING_DP = 4.dp
        var tabWidth by remember { mutableStateOf(0.dp) }

        val tabIndex = screen.state.selectedTab.ordinal.toFloat() + 1
        val selectedTabIndicatorOffsetDp: Dp by animateDpAsState(
            when (screen.state.selectedTab) {
                Tabs.Search -> tabWidth * (tabIndex / TABS_COUNT)
                else -> tabWidth * (tabIndex / TABS_COUNT) - INDICATOR_PADDING_DP
            }
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxSize(.9f)
                .onGloballyPositioned { coordinates ->
                    tabWidth = coordinates.size.width.dp
                }
                .background(color = Color(0xffc0c0c0), shape = CircleShape)) {

            Box(
                modifier = Modifier
                    .padding(INDICATOR_PADDING_DP)
                    .fillMaxHeight()
                    .width(tabWidth / 3 - INDICATOR_PADDING_DP)
                    .offset(x = selectedTabIndicatorOffsetDp)
                    .clip(CircleShape)
                    .background(Color(0xfffcfcf))
//                    .clip(Shapes.card4)
//                    .background(white100)

            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val modifier = Modifier
                    .fillMaxHeight()
                    .width(tabWidth / TABS_COUNT)
                    .clip(CircleShape)
//                        .clickable/*(
//                        interactionSource = MutableInteractionSource(),
//                        indication = null
//                    ) */{ screen.input.onTabSelected(index) }


                fun getTabIcon(tab: Tabs): ImageVector {
                    return when (tab) {
                        Tabs.Search -> Icons.Rounded.Search
                        Tabs.Favorites -> Icons.Rounded.Favorite
                        Tabs.CreateAd -> Icons.Rounded.AddCircle
                        Tabs.Cart -> Icons.Rounded.ShoppingCart
                        Tabs.Profile -> Icons.Rounded.Person
                    }
                }

                fun getTabText(tab: Tabs): Int {
                    return when (tab) {
                        Tabs.Search -> R.string.search_tab
                        Tabs.Favorites -> R.string.favorite_tab
                        Tabs.CreateAd -> R.string.create_new_tab
                        Tabs.Cart -> R.string.cart_tab
                        Tabs.Profile -> R.string.profile_tab
                    }
                }

                Tabs.entries.forEach { tab ->
                    Column(modifier = modifier.clickable {
                        screen.SelectTabUseCase(tab)

                    }, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(getTabIcon(tab), contentDescription = "cart")
                        Text(text = stringResource(getTabText(tab)))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        MainScreenView(
            screen = MainScreen(),
            tabScreen = SearchScreen(prevScreen = null, innerScreen = null)
        )
    }
}