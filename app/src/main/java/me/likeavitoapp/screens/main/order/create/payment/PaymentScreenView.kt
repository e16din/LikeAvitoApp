package me.likeavitoapp.screens.main.order.create.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yandex.mapkit.MapKitFactory
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenProvider(screen: PaymentScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        ActionTopBar(
            title = stringResource(R.string.pay_title),
            onClose = {
                screen.ClickToCloseUseCase()
            },
            onDone = {
                screen.ClickToDoneUseCase()
            },
        ) { innerPadding ->
            PaymentScreenView(screen, Modifier.padding(innerPadding))
        }
    }

    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        onDispose {
            MapKitFactory.getInstance().onStop()
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun PaymentScreenView(screen: PaymentScreen, modifier: Modifier) = with(screen) {

    Column(modifier = modifier.fillMaxSize()) {
        Card {

        }
        Button(onClick = {
            screen.ClickToPayUseCase()
        }) {
            Text(stringResource(R.string.pay_button))
        }
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    LikeAvitoAppTheme {
        val scope = mockCoroutineScope()
        PaymentScreenProvider(
            screen = PaymentScreen(
                navigatorPrev = mockScreensNavigator(),
                navigatorNext = mockScreensNavigator(),
                scope = scope,
                sources = mockDataSource(),
                ad = MockDataProvider().ads.first()
            )
        )
    }
}