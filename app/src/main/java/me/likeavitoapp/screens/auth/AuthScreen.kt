package me.likeavitoapp.screens.auth

import me.likeavitoapp.Debouncer
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.get
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.Worker
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.MainScreen
import java.util.regex.Pattern


class AuthScreen(val navigator: ScreensNavigator) : IScreen {

    class State {
        val email = UpdatableState("")
        val password = UpdatableState("")

        val emailErrorEnabled = UpdatableState(false)
        val loginButtonEnabled = UpdatableState(false)

        val login = Worker(Unit)
    }

    val state = State()


    var emailDebouncer: Debouncer<String>? = null

    fun StartScreenUseCase() {
        recordScenarioStep()

        emailDebouncer = Debouncer<String>("") { lastEmail ->
            get.scope().launchWithHandler {
                var isEmailValid = false
                if (lastEmail.isNotBlank()) {
                    fun checkEmail(email: String): Boolean {
                        val checkEmailPattern = Pattern.compile(
                            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
                        )

                        return checkEmailPattern.matcher(email).matches()
                    }

                    state.emailErrorEnabled.inverse()
                    isEmailValid = checkEmail(lastEmail)

                } else {
                    state.emailErrorEnabled.post(false)
                    isEmailValid = true
                }

                state.loginButtonEnabled.post(
                    lastEmail.isNotBlank() && state.password.value.isNotBlank() && isEmailValid
                )
            }
        }
    }

    fun ChangeEmailUseCase(newEmail: String) {
        recordScenarioStep()

        state.email.post(newEmail)
    }

    fun ChangePasswordUseCase(newPassword: String) {
        recordScenarioStep()

        get.scope().launchWithHandler {
            state.password.post(newPassword)
            val isEmailValid = !state.emailErrorEnabled.value
            state.loginButtonEnabled.post(
                state.email.value.isNotBlank() == true && newPassword.isNotBlank() && isEmailValid
            )
        }
    }

    fun ClickToLoginUseCase() {
        recordScenarioStep()

        get.scope().launchWithHandler {
            state.loginButtonEnabled.post(false)
            state.login.working.post(true)
            val result = get.sources().backend.userService.login(state.email.value, state.password.value)
            val loginData = result.getOrNull()
            if (loginData?.user != null) {
                get.sources().app.user.post(loginData.user)

                get.sources().platform.appDataStore.saveId(loginData.user.id)

                navigator.startScreen(MainScreen())

            } else {
                state.login.working.post(false)
                state.login.fail.post(true)
            }
        }
    }
}