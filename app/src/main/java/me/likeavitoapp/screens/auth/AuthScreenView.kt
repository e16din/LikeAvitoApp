package me.likeavitoapp.screens.auth

import android.widget.Toast
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.likeavitoapp.R
import me.likeavitoapp.actualScope


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreenProvider(screen: AuthScreen) {
    AuthScreenView(screen)
}

@Composable
fun <T> StateFlow<T>.collectAsStateSavable(): State<T> {
    val state = rememberSaveable { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        collect {
            state.value = it
        }
    }
    return state
}

@ExperimentalLayoutApi
@Composable
fun AuthScreenView(screen: AuthScreen) {
    val scope = actualScope()
    val localFocusManager = LocalFocusManager.current

    val email = screen.state.email.collectAsStateSavable()
    val password = screen.state.password.collectAsState()
    val emailErrorEnabled = screen.state.emailErrorEnabled.collectAsState()
    val loginButtonEnabled = screen.state.loginButtonEnabled.collectAsState()
    val loginLoading = screen.state.login.loading.collectAsState()
    val loginLoadingFailed = screen.state.login.loadingFailed.collectAsState()

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
                value = email.value,
                onValueChange = { value ->
                    scope.launch {
                        screen.ChangeEmail(value)
                    }
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
                isError = emailErrorEnabled.value,
                supportingText = {
                    if (emailErrorEnabled.value) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.email_error_text),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (emailErrorEnabled.value) {
                        Icon(
                            Icons.Default.Info,
                            "error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password.value,
                onValueChange = { value -> screen.ChangePassword(value) },
                label = { Text(stringResource(R.string.password_field)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        screen.Login()
                    }
                },
                modifier = Modifier.width(200.dp),
                enabled = loginButtonEnabled.value
            ) {
                if (loginLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = stringResource(R.string.login_button))
                }
            }
        }
    }

    val context = LocalContext.current
    val errorMessage = stringResource(R.string.authorization_failed)
    LaunchedEffect(loginLoadingFailed.value) {
        if (loginLoadingFailed.value) {
            screen.state.login.loadingFailed.value = false
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AuthScreenView(screen = AuthScreen())
}