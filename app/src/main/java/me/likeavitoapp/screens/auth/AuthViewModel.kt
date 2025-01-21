package me.likeavitoapp.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.likeavitoapp.AppModel
import me.likeavitoapp.AppPlatform
import me.likeavitoapp.Backend
import me.likeavitoapp.MainScreen
import java.util.regex.Pattern


class AuthViewModel(
    val uiState: UiState = UiState(),
    val modelState: ModelState = ModelState(),
    val changeEmailUseCase: ChangeEmailUseCase = ChangeEmailUseCase(uiState, modelState),
    val changePasswordUseCase: ChangePasswordUseCase = ChangePasswordUseCase(uiState, modelState),
    val clickLoginUseCase: ClickLoginUseCase = ClickLoginUseCase(uiState)
) : ViewModel() {

    // NOTE: emit only from this class
    class UiState {
        val emailFlow = MutableStateFlow("")
        val passwordFlow = MutableStateFlow("")

        val emailErrorEnabledFlow = MutableStateFlow(false)
        val loginButtonEnabledFlow = MutableStateFlow(false)
        val loginLoadingEnabledFlow = MutableStateFlow(false)

        val loginErrorMessageFlow = MutableStateFlow(false)
    }

    // NOTE: emit only from ui classes
    class ModelState {
        var email = ""
        var password = ""
    }

    fun onEmailChanged(email: String) {
        viewModelScope.launch {
            changeEmailUseCase.runWith(email)
        }
    }

    fun onPasswordChanged(password: String) {
        viewModelScope.launch {
            changePasswordUseCase.runWith(password)
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            clickLoginUseCase.run()
        }
    }
}

class ChangeEmailUseCase(
    val uiState: AuthViewModel.UiState,
    val modelState: AuthViewModel.ModelState,
) {
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

    suspend fun runWith(email: String) {
        modelState.email = email
        uiState.emailFlow.emit(email)

        var isEmailValid = false
        if (email.isNotBlank()) {
            isEmailValid = checkEmail(email)
            uiState.emailErrorEnabledFlow.emit(!isEmailValid)
        } else {
            isEmailValid = true
            uiState.emailErrorEnabledFlow.emit(false)
        }

        uiState.loginButtonEnabledFlow.emit(
            email.isNotBlank()
                    && modelState.password.isNotBlank()
                    && isEmailValid
        )
    }
}

class ChangePasswordUseCase(
    val uiState: AuthViewModel.UiState,
    val modelState: AuthViewModel.ModelState,
) {
    suspend fun runWith(password: String) {
        modelState.password = password
        uiState.passwordFlow.emit(password)

        val isEmailValid = !uiState.emailErrorEnabledFlow.value
        uiState.loginButtonEnabledFlow.emit(
            modelState.email.isNotBlank()
                    && password.isNotBlank()
                    && isEmailValid
        )
    }
}

class ClickLoginUseCase(
    val uiState: AuthViewModel.UiState,
    val app: AppModel = AppModel,
    val backend: Backend = AppPlatform.get.backend
) {
    suspend fun run() {
        uiState.loginButtonEnabledFlow.emit(false)
        uiState.loginLoadingEnabledFlow.emit(true)

        val result = backend.userService.login(
            uiState.emailFlow.value,
            uiState.passwordFlow.value
        )

        if (result.isSuccess) {
            app.screens.emit(MainScreen())
        } else {
            uiState.loginErrorMessageFlow.emit(true)
            uiState.loginErrorMessageFlow.emit(false)
        }
    }
}