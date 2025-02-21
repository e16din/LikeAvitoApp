package me.likeavitoapp.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import me.likeavitoapp.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
inline fun ActionTopBar(
    title:String,
    withDoneButton:Boolean = true,
    crossinline onClose: () -> Unit,
    crossinline onDone: () -> Unit,
    crossinline content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onClose()

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close"
                        )
                    }
                },
                actions = {
                    if(withDoneButton) {
                        IconButton(onClick = {
                            onDone()

                        }) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        content.invoke(innerPadding)
    }
}