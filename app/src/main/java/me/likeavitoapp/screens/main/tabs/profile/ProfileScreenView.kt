package me.likeavitoapp.screens.main.tabs.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.main.tabs.NextTabProvider
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun ProfileScreenProvider(screen: ProfileScreen, tabsNavigator: ScreensNavigator) {

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProfileScreenView(screen)
        }

        NextTabProvider(screen, tabsNavigator)
    }

    DisposableEffect(Unit) {
        onDispose {
            screen.CloseScreenUseCase()
        }
    }
}

@Composable
fun ProfileScreenView(screen: ProfileScreen) {
    val logoutLoading by screen.state.logout.working.collectAsState()
    val userVal = get.sources().app.user.collectAsState()
    val user = userVal.value!!
    val chats by screen.state.chats.output.collectAsState()

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
                    url = user.photoUrl
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = user.name,
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
            user.contacts.phone?.let {
                ContactItem(
                    label = stringResource(R.string.phone_title),
                    value = it,
                    screen = screen
                )
            }
            user.contacts.email?.let {
                ContactItem(
                    label = stringResource(R.string.email_title),
                    value = it,
                    screen = screen
                )
            }
            user.contacts.whatsapp?.let {
                ContactItem(
                    label = stringResource(R.string.whatsapp_title),
                    value = it,
                    screen = screen
                )
            }
            user.contacts.telegram?.let {
                ContactItem(
                    label = stringResource(R.string.telegram_title),
                    value = it,
                    screen = screen
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            {
                screen.ClickToOwnAds()
            }, Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(R.string.own_ads_button))
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(thickness = 1.dp)
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            text = stringResource(R.string.chat_label),
            style = AppTypography.titleLarge
        )

        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                Card(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clickable {
                            screen.ClickToChatUseCase(chat)
                        }) {
                    Box {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 24.dp)
                        ) {
                            Row {
                                Icon(Icons.Default.ShoppingCart, "ad")
                                Text(
                                    chat.title,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                            Row(Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Person, "person")
                                Text(chat.userName, Modifier.padding(horizontal = 12.dp))
                            }

                            Row(Modifier.padding(top = 6.dp)) {
                                Icon(Icons.Default.Email, "last message")
                                OutlinedCard(Modifier.padding(horizontal = 8.dp)) {
                                    Text(
                                        chat.messages.last().text,
                                        Modifier.padding(horizontal = 12.dp)
                                    )
                                }
                            }
                        }

                        val newMessagesCount = chat.messages.count { it.isNew }
                        if (newMessagesCount > 0) {
                            Text(
                                "$newMessagesCount",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                screen.ClickToLogoutUseCase()
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (logoutLoading) {
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
    get = mockMainSet()
    LikeAvitoAppTheme {
        ProfileScreenView(
            ProfileScreen(
                navigator = mockScreensNavigator()
            )
        )
    }
}