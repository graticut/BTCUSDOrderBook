package com.example.testprojectsb.network.service

import com.example.testprojectsb.network.model.Transaction
import com.google.gson.Gson

/**
 * Created by grati on 11/25/2019.
 */
class OrderBookBuilder {

    /**
     * Builds an OrderBookItem from a raw message text. Ex: [123, [11.0,2,33.0]] -> Transaction(11.0, 33.0, 2)
     */
    fun buildTransactionFromRawMessage(text: String): Transaction {
        val orderBookDataList = WSUtil.filterChannelIdAndBrackets(text).split(",").toMutableList()
        val price = orderBookDataList[0].toDouble()
        val count = orderBookDataList[1].toDouble().toInt()
        val amount = orderBookDataList[2].toDouble()
        return Transaction(price, amount, count)
    }

    /**
     * Builds an OrderBookItem list from a message text.
     * Ex: [123, [11.0,2.0,33.0], [111.0,22,333.0]] -> [Transaction(11.0, 33.0, 2), Transaction(111.0, 333.0, 22)]
     */
    fun buildSnapshotTransactions(text: String): List<Transaction> {
        val newText = text.removePrefix("[").removeSuffix("]")
        val arrayText = newText.split(",", limit = 2).last()
        val snapshotList= Gson().fromJson(arrayText, Array<Array<Double>>::class.java).toList()
        return snapshotList.map{
            val price = it[0]
            val count = it[1].toInt()
            val amount = it[2]
            Transaction(price, amount, count)
        }
    }
}