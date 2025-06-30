package me.likeavitoapp.screens.main.tabs.profile.edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.io.IOException
import me.likeavitoapp.MainActivity
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.log
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme
import me.likeavitoapp.ui.theme.backgroundLight


@Composable
fun EditProfileScreenProvider(screen: EditProfileScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        ActionTopBar(
            title = stringResource(R.string.edit_profile_title),
            onClose = {
                screen.ClickToCloseUseCase()
            },
            onDone = {
                screen.ClickToDoneUseCase()
            },
        ) { innerPadding ->
            EditProfileScreenView(screen, Modifier.padding(innerPadding))
        }
    }

//    PickImageHandler { bytes ->
//        screen.ChangeUserPhotoUseCase(bytes)
//    }

    BackHandler {
        screen.PressBackUseCase()
    }

    DisposableEffect(Unit) {
        onDispose {
            screen.CloseScreenUseCase()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenView(screen: EditProfileScreen, modifier: Modifier) {
    val avatarPickerEnabled by screen.state.avatarPickerEnabled.collectAsState()

    Box(modifier = modifier) {
        ContentView(screen)

        if (avatarPickerEnabled) {
            val activity = LocalActivity.current
            val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
                uri?.let {
                    try {
                        val bytes =
                            (activity as MainActivity).contentResolver.openInputStream(uri)?.readBytes()
                        screen.ChangeUserPhotoUseCase(bytes)

                    } catch (error: IOException) {
                        error.log()
                        screen.ChangeUserPhotoUseCase(null)
                    }
                }
            }
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }
}

@Composable
private fun ContentView(
    screen: EditProfileScreen
) {
    val user = get.sources().app.user.collectAsState().value!!

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.clickable {
                screen.ClickToEditPhotoUseCase()
            }) {
                ActualAsyncImage(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp)
                        .clip(CircleShape),
                    url = user.photoUrl
                )

                Icon(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(backgroundLight)
                        .clip(CircleShape)
                        .align(Alignment.BottomEnd),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "edit"
                )
            }

            val name by screen.state.name.collectAsState()
            TextField(
                value = name,
                onValueChange = { value ->
                    screen.ChangeUserNameUseCase(value)
                },
                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            )
        }

        Spacer(Modifier.size(24.dp))

        HorizontalDivider(thickness = 1.dp)
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            text = stringResource(R.string.contacts_title),
            style = AppTypography.titleLarge
        )
        val phone = screen.state.phone.collectAsState()
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            phone.value?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.phone_title)) },
                    onValueChange = {
                        screen.ChangePhoneUseCase(it)
                    })
            }
            val email = screen.state.email.collectAsState()
            email.value?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.email_title)) },
                    onValueChange = {
                        screen.ChangeEmailUseCase(it)
                    })
            }

            val whatsapp = screen.state.whatsapp.collectAsState()
            whatsapp.value?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.whatsapp_title)) },
                    onValueChange = {
                        screen.ChangeWhatsappUseCase(it)
                    })
            }

            val telegram = screen.state.telegram.collectAsState()
            telegram.value?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.telegram_title)) },
                    onValueChange = {
                        screen.ChangeTelegramUseCase(it)
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        EditProfileScreenView(
            EditProfileScreen(
                navigator = mockScreensNavigator()
            ),
            Modifier
        )
    }
}