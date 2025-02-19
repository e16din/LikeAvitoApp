package me.likeavitoapp.model

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.MainSet
import me.likeavitoapp.develop
import me.likeavitoapp.get
import me.likeavitoapp.log

// NOTE: Решение вопроса: "Как писать в стиле TDD/BDD, и не писать авто-тесты?
// Чтобы и функционал и тесты можно было писать сразу в одном месте?"
// Как плюс стало нагляднее и это можно переиспользовать в unit тестах
// (которые есть по сути другой клиент для исполнения наших единожды написанных функций)

// NOTE: однажды написанные тесты запускаются при прогоне юнит-тестов,
// при использовании приложения (при develop == true) и
// при обновлении Compose Preview

// NOTE: Так удобнее переносить и переиспользовать


class TestCase<T>(val input: T, val expectOutput: T? = null, val expectIsValid: Boolean? = null)

infix fun <T> T.expectIsValid(expect: Boolean) = TestCase(input = this, expectIsValid = expect)
infix fun <T> T.expectOutput(output: T) = TestCase(input = this, expectOutput = output)

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
                log("$log <-", tag = "")
                onResult(false)
                return false
            }
        }

        log("$log <-", tag = "")
        onResult(true)
        return true
    }

    return true
}

inline fun <T> withTests(
    realInput: T,
    crossinline outputMaker: (input: T) -> T = { it }, // делаем из инпута аутпут
    testsEnabled: Boolean = develop,
    testCases: List<TestCase<T>>,
    withAssert: Boolean = develop,
    crossinline validator: (T) -> Boolean = { true }, // валидируем каждый инпут (реальный и тестовые) (обычно мы это чекаем в if/else)
    crossinline onDone: (T) -> Unit = {} // в конце выдаем аутпут из реального инпута
): Pair<T, Boolean> {
    if (testsEnabled) {
        var succeedCount = 0

        testCases.forEachIndexed { i, it ->
            val output = outputMaker(it.input)
            val caseResult = validator(output)

            val testResult = if (it.expectIsValid != null)
                caseResult == it.expectIsValid
            else
                output == it.expectOutput
            val numberInList = "${i + 1}/${testCases.size}"


            val logMessage =
                if (it.expectIsValid != null) {
                    if (testResult) succeedCount += 1
                    "Test $numberInList ${if (testResult) "Succeed" else "Failed"} { input: \"${it.input}\", output: \"$output\", check(output) ${if (testResult) "==" else "!="} ${it.expectIsValid} }"
                } else {
                    if (output == it.expectOutput) succeedCount += 1
                    "Test $numberInList ${if (output == it.expectOutput) "Succeed" else "Failed"} { input: \"${it.input}\", output: \"$output\", $output ${if (output == it.expectOutput) "==" else "!="} ${it.expectOutput} }"
                }

            log(logMessage, tag = "")

            if (withAssert) {
                assert(testResult) { logMessage }
            }
        }
        println()
        log("Result: $succeedCount of ${testCases.size} is succeed!", tag = "")
    }

    val output = outputMaker(realInput)
    onDone(output)
    return Pair(realInput, validator(output))
}

fun check(function: () -> Boolean) = function
fun runTests(function: () -> Unit) = function

// NOTE: mocks

private val emptyScreenNavigator by lazy { ScreensNavigator() }
fun mockScreensNavigator() = emptyScreenNavigator
fun mockMainSet() = MainSet().apply {
    init(object : IAppPlatform{
        override val appDataStore: IAppPlatform.IAppDataStore
            get() = object : IAppPlatform.IAppDataStore {
                override suspend fun loadId(): Long? {
                    return null
                }

                override suspend fun saveId(userId: Long) {
                }

                override suspend fun loadToken(): String? {
                    return null
                }

                override suspend fun saveToken(token: String) {
                }

                override suspend fun clear() {
                }
            }
    }, CoroutineScope(get.defaultContext))
}