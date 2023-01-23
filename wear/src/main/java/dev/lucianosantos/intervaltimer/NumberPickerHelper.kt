package dev.lucianosantos.intervaltimer

import android.util.Log
import android.view.MotionEvent
import com.shawnlin.numberpicker.NumberPicker

val TAG = "NumberPickerStyle"

class NumberPickerHelper() {
    fun getTimeSeconds(minutesNumberPicker: NumberPicker, secondsNumberPicker: NumberPicker) : Long {
        return (minutesNumberPicker.value*60 + secondsNumberPicker.value).toLong()
    }
}

//
//private fun setupNumberPickersFocus(numberPickers: List<NumberPicker>) {
//    for (npi in numberPickers) {
//        npi.setOnTouchListener { v, event ->
//            when(event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    (v as NumberPicker).setFocus()
//                    for (npj in numberPickers) {
//                        if (npi != npj) {
//                            npj.unsetFocus()
//                        }
//                    }
//                }
//            }
//
//            return@setOnTouchListener v.onTouchEvent(event)
//        }
//    }
//}
//
//fun NumberPicker.setFocus() {
//    Log.d(TAG, "SET FOCUS")
//    this.textColor = resources.getColor(R.color.primaryColor)
//    this.selectedTextColor = resources.getColor(R.color.primaryColor)
//    invalidate()
//}
//
//fun NumberPicker.unsetFocus() {
//    Log.d(TAG, "UNSET FOCUS")
//    wrapSelectorWheel = true
//    this.textColor = resources.getColor(android.R.color.transparent)
//    this.selectedTextColor = resources.getColor(android.R.color.white)
//    invalidate()
//}