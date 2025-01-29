package me.likeavitoapp.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.Launcher
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.Screen
import me.likeavitoapp.dataSources
import me.likeavitoapp.screens.main.cart.CartScreen
import me.likeavitoapp.screens.main.cart.CartScreenProvider
import me.likeavitoapp.screens.main.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.profile.ProfileScreen
import me.likeavitoapp.screens.main.profile.ProfileScreenProvider
import me.likeavitoapp.screens.main.search.SearchScreen
import me.likeavitoapp.screens.main.search.SearchScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun MainScreenProvider(screen: MainScreen) {
    val sources = remember { dataSources() }

    Launcher(launchOnce = {
        screen.StartScreenUseCase()
    }) {
        val tabScreen = screen.innerScreen?.collectAsState()
        MainScreenView(
            screen = screen,
            tabScreen = tabScreen!!.value
        )
    }

    BackHandler {
        sources.app.PressBack(screen)
    }
}

@Composable
fun MainScreenView(screen: MainScreen, tabScreen: Screen) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Content:
        Box(modifier = Modifier.weight(1f)) {
            when (tabScreen) {
                is SearchScreen -> SearchScreenProvider(tabScreen)
                is FavoritesScreen -> FavoritesScreenProvider(tabScreen)
                is ProfileScreen -> ProfileScreenProvider(tabScreen)
                is CartScreen -> CartScreenProvider(tabScreen)
            }
        }

        Box() {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.Bottom
            ) {
                val modifier = Modifier
                // Search

                Column(
                    modifier = modifier
                        .weight(1f)
                        .background(
                            if (tabScreen is SearchScreen)
                                Color(0xffcccccc) else Color(0xffffffff)
                        )
                        .clickable(onClick = {
                            screen.ClickToSearchUseCase()
                        }), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(Icons.Rounded.Search, contentDescription = "search")
                    Text(text = stringResource(R.string.search_tab), fontSize = 9.sp, maxLines = 1)
                }

                // Favorites
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topEnd = 8.dp))
                        .background(
                            if (tabScreen is FavoritesScreen)
                                Color(0xffcccccc) else Color(0xffffffff)
                        )
                        .clickable(onClick = {
                            screen.ClickToFavoritesUseCase()
                        }), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(Icons.Rounded.Favorite, contentDescription = "favorite")
                    Text(
                        text = stringResource(R.string.favorite_tab),
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }

                // CreateAd Stub
                Spacer(modifier = Modifier
                    .weight(1f)
                    .height(72.dp))

                // Cart
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 8.dp))
                        .background(
                            if (tabScreen is CartScreen)
                                Color(0xffcccccc) else Color(0xffffffff)
                        )
                        .clickable {
                            screen.ClickToCartUseCase()

                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(Icons.Rounded.ShoppingCart, contentDescription = "cart")
                    Text(text = stringResource(R.string.cart_tab), fontSize = 9.sp, maxLines = 1)
                }

                // Profile
                Column(
                    modifier = modifier
                        .weight(1f)
                        .background(
                            if (tabScreen is ProfileScreen)
                                Color(0xffcccccc) else Color(0xffffffff)
                        )
                        .clickable {
                            screen.ClickToProfileUseCase()

                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(Icons.Rounded.Person, contentDescription = "profile")
                    Text(text = stringResource(R.string.profile_tab), fontSize = 9.sp, maxLines = 1)
                }
            }

            // CreateAd
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(Color(0xfffcfcfc))
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                    .clickable {
                        screen.ClickToCreateAdUseCase()

                    }
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "create_ad",
                    modifier = Modifier.size(40.dp)
                )
//                Text(text = stringResource(R.string.create_new_tab), fontSize = 9.sp, maxLines = 1)
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        MainScreenView(
            screen = MainScreen(
                sources = MockDataProvider().dataSources()
            ),
            tabScreen = SearchScreen(prevScreen = null, innerScreen = null)
        )
    }
}