package dev.lucianosantos.intervaltimer.core.ui

import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import com.shawnlin.numberpicker.NumberPicker
import dev.lucianosantos.intervaltimer.core.R

private fun NumberPicker.setFocus() {
    this.textColor = ResourcesCompat.getColor(resources, R.color.np_textColorOnFocus, null)
    this.selectedTextColor = ResourcesCompat.getColor(resources, R.color.np_selectedTextColorOnFocus, null)
    invalidate()
}

private fun NumberPicker.unsetFocus() {
    this.textColor = ResourcesCompat.getColor(resources, R.color.np_textColor, null)
    this.selectedTextColor = ResourcesCompat.getColor(resources, R.color.np_selectedTextColor, null)
    invalidate()
}

class NumberPickerHelper() {
    fun setupNumberPickersFocus(numberPickers: List<NumberPicker>) {
        for (npi in numberPickers) {
            npi.setOnTouchListener { v, event ->
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        (v as NumberPicker).setFocus()
                        for (npj in numberPickers) {
                            if (npi != npj) {
                                npj.unsetFocus()
                            }
                        }
                    }
                }

                return@setOnTouchListener v.onTouchEvent(event)
            }
        }
    }
}