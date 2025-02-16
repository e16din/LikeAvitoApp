package me.likeavitoapp

import androidx.compose.runtime.MutableState
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
    loading: () -> Result<T>,
    crossinline onSuccess: (data: T) -> Unit
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