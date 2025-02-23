package me.likeavitoapp.screens.main.tabs.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.get
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.OfferMessage
import me.likeavitoapp.model.TextMessage
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun ChatScreenProvider(screen: ChatScreen) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ChatScreenView(screen)
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun ChatScreenView(screen: ChatScreen) {
    var messageText = screen.state.message.collectAsState()
    val messages = screen.state.messages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageItem(message)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText.value,
                onValueChange = { text ->
                    screen.ChangeMessageUseCase(text)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            )

            Button(onClick = {
                if (messageText.value.isNotBlank()) {
                    screen.ClickToSendUseCase()
                }
            }) {
                Text("Отправить")
            }
        }
    }
}

@Composable
fun MessageItem(message: IMessage) {
    when (message) {
        is TextMessage -> TextMessageView(message)
        is OfferMessage -> OfferMessageView(message)
    }
}

@Composable
fun TextMessageView(message: TextMessage) {
    Text(text = message.text)
}

@Composable
fun OfferMessageView(message: OfferMessage) {
    val displayText = if (message.isMy) {
        "Куплю за: ${message.newPrice}"
    } else {
        "Продам за: ${message.newPrice}"
    }
    Text(text = displayText)
}

@Preview
@Composable
fun ChatScreenPreview() {
    get = mockMainSet()
    val screen = ChatScreen(
        ad = MockDataProvider().ads.first(),
        navigator = mockScreensNavigator(),
    )

    LikeAvitoAppTheme {
        ChatScreenView(screen)
    }
}