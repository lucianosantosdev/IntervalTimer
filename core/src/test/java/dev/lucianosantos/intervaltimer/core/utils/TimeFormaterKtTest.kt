package dev.lucianosantos.intervaltimer.core.utils

import org.junit.Test

class TimeFormaterKtTest {
    @Test
    fun `Verify seconds are formated into minutes and seconds`() {
        assert(formatMinutesAndSeconds(1) == "00:01")
        assert(formatMinutesAndSeconds(10) == "00:10")
        assert(formatMinutesAndSeconds(100) == "01:40")
        assert(formatMinutesAndSeconds(1000) == "16:40")
    }
}