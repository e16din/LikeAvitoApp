package me.likeavitoapp.screens.main.addetails

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.className
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.OfferMessage
import me.likeavitoapp.model.TextMessage
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.ClosableMessage
import me.likeavitoapp.screens.main.addetails.photo.PhotoScreen
import me.likeavitoapp.screens.main.addetails.photo.PhotoScreenProvider
import me.likeavitoapp.screens.main.order.ChatView
import me.likeavitoapp.screens.main.order.create.CreateOrderScreen
import me.likeavitoapp.screens.main.order.create.CreateOrderScreenProvider
import me.likeavitoapp.screens.main.tabs.chat.ChatScreen
import me.likeavitoapp.screens.main.tabs.chat.ChatScreenProvider
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme
import me.likeavitoapp.ui.theme.backgroundLight


@Composable
fun AdDetailsScreenProvider(screen: AdDetailsScreen) {
    Surface(modifier = Modifier.fillMaxSize()) {
        AdDetailsScreenView(screen)
    }

    BackHandler {
        screen.PressBackUseCase()
    }

    DisposableEffect(Unit) {
        onDispose {
            screen.CloseScreenUseCase()
        }
    }
}

@Composable
fun AdDetailsScreenView(screen: AdDetailsScreen) = with(screen.state) {
    val favoriteSelected by screen.state.ad.isFavorite.collectAsState()
    val timerLabel = ad.timerLabel.collectAsState(AdDetailsScreen::class)

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = ad.title,
            style = AppTypography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box {
            val pagerState = rememberPagerState(pageCount = {
                ad.photoUrls.size
            })
            Box() {
                HorizontalPager(state = pagerState) { page ->

                    val url = ad.photoUrls[page]
                    ActualAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clickable {
                                screen.ClickToPhotoUseCase(url)
                            },
                        url = url
                    )


                }
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            backgroundLight
                        )
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                    text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}"
                )
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                onClick = {
                    screen.ClickToFavoriteUseCase(ad)
                }
            ) {
                Icon(
                    imageVector = if (favoriteSelected)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "favorite",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Red
                )
            }
        }

        AnimatedVisibility(!timerLabel.value.isEmpty()) {
            ClosableMessage(
                text = stringResource(R.string.continue_order_label, timerLabel.value),
                onCloseClick = {
                    screen.ClickToCloseTimerLabel(ad)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            )
        }

        Text(
            text = ad.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Row(modifier = Modifier) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    screen.ClickToBuyUseCase(ad)
                }) {
                Text(text = "Купить за " + "${ad.price}₽")
            }

            Spacer(Modifier.weight(1f))

            if (ad.isBargainingEnabled) {
                Button(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        screen.ClickToBargainingUseCase(ad)
                    }) {
                    Text(text = stringResource(R.string.bargaining_button))
                }
            }
        }

        if (screen.state.messages.value.isNotEmpty()) {
            Text(stringResource(R.string.new_messages_label, screen.state.messages.value.size))
            Column {
                screen.state.messages.value.filter { it.isNew }.forEach {
                    when (it) {
                        is TextMessage -> Text(it.text)
                        is OfferMessage -> Text("offer: ${it.newPrice}")
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    screen.ClickToOpenChatUseCase()
                }) {
                Text(stringResource(R.string.move_to_chat_button))
            }

            ChatView()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdDetailsScreenPreview() {
    mainSet = mockMainSet()
    LikeAvitoAppTheme {
        AdDetailsScreenView(
            AdDetailsScreen(
                ad = MockDataProvider().ads.first(),
                navigator = mockScreensNavigator(),
            )
        )
    }
}