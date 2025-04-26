package me.likeavitoapp.screens.main.createad.steps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.ActionTopBar


@Composable
fun DeliveryStepScreenProvider(screen: DeliveryStepScreen) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ActionTopBar(
                title = stringResource(R.string.add_new_ad_title),
                onDone = {
                    screen.ClickToDoneUseCase()
                },
                onClose = {
                    screen.PressBackUseCase()
                },
                withDoneButton = true
            ) { innerPadding ->
                DeliveryStepScreenView(screen, Modifier.padding(innerPadding))
            }
        }

        BackHandler {
            screen.PressBackUseCase()
        }
    }
}

@Composable
fun DeliveryStepScreenView(screen: DeliveryStepScreen, modifier: Modifier) = with(screen) {
    val types by screen.state.types.collectAsState()
    val selectedTypes = screen.state.selectedTypes

    Column {
        Column {
            types.forEach { type ->
                Row(
                    Modifier.clickable {
                        screen.SelectEnabledPointsType(type.id)
                    }
                ) {
                    Checkbox(
                        checked = selectedTypes.contains(type.id),
                        onCheckedChange = {
                            screen.SelectEnabledPointsType(type.id)
                        }
                    )
                    Text(
                        stringResource(R.string.bargaining_enabled_checkbox),
                        Modifier
                            .padding(horizontal = 8.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }

        val address by screen.state.address.collectAsState()


        Spacer(Modifier.size(24.dp))

        Button(
            onClick = {
                screen.ClickToNextUseCase()
            },
            Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.next_button))
        }
    }
}