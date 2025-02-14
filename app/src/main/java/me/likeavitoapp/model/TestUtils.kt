package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.develop
import me.likeavitoapp.log
import me.likeavitoapp.logError
import me.likeavitoapp.main

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
    vararg checks: () -> Boolean,
    enabled: Boolean = true,
    onResult: (Boolean) -> Unit = {},
): Boolean {
    if (enabled) {
        println()
        var log = ""
        checks.forEachIndexed { i, check ->
            log += "[$i]"
            if (!check()) {
                println(log)
                onResult(false)
                return false
            }
        }

        println(log)
        onResult(true)
        return true
    }

    return true
}

inline fun <T> withResult(value: T, crossinline case: (T) -> Boolean): Result<T> {
    val result = case(value)
    println("Output: check($value) | Checking Result: $result")
    println()

    return if (result) {
        Result.success(value)
    } else {
        Result.failure(IllegalArgumentException("invalid data: $value"))
    }
}

inline fun <T> withTests(
    realOutput: T,
    testCases: List<TestCase<T>>,
    enabled: Boolean = develop,
    withAssert: Boolean = develop,
    crossinline case: (T) -> Boolean
): Result<T> {
    if (enabled) {
        testCases.forEach {
            val caseResult = case(it.value)

            val testResult = caseResult == it.expect
            if (testResult) {
                println("Test Succeed: check(${it.value}) == ${it.expect}")
            } else {
                println("Test Failed: check(${it.value}) != ${it.expect}")
            }
            if (withAssert) {
                assert(testResult)
            }
        }
    }

    return withResult(realOutput, case)
}

fun check(function: () -> Boolean) = function

// NOTE: mocks

fun mockDataSource() = DataSources(
    app = AppModel(),
    platform = AppPlatform(),
    backend = AppBackend(),
)

fun mockCoroutineScope() = CoroutineScope(main.defaultContext)
fun mockScreensNavigator() = ScreensNavigator()