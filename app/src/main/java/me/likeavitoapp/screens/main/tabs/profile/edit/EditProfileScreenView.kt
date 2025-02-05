package me.likeavitoapp.screens.main.tabs.profile.edit

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.model.Contacts
import me.likeavitoapp.R
import me.likeavitoapp.defaultContext
import me.likeavitoapp.initApp
import me.likeavitoapp.model.User
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.main.tabs.cart.CartScreen
import me.likeavitoapp.screens.main.tabs.cart.CartScreenProvider
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreen
import me.likeavitoapp.screens.main.tabs.favorites.FavoritesScreenProvider
import me.likeavitoapp.screens.main.tabs.profile.ProfileScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreen
import me.likeavitoapp.screens.main.tabs.search.SearchScreenProvider
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun EditProfileScreenProvider(screen: EditProfileScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        EditProfileScreenView(screen)

    }

    BackHandler {
        screen.PressBack()
    }
}

@Composable
fun EditProfileScreenView(screen: EditProfileScreen) {
    Column(modifier = Modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ActualAsyncImage(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp)
                        .clip(CircleShape),
                    url = screen.state.user.photoUrl
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = screen.state.user.name,
                    style = AppTypography.headlineLarge
                )
            }
        }

        Spacer(Modifier.size(24.dp))

        HorizontalDivider(thickness = 1.dp)
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            text = stringResource(R.string.contacts_title),
            style = AppTypography.titleLarge
        )
//        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//            screen.state.user.contacts.phone?.let {
//                ContactItem(label = stringResource(R.string.phone_title), value = it, screen = screen)
//            }
//            screen.state.user.contacts.email?.let {
//                ContactItem(label = stringResource(R.string.email_title), value = it, screen = screen)
//            }
//            screen.state.user.contacts.whatsapp?.let {
//                ContactItem(label = stringResource(R.string.whatsapp_title), value = it, screen = screen)
//            }
//            screen.state.user.contacts.telegram?.let {
//                ContactItem(label = stringResource(R.string.telegram_title), value = it, screen = screen)
//            }
//        }
    }
}

@Composable
fun ContactItem(label: String, value: String, screen: ProfileScreen) {
    Column(modifier = Modifier
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
    LikeAvitoAppTheme {
        EditProfileScreenView(
            EditProfileScreen(
                parentNavigator = mockScreensNavigator(),
                scope = mockCoroutineScope(),
                sources = mockDataSource(),
                user = MockDataProvider().getUser()
            )
        )
    }
}