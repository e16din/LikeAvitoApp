package me.likeavitoapp.screens.main.tabs.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.screens.main.tabs.TabsRootScreen
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun ProfileScreenProvider(screen: ProfileScreen, tabsRootScreen: TabsRootScreen) {

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProfileScreenView(screen)
        }

        NextTabProvider(screen, tabsRootScreen)
    }

    DisposableEffect(Unit) {
        onDispose {
            screen.CloseScreenUseCase()
        }
    }
}

@Composable
fun ProfileScreenView(screen: ProfileScreen) {
    val logoutLoading = screen.state.logout.working.collectAsState()
    val photoUrl = screen.state.user.photoUrl.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ActualAsyncImage(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp)
                        .clip(CircleShape),
                    url = photoUrl.value
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = screen.state.user.name,
                    style = AppTypography.headlineLarge
                )
            }

            IconButton(
                onClick = {
                    screen.ClickToEditProfileUseCase()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
            }
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
                ContactItem(
                    label = stringResource(R.string.phone_title),
                    value = it,
                    screen = screen
                )
            }
            screen.state.user.contacts.email?.let {
                ContactItem(
                    label = stringResource(R.string.email_title),
                    value = it,
                    screen = screen
                )
            }
            screen.state.user.contacts.whatsapp?.let {
                ContactItem(
                    label = stringResource(R.string.whatsapp_title),
                    value = it,
                    screen = screen
                )
            }
            screen.state.user.contacts.telegram?.let {
                ContactItem(
                    label = stringResource(R.string.telegram_title),
                    value = it,
                    screen = screen
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                screen.ClickToLogoutUseCase()
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (logoutLoading.value) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.logout_button))
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
fun ProfileScreenPreview() {
    mainSet = mockMainSet()
    LikeAvitoAppTheme {
        ProfileScreenView(
            ProfileScreen(
                navigator = mockScreensNavigator(),
                user = MockDataProvider().user
            )
        )
    }
}