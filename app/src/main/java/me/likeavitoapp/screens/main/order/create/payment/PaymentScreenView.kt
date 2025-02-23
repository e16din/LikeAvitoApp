package me.likeavitoapp.screens.main.order.create.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.measureTextWidth
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.Worker
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.model.runTests
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenProvider(screen: PaymentScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        ActionTopBar(
            withDoneButton = false,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenView(screen: PaymentScreen, modifier: Modifier) = with(screen) {
    val validationEnabled by state.validationEnabled.collectAsState()

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
                        .padding(start = 32.dp, top = 114.dp)
                        .height(210.dp)
                        .widthIn(0.dp, 320.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, bottom = 8.dp)
                    ) {
                        PaymentTextField(
                            updatableState = state.cvvCvc,
                            validationEnabled = validationEnabled,
                            label = stringResource(R.string.cvv_label),
                            example = "123",
                            error = stringResource(R.string.incorrect_cvv_number_label),
                            onValueChange = { prevValue, newValue ->
                                screen.ChangeCvvCvcUseCase(newValue)
                            },
                            isLastField = true
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

                            PaymentTextField(
                                updatableState = state.cardNumber,
                                validationEnabled = validationEnabled,

                                label = stringResource(R.string.card_number_label),
                                example = "1111 1111 1111 1111",
                                error = stringResource(R.string.incorrect_card_number_label),
                                onValueChange = { prevValue, newValue ->
                                    val removeOneChar = isRemove(prevValue, newValue)
                                    screen.ChangeCardNumberUseCase(
                                        value = if (removeOneChar) prevValue else newValue,
                                        removeOneChar = removeOneChar
                                    )
                                },
                                requiredWidth = 240.dp
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Column(
                            modifier = Modifier
                                .padding(start = 24.dp, bottom = 8.dp)
                        ) {
                            PaymentTextField(
                                updatableState = state.mmYy,
                                validationEnabled = validationEnabled,
                                label = stringResource(R.string.mm_yy_label),
                                example = "mm/yy",
                                error = stringResource(R.string.incorrect_mm_yy_label),
                                onValueChange = { prevValue, newValue ->
                                    val removeOneChar = isRemove(prevValue, newValue)
                                    screen.ChangeMmYyUseCase(
                                        value = if (removeOneChar) prevValue else newValue,
                                        removeOneChar = removeOneChar
                                    )
                                }
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

//TODO: set visual transformation
// https://stackoverflow.com/a/69064274/6445611
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PaymentTextField(
    updatableState: Worker<TextFieldValue>,
    validationEnabled: Boolean,
    label: String,
    example: String,
    error: String,
    onValueChange: (prev: TextFieldValue, new: TextFieldValue) -> Unit,
    requiredWidth: Dp? = null,
    isLastField: Boolean = false
) {
    val value by updatableState.output.collectAsState()
    val valueHasFail by updatableState.fail.collectAsState()

    val localFocusManager = LocalFocusManager.current

    val fieldTextStyle = TextStyle.Default

    Column {
        if (validationEnabled) {
            Row {
                val isError = valueHasFail || value.text.isEmpty()
                val color = if (isError) MaterialTheme.colorScheme.error else Color.Green
                val text = if (isError) error else stringResource(R.string.valid_label)
                Icon(
                    Icons.Default.Info,
                    "error",
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    color = color,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically),
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall
            )
        }
//        val colors = TextFieldDefaults.colors()
//        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        CompositionLocalProvider(LocalTextToolbar provides EmptyTextToolbar) {
        BasicTextField(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 0.dp)
                .fillMaxWidth(),
            value = value,
            textStyle = fieldTextStyle,
            onValueChange = { newValue ->
                if (newValue.text.length > example.length) {
                    return@BasicTextField
                }

                if (newValue.selection.length == 0) {
                    onValueChange(updatableState.output.value, newValue)
                }
            },
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = {
                        Spacer(Modifier.padding(8.dp))
                        Box(Modifier) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                    .width(
                                        requiredWidth
                                            ?: measureTextWidth(example, fieldTextStyle)
                                    ),

                                ) {
                                Box(Modifier.align(Alignment.Center)) {
                                    innerTextField()
                                }
                            }
                        }
                    },
                    enabled = true,
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,// MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = Color.Black,//MaterialTheme.colorScheme.outline,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        top = 0.dp,
                        bottom = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = if (isLastField) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    localFocusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
    }
//    }
  }
}

// TODO: create custom TextToolbar
object EmptyTextToolbar : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {}

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
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
        ad = MockDataProvider().ads.first(),
        orderType = Order.Type.Pickup
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


