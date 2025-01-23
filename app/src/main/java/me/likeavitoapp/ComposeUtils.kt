package me.likeavitoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


@Composable
inline fun Launch(
    key: Any? = Unit,
    crossinline useCase: (scope: CoroutineScope) -> Deferred<Unit>,
    composable: @Composable () -> Unit
) {
    val isDoneFlow = MutableStateFlow(false)
    val isDoneState by isDoneFlow.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key) {
        useCase(scope).await()
        isDoneFlow.emit(true)
    }

    if (isDoneState) {
        composable()
    }
}

@Composable
inline fun OnTextChanged(
    debounceMs: Long = 0,
    crossinline onStart: (value: TextFieldValue) -> Unit = {},
    crossinline content: (value: TextFieldValue) -> Unit
): (value: TextFieldValue) -> Unit {

    val scope = rememberCoroutineScope()
    val valueFlow = remember { MutableStateFlow(TextFieldValue()) }
    var isEdited = remember { false }
    LaunchedEffect(valueFlow) {
        scope.launch {
            valueFlow
                .debounce(timeoutMillis = debounceMs)
                .collect {
                    if (!isEdited && it.text.length > 0) {
                        isEdited = true
                        content.invoke(it)

                    } else if (isEdited && it.text.length > 0) {
                        content.invoke(it)

                    } else if (isEdited && it.text.length == 0) {
                        content.invoke(it)
                        isEdited = false
                    }
                }
        }
    }

    return { value ->
        onStart(value)
        scope.launch {
            valueFlow.emit(value)
        }
    }
}

class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Int
) : NestedScrollConnection {

    var appBarOffset: Int by mutableIntStateOf(0)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y.toInt()
        val newOffset = appBarOffset + delta
        val previousOffset = appBarOffset
        appBarOffset = newOffset.coerceIn(-appBarMaxHeight, 0)
        val consumed = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }
}