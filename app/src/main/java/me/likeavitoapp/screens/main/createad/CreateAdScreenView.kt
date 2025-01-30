package me.likeavitoapp.screens.main.createad

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable


@Composable
fun CreateAdScreenProvider(screen: CreateAdScreen) {
    BackHandler {
        screen.input.onBackClick = {
            screen.PressBackUseCase()
        }
    }
}

@Composable
fun CreateAdScreenView(screen: CreateAdScreen) {
}