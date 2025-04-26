package me.likeavitoapp.screens.main.createad.steps

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
fun DescriptionStepScreenProvider(screen: DescriptionStepScreen) {
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
                DescriptionStepScreenView(screen, Modifier.padding(innerPadding))
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DescriptionStepScreenView(screen: DescriptionStepScreen, modifier: Modifier) = with(screen) {
    val descriptionFocusRequester = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        val title by screen.state.title.collectAsState()
        OutlinedTextField(
            value = title,
            onValueChange = { value ->
                screen.ChangeTitleUseCase(value)
            },
            label = {
                Text(stringResource(R.string.title_label))
            },
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            maxLines = 2,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    descriptionFocusRequester.requestFocus()
                }
            )
        )


        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))


        val photos = screen.state.photos

        val screenWidth = get.sources().platform.screenWidthDp

        val scrollToEnd by screen.state.scrollPhotosToEnd.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(scrollToEnd) {
            if (scrollToEnd) {
                screen.state.scrollPhotosToEnd.next(false)
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

        val description by screen.state.description.collectAsState()
        OutlinedTextField(
            value = description,
            onValueChange = { value ->
                screen.ChangeDescriptionUseCase(value)
            },
            label = {
                Text(stringResource(R.string.description_label))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .focusRequester(descriptionFocusRequester),
            minLines = 6,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {

                }
            )
        )

        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))

        val isBargainingEnabled by screen.state.isBargainingEnabled.collectAsState()
        Row(
            Modifier.clickable {
                screen.ClickToIsBargainingUseCase()
            }
        ) {
            Checkbox(
                checked = isBargainingEnabled,
                onCheckedChange = {
                    screen.ChangeIsBargainingUseCase(it)
                }
            )
            Text(
                stringResource(R.string.bargaining_enabled_checkbox),
                Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(Modifier.size(8.dp))

        Button(
            onClick = {
                screen.ClickToNextUseCase()
            },
            Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.next_button))
        }
    }

}