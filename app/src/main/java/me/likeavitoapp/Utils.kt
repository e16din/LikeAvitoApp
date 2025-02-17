package me.likeavitoapp

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker

fun Any.className(): String {
    return javaClass.simpleName
}

suspend inline fun <T> MutableState<T>.setUi(value: T) {
    withContext(Dispatchers.Main) {
        this@setUi.value = value
    }
}

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

class Debouncer<T>(var value: T, timeoutMs: Long = 390L, onTimeout: (value: T) -> Unit) {
    var lastSetTimeMs = System.currentTimeMillis()

    init {
        while (true) {
            if (System.currentTimeMillis() - lastSetTimeMs >= timeoutMs) {
                onTimeout(value)
                break
            }
        }
    }

    fun set(newValue: T) {
        value = newValue
        lastSetTimeMs = System.currentTimeMillis()
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
    val cursorIndex = source.indexOf(cursor)
    if (removeOneChar) {
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
        log("result: $result")
        result.toString()
    } else {
        log("result: $result")
        result.toString().dropLast(dropCount)
    }
}

private fun String.fetchDigits(withChars: List<Char> = emptyList()): String {
    return this.filter { it.isDigit() || withChars.contains(it) }
}

// NOTE: for debug
fun main() {
    val stringBuilder = StringBuilder()
    fun test(value: TextFieldValue): TextFieldValue {
        var result = me.likeavitoapp.format(
            text = stringBuilder.append(value.text).insert(value.selection.start, "|").toString(),
            mask = "##/##",
            delimiter = '/',
            stringBuilder = stringBuilder,
            removeOneChar = false
        )
        stringBuilder.clear()
        var newPosition = result.indexOf('|')
        result = result.replace("|", "")

        return TextFieldValue(result, TextRange(newPosition))
    }

    log(test(TextFieldValue("1234", TextRange(4))))
}