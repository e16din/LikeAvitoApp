package me.likeavitoapp.screens.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.screens.main.MainScreen
import java.util.regex.Pattern


class AuthScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : IScreen {

    class State {
        val email = MutableStateFlow("")
        val password = MutableStateFlow("")

        val emailErrorEnabled = MutableStateFlow(false)
        val loginButtonEnabled = MutableStateFlow(false)

        val login = Loadable(emptyList<Unit>())
    }

    val state = State()

    init {
        scope.launchWithHandler {
            state.email.debounce(390).collect { lastEmail ->
                var isEmailValid = false
                if (lastEmail.isNotBlank()) {
                    fun checkEmail(email: String): Boolean {
                        val checkEmailPattern = Pattern.compile(
                            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
                        )

                        return checkEmailPattern.matcher(email).matches()
                    }

                    isEmailValid = checkEmail(lastEmail)
                    state.emailErrorEnabled.value = !isEmailValid

                } else {
                    isEmailValid = true
                    state.emailErrorEnabled.value = false
                }

                state.loginButtonEnabled.value =
                    lastEmail.isNotBlank() && state.password.value.isNotBlank() && isEmailValid
            }
        }
    }

    fun ChangeEmailUseCase(newEmail: String) {
        state.email.value = newEmail
    }

    fun ChangePasswordUseCase(newPassword: String) {
        state.password.value = newPassword
        val isEmailValid = !state.emailErrorEnabled.value
        state.loginButtonEnabled.value =
            state.email.value.isNotBlank() == true && newPassword.isNotBlank() && isEmailValid
    }

    fun LoginUseCase() {
        state.loginButtonEnabled.value = false
        state.login.loading.value = true

        scope.launchWithHandler {
            val result = sources.backend.userService.login(state.email.value, state.password.value)
            val loginData = result.getOrNull()
            if (loginData?.user != null) {
                sources.app.user = loginData.user

                sources.platform.appDataStore.saveId(loginData.user.id)

                parentNavigator.startScreen(MainScreen())

            } else {
                state.login.loading.value = false
                state.login.loadingFailed.value = true
            }
        }
    }
}