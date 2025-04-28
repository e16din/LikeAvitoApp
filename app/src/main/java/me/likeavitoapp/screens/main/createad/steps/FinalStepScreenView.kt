package me.likeavitoapp.screens.main.createad.steps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.likeavitoapp.R
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.CheckBoxLabel
import me.likeavitoapp.screens.OutlinedCardLabel


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
    val premiumFocusRequester = remember { FocusRequester() }
    val autoupdateFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        premiumFocusRequester.requestFocus()
    }

    Column(modifier) {
        OutlinedCardLabel(
            label = stringResource(R.string.premium_label),
            focusRequester = premiumFocusRequester
        ) {
            Text(
                stringResource(R.string.about_premium_label),
                Modifier.padding(start = 16.dp, top = 16.dp)
            )
            val isPremium1 by screen.state.isPremiumEnabled.collectAsState()
            CheckBoxLabel(
                label = stringResource(R.string.premium_checkbox),
                checked = isPremium1
            ) { checked ->
                screen.ChangeIsPremiumUseCase(checked)
                premiumFocusRequester.requestFocus()
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCardLabel(
            label = stringResource(R.string.autoupdate_card_label),
            focusRequester = autoupdateFocusRequester
        ) {
            Text(
                stringResource(R.string.autoupdate_label),
                Modifier.padding(start = 16.dp, top = 16.dp)
            )
            val isAutoupdateEnabled1 by screen.state.isAutoupdateEnabled.collectAsState()
            CheckBoxLabel(
                label = stringResource(R.string.autoupdate_checkbox),
                checked = isAutoupdateEnabled1
            ) { checked ->
                screen.ChangeIsAutoupdateUseCase(checked)
                autoupdateFocusRequester.requestFocus()
            }
        }

        Spacer(Modifier.height(21.dp))

        val price by screen.state.price.collectAsState()
        val priceText = if (price == 0) stringResource(R.string.free_label) else "$price"
        Text(
            text = stringResource(R.string.price_with_value_label, priceText),
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.size(24.dp))

        Button(
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