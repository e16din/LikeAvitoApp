package me.likeavitoapp.screens.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.exceptionHandler
import me.likeavitoapp.screens.main.search.SearchScreen
import java.util.regex.Pattern

class HideLoginErrorUseCase(
    val sources: DataSources<AuthScreen>
) {
    fun run() = with(sources.screen) {
        if (state.loginErrorMessage) {
            state.loginErrorMessage = false
        }
    }
}

class ChangeEmailUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<AuthScreen>
) {
    private var emailFlow: MutableStateFlow<String>? = null

    private val checkEmailPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun checkEmail(email: String): Boolean {
        return checkEmailPattern
            .matcher(email)
            .matches()
    }

    fun runWith(newEmail: String) = with(sources.screen.state) {
        if (newEmail != email) {
            email = newEmail
        }
        if (emailFlow == null) {
            scope.launch(exceptionHandler) {
                emailFlow = MutableStateFlow(newEmail)
                emailFlow
                    ?.debounce(390)
                    ?.collect { lastEmail ->
                        var isEmailValid = false
                        if (lastEmail.isNotBlank()) {
                            isEmailValid = checkEmail(lastEmail)
                            emailErrorEnabled = !isEmailValid
                        } else {
                            isEmailValid = true
                            emailErrorEnabled = false
                        }

                        loginButtonEnabled = lastEmail.isNotBlank()
                                && password.isNotBlank()
                                && isEmailValid
                    }
            }

        } else {
            emailFlow?.tryEmit(newEmail)
        }
    }
}

class ChangePasswordUseCase(val sources: DataSources<AuthScreen>) {
    fun runWith(newPassword: String) = with(sources.screen.state) {
        password = newPassword
        val isEmailValid = !emailErrorEnabled
        loginButtonEnabled = email.isNotBlank()
                && newPassword.isNotBlank()
                && isEmailValid
    }
}

class LoginUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<AuthScreen>
) {
    fun run() = with(sources.screen) {
        state.loginButtonEnabled = false
        state.loginLoadingEnabled = true

        scope.launch(exceptionHandler) {
            val result = sources.backend.userService.login(state.email, state.password)
            val newUser = result.getOrNull()
            if (newUser?.id != null) {
                sources.app.user.apply {
                    id = newUser.id
                    name = newUser.name
                    contacts = newUser.contacts
                    ownAds = newUser.ownAds
                }
                sources.platform.userIdStore.save(newUser.id!!) // NOTE: please throw NPE if it is null

                sources.app.currentScreenFlow.emit(SearchScreen())

            } else {
                state.loginErrorMessage = true
                delay(2000)
                if (state.loginErrorMessage) {
                    state.loginErrorMessage = false
                }
            }
        }
    }
}