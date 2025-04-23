package me.likeavitoapp.screens.main.createad

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.io.IOException
import me.likeavitoapp.MainActivity
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.log
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.backgroundLight


@Composable
fun CreateAdScreenProvider(screen: CreateAdScreen) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ActionTopBar(
                title = stringResource(R.string.add_new_ad_title),
                onDone = {
                    screen.ClickToDoneUseCase()
                },
                onClose = {
                    screen.PressBackUseCase()
                },
                withDoneButton = true
            ) { innerPadding ->
                CreateAdScreenView(screen, Modifier.padding(innerPadding))
            }
        }

        val imagePickerEnabled by screen.state.imagePickerEnabled.collectAsState()
        if (imagePickerEnabled) {
            val activity = LocalActivity.current
            val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
                uri?.let {
                    try {
                        val bytes =
                            (activity as MainActivity).contentResolver.openInputStream(uri)
                                ?.readBytes()
                        screen.ChangePhotosUseCase(bytes)

                    } catch (error: IOException) {
                        error.log()
                        screen.ChangePhotosUseCase(null)
                    }
                }
            }
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun CreateAdScreenView(screen: CreateAdScreen, modifier: Modifier) = with(screen) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val title by screen.state.title.collectAsState()
        TextField(
            value = title,
            onValueChange = { value ->
                screen.ChangeTitleUseCase(value)
            },
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
        )


        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))


        val photos = screen.state.photos

        val screenWidth = get.sources().platform.screenWidthDp

        val scrollToEnd by screen.state.scrollToEnd.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(scrollToEnd) {
            if (scrollToEnd) {
                screen.state.scrollToEnd.next(false)
                listState.scrollToItem(photos.size - 1)
            }
        }

        Column {
            val height = 210.dp
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                state = listState,
                contentPadding = PaddingValues(start = 8.dp)
            ) {
                itemsIndexed(photos) { index, bytes ->
                    Box {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AnimatedVisibility(true) {
                                ActualAsyncImage(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .width((screenWidth * 0.75f).dp)
                                        .height(height),
                                    byteArray = bytes
                                )
                            }
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
                            text = "${index + 1} / ${photos.size}"
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "clear",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(4.dp)
                                .clickable {
                                    screen.ClickToRemovePhotoUseCase(bytes)
                                },
                        )
                    }
                }
            }

            Spacer(Modifier.size(8.dp))

            OutlinedButton(
                onClick = {
                    screen.ClickToAddPhotoUseCase()
                },
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.add_photo_button))
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))
    }

}

//        Box {
//
//            Text(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(8.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        backgroundLight
//                    )
//                    .padding(vertical = 4.dp, horizontal = 12.dp),
//                text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}"
//            )
//        }
//
//        IconButton(
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(12.dp)
//                .clip(CircleShape)
//                .background(Color.Transparent),
//            onClick = {
//                screen.ClickToFavoriteUseCase(ad)
//            }
//        ) {
//            Icon(
//                imageVector = if (favoriteSelected)
//                    Icons.Default.Favorite
//                else
//                    Icons.Default.FavoriteBorder,
//                contentDescription = "favorite",
//                modifier = Modifier.size(32.dp),
//                tint = Color.Red
//            )
//        }
//    }
//
//    AnimatedVisibility(timerLabel.value.isNotEmpty() && timerLabel.value != "00:00") {
//        ClosableMessage(
//            text = stringResource(R.string.continue_order_label, timerLabel.value),
//            onCloseClick = {
//                screen.ClickToCloseTimerLabel(ad)
//            },
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(vertical = 16.dp, horizontal = 16.dp)
//                .clickable {
//                    screen.ClickToBuyUseCase(ad)
//                }
//        )
//    }
//
//    Text(
//        text = ad.description,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
//        maxLines = 3,
//        overflow = TextOverflow.Ellipsis
//    )
//
//    if (!ad.isOrdered) {
//        Row(modifier = Modifier) {
//            Button(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                onClick = {
//                    screen.ClickToBuyUseCase(ad)
//                }) {
//                Text(text = stringResource(R.string.buy_button, ad.price))
//            }
//
//            Spacer(Modifier.weight(1f))
//
//            if (ad.isBargainingEnabled) {
//                Box(modifier = Modifier) {
//                    val newMessagesCounters by get.sources().app.newMessagesCount.collectAsState()
//
//                    Button(
//                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
//                        onClick = {
//                            screen.ClickToBargainingUseCase(ad)
//                        }) {
//                        Text(text = stringResource(R.string.bargaining_button))
//                    }
//
//                    if (newMessagesCounters.size > 0) {
//                        val pair = newMessagesCounters.firstOrNull { it.first == screen.ad.id }
//                        val count = pair?.second ?: 0
//                        if (count > 0) {
//                            Text(
//                                "$count",
//                                color = Color.White,
//                                modifier = Modifier
//                                    .padding(end = 6.dp, top = 4.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.Red)
//                                    .padding(horizontal = 8.dp)
//                                    .align(Alignment.TopEnd)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    } else {
//        Button(
//            modifier = Modifier
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            enabled = false,
//            onClick = {
//                screen.ClickToBuyUseCase(ad)
//            }) {
//            Text(stringResource(R.string.ordered_button))
//        }
//    }
//}
//}