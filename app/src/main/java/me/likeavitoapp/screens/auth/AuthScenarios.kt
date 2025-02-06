package me.likeavitoapp.screens.auth

import kotlinx.coroutines.delay
import me.likeavitoapp.checkState
import kotlin.math.roundToInt

class AuthScenarios(val screen: AuthScreen) {

    suspend fun start() {
        val startMs = System.currentTimeMillis()
        RunIncorrectEmailScenario()
        delay(4000)
        RunClearEmailScenario()
        delay(1500)
        RunCorrectEmailScenario()
        delay(1500)
        RunEnterPasswordScenario()
        delay(1500)

        RunClearEmailScenario()
        delay(1500)
        RunClearPasswordScenario()
        delay(1500)

        RunLoginFailedScenario()
        delay(4000)
        RunLoginSuccessScenario()

        val currentMs = System.currentTimeMillis()
        val timeS = ((currentMs - startMs) / 1000f).roundToInt()
        println("Auth Scenarios successful finished in ${timeS}s")
    }


    suspend fun RunIncorrectEmailScenario() {
        var email = ""
        "incorrect@email".forEach { c ->
            delay(100)
            email += c
            screen.ChangeEmailUseCase(email)
        }
        delay(1000)
        checkState(screen.state.emailErrorEnabled.value)

        RunClearEmailScenario()
        email = ""
        delay(1000)
        checkState(!screen.state.emailErrorEnabled.value)

        "inc##orrect@em.com".forEach { c ->
            delay(100)
            email += c
            screen.ChangeEmailUseCase(email)
        }
        delay(1000)
        checkState(screen.state.emailErrorEnabled.value)

        RunClearEmailScenario()
        email = ""
        delay(1000)
        checkState(!screen.state.emailErrorEnabled.value)

        "dsflkgsd".forEach { c ->
            delay(100)
            email += c
            screen.ChangeEmailUseCase(email)
        }
        delay(1000)
        checkState(screen.state.emailErrorEnabled.value)
    }

    suspend fun RunClearEmailScenario() {
        while (screen.state.email.value.isNotEmpty()) {
            delay(100)
            screen.ChangeEmailUseCase(screen.state.email.value.dropLast(1))
        }
    }

    suspend fun RunClearPasswordScenario() {
        while (screen.state.password.value.isNotEmpty()) {
            delay(100)
            screen.ChangePasswordUseCase(screen.state.password.value.dropLast(1))
        }
    }

    suspend fun RunCorrectEmailScenario(value: String = "correct@email.com") {
        var email = ""
        value.forEach { c ->
            delay(100)
            email += c
            screen.ChangeEmailUseCase(email)
        }
        delay(1000)
        checkState(!screen.state.emailErrorEnabled.value)
    }

    suspend fun RunEnterPasswordScenario(value: String = "1d24sD#$14") {
        var password = ""
        value.forEach { c ->
            delay(100)
            password += c
            screen.ChangePasswordUseCase(password)
        }
        delay(300)
        repeat(2) {
            delay(200)
            password = password.dropLast(1)
            screen.ChangePasswordUseCase(password)
        }
        delay(1000)
        checkState(screen.state.loginButtonEnabled.value)
    }

    suspend fun RunLoginFailedScenario() {
        RunCorrectEmailScenario("failed@ss.ss")
        RunEnterPasswordScenario("failed")
        screen.ClickToLoginUseCase()
        checkState(screen.state.login.loadingFailed.value)
    }

    suspend fun RunLoginSuccessScenario() {
        RunCorrectEmailScenario("ss@ss.ss")
        RunEnterPasswordScenario("12345678")
        screen.ClickToLoginUseCase()
        checkState(!screen.state.login.loadingFailed.value)
    }
}
