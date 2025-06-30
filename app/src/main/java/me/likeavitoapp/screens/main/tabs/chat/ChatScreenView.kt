package me.likeavitoapp.screens.main.tabs.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.IMessage
import me.likeavitoapp.model.PreviewTextMessage
import me.likeavitoapp.model.TextMessage
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.DetailsTopBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme

@Composable
fun ChatScreenProvider(screen: ChatScreen) {

    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        DetailsTopBar(
            title = screen.title,
            onBack = {
                screen.PressBackUseCase()
            },
        ) { innerPadding ->
            ChatScreenView(screen, Modifier.padding(innerPadding))
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun ChatScreenView(screen: ChatScreen, modifier: Modifier) {
    val messageText by screen.state.message.collectAsState()
    val messages = screen.state.messages
    val userId = screen.state.userId
    val scrollToEnd by screen.state.scrollToEnd.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToEnd) {
        if (scrollToEnd) {
            screen.state.scrollToEnd.next(false)
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            userScrollEnabled = true,
            state = listState,
            verticalArrangement = Arrangement.Bottom,
        ) {

            items(messages, key = { it.id }) { message ->
                TextMessageView(message.userId == userId, message)
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            BasicTextField(
                value = messageText,
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
                if (messageText.isNotBlank()) {
                    screen.ClickToSendUseCase()
                }
            }) {
                Text(stringResource(R.string.send_button))
            }
        }
    }
}

@Composable
fun TextMessageView(isMy: Boolean, message: IMessage) {
    Column(Modifier.fillMaxWidth()) {
        when {
            message is PreviewTextMessage -> {
                Box(
                    Modifier
                        .padding(start = 64.dp, top = 6.dp)
                        .clip(RoundedCornerShape(50, 50, 0, 50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .align(Alignment.End),
                ) {
                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            message is TextMessage -> {
                Box(
                    Modifier
                        .padding(
                            start = if (isMy) 64.dp else 0.dp,
                            end = if (isMy) 0.dp else 64.dp,
                            top = 6.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                if (isMy) 50 else 0,
                                50,
                                if (isMy) 0 else 50,
                                50
                            )
                        )
                        .background(
                            if (isMy)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .align(if (isMy) Alignment.End else Alignment.Start),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = message.text,
                        color = if (!isMy)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun ChatScreenPreview() {
    get = mockMainSet()
    val screen = ChatScreen(
        adId = -1,
        title = "Title",
        navigator = mockScreensNavigator(),
    )

    LikeAvitoAppTheme {
        ChatScreenView(screen, Modifier)
    }
}