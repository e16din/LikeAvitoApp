package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.MainSet
import me.likeavitoapp.develop
import me.likeavitoapp.mainSet

// NOTE: Решение вопроса: "Как писать в стиле TDD/BDD, и не писать авто-тесты?
// Чтобы и функционал и тесты можно было писать сразу в одном месте?"
// Как плюс стало нагляднее и это можно переиспользовать в unit тестах
// (которые есть по сути другой клиент для исполнения наших единожды написанных функций)

// NOTE: однажды написанные тесты запускаются при прогоне юнит-тестов,
// при использовании приложения (при develop == true) и
// при обновлении Compose Preview

// NOTE: Так удобнее переносить и переиспользовать


class TestCase<T>(val input: T, val expect: Boolean)

fun <T> T.expect(expect: Boolean) = TestCase(input = this, expect = expect)

fun <T> useCase(text: String, value: T): T {
    println("Use Case: $text")
    return value
}

inline fun <T> T.expect(
    text: String,
    vararg values: Any?,
    crossinline function: (T) -> Unit = {}
) {
    println("Expect: $text")
    function(this)
}

inline fun Any.checkList(
    vararg checks: () -> Boolean,
    enabled: Boolean = true,
    onResult: (Boolean) -> Unit = {},
): Boolean {
    if (enabled) {
        println()
        var log = "* Checks: "
        checks.forEachIndexed { i, check ->
            log += "[$i]"
            if (!check()) {
                println("$log <-")
                onResult(false)
                return false
            }
        }

        println("$log <-")
        onResult(true)
        return true
    }

    return true
}

fun <T> withResult(value: T, caseResult: Boolean): Result<T> {
    println("Output: check($value) | Checking Result: $caseResult")
    println()

    return if (caseResult) {
        Result.success(value)
    } else {
        Result.failure(IllegalArgumentException("invalid data: $value"))
    }
}

inline fun <T> withTests(
    realInput: T,
    outputMaker: (input: T) -> T = { it },
    testCases: List<TestCase<T>>,
    enabled: Boolean = develop,
    withAssert: Boolean = develop,
    crossinline case: (T) -> Boolean
): Pair<T, Boolean> {
    if (enabled) {
        testCases.forEachIndexed { i, it ->
            val output = outputMaker(it.input)
            val caseResult = case(output)

            val testResult = caseResult == it.expect
            val numberInList = "${i + 1}/${testCases.size}"

            val logMessage =
                if (testResult)
                    "Test $numberInList Succeed { input: \"${it.input}\", output: \"$output\", check(output) == ${it.expect} }"
                else
                    "Test $numberInList Failed { input: \"${it.input}\", output: \"$output\", check(output) != ${it.expect} }"

            println(logMessage)

            if (withAssert) {
                assert(testResult) { logMessage }
            }
        }
    }

    return Pair(realInput, case(outputMaker(realInput)))
}

fun check(function: () -> Boolean) = function
fun runTests(function: () -> Unit) = function

// NOTE: mocks

private val emptyScreenNavigator by lazy { ScreensNavigator() }
fun mockScreensNavigator() = emptyScreenNavigator
fun mockMainSet() = MainSet().apply {
    init(AppPlatform(), CoroutineScope(mainSet.defaultContext))
}