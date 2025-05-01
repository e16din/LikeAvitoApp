package me.likeavitoapp.screens.main.createad

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import me.likeavitoapp.screens.main.createad.steps.CategoryStepScreen
import me.likeavitoapp.screens.main.createad.steps.CategoryStepScreenView
import me.likeavitoapp.screens.main.createad.steps.DeliveryStepScreen
import me.likeavitoapp.screens.main.createad.steps.DeliveryStepScreenView
import me.likeavitoapp.screens.main.createad.steps.DescriptionStepScreen
import me.likeavitoapp.screens.main.createad.steps.DescriptionStepScreenView
import me.likeavitoapp.screens.main.createad.steps.FinalStepScreen
import me.likeavitoapp.screens.main.createad.steps.FinalStepScreenView


@Composable
fun CreateAdScreenProvider(screen: CreateAdScreen) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val doneEnabled by screen.state.doneEnabled.collectAsState()
            ActionTopBar(
                title = stringResource(R.string.add_new_ad_title),
                onDone = {
                    screen.ClickToDoneUseCase()
                },
                onClose = {
                    screen.PressBackUseCase()
                },
                withDoneButton = doneEnabled
            ) { innerPadding ->
                CreateAdScreenView(screen, Modifier.padding(innerPadding))
            }
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateAdScreenView(screen: CreateAdScreen, modifier: Modifier) = with(screen) {
    val steps by screen.state.steps.collectAsState()
    val activeStep by screen.state.activeStep.collectAsState()
    val stepScreen by screen.stepsNavigator.screen.collectAsState()
    val errorStepIndex by screen.state.errorStepIndex.collectAsState()
    val doneEnabled by screen.state.doneEnabled.collectAsState()

    Column(modifier.imePadding()) {
        LazyColumn {
            var activeIndex = 0
            itemsIndexed(steps) { i, step ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(
                            if (errorStepIndex == i) 2.dp else 0.dp,
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(2.dp)
                        )
                ) {
                    if (step == activeStep) {
                        Column {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.inverseSurface
                                    )
                                    .clickable {
                                        screen.ClickToStepUseCase(step)
                                    }
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.inverseSurface,
                                        RoundedCornerShape(
                                            topStart = 2.dp,
                                            topEnd = 2.dp,
                                            bottomEnd = 0.dp,
                                            bottomStart = 0.dp
                                        )
                                    )

                            ) {

                                Text(
                                    step.label, color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f)
                                )
                            }

                            activeIndex = i
                            when (step) {
                                CreateAdStep.Description ->
                                    DescriptionStepScreenView(
                                        stepScreen as DescriptionStepScreen,
                                        Modifier
                                    )

                                CreateAdStep.Category ->
                                    CategoryStepScreenView(
                                        stepScreen as CategoryStepScreen,
                                        Modifier
                                    )

                                CreateAdStep.PickupPoints ->
                                    DeliveryStepScreenView(
                                        stepScreen as DeliveryStepScreen,
                                        Modifier
                                    )

                                CreateAdStep.Additions ->
                                    FinalStepScreenView(stepScreen as FinalStepScreen, Modifier)
                            }
                        }

                    } else {
                        val isNotNext = i - 1 != activeIndex
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isNotNext)
                                        MaterialTheme.colorScheme.background
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                                .clickable {
                                    screen.ClickToStepUseCase(step)
                                }
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(2.dp)
                                )

                        ) {
                            val tintColor = if (isNotNext)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onPrimary
                            Text(
                                step.label, color = tintColor,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f)
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight, "arrow",
                                tint = tintColor,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
            
            item {
                AnimatedVisibility(doneEnabled) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Button(
                            onClick = {
                                screen.ClickToCreateAdUseCase()
                            },
                            Modifier.align(Alignment.Center)
                        ) {
                            Text(stringResource(R.string.create_ad_button))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}