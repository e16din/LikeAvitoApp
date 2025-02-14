package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.defaultContext
import me.likeavitoapp.develop
import me.likeavitoapp.log
import me.likeavitoapp.logError

// NOTE: Решение вопроса: "Как писать в стиле TDD/BDD, и не писать авто-тесты?
// Чтобы и функционал и тесты можно было писать сразу в одном месте?"
// Как плюс стало нагляднее и это можно переиспользовать в unit тестах
// (которые есть по сути другой клиент для исполнения наших единожды написанных функций)

data class TestCase<T>(val value: T, val expect: Boolean)

fun <T> useCase(text: String, value: T): T {
    return value
}

inline fun <T> T.expect(
    text: String,
    vararg values: Any?,
    crossinline function: (T) -> Unit = {}
) {
    function(this)
}

inline fun Any.checkList(
    vararg checks: Boolean,
    enabled: Boolean = true,
    onResult: (Boolean) -> Unit = {},
): Boolean {
    val success = checks.all { it }
    if (enabled) {
        onResult(success)
        return success
    }

    return true
}

inline fun <T> withResult(value: T, crossinline case: (T) -> Boolean): Result<T> {
    val result = case(value)
    log("Output: $value | Checking Result: ${value}")
    return if (result) {
        Result.success(value)
    } else {
        Result.failure(IllegalArgumentException("invalid data: $value"))
    }
}

inline fun <T> withTests(
    realOutput: T,
    enabled: Boolean = develop,
    withAssert: Boolean = develop,
    testOutputs: List<TestCase<T>>,
    crossinline case: (T) -> Boolean
): Result<T> {
    if (enabled) {
        testOutputs.forEach {
            val caseResult = case(it.value)

            val testResult = caseResult == it.expect
            if (testResult) {
                log("Test Succeed: ${it.value} == ${it.expect}")
            } else {
                logError("Test Failed: ${it.value} != ${it.expect} ", prefix = "")
            }
            if (withAssert) {
                assert(testResult)
            }
        }
    }

    return withResult(realOutput, case)
}

inline fun check(function: () -> Boolean) = function()

// NOTE: mocks

fun mockDataSource() = DataSources(
    app = AppModel(),
    platform = AppPlatform(),
    backend = AppBackend(),
)

fun mockCoroutineScope() = CoroutineScope(defaultContext)
fun mockScreensNavigator() = ScreensNavigator()