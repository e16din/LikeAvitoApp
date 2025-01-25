package me.likeavitoapp.screens.main.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.likeavitoapp.Contacts
import me.likeavitoapp.R
import me.likeavitoapp.User
import me.likeavitoapp.screens.splash.SplashScreenView
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun ProfileScreenProvider() {

}

@Composable
fun ProfileScreenView(screen: ProfileScreen) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            // Edit Button
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = screen.state.user.photoUrl,
                contentDescription = null,
                modifier = Modifier.clip(CircleShape)
                    .padding(16.dp)
                    .size(64.dp)

            )

            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = screen.state.user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        }

        Spacer(Modifier.size(24.dp))

        HorizontalDivider(thickness = 1.dp)
        Text(modifier = Modifier.padding(start = 16.dp, top = 12.dp),text = stringResource(R.string.contacts_title), fontSize = 24.sp)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            screen.state.user.contacts.phone?.let {
                ContactItem(title = stringResource(R.string.phone_title), value = it)
            }
            screen.state.user.contacts.email?.let {
                ContactItem(title = stringResource(R.string.email_title), value = it)
            }
            screen.state.user.contacts.whatsapp?.let {
                ContactItem(title = stringResource(R.string.whatsapp_title), value = it)
            }
            screen.state.user.contacts.telegram?.let {
                ContactItem(title = stringResource(R.string.telegram_title), value = it)
            }
        }
    }
}

@Composable
fun ContactItem(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    LikeAvitoAppTheme {
        ProfileScreenView(ProfileScreen(User(
            id = 0,
            name = "Иван Иванов",
            contacts = Contacts(telegram = "@alex_ku_san"),
            ownAds = emptyList(),
            photoUrl = ""
        )))
    }
}