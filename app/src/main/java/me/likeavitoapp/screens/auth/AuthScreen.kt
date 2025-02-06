package me.likeavitoapp.screens.auth

import kotlinx.coroutines.CoroutineScope
import me.likeavitoapp.Debouncer
import me.likeavitoapp.inverse
import me.likeavitoapp.launchWithHandler
import me.likeavitoapp.model.DataSources
import me.likeavitoapp.model.BaseScreen
import me.likeavitoapp.model.Loadable
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.provideCoroutineScope
import me.likeavitoapp.provideDataSources
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.MainScreen
import java.util.regex.Pattern
import kotlin.String


class AuthScreen(
    val parentNavigator: ScreensNavigator,
    val scope: CoroutineScope = provideCoroutineScope(),
    val sources: DataSources = provideDataSources()
) : BaseScreen() {

    class State {
        val email = UpdatableState("")
        val password = UpdatableState("")

        val emailErrorEnabled = UpdatableState(false)
        val loginButtonEnabled = UpdatableState(false)

        val login = Loadable(Unit)
    }

    val state = State()


    var emailDebouncer: Debouncer<String>? = null

    fun StartScreenUseCase() {
        recordScenarioStep()

        emailDebouncer = Debouncer<String>("") { lastEmail ->
            scope.launchWithHandler {
                var isEmailValid = false
                if (lastEmail.isNotBlank()) {
                    fun checkEmail(email: String): Boolean {
                        val checkEmailPattern = Pattern.compile(
                            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
                        )

                        return checkEmailPattern.matcher(email).matches()
                    }

                    isEmailValid = checkEmail(lastEmail)
                    state.emailErrorEnabled.inverse()

                } else {
                    isEmailValid = true
                    state.emailErrorEnabled.post(false)
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

        scope.launchWithHandler {
            state.password.post(newPassword)
            val isEmailValid = !state.emailErrorEnabled.value
            state.loginButtonEnabled.post(
                state.email.value.isNotBlank() == true && newPassword.isNotBlank() && isEmailValid
            )
        }
    }

    fun ClickToLoginUseCase() {
        recordScenarioStep()

        scope.launchWithHandler {
            state.loginButtonEnabled.post(false)
            state.login.loading.post(true)
            val result = sources.backend.userService.login(state.email.value, state.password.value)
            val loginData = result.getOrNull()
            if (loginData?.user != null) {
                sources.app.user = loginData.user

                sources.platform.appDataStore.saveId(loginData.user.id)

                parentNavigator.startScreen(MainScreen())

            } else {
                state.login.loading.post(false)
                state.login.loadingFailed.post(true)
            }
        }
    }
}