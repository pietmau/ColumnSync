package com.pietrantuono.columnsync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.getValue
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
                SyncedColumn()
            }
        }
    }
}

@Composable
private fun SyncedColumn() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val stateRowOne = rememberLazyListState()
        val stateRowTwo = rememberLazyListState()

        val key1 by remember {
            derivedStateOf {
                stateRowOne.firstVisibleItemScrollOffset
            }
        }
        LaunchedEffect(key1) {
            stateRowTwo.scrollToItem(
                stateRowOne.firstVisibleItemIndex,
                stateRowOne.firstVisibleItemScrollOffset,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = stateRowOne,
        ) {
            items(100) {
                Text("Item $it")
            }
        }
        Spacer(modifier = Modifier.width(48.dp))

        val key2 by remember {
            derivedStateOf {
                stateRowTwo.firstVisibleItemScrollOffset
            }
        }

        LaunchedEffect(key2) {
            stateRowOne.scrollToItem(
                stateRowTwo.firstVisibleItemIndex,
                stateRowTwo.firstVisibleItemScrollOffset,
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = stateRowTwo,
        ) {
            items(100) {
                Text("Item $it")
            }
        }
    }
}

