package com.example.testprojectsb.network.service

import android.util.Log
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.SubscribedItem
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.model.Transaction
import com.google.gson.Gson
import kotlin.math.max

/**
 * Utility class for parsing messages from the server and building the model
 *
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
            try {
                val element = Gson().fromJson(message, SubscribedItem::class.java)
                if (element.event == "subscribed") {
                    MessageType.SUBSCRIBED
                } else {
                    MessageType.UNKNOWN
                }
            } catch (e: Exception) {
                return MessageType.UNKNOWN
            }

        } else if (messageHasThreeArrays(message)) {
            MessageType.ORDERBOOK_SNAPSHOT
        } else if (messageHasTwoArrays(message)) {
            when (extractChannelId(message)) {
                tickerChannelId -> MessageType.TICKER
                orderBookChannelId -> MessageType.ORDERBOOK
                else -> MessageType.UNKNOWN
            }
        } else {
            MessageType.UNKNOWN
        }
    }

    private fun messageHasTwoArrays(message: String) = message.endsWith("]]")

    private fun extractChannelId(message: String) =
        message.split(",").map { it.replace("[", "").replace("]", "") }.toMutableList().first()

    private fun messageHasThreeArrays(message: String) = message.endsWith("]]]")

    private fun isDataMessage(message: String) = message.startsWith("[")

    fun buildTicker(text: String): Ticker {
        val tickerDataList = filterChannelIdAndBrackets(
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

    private fun filterChannelIdAndBrackets(text: String): String {
        val newText = text.removePrefix("[").removeSuffix("]")
        return newText.split(",", limit = 2).last().removePrefix("[").removeSuffix("]")
    }

    fun buildOrderBookFromRawMessage(text: String): List<OrderBookItem> {
        return buildOrderBook(
            filterChannelIdAndBrackets(
                text
            )
        )
    }

    private fun buildOrderBook(text: String): List<OrderBookItem> {
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
        val transaction = Transaction(price, amount)
        if (count > 0) {
            if (transaction.amount > 0) {
                updateTransactions(
                    bids,
                    transaction
                )
            } else {
                updateTransactions(
                    asks,
                    transaction
                )
            }
        } else if (count == 0) {
            when (transaction.amount) {
                1.0 -> {
                    bids.removeIf { transaction.price == it.price }}
                -1.0 -> {
                    asks.removeIf { transaction.price == it.price }}
                else -> Log.w(TAG, "Count is zero and amount is neither 1 nor -1")
            }
        } else {
            Log.w(TAG, "Count is negative")
        }
    }

    private fun updateTransactions(currentTransactions: MutableList<Transaction>, newTransaction: Transaction) {
        currentTransactions.removeIf {it.price == newTransaction.price}
        currentTransactions.add(0, newTransaction)
    }

    fun buildSnapshotOrderBooks(text: String): List<OrderBookItem> {
        val newText = text.removePrefix("[").removeSuffix("]")
        val arrayText = newText.split(",", limit = 2).last()
        val snapshotList= Gson().fromJson(arrayText, Array<Array<Double>>::class.java).toList()
        snapshotList.forEach{
            updateTransactionLists(
                it.joinToString()
            )
        }
        return getCurrentOrderBook()
    }
}