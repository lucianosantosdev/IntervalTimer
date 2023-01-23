package dev.lucianosantos.intervaltimer

import android.view.MotionEvent
import com.shawnlin.numberpicker.NumberPicker

private fun NumberPicker.setFocus() {
    this.textColor = resources.getColor(R.color.np_textColorOnFocus)
    this.selectedTextColor = resources.getColor(R.color.np_selectedTextColorOnFocus)
    invalidate()
}

private fun NumberPicker.unsetFocus() {
    this.textColor = resources.getColor(R.color.np_textColor)
    this.selectedTextColor = resources.getColor(R.color.np_selectedTextColor)
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

    fun getTimeSeconds(minutesNumberPicker: NumberPicker, secondsNumberPicker: NumberPicker) : Long {
        return (minutesNumberPicker.value*60 + secondsNumberPicker.value).toLong()
    }
}