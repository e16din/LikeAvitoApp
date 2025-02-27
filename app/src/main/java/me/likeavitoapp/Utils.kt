package me.likeavitoapp

import android.content.Context
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker

fun Any.className(): String {
    return javaClass.simpleName
}

@Deprecated("Use Worker<*>.act { } instead")
suspend inline fun <reified T> Worker<*>.load(
    loading: () -> Result<T>, crossinline onSuccess: (data: T) -> Unit
) {
    this.working.post(true)

    val result = loading()
    val newData = result.getOrNull()

    this.working.post(false)

    if (newData != null) {
        withContext(get.defaultContext + Dispatchers.Main) {
            onSuccess(newData)
        }

    } else {
        log("!!!loadingFailed!!!")
        this@load.fail.post(true)
    }
}

fun UpdatableState<Boolean>.inverse() {
    this.post(!this.value)
}

fun CharSequence.isDigitsOnly(): Boolean {
    val len = this.length
    var cp: Int
    var i = 0
    while (i < len) {
        cp = Character.codePointAt(this, i)
        if (!Character.isDigit(cp)) {
            return false
        }
        i += Character.charCount(cp)
    }
    return true
}

fun checkLuhnAlgorithm(digits: String): Boolean {
    // Проверяем, что номер состоит только из цифр и имеет длину от 13 до 19
    if (digits.length < 13 || digits.length > 19) {
        return false
    }

    // Применяем алгоритм Луна
    var sum = 0
    val shouldDouble = digits.length % 2 == 0

    for (i in digits.indices) {
        var digit = digits[i].digitToInt()

        // Удваиваем каждую вторую цифру
        if ((i % 2 == 0 && shouldDouble) || (i % 2 != 0 && !shouldDouble)) {
            digit *= 2
            // Если результат больше 9, вычитаем 9
            if (digit > 9) {
                digit -= 9
            }
        }
        sum += digit
    }

    // Проверяем, делится ли сумма на 10 без остатка
    return sum % 10 == 0
}

fun format(
    text: String,
    mask: String,
    maskChar: Char = '#',
    cursor: Char = '|',
    delimiter: Char = ' ',
    removeOneChar: Boolean = false,
    stringBuilder: StringBuilder = StringBuilder()
): String {
    stringBuilder.clear()

    var source = text.fetchDigits(withChars = listOf(cursor))
    if (source.isEmpty()) {
        return "$cursor"
    }
    val cursorIndex = source.indexOf(cursor)
    if (cursorIndex > 0 && removeOneChar) {
        source = source.removeRange(cursorIndex - 1, cursorIndex)
    }
    if (source.replace("$cursor", "").isEmpty()) {
        return "$cursor"
    }

    val result = stringBuilder.append(mask)
    var sourceIndex = 0
    var i = 0
    while (sourceIndex < source.length) {

        if (i < result.length) {
            if (result[i] != delimiter) {

                result[i] = source[sourceIndex]
                if (result[i] == cursor) {
                    result.insert(i + 1, maskChar)
                }
                sourceIndex += 1
            }
        } else {
            result.append(source[sourceIndex])
            sourceIndex += 1
        }

        i += 1
    }

    var dropCount = 0
    i = result.length - 1
    while (result[i] == maskChar || result[i] == delimiter) {
        dropCount += 1
        i -= 1
    }

    return if (dropCount == 0) {
        result.toString()
    } else {
        result.toString().dropLast(dropCount)
    }
}

private fun String.fetchDigits(withChars: List<Char> = emptyList()): String {
    return this.filter { it.isDigit() || withChars.contains(it) }
}

@Composable
fun measureTextWidth(text: String, style: TextStyle = TextStyle.Default): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

fun Context.launchCustomTabs(
    url: String,
    colors: CustomTabColorSchemeParams = CustomTabColorSchemeParams.Builder().build()
) {
    with(
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colors)
            .build()
    ) {
        intent.setPackage("com.android.chrome")
        launchUrl(this@launchCustomTabs, url.toUri())
    }
}

// NOTE: for debug
fun main() {

}