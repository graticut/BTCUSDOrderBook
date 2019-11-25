package com.example.testprojectsb

import com.example.testprojectsb.network.service.TickerBuilder
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by grati on 11/23/2019.
 */
@RunWith(MockitoJUnitRunner::class)
class TickerTest {

    val tickerBuilder = TickerBuilder()

    @Test
    fun givenTickerMessage_shouldReturnTickerObject() {
        val message = "[365478,[7341.2,29.64982408,7341.3,9.1323093,138.8128004,0.0193,7341.2128004,6636.32056508,7419.1,7141]]"
        val ticker = tickerBuilder.buildTicker(message)
        assertEquals(ticker.lastPrice, 7341.2128004, 0.01)
        assertEquals(ticker.low, 7141.0, 0.01)
        assertEquals(ticker.high, 7419.1, 0.01)
        assertEquals(ticker.dailyChange, 138.8128004, 0.01)
        assertEquals(ticker.dailyChangePercentage, 0.0193, 0.01)

        assertEquals(ticker.volume.toString(), (7341.2128004.toBigDecimal() * 6636.32056508.toBigDecimal()).toString())
    }

}