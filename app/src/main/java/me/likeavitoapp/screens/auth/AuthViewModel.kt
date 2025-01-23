package me.likeavitoapp.screens.auth

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import me.likeavitoapp.DataSources
import me.likeavitoapp.MainScreen
import me.likeavitoapp.UserDataSource
import me.likeavitoapp.exceptionHandler
import java.util.regex.Pattern


class AuthViewModel : ViewModel() {
    val userDataSource = AuthDataSource()

    private val sources = DataSources(userDataSource)

    private val changeEmailUserCase = ChangeEmailUseCase(viewModelScope, sources)
    private val changePasswordUserCase = ChangePasswordUseCase(sources)
    private val loginUserCase = LoginUseCase(viewModelScope, sources)
    private val hideLoginErrorUserCase = HideLoginErrorUseCase(sources)

    init {
        with(sources.user.input) {
            onEmail = { email ->
                changeEmailUserCase.runWith(email)
            }

            onPassword = { password ->
                changePasswordUserCase.runWith(password)
            }

            onLogin = {
                loginUserCase.run()
            }

            onErrorToastClick = {
                hideLoginErrorUserCase.run()
            }
        }
    }
}

class AuthDataSource(
    val input: Input = Input(),
    val state: State = State()
) : UserDataSource {
    class Input(
        var onEmail: (email: String) -> Unit = {},
        var onPassword: (password: String) -> Unit = {},
        var onLogin: () -> Unit = {},
        var onErrorToastClick: () -> Unit = {}
    )

    // ADVICE: set states only from use cases
    class State {
        var email by mutableStateOf("")
        var password by mutableStateOf("")

        var emailErrorEnabled by mutableStateOf(false)
        var loginButtonEnabled by mutableStateOf(false)
        var loginLoadingEnabled by mutableStateOf(false)

        var loginErrorMessage by mutableStateOf(false)
    }
}

class HideLoginErrorUseCase(
    val sources: DataSources<AuthDataSource>
) {
    fun run() = with(sources.user) {
        if (state.loginErrorMessage) {
            state.loginErrorMessage = false
        }
    }
}

class ChangeEmailUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<AuthDataSource>
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

    fun runWith(newEmail: String) = with(sources.user.state) {
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

class ChangePasswordUseCase(val sources: DataSources<AuthDataSource>) {
    fun runWith(newPassword: String) = with(sources.user.state) {
        password = newPassword
        val isEmailValid = !emailErrorEnabled
        loginButtonEnabled = email.isNotBlank()
                && newPassword.isNotBlank()
                && isEmailValid
    }
}

class LoginUseCase(
    val scope: CoroutineScope,
    val sources: DataSources<AuthDataSource>
) {
    fun run() = with(sources.user) {
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

                sources.app.currentScreenFlow.emit(MainScreen())

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