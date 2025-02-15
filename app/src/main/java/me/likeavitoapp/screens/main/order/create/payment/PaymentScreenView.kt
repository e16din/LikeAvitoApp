package me.likeavitoapp.screens.main.order.create.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.mainSet
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.model.runTests
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.SimpleTextField
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(start = 16.dp, top = 96.dp)
                    .height(210.dp)
//                    .widthIn(0.dp, 232.dp)
            ) {
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                ) {
                    Text(
                        "три цифры с обратной стороны карты",
                        style = MaterialTheme.typography.titleSmall
                    )
                    val cvvCvc by state.paymentData.cvvCvc.output.collectAsState()
                    TextField(
                        modifier = Modifier.width(64.dp),
                        value = cvvCvc,
                        onValueChange = {
                            screen.ChangeCvvCvcUseCase(it)
                        },
                        placeholder = { Text("123") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary, CardDefaults.shape)
                    .height(210.dp)
                    .widthIn(0.dp, 320.dp)

            ) {
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Номер карты", style = MaterialTheme.typography.titleSmall)

                    val cardNumber by state.paymentData.cardNumber.output.collectAsState()
                    TextField(
                        value = cardNumber,
                        placeholder = { Text("1111 1111 1111 1111") },
                        onValueChange = {
                            screen.ChangeCardNumberUseCase(it)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                ) {
                    Text("Действует до", style = MaterialTheme.typography.titleSmall)
                    val mmYY by state.paymentData.mmYy.output.collectAsState()
                    SimpleTextField(
                        modifier = Modifier.width(92.dp).clip(RoundedCornerShape(16)),
                        value = mmYY,
                        onValueChange = {
                            screen.ChangeMmYyUseCase(it)
                        },
                        placeholder = { Text("mm/yy") },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        }
        Spacer(Modifier.weight(0.3f))
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                screen.ClickToPayUseCase()
            }
        ) {
            Text(stringResource(R.string.pay_button))
        }
        Spacer(Modifier.weight(0.7f))
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    mainSet = mockMainSet()
    val screen = PaymentScreen(
        navigatorPrev = mockScreensNavigator(),
        navigatorNext = mockScreensNavigator(),
        ad = MockDataProvider().ads.first()
    )
    LikeAvitoAppTheme {
        PaymentScreenProvider(
            screen = screen
        )
    }

    runTests {
        screen.ChangeCardNumberUseCase("")
        screen.ChangeMmYyUseCase("")
        screen.ChangeCvvCvcUseCase("")
    }
}