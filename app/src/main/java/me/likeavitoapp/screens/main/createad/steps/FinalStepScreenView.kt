package me.likeavitoapp.screens.main.createad.steps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.ActionTopBar


@Composable
fun FinalStepScreenProvider(screen: FinalStepScreen) {
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
                FinalStepScreenView(screen, Modifier.padding(innerPadding))
            }
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FinalStepScreenView(screen: FinalStepScreen, modifier: Modifier) = with(screen) {
    Column(modifier) {
        Text(stringResource(R.string.about_premium_label))
        val isPremium by screen.state.isPremiumEnabled.collectAsState()
        Row(
            Modifier
                .clickable {
                    screen.ClickToIsPremiumUseCase()
                }
                .padding(top = 4.dp)
        ) {
            Checkbox(
                checked = isPremium,
                onCheckedChange = {
                    screen.ChangeIsPremiumUseCase(it)
                }
            )
            Text(
                stringResource(R.string.bargaining_enabled_checkbox),
                Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))

        Text(stringResource(R.string.autoupdate_label))
        val isDeliveryEnabled by screen.state.isAutoupdateEnabled.collectAsState()
        Row(
            Modifier
                .clickable {
                    screen.ClickToIsAutoupdateUseCase()
                }
                .padding(top = 4.dp)
        ) {
            Checkbox(
                checked = isDeliveryEnabled,
                onCheckedChange = {
                    screen.ChangeIsAutoupdateUseCase(it)
                }
            )
            Text(
                stringResource(R.string.autoupdate_checkbox),
                Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(Modifier.height(4.dp))

        val price by screen.state.price.collectAsState()
        val priceText = if(price == 0) stringResource(R.string.free_label) else "$price"
        Text(stringResource(R.string.price_with_value_label, priceText))


        Spacer(Modifier.size(8.dp))

        OutlinedButton(
            onClick = {
                screen.ClickToCreateAdUseCase()
            },
            Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.create_ad_button))
        }
    }

}