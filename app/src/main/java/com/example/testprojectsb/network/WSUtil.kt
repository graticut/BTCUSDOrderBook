package com.example.testprojectsb.network

import android.util.Log
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.model.Transaction
import com.google.gson.Gson
import kotlin.math.max

/**
 * Created by grati on 11/22/2019.
 */


enum class MessageType {
    TICKER, ORDERBOOK, ORDERBOOK_SNAPSHOT, SUBSCRIBED, UNKNOWN
}

object WSUtil {

    private val TAG = javaClass.simpleName

    private var asks: MutableList<Transaction> = mutableListOf()
    private var bids: MutableList<Transaction> = mutableListOf()

    fun getMessageType(message: String, tickerChannelId: String, orderBookChannelId: String): MessageType {
        return if (!isDataMessage(message)) {
            val element = Gson().fromJson(message, Element::class.java)
            if (element.event == "subscribed") {
                MessageType.SUBSCRIBED
            } else {
                MessageType.UNKNOWN
            }
        } else if (messageArrayIs3D(message)) {
            MessageType.ORDERBOOK_SNAPSHOT
        } else if (messageArrayIs2D(message)) {
            when (extractChannelId(message)) {
                tickerChannelId -> MessageType.TICKER
                orderBookChannelId -> MessageType.ORDERBOOK
                else -> MessageType.UNKNOWN
            }
        } else {
            MessageType.UNKNOWN
        }
    }

    private fun messageArrayIs2D(message: String) = message.endsWith("]]")

    private fun extractChannelId(message: String) =
        message.split(",").map { it.replace("[", "").replace("]", "") }.toMutableList().first()

    private fun messageArrayIs3D(message: String) = message.endsWith("]]]")

    private fun isDataMessage(message: String) = message.startsWith("[")

    fun buildTicker(text: String): Ticker {
        val tickerDataList = clearArrayText(text).split(",").toMutableList()
        val lastPrice = tickerDataList[6].toDouble()
        val volume = tickerDataList[7].toDouble()
        val high = tickerDataList[8].toDouble()
        val low = tickerDataList[9].toDouble()
        val dailyChange = tickerDataList[4].toDouble()
        val dailyChangePercentage = tickerDataList[5].toDouble()
        val volumeValue = lastPrice.toBigDecimal().multiply(volume.toBigDecimal())
        return Ticker(volumeValue, lastPrice, low, high, dailyChange, dailyChangePercentage)
    }

    private fun clearArrayText(text: String): String {
        val newText = text.removePrefix("[").removeSuffix("]")
        return newText.split(",", limit = 2).last().removePrefix("[").removeSuffix("]")
    }

    fun buildOrderBook(text: String): List<OrderBookItem> {
        return buildOrderBookClearedArray(clearArrayText(text))
    }

    private fun buildOrderBookClearedArray(text: String): List<OrderBookItem> {
        updateTransactionLists(text)
        return getCurrentOrderBook()
    }

    private fun getCurrentOrderBook(): MutableList<OrderBookItem> {
        val orderBookItems = mutableListOf<OrderBookItem>()
        if (bids.size > 20) bids = bids.subList(0, 20)
        if (asks.size > 20) asks = asks.subList(0, 20)
        val size = max(asks.size, bids.size)
        for (i in 0 until size) {
            val bid = if (i < bids.size) bids[i] else null
            val ask = if (i < asks.size) asks[i] else null
            orderBookItems.add(OrderBookItem(bid, ask))
        }
        return orderBookItems
    }

    private fun updateTransactionLists(text: String) {
        val orderBookDataList = text.split(",").toMutableList()
        val price = orderBookDataList[0].toDouble()
        val count = orderBookDataList[1].toDouble().toInt()
        val amount = orderBookDataList[2].toDouble()
        if (count > 0) {
            val transaction = Transaction(price, amount, orderBookDataList[2])
            if (amount > 0) {
                bids.add(0, transaction)
            } else {
                asks.add(0, transaction)
            }
        } else if (count == 0) {
            when (amount) {
                1.0 -> {
                    bids.removeIf { price == it.price }
                }
                -1.0 -> {
                    asks.removeIf { price == it.price }
                }
                else -> Log.w(TAG, "Count is zero and amount is neither 1 nor -1")
            }
        } else {
            Log.w(TAG, "Count is negative")
        }
    }

    fun buildSnapshotOrderBooks(text: String): List<OrderBookItem> {
        val newText = text.removePrefix("[").removeSuffix("]")
        val arrayText = newText.split(",", limit = 2).last()
        val snapshotList= Gson().fromJson(arrayText, Array<Array<Double>>::class.java).toList()
        snapshotList.forEach{ updateTransactionLists(it.joinToString()) }
        return getCurrentOrderBook()
    }
}