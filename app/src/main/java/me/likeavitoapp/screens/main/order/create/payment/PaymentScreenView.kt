package me.likeavitoapp.screens.main.order.create.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.expect
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

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun PaymentScreenView(screen: PaymentScreen, modifier: Modifier) = with(screen) {
    Column(modifier = modifier.fillMaxSize()) {
        Box {
            Card {
                // card number Номер карты
                val cardNumber by state.paymentData.cardNumber.output.collectAsState()
                TextField(
                    value = cardNumber,
                    label = { Text("Номер карты") },
                    placeholder = { Text("1111 1111 1111 1111") },
                    /*visualTransformation = object : VisualTransformation {
                        override fun filter(text: AnnotatedString): TransformedText {
                            val digits = text.text.replace(Regex("[^\\d]"), "")
                            val formattedText = formatPhoneNumber(digits)

                            return TransformedText(
                                AnnotatedString(formattedText),
                                OffsetMapping.Identity
                            )
                        }
                    },*/
                    onValueChange = {
                        screen.ChangeCardNumberUseCase(it)
                    }
                )

                val mmYY by state.paymentData.mmYy.output.collectAsState()
                TextField(
                    value = mmYY,
                    onValueChange = {
                        screen.ChangeMmYyUseCase(it)
                        expect(
                            "should to show month and year in format: mm/yy",
                            "mm from 01 to 12",
                            "yy from 00 to 99",
                            "digits and / with spaces only"
                        )
                    },
                    label = { Text("Действует до") }
                )
            }
            Card(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .widthIn(0.dp, 232.dp)
            ) {

                val cvvCvc by state.paymentData.cvvCvc.output.collectAsState()
                TextField(
                    value = cvvCvc,
                    onValueChange = {
                        screen.ChangeCvvCvcUseCase(it)
                        expect(
                            "should to show cvv in format: 123",
                            "is number",
                            "length == 3"
                        )
                    },
                    label = { Text("три цифры с обратной стороны карты") }
                )
            }
        }
        Button(onClick = {
            screen.ClickToPayUseCase()
        }) {
            Text(stringResource(R.string.pay_button))
        }
    }
}

fun formatPhoneNumber(digits: String): String {
    return when {
        digits.length >= 10 -> "(${digits.substring(0, 3)}) ${
            digits.substring(
                3,
                6
            )
        }-${digits.substring(6, 10)}"

        digits.length >= 6 -> "(${digits.substring(0, 3)}) ${digits.substring(3)}"
        digits.length >= 3 -> "(${digits.substring(0, 3)}) ${digits.substring(3)}"
        else -> digits
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    LikeAvitoAppTheme {
        PaymentScreenProvider(
            screen = PaymentScreen(
                navigatorPrev = mockScreensNavigator(),
                navigatorNext = mockScreensNavigator(),
                scope = mockCoroutineScope(),
                sources = mockDataSource(),
                ad = MockDataProvider().ads.first()
            )
        )
    }
}