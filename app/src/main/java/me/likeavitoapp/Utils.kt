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
    stringBuilder: StringBuilder = StringBuilder()
): String {
    stringBuilder.clear()
    val source = text.replace("$delimiter", "")
    if (source.isEmpty()) {
        return ""
    }

    var builder = stringBuilder.append(mask)
    var deltaIndex = 0
    var i = 0
    while (i < source.length) {
        if (builder[i] == delimiter) {
            deltaIndex += 1
        }

        if (i + deltaIndex < builder.length) {
            builder[i + deltaIndex] = source[i]
        } else {
            builder.append(source[i])
        }

        if (source[i] == cursor) {
            if (i + 1 < source.length) {
                builder.insert(i + deltaIndex + 1, source[i + 1])
                i += 1
            } else {
                if (i - 1 > 0 && builder[i - 1] == delimiter) {
                    // = cursor
                }
            }
        }

        i += 1
    }

    var dropCount = 0
    i = builder.length - 1
    while (builder[i] == maskChar || builder[i] == delimiter) {
        dropCount += 1
        i -= 1
    }

    return if (dropCount == 0) {
        builder.toString()
    } else {
        builder.toString().dropLast(dropCount)
    }
}

fun format2(
    text: String,
    mask: String,
    maskChar: Char = '#',
    cursor: Char = '|',
    delimiter: Char = ' ',
    removeOneChar: Boolean = false,
    stringBuilder: StringBuilder = StringBuilder()
): String {
    stringBuilder.clear()

    var source = text.replace("$delimiter", "")
    val cursorIndex = source.indexOf(cursor)
    if(removeOneChar){
        source = source.removeRange(cursorIndex - 1, cursorIndex)
    }
    if (source.replace("$cursor", "").isEmpty()) {
        return "$cursor"
    }

    val result = stringBuilder.append(mask)
    var sourceIndex = 0
    var i = 0
    while (sourceIndex < source.length) {

        if(i < result.length) {
            if (result[i] != delimiter) {
                println("${result[i]}, ${source[sourceIndex]}")

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
        println("result: $result")
        result.toString()
    } else {
        println("result: $result")
        result.toString().dropLast(dropCount)
    }
}

// NOTE: for debug
fun main() {
    val stringBuilder = StringBuilder()
    fun format(value: TextFieldValue): TextFieldValue {
        var result = me.likeavitoapp.format2(
            text = stringBuilder.append(value.text).insert(value.selection.start, "|").toString(),
            mask = "##/##",
            delimiter = '/',
            stringBuilder = stringBuilder,
            removeOneChar = true
        )
        stringBuilder.clear()
        var newPosition = result.indexOf('|')
        result = result.replace("|", "")

        return TextFieldValue(result, TextRange(newPosition))
    }

//    var result = me.likeavitoapp.format(
//        text = stringBuilder.append("1234").insert(1, "|").toString(),
//        mask = "##/##",
//        delimiter = '/',
//        stringBuilder = stringBuilder
//    )
//    println(result)
    println(format(TextFieldValue("1234", TextRange(4))))
}