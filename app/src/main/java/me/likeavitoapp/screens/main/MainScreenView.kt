package me.likeavitoapp.screens.main

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.likeavitoapp.DataSources
import me.likeavitoapp.R
import me.likeavitoapp.screens.main.search.SearchScreenProvider
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun MainScreenProvider() {
    val scope = rememberCoroutineScope()

    val sources = DataSources<MainScreen>()

    val selectTabUseCase = SelectTabUseCases(sources)

    MainScreenView(sources.screen)

    LaunchedEffect(Unit) {
        sources.screen.input.onTabSelected = { tab ->
            selectTabUseCase.runWith(tab)
        }
    }
}

@Composable
fun MainScreenView(screen: MainScreen) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Content:
        when (screen.state.selectedTab) {
            MainScreen.Tabs.Search -> SearchScreenProvider()
            MainScreen.Tabs.Favorites -> {

//                TODO()
            }
            MainScreen.Tabs.Profile -> {
//                TODO()
            }
        }

        // Tabs:
        val TABS_COUNT = 3

        val INDICATOR_PADDING_DP = 4.dp
        var tabWidth by remember { mutableStateOf(0.dp) }

        val selectedTabIndicatorOffsetDp: Dp by animateDpAsState(
            when (screen.state.selectedTab) {
                MainScreen.Tabs.Search -> tabWidth * (1f / TABS_COUNT)
                MainScreen.Tabs.Favorites -> tabWidth * (2f / TABS_COUNT) - INDICATOR_PADDING_DP
                MainScreen.Tabs.Profile -> tabWidth * (3f / TABS_COUNT) - INDICATOR_PADDING_DP
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

                Box(modifier = modifier.clickable {
                    screen.input.onTabSelected(MainScreen.Tabs.Search)

                }, contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.search_tab))
                }

                Box(modifier = modifier.clickable {
                    screen.input.onTabSelected(MainScreen.Tabs.Favorites)

                }, contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.favorite_tab))
                }

                Box(modifier = modifier.clickable {
                    screen.input.onTabSelected(MainScreen.Tabs.Profile)

                }, contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.profile_tab))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        MainScreenView(MainScreen())
    }
}