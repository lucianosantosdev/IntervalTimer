package dev.lucianosantos.intervaltimer.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.minutes

enum class NumberPickerType {
    NUMBER,
    TIME,
}

@Composable
fun NumberPicker(
    value: Int,
    type: NumberPickerType = NumberPickerType.NUMBER,
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
            Text(text = "-")
        }

        if(type == NumberPickerType.TIME) {
            TimerNumberPicker(
                value = value,
                onValueChange = onValueChange
            )
        } else {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Button(onClick = {
            onValueChange(value + 1)
        }) {
            Text(text = "+")
        }
    }
}

@Composable
fun TimerNumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val minutes = getMinutesFromSeconds(value)
    val seconds = getSecondsFromSeconds(value)

    Text(text = minutes.toString())
    Text(text = seconds.toString())
    NumberSlider(
        value = getMinutesFromSeconds(value),
        onValueChange = {
            onValueChange(getSecondsFromSeconds(it))
        }
    )
    Text(text = ":", fontSize = 32.sp)
    NumberSlider(
        value = getSecondsFromSeconds(value),
        onValueChange = {
            onValueChange(getSecondsFromSeconds(it))
        }
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberSlider(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val itemCount = 1000
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = (itemCount / 2 ) - (itemCount/2 % 60) + (value),
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
    val elementSize = 50.dp

    val list = List(itemCount) { it }

    var isFocused by remember { mutableStateOf(false) }
    val visibleColumns = if (isFocused) 3 else 1

    LazyColumn(
        modifier = Modifier
            .height(elementSize * visibleColumns)
            .onFocusChanged {
                isFocused = it.isFocused
            }
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
                val text = (item % 60).toString().padStart(2, '0')
                val fontSize = (elementSize.value / text.length).coerceIn(1f, 100f)
                val currentIndex = lazyListState.firstVisibleItemIndex + visibleColumns / 2

                Text(
                    modifier = Modifier.alpha(
                        if (i == currentIndex) {
                            1F
                        } else {
                            0.5F
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = fontSize.sp,
                    ),
                    color = when {
                        !isFocused -> MaterialTheme.colorScheme.onBackground
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
            type = NumberPickerType.TIME,
            onValueChange = {}
        )
    }
}

