package me.likeavitoapp.screens.main.order.create.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.get
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
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box {
                Card(
                    modifier = Modifier
                        .padding(start = 32.dp, top = 96.dp)
                        .height(210.dp)
                        .widthIn(0.dp, 320.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, bottom = 8.dp)
                    ) {
                        Text(
                            "три цифры с обратной стороны карты",
                            style = MaterialTheme.typography.titleSmall
                        )
                        val cvvCvc by state.cvvCvc.output.collectAsState()
                        SimpleTextField(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 8.dp)
                                .width(112.dp)
                                .clip(RoundedCornerShape(16)),
                            value = cvvCvc,
                            onValueChange = {
                                if (it.text.length > "123".length) {
                                    return@SimpleTextField
                                }

                                if (it.selection.length == 0) {
                                    screen.ChangeCvvCvcUseCase(it)
                                }
                            },
                            placeholder = { Text("123") },
                            colors = TextFieldDefaults.colors().copy(
                                focusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black.copy(alpha = 0.6f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                val cardGradient = remember {
                    object : ShaderBrush() {
                        override fun createShader(size: Size): Shader {
                            val biggerDimension = maxOf(size.height, size.width)
                            return RadialGradientShader(
                                colors = listOf(Color(0xFF2be4dc), Color(0xFF243484)),
                                center = size.center.copy(
                                    x = size.center.x + 50,
                                    y = size.center.y + 160
                                ),
                                radius = biggerDimension / 0.75f,
                                colorStops = listOf(0f, 0.95f)
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .height(210.dp)
                        .widthIn(0.dp, 320.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(cardGradient),
                    ) {
                        Spacer(Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .width(320.dp)
                                .padding(horizontal = 24.dp)
                        ) {
                            Text("Номер карты", style = MaterialTheme.typography.titleSmall)

                            val cardNumber by state.cardNumber.output.collectAsState()
                            SimpleTextField(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .widthIn(0.dp, 320.dp)
                                    .clip(RoundedCornerShape(16)),
                                value = cardNumber,
                                placeholder = { Text("1111 1111 1111 1111") },
                                onValueChange = {
                                    if (it.text.length > "1111 1111 1111 1111".length) {
                                        return@SimpleTextField
                                    }

                                    val removeOneChar = isRemove(cardNumber, it)
                                    if (removeOneChar) {
                                        screen.ChangeCardNumberUseCase(cardNumber, removeOneChar)
                                    } else {
                                        screen.ChangeCardNumberUseCase(it, false)
                                    }
                                },
                                colors = TextFieldDefaults.colors().copy(
                                    focusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black.copy(alpha = 0.6f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .padding(start = 24.dp, bottom = 8.dp)
                        ) {
                            Text("Действует до", style = MaterialTheme.typography.titleSmall)

                            val mmYy by state.mmYy.output.collectAsState()

                            SimpleTextField(
                                modifier = Modifier
                                    .padding(top = 4.dp, bottom = 8.dp)
                                    .width(144.dp)
                                    .clip(RoundedCornerShape(16)),
                                value = mmYy,
                                onValueChange = {
                                    if (it.text.length > "mm/yy".length) {
                                        return@SimpleTextField
                                    }

                                    val removeOneChar = isRemove(mmYy, it)
                                    if (removeOneChar) {
                                        screen.ChangeMmYyUseCase(mmYy, removeOneChar)
                                    } else {
                                        screen.ChangeMmYyUseCase(it, false)
                                    }
                                },
                                placeholder = { Text("mm/yy") },
                                colors = TextFieldDefaults.colors().copy(
                                    focusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black.copy(alpha = 0.6f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                    }
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

private fun isRemove(
    mmYy: TextFieldValue,
    value: TextFieldValue
): Boolean = mmYy.text.length - value.text.length == 1
            && mmYy.selection.end - value.selection.end == 1

@Preview
@Composable
fun PaymentScreenPreview() {
    get = mockMainSet()
    val screen = PaymentScreen(
        navigator = mockScreensNavigator(),
        ad = MockDataProvider().ads.first()
    )
    LikeAvitoAppTheme {
        PaymentScreenProvider(
            screen = screen
        )
    }

    runTests {
        screen.ChangeCardNumberUseCase(TextFieldValue(""), false)
        screen.ChangeMmYyUseCase(TextFieldValue(""), false)
        screen.ChangeCvvCvcUseCase(TextFieldValue(""))
    }
}

// NOTE: Я искал узкие места в разработке мобильных приложений
// и необходимость поддерживать проект в едином стиле и единой структуре -
// это одно из наиболее ограничивающих бутылочных горлышек
// потому как и человек и технологии развиваются,
// а написанный код остается прежним

// NOTE: чтобы не переписывать каждый раз весь код следует позаботиться о том
// чтобы добавляемый код был независим от существующего,
// как бы это лучше сделать?

// (есть еще вариант - уже написанный код должен сам адаптироваться под новые реалии)

// SUGGEST: для успешной разработки следует пренебречь единообразием,
// и рефакторинг производить лишь когда приложение закончено,
// либо вообще не производить полный рефакторинг, а только точечный,
// как если бы редактрировали отдельный модуль

// NOTE: стремясь делать части независимыми мы делаем код зависимым от этого,
// так что это не решает задачу, то что связано остается связанным,
// только обрастает избыточными абстрациями и прослойками

// SUGGEST: потому следует отказаться от лишних прослоек/посредников
// и писать код так просто как это возможно

// SUGGEST: не разделяй то что связано, если на то нет веской причины
// (например параллельная разработка, хотя и это можно решить другими способами - git, совместное редактирование )

// SUGGEST: Код следует делать антихрупким,
// т.е. отказаться от монолитной архитектуры которая делает проект хрупким
// у нас уже есть ограничивающие структуры в виде ЯП, ОS, UI-фреймворка, и объектной модели данных,
// следует действовать в этих рамках и не добавлять, искусственно, новых

// Лучший код - это код которого нет, а функционал выполняется