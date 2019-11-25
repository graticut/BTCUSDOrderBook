package com.example.testprojectsb.network.service

import com.example.testprojectsb.network.model.Ticker

/**
 * Created by grati on 11/25/2019.
 */
class TickerBuilder {

    fun buildTicker(text: String): Ticker {
        val tickerDataList = WSUtil.filterChannelIdAndBrackets(
            text
        ).split(",").toMutableList()
        val lastPrice = tickerDataList[6].toDouble()
        val volume = tickerDataList[7].toDouble()
        val high = tickerDataList[8].toDouble()
        val low = tickerDataList[9].toDouble()
        val dailyChange = tickerDataList[4].toDouble()
        val dailyChangePercentage = tickerDataList[5].toDouble()
        val volumeValue = lastPrice.toBigDecimal().multiply(volume.toBigDecimal())
        return Ticker(volumeValue, lastPrice, low, high, dailyChange, dailyChangePercentage)
    }
}