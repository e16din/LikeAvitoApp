package me.likeavitoapp.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
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


@Composable
fun AuthScreenView(
    viewModel: AuthViewModel = viewModel()
) {
    val emailState = viewModel.uiState.emailFlow.collectAsState()
    val passwordState = viewModel.uiState.passwordFlow.collectAsState()
    val emailErrorEnabledState = viewModel.uiState.emailErrorEnabledFlow.collectAsState()
    val loginButtonEnabledState = viewModel.uiState.loginButtonEnabledFlow.collectAsState()
    val loginLoadingEnabledState = viewModel.uiState.loginLoadingEnabledFlow.collectAsState()
    val loginErrorMessageState = viewModel.uiState.loginErrorMessageFlow.collectAsState()

    val localFocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.autorization_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = emailState.value,
            onValueChange = { value ->
                viewModel.onEmailChanged(value)
            },
            label = { Text(stringResource(R.string.email_field)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    localFocusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailErrorEnabledState.value,
            supportingText = {
                if (emailErrorEnabledState.value) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.email_error_text),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (emailErrorEnabledState.value) {
                    Icon(Icons.Default.Info, "error", tint = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = passwordState.value,
            onValueChange = { value ->
                viewModel.onPasswordChanged(value)
            },
            label = { Text(stringResource(R.string.password_field)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onLoginClick()
            },
            modifier = Modifier.width(200.dp),
            enabled = loginButtonEnabledState.value
        ) {
            if (loginLoadingEnabledState.value) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.login_button))
            }
        }
    }

    val context = LocalContext.current
    val errorMessage = stringResource(R.string.authorization_failed)
    LaunchedEffect(loginErrorMessageState) {
        if (loginLoadingEnabledState.value) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AuthScreenView(
        viewModel = AuthViewModel(),
    )
}