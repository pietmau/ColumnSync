package com.pietrantuono.columnsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pietrantuono.columnsync.ui.theme.ColumnSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColumnSyncTheme {
                SyncColumn()
            }
        }
    }
}

@Composable
private fun SyncColumn() {
    Row(
        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val scroll = rememberLazyListState()
        val offset = rememberCurrentOffset(scroll)
        LazyColumn(
            modifier = Modifier.weight(1f), state = scroll
        ) {
            items(100) {
                Text("Item $it")
            }
        }
        Spacer(modifier = Modifier.width(48.dp))
        val synced = rememberLazyListState()
        LaunchedEffect(offset.value) {
            synced.scrollBy(offset.value.toFloat())
        }
        LazyColumn(
            modifier = Modifier.weight(1f), state = synced
        ) {
            items(100) {
                Text("Item $it")
            }
        }
    }
}


@Composable
fun rememberCurrentOffset(state: LazyListState): State<Int> {
    val position = remember { derivedStateOf { state.firstVisibleItemIndex } }
    val itemOffset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val lastPosition = rememberPrevious(position.value)
    val lastItemOffset = rememberPrevious(itemOffset.value)
    val currentOffset = remember { mutableStateOf(0) }

    LaunchedEffect(position.value, itemOffset.value) {
        if (lastPosition == null || position.value == 0) {
            currentOffset.value = itemOffset.value
        } else if (lastPosition == position.value) {
            currentOffset.value += (itemOffset.value - (lastItemOffset ?: 0))
        } else if (lastPosition > position.value) {
            currentOffset.value -= (lastItemOffset ?: 0)
        } else { // lastPosition.value < position.value
            currentOffset.value += itemOffset.value
        }
    }

    return currentOffset
}

@Composable
fun <T> rememberPrevious(
    current: T,
    shouldUpdate: (prev: T?, curr: T) -> Boolean = { a: T?, b: T -> a != b },
): T? {
    val ref = rememberRef<T>()

    // launched after render, so the current render will have the old value anyway
    SideEffect {
        if (shouldUpdate(ref.value, current)) {
            ref.value = current
        }
    }

    return ref.value
}

@Composable
fun <T> rememberRef(): MutableState<T?> {
    // for some reason it always recreated the value with vararg keys,
    // leaving out the keys as a parameter for remember for now
    return remember() {
        object : MutableState<T?> {
            override var value: T? = null

            override fun component1(): T? = value

            override fun component2(): (T?) -> Unit = { value = it }
        }
    }
}