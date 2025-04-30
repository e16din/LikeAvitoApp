package me.likeavitoapp.screens.main.createad.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.CheckBoxLabel
import me.likeavitoapp.screens.OutlinedCardLabel


@Composable
fun DeliveryStepScreenView(screen: DeliveryStepScreen, modifier: Modifier) = with(screen) {
    val types by screen.state.types.collectAsState()
    val selectedTypes = screen.state.selectedTypes

    val localFocusManager = LocalFocusManager.current
    val typesFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        typesFocusRequester.requestFocus()
    }

    Column(modifier) {

        OutlinedCardLabel(
            label = stringResource(R.string.possible_pickup_points_arg),
            focusRequester = typesFocusRequester
        ) {
                Column(
                    Modifier.padding(10.dp)
                ) {
                    types.forEach { type ->
                        CheckBoxLabel(
                            label = type.name,
                            checked = selectedTypes.contains(type.id)
                        ) { checked ->
                            screen.ChangeEnabledPointsType(type.id, checked)
                            typesFocusRequester.requestFocus()
                        }
                    }
                }
        }

        Spacer(Modifier.height(12.dp))

        val address by screen.state.address.collectAsState()
        val ownerAddressId = 0
        OutlinedTextField(
            value = address,
            enabled = selectedTypes.contains(ownerAddressId),
            onValueChange = { value ->
                screen.ChangeAddressUseCase(value)
            },
            label = {
                Text(stringResource(R.string.address_arg))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            minLines = 1,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus()
                }
            )
        )

        Spacer(Modifier.size(24.dp))
    }
}