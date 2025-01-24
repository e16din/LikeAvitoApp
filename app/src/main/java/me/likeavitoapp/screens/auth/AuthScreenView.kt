package me.likeavitoapp.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.DataSources
import me.likeavitoapp.R
import me.likeavitoapp.Toast


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreenProvider() {
    val scope = rememberCoroutineScope()

    val sources = DataSources<AuthScreen>()

    val changeEmailUserCase = ChangeEmailUseCase(scope, sources)
    val changePasswordUserCase = ChangePasswordUseCase(sources)
    val loginUserCase = LoginUseCase(scope, sources)
    val hideLoginErrorUserCase = HideLoginErrorUseCase(sources)

    AuthScreenView(sources.screen)

    LaunchedEffect(Unit) {
        with(sources.screen.input) {
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

@ExperimentalLayoutApi
@Composable
fun AuthScreenView(screen: AuthScreen) {
    val localFocusManager = LocalFocusManager.current
    val errorMessage = stringResource(R.string.authorization_failed)

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .imePadding()
                .imeNestedScroll(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.autorization_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = screen.state.email,
                onValueChange = { value -> screen.input.onEmail(value) },
                label = { Text(stringResource(R.string.email_field)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        localFocusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = screen.state.emailErrorEnabled,
                supportingText = {
                    if (screen.state.emailErrorEnabled) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.email_error_text),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (screen.state.emailErrorEnabled) {
                        Icon(Icons.Default.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = screen.state.password,
                onValueChange = { value -> screen.input.onPassword(value) },
                label = { Text(stringResource(R.string.password_field)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { screen.input.onLogin() },
                modifier = Modifier.width(200.dp),
                enabled = screen.state.loginButtonEnabled
            ) {
                if (screen.state.loginLoadingEnabled) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = stringResource(R.string.login_button))
                }
            }
        }

        AnimatedVisibility(screen.state.loginErrorMessage) {
            Toast(
                message = errorMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AuthScreenView(screen = AuthScreen())
}