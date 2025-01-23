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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.likeavitoapp.R
import me.likeavitoapp.Toast


@ExperimentalLayoutApi
@Composable
fun AuthScreenView(
    viewModel: AuthViewModel = viewModel()
) = with(viewModel.userDataSource) {
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
                value = state.email,
                onValueChange = { value -> input.onEmail(value) },
                label = { Text(stringResource(R.string.email_field)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        localFocusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.emailErrorEnabled,
                supportingText = {
                    if (state.emailErrorEnabled) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.email_error_text),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (state.emailErrorEnabled) {
                        Icon(Icons.Default.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = state.password,
                onValueChange = { value -> input.onPassword(value) },
                label = { Text(stringResource(R.string.password_field)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { input.onLogin() },
                modifier = Modifier.width(200.dp),
                enabled = state.loginButtonEnabled
            ) {
                if (state.loginLoadingEnabled) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = stringResource(R.string.login_button))
                }
            }
        }

        AnimatedVisibility(state.loginErrorMessage) {
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
    AuthScreenView(
        viewModel = AuthViewModel()
    )
}