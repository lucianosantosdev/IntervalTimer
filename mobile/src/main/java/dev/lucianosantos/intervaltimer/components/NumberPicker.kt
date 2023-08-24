package dev.lucianosantos.intervaltimer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds

@Composable
fun NumberPicker(
    value: Int,
    type: PickerType = PickerType.NUMBER,
    onValueChange: (Int) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            onValueChange(value - 1)
        }) {
            Text(
                text = "-",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))

        if(type == PickerType.TIME) {
            TimerNumberPicker(
                value = value,
                onValueChange = onValueChange,
            )
        } else {
            NumberSlider(
                value= value,
                onValueChange = onValueChange
            )
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = {
            onValueChange(value + 1)
        }) {
            Text(
                text = "+",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimerNumberPicker(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NumberSlider(
            value = getMinutesFromSeconds(value),
            onValueChange = {
                val newMinutes = it
                val seconds = getSecondsFromSeconds(value)
                onValueChange(getSecondsFromMinutesAndSeconds(newMinutes, seconds))
            },
            padStart = 2,
            itemCount = 59
        )
        Text(text = " : ", fontSize = 32.sp)
        NumberSlider(
            value = getSecondsFromSeconds(value),
            onValueChange = {
                val minutes = getMinutesFromSeconds(value)
                val newSeconds = it
                onValueChange(getSecondsFromMinutesAndSeconds(minutes, newSeconds))
            },
            padStart = 2,
            itemCount = 59
        )

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberSlider(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    padStart: Int = 0,
    itemCount: Int = 100,
) {
    val list = List(itemCount + 3) { it }

    val visibleColumns = 3
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = value,
        initialFirstVisibleItemScrollOffset = 100
    )

    val isScrolling by remember {
        derivedStateOf {
            lazyListState.isScrollInProgress
        }
    }
    val currentIndex = lazyListState.firstVisibleItemIndex + visibleColumns / 2


    LaunchedEffect(isScrolling) {
        val index = lazyListState.firstVisibleItemIndex
        if (!isScrolling) {
            onValueChange(list[index])
        }
    }

    LaunchedEffect(value) {
        lazyListState.scrollToItem(
            value,
            0
        )
    }

    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
    val elementSize = 40.dp


    LazyColumn(
        modifier = modifier
            .height(elementSize * visibleColumns)
            .focusable(),
        state = lazyListState,
        flingBehavior = snapBehavior,

    ) {
        itemsIndexed(
            list
        ) {i, item ->
            Box(
                modifier = Modifier.height(elementSize),
                contentAlignment = Alignment.Center
            ) {
                val text = if(i == 0 || i == itemCount + 2) "" else (item - 1).toString().padStart(padStart, '0')

                Text(
                    modifier = Modifier.alpha(
                        when {
                            i == currentIndex -> 1F
                            isScrolling -> 0.5F
                            else -> 0F
                        }
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 32.sp,
                    ),
                    color = when {
                        !isScrolling -> MaterialTheme.colorScheme.onBackground
                        i == currentIndex -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.error
                    },
                    text = text
                )

            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun NumberSliderPreview() {
    Surface {
        NumberPicker(
            value = 70,
            type = PickerType.TIME,
            onValueChange = {}
        )
    }
}

