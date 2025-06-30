package me.likeavitoapp.screens.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.developer.primitives.Debouncer
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
                    state.emailErrorEnabled.next(false)
                    isEmailValid = true
                }

                state.loginButtonEnabled.next(
                    lastEmail.isNotBlank() && state.password.value.isNotBlank() && isEmailValid
                )
            }
        }
    }

    fun ChangeEmailUseCase(newEmail: String) {
        recordScenarioStep()

        state.email.next(newEmail)
    }

    fun ChangePasswordUseCase(newPassword: String) {
        recordScenarioStep()

        state.password.next(newPassword)
        val isEmailValid = !state.emailErrorEnabled.value
        state.loginButtonEnabled.next(
            state.email.value.isNotBlank()
                    && newPassword.isNotBlank()
                    && isEmailValid
        )
    }

    fun ClickToLoginUseCase() {
        recordScenarioStep()

        state.loginButtonEnabled.next(false)
        state.login.working.next(true)
        get.scope().launchWithHandler {
            val result =
                get.sources().backend.userService.login(state.email.value, state.password.value)
            val loginData = result.getOrNull()
            if (loginData?.user != null) {
                get.sources().platform.appDataStore.saveUserId(loginData.user.id)

                withContext(Dispatchers.Main) {
                    get.sources().app.user.next(loginData.user)

                    val mainScreen = MainScreen()
                    get.sources().app.mainScreen = mainScreen
                    navigator.startScreen(mainScreen,)
                }

            } else {
                withContext(Dispatchers.Main) {
                    state.login.working.next(false)
                    state.login.fail.next(true)
                }
            }
        }
    }
}