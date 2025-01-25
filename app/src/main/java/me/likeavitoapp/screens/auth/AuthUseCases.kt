package me.likeavitoapp.screens.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.UseCaseResult
import me.likeavitoapp.screens.main.search.SearchScreen
import java.util.regex.Pattern

fun HideLoginErrorUseCase(
    scope: CoroutineScope,
    sources: DataSources<AuthScreen>
): UseCaseResult<AuthScreen> = with(sources.screen) {
    if (state.login.loadingFailed) {
        state.login.loadingFailed = false
    }

    return@with UseCaseResult(sources, scope)
}


fun ChangeEmailUseCase(
    scope: CoroutineScope,
    sources: DataSources<AuthScreen>,
    newEmail: String,
    justUpdate: Boolean = false
): UseCaseResult<AuthScreen> = with(sources.screen.state) {

    email = newEmail

    if (justUpdate) {
        return@with UseCaseResult(sources, scope)
    }

    var isEmailValid = false
    if (newEmail.isNotBlank()) {
        fun checkEmail(email: String): Boolean {
            val checkEmailPattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
            )

            return checkEmailPattern
                .matcher(email)
                .matches()
        }

        isEmailValid = checkEmail(newEmail)
        emailErrorEnabled = !isEmailValid

    } else {
        isEmailValid = true
        emailErrorEnabled = false
    }

    loginButtonEnabled = newEmail.isNotBlank()
            && password.isNotBlank()
            && isEmailValid


    return@with UseCaseResult(sources, scope)
}

fun ChangePasswordUseCase(
    scope: CoroutineScope,
    sources: DataSources<AuthScreen>,
    newPassword: String
): UseCaseResult<AuthScreen> = with(sources.screen.state) {
    password = newPassword
    val isEmailValid = !emailErrorEnabled
    loginButtonEnabled = email.isNotBlank()
            && newPassword.isNotBlank()
            && isEmailValid

    return@with UseCaseResult(sources, scope)
}

fun LoginUseCase(
    scope: CoroutineScope,
    sources: DataSources<AuthScreen>
): UseCaseResult<AuthScreen> = with(sources.screen) {
    state.loginButtonEnabled = false
    state.login.loading = true

    val job = scope.launch {
        val result = sources.backend.userService.login(state.email, state.password)
        val loginData = result.getOrNull()
        if (loginData?.user != null) {
            sources.app.user = loginData.user

            sources.platform.authDataStore.saveId(loginData.user.id)

            sources.app.currentScreenFlow.emit(SearchScreen())

        } else {
            state.login.loadingFailed = true
        }
    }

    return@with UseCaseResult(sources, scope, job)
}