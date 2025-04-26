package me.likeavitoapp.screens.main.createad

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect


@Composable
fun CreateAdScreenProvider(screen: CreateAdStepsScreen) {
    LaunchedEffect(Unit) {
        screen.StartScreenUseCase()
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}