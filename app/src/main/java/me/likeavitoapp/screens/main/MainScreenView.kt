package me.likeavitoapp.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.R
import me.likeavitoapp.className
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.log
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.main.addetails.AdDetailsScreen
import me.likeavitoapp.screens.main.addetails.AdDetailsScreenProvider
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.order.create.CreateOrderScreenProvider
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreen
import me.likeavitoapp.screens.main.order.details.OrderDetailsScreenProvider
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreenProvider
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreen
import me.likeavitoapp.screens.main.tabs.profile.edit.EditProfileScreenProvider
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme
import me.likeavitoapp.ui.theme.onPrimaryContainerLightMediumContrast
import me.likeavitoapp.ui.theme.onSecondaryContainerLight
import me.likeavitoapp.ui.theme.primaryContainerLightMediumContrast
import me.likeavitoapp.ui.theme.primaryLight
import me.likeavitoapp.ui.theme.primaryLightMediumContrast
import me.likeavitoapp.ui.theme.secondaryContainerLight

@Composable
fun MainScreenProvider(screen: MainScreen) {
    val nextScreen = screen.navigator.screen.collectAsState()

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        MainScreenView(screen)

        with(nextScreen.value) {
            when (this) {
                is AdDetailsScreen -> AdDetailsScreenProvider(this)
                is OrderDetailsScreen -> OrderDetailsScreenProvider(this)
                is CreateOrderScreen -> CreateOrderScreenProvider(this)
                is EditProfileScreen -> EditProfileScreenProvider(this)
                is ChatScreen -> ChatScreenProvider(this)
            }
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

val tabBarHeight = 58.dp

@Composable
fun MainScreenView(screen: MainScreen) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.padding(bottom = tabBarHeight)) {
            NextTabProvider(screen, screen.tabsRootScreen)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {
            TabsView(screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (16).dp)
            ) {
                ButtonCreateNewView(screen)
            }
        }
    }
}

@Composable
fun BoxScope.ButtonCreateNewView(screen: MainScreen) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .size(86.dp)
            .clip(CircleShape)
            .background(primaryContainerLightMediumContrast)
            .border(width = 1.dp, color = primaryLightMediumContrast, shape = CircleShape)
            .clickable {
                screen.ClickToCreateAdUseCase()

            }
    ) {
        Icon(
            Icons.Rounded.Add,
            contentDescription = "create_ad",
            tint = onPrimaryContainerLightMediumContrast,
            modifier = Modifier.size(40.dp)
        )
        //                Text(text = stringResource(R.string.create_new_tab), fontSize = 9.sp, maxLines = 1)
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun BoxScope.TabsView(screen: MainScreen) {
    val tabScreen = screen.tabsRootScreen.navigator.screen.collectAsState()

    log("Tab: ${tabScreen.value.className()}")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(tabBarHeight)
            .background(secondaryContainerLight)
            .align(Alignment.BottomStart),
        verticalAlignment = Alignment.Bottom,
    ) {
        val modifier = Modifier
        // Search
        Column(
            modifier = modifier
                .weight(1f)
                .background(
                    if (tabScreen.value is SearchScreen)
                        primaryLight else secondaryContainerLight
                )
                .clickable(onClick = {
                    screen.ClickToSearchUseCase()

                }), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                Icons.Rounded.Search,
                contentDescription = "search",
                tint = onSecondaryContainerLight
            )
            Text(
                text = stringResource(R.string.search_tab),
                fontSize = 9.sp,
                maxLines = 1,
                color = onSecondaryContainerLight
            )
        }

        // Favorites
        Column(
            modifier = modifier
                .weight(1f)
                .background(
                    if (tabScreen.value is FavoritesScreen)
                        primaryLight else secondaryContainerLight
                )
                .clickable(onClick = {
                    screen.ClickToFavoritesUseCase()
                }), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                Icons.Rounded.Favorite,
                contentDescription = "favorite",
                tint = onSecondaryContainerLight
            )
            Text(
                text = stringResource(R.string.favorite_tab),
                fontSize = 9.sp,
                maxLines = 1,
                color = onSecondaryContainerLight
            )
        }

        // CreateAd Stub
        Box(
            modifier = Modifier
                .weight(0.45f)
                .background(
                    if (tabScreen.value is FavoritesScreen)
                        primaryLight else secondaryContainerLight
                )
                .height(tabBarHeight)
        )
        Box(
            modifier = Modifier
                .weight(0.45f)
                .background(
                    if (tabScreen.value is CartScreen)
                        primaryLight else secondaryContainerLight
                )
                .height(tabBarHeight)
        )

        // Cart
        Column(
            modifier = modifier
                .weight(1f)
                .background(
                    if (tabScreen.value is CartScreen)
                        primaryLight else secondaryContainerLight
                )
                .clickable {
                    screen.ClickToCartUseCase()

                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                Icons.Rounded.ShoppingCart,
                contentDescription = "cart",
                tint = onSecondaryContainerLight
            )
            Text(
                text = stringResource(R.string.cart_tab),
                fontSize = 9.sp,
                maxLines = 1,
                color = onSecondaryContainerLight
            )
        }

        // Profile
        Column(
            modifier = modifier
                .weight(1f)
                .background(
                    if (tabScreen.value is ProfileScreen)
                        primaryLight else secondaryContainerLight
                )
                .clickable {
                    screen.ClickToProfileUseCase()

                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                Icons.Rounded.Person,
                contentDescription = "profile",
                tint = onSecondaryContainerLight
            )
            Text(
                text = stringResource(R.string.profile_tab),
                fontSize = 9.sp,
                maxLines = 1,
                color = onSecondaryContainerLight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        MainScreenView(
            screen = MainScreen(
                sources = mockDataSource(),
                scope = mockCoroutineScope()
            )
        )
    }
}