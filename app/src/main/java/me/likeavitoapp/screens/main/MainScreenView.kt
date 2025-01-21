package me.likeavitoapp.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import me.likeavitoapp.Ad
import me.likeavitoapp.Contacts

import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun MainScreenView(viewModel:MainViewModel = viewModel()) {
    val adsState = viewModel.uiState.adsState.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(adsState.value.size) { index ->
            val item = adsState.value[index]
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LikeAvitoAppTheme {
        MainScreenView()
    }
}