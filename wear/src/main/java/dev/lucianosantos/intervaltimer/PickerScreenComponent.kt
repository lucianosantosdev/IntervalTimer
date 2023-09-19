package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.annotation.PluralsRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PickerGroup
import androidx.wear.compose.material.PickerGroupItem
import androidx.wear.compose.material.PickerScope
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TouchExplorationStateProvider
import androidx.wear.compose.material.rememberPickerGroupState
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds

@Composable
fun PickerScreenComponent(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    value: Int,
    type: PickerType = PickerType.TIME,
    onValueChange: (Int) -> Unit,
) {
    val fullyDrawn = remember { Animatable(0f) }

    // Omit scaling according to Settings > Display > Font size for this screen
    val typography = MaterialTheme.typography.copy(
        display3 = MaterialTheme.typography.display3.copy(
            fontSize = with(LocalDensity.current) { 30.dp.toSp() }
        )
    )
    val minuteState = rememberPickerState(
        initialNumberOfOptions = 100,
        initiallySelectedOption = getMinutesFromSeconds(seconds = value)
    )
    val secondState = rememberPickerState(
        initialNumberOfOptions = 60,
        initiallySelectedOption = getSecondsFromSeconds(seconds = value)
    )
    val sectionState = rememberPickerState(
        initialNumberOfOptions = 100,
        initiallySelectedOption = value
    )



    val touchExplorationStateProvider = remember { DefaultTouchExplorationStateProvider() }
    val touchExplorationServicesEnabled by touchExplorationStateProvider
        .touchExplorationState()

    MaterialTheme(typography = typography) {
        // When the time picker loads, none of the individual pickers are selected in talkback mode,
        // otherwise hours picker should be focused.
        val pickerGroupState = if (touchExplorationServicesEnabled) {
            rememberPickerGroupState(
                FocusableElementsTimePicker.NONE.index
            )
        } else {
            rememberPickerGroupState(
                FocusableElementsTimePicker.HOURS.index
            )
        }

        val sectionString = stringResource(R.string.label_sections)
        val minuteString = stringResource(R.string.label_minute)
        val secondString = stringResource(R.string.label_second)

        val sectionContentDescription = createDescription(
            pickerGroupState,
            sectionState.selectedOption,
            sectionString,
            R.plurals.time_picker_sections_content_description
        )

        val minuteContentDescription = createDescription(
            pickerGroupState,
            minuteState.selectedOption,
            minuteString,
            R.plurals.time_picker_minutes_content_description
        )

        val secondContentDescription = createDescription(
            pickerGroupState,
            secondState.selectedOption,
            secondString,
            R.plurals.time_picker_seconds_content_description
        )

        val textStyle = MaterialTheme.typography.display3
        val pickerOption = pickerTextOption(textStyle) { "%02d".format(it) }
        val focusRequesterConfirmButton = remember { FocusRequester() }

        val onPickerSelected =
            { current: FocusableElementsTimePicker, next: FocusableElementsTimePicker ->
                if (pickerGroupState.selectedIndex != current.index) {
                    pickerGroupState.selectedIndex = current.index
                } else {
                    pickerGroupState.selectedIndex = next.index
                    if (next == FocusableElementsTimePicker.CONFIRM_BUTTON) {
                        focusRequesterConfirmButton.requestFocus()
                    }
                }
            }

        Box(
            modifier = modifier
                .fillMaxSize()
                .alpha(fullyDrawn.value)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.button,
                    maxLines = 1
                )
                val weightsToCenterVertically = 0.5f
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val pickerGroupItems = if(type == PickerType.TIME) {
                        mutableListOf(
                            PickerGroupItem(
                                pickerState = minuteState,
                                modifier = Modifier.size(40.dp, 100.dp),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementsTimePicker.MINUTES,
                                        FocusableElementsTimePicker.SECONDS
                                    )
                                },
                                contentDescription = minuteContentDescription,
                                option = pickerOption
                            ),
                            PickerGroupItem(
                                pickerState = secondState,
                                modifier = Modifier.size(40.dp, 100.dp),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementsTimePicker.SECONDS,
                                        FocusableElementsTimePicker.CONFIRM_BUTTON
                                    )
                                },
                                contentDescription = secondContentDescription,
                                option = pickerOption
                            )
                        )
                    } else {
                        mutableListOf(
                            PickerGroupItem(
                                pickerState = sectionState,
                                modifier = Modifier.size(40.dp, 100.dp),
                                onSelected = {
                                    onPickerSelected(
                                        FocusableElementsTimePicker.SECONDS,
                                        FocusableElementsTimePicker.CONFIRM_BUTTON
                                    )
                                },
                                contentDescription = sectionContentDescription,
                                option = pickerOption
                            )
                        )
                    }
                    PickerGroup(
                        *pickerGroupItems.toTypedArray(),
                        pickerGroupState = pickerGroupState,
                        separator = { Separator(6.dp, textStyle) },
                        autoCenter = false,
                        touchExplorationStateProvider = touchExplorationStateProvider
                    )
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(weightsToCenterVertically)
                )
                Button(
                    onClick = {
                        val selectedValue = if (type == PickerType.TIME) {
                            getSecondsFromMinutesAndSeconds(
                                minutes = minuteState.selectedOption,
                                seconds = secondState.selectedOption
                            )
                        } else {
                            sectionState.selectedOption
                        }
                        onValueChange(selectedValue)
                    },
                    modifier = Modifier
                        .semantics {
                            focused = pickerGroupState.selectedIndex ==
                                    FocusableElementsTimePicker.CONFIRM_BUTTON.index
                        }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = buttonContentDescription(icon),
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    LaunchedEffect(Unit) {
        fullyDrawn.animateTo(1f)
    }
}

@Composable
fun buttonContentDescription(icon : ImageVector) = when(icon) {
    Icons.Default.PlayArrow -> stringResource(id = R.string.play_icon_content_description)
    Icons.Default.Pause -> stringResource(id = R.string.pause_icon_content_description)
    Icons.Default.Stop -> stringResource(id = R.string.stop_icon_content_description)
    Icons.Default.ArrowForward -> stringResource(id = R.string.arrow_forward_icon_content_description)
    Icons.Default.Refresh -> stringResource(id = R.string.refresh_icon_content_description)
    else -> ""
}

@Composable
fun Separator(width: Dp, textStyle: TextStyle) {
    Spacer(Modifier.width(width))
    Text(
        text = ":",
        style = textStyle,
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.clearAndSetSemantics {}
    )
    Spacer(Modifier.width(width))
}

internal class DefaultTouchExplorationStateProvider : TouchExplorationStateProvider {
    @Composable
    override fun touchExplorationState(): State<Boolean> {
        val context = LocalContext.current
        val accessibilityManager = remember {
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        }

        val listener = remember { Listener() }

        LocalLifecycleOwner.current.lifecycle.ObserveState(
            handleEvent = { event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    listener.register(accessibilityManager)
                }
            },
            onDispose = {
                listener.unregister(accessibilityManager)
            }
        )

        return remember { derivedStateOf { listener.isEnabled() } }
    }

    @Composable
    private fun Lifecycle.ObserveState(
        handleEvent: (Lifecycle.Event) -> Unit = {},
        onDispose: () -> Unit = {}
    ) {
        DisposableEffect(this) {
            val observer = LifecycleEventObserver { _, event ->
                handleEvent(event)
            }
            this@ObserveState.addObserver(observer)
            onDispose {
                onDispose()
                this@ObserveState.removeObserver(observer)
            }
        }
    }

    private class Listener :
        AccessibilityManager.AccessibilityStateChangeListener,
        AccessibilityManager.TouchExplorationStateChangeListener {

        private var accessibilityEnabled by mutableStateOf(false)
        private var touchExplorationEnabled by mutableStateOf(false)

        fun isEnabled() = accessibilityEnabled && touchExplorationEnabled

        override fun onAccessibilityStateChanged(it: Boolean) {
            accessibilityEnabled = it
        }

        override fun onTouchExplorationStateChanged(it: Boolean) {
            touchExplorationEnabled = it
        }

        fun register(am: AccessibilityManager) {
            accessibilityEnabled = am.isEnabled
            touchExplorationEnabled = am.isTouchExplorationEnabled

            am.addTouchExplorationStateChangeListener(this)
            am.addAccessibilityStateChangeListener(this)
        }

        fun unregister(am: AccessibilityManager) {
            am.removeTouchExplorationStateChangeListener(this)
            am.removeAccessibilityStateChangeListener(this)
        }
    }
}


private enum class FocusableElementsTimePicker(val index: Int) {
    HOURS(0),
    MINUTES(1),
    SECONDS(2),
    CONFIRM_BUTTON(3),
    NONE(-1);

    companion object {
        private val map = FocusableElementsTimePicker.values().associateBy { it.index }
        operator fun get(value: Int) = map[value]
    }
}

internal fun pickerTextOption(textStyle: TextStyle, indexToText: (Int) -> String):
        (@Composable PickerScope.(optionIndex: Int, pickerSelected: Boolean) -> Unit) = { value: Int, pickerSelected: Boolean ->
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = indexToText(value),
            maxLines = 1,
            style = textStyle,
            color =
            if (pickerSelected) MaterialTheme.colors.secondary
            else MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        )
    }
}


@Composable
private fun createDescription(
    pickerGroupState: androidx.wear.compose.material.PickerGroupState,
    selectedValue: Int,
    label: String,
    @PluralsRes resourceId: Int
): String {
    return when (pickerGroupState.selectedIndex) {
        FocusableElementsTimePicker.NONE.index -> label
        else -> pluralStringResource(resourceId, selectedValue, selectedValue)
    }
}

@WearPreviewLargeRound
@Composable
fun PickerScreenComponentPreview() {
    PickerScreenComponent(
        title = "teste",
        value = 0,
        onValueChange = {},
        icon = Icons.Default.ArrowForward,
    )
}
