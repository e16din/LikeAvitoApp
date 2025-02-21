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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.io.IOException
import me.likeavitoapp.MainActivity
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
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

    val userPickerEnabled = screen.state.userPickerEnabled.collectAsState()
    val photoUrl = screen.state.user.photoUrl.collectAsState()

    Box(modifier = modifier) {
        ContentView(screen, photoUrl)

        if (userPickerEnabled.value) {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }
}

@Composable
private fun ContentView(
    screen: EditProfileScreen,
    photoUrl: State<String>
) {
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
                    url = photoUrl.value
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

            TextField(
                value = screen.state.user.name,
                onValueChange = {

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
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            screen.state.user.contacts.phone?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.phone_title)) },
                    onValueChange = {

                    })
            }
            screen.state.user.contacts.email?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.email_title)) },
                    onValueChange = {

                    })
            }
            screen.state.user.contacts.whatsapp?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.whatsapp_title)) },
                    onValueChange = {

                    })
            }
            screen.state.user.contacts.telegram?.let {
                TextField(
                    value = it,
                    label = { Text(stringResource(R.string.telegram_title)) },
                    onValueChange = {

                    })
            }
        }
    }
}

@Composable
fun ContactItem(label: String, value: String, screen: ProfileScreen) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                screen.ClickToContactUseCase(label, value)
            }) {
        Text(
            text = label,
            style = AppTypography.labelMedium,
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
        )
        Spacer(Modifier.size(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        EditProfileScreenView(
            EditProfileScreen(
                navigator = mockScreensNavigator(),
                user = MockDataProvider().user
            ),
            Modifier
        )
    }
}