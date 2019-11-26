package com.example.testprojectsb.network.service

import com.example.testprojectsb.network.model.SubscribedItem
import com.google.gson.Gson

/**
 * Utility class for parsing messages from the server and building the model
 *
 * Created by grati on 11/22/2019.
 */


enum class MessageType {
    TICKER, ORDERBOOK, ORDERBOOK_SNAPSHOT, SUBSCRIBED, UNKNOWN
}

object WSUtil {

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

    fun filterChannelIdAndBrackets(text: String): String {
        val newText = text.removePrefix("[").removeSuffix("]").trim()
        return newText.split(",", limit = 2).last().trim().removePrefix("[").removeSuffix("]")
    }
}