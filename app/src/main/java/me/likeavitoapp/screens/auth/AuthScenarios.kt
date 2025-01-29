package me.likeavitoapp.screens.auth

import kotlinx.coroutines.delay
import me.likeavitoapp.checkState
import me.likeavitoapp.dataSourcesWithScreen
import kotlin.math.roundToInt

suspend fun RunAllAuthScenarios() {
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
    val sources = dataSourcesWithScreen<AuthScreen>()
    var email = ""
    "incorrect@email".forEach { c ->
        delay(100)
        email += c
        sources.screen.ChangeEmailUseCase(email)
    }
    delay(1000)
    checkState(sources.screen.state.emailErrorEnabled.value)

    RunClearEmailScenario()
    email = ""
    delay(1000)
    checkState(!sources.screen.state.emailErrorEnabled.value)

    "inc##orrect@em.com".forEach { c ->
        delay(100)
        email += c
        sources.screen.ChangeEmailUseCase(email)
    }
    delay(1000)
    checkState(sources.screen.state.emailErrorEnabled.value)

    RunClearEmailScenario()
    email = ""
    delay(1000)
    checkState(!sources.screen.state.emailErrorEnabled.value)

    "dsflkgsd".forEach { c ->
        delay(100)
        email += c
        sources.screen.ChangeEmailUseCase(email)
    }
    delay(1000)
    checkState(sources.screen.state.emailErrorEnabled.value)
}

suspend fun RunClearEmailScenario() {
    val sources = dataSourcesWithScreen<AuthScreen>()
    while (sources.screen.state.email.value.isNotEmpty()) {
        delay(100)
        sources.screen.ChangeEmailUseCase(sources.screen.state.email.value.dropLast(1))
    }
}

suspend fun RunClearPasswordScenario() {
    val sources = dataSourcesWithScreen<AuthScreen>()
    while (sources.screen.state.password.value.isNotEmpty()) {
        delay(100)
        sources.screen.ChangePasswordUseCase(sources.screen.state.password.value.dropLast(1))
    }
}

suspend fun RunCorrectEmailScenario(value: String = "correct@email.com") {
    val sources = dataSourcesWithScreen<AuthScreen>()
    var email = ""
    value.forEach { c ->
        delay(100)
        email += c
        sources.screen.ChangeEmailUseCase(email)
    }
    delay(1000)
    checkState(!sources.screen.state.emailErrorEnabled.value)
}

suspend fun RunEnterPasswordScenario(value: String = "1d24sD#$14") {
    val sources = dataSourcesWithScreen<AuthScreen>()
    var password = ""
    value.forEach { c ->
        delay(100)
        password += c
        sources.screen.ChangePasswordUseCase(password)
    }
    delay(300)
    repeat(2) {
        delay(200)
        password = password.dropLast(1)
        sources.screen.ChangePasswordUseCase(password)
    }
    delay(1000)
    checkState(sources.screen.state.loginButtonEnabled.value)
}

suspend fun RunLoginFailedScenario() {
    val sources = dataSourcesWithScreen<AuthScreen>()
    RunCorrectEmailScenario("failed@ss.ss")
    RunEnterPasswordScenario("failed")
    sources.screen.LoginUseCase()
    checkState(sources.screen.state.login.loadingFailed.value)
}

suspend fun RunLoginSuccessScenario() {
    val sources = dataSourcesWithScreen<AuthScreen>()
    RunCorrectEmailScenario("ss@ss.ss")
    RunEnterPasswordScenario("12345678")
    sources.screen.LoginUseCase()
    checkState(!sources.screen.state.login.loadingFailed.value)
}