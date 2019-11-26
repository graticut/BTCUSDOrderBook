package com.example.testprojectsb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Transaction
import com.example.testprojectsb.network.service.IService
import io.reactivex.Observable
import kotlin.math.max

/**
 * Registers as a listener for any OrderBook updated and forwards them to any listeners of his own
 *
 * Created by grati on 11/21/2019.
 */
class OrderBookViewModel(val service: IService, private val schedulerProvider: BaseSchedulerProvider = SchedulerProvider()): ViewModel() {

    private val TAG = javaClass.simpleName

    private var asks: MutableList<Transaction> = mutableListOf()
    private var bids: MutableList<Transaction> = mutableListOf()

    /**
     * Subscribes to the service for Transaction results. Whenever a new transaction is received, the
     * local lists are updated and the result is mapped to the current OrderBook
     */
    fun subscribeToBookOrderUpdates(): Observable<List<OrderBookItem>> {
        return service.subscribeToTransactionUpdates()
            .subscribeOn(schedulerProvider.io()).observeOn(
                schedulerProvider.ui())
            .doOnNext{updateTransactionLists(it)}
            .map {
                getCurrentOrderBook()
            }
    }

    /**
     * Decides how we update the transactions (ads/bids) based on the current transaction values
     */
    private fun updateTransactionLists(transaction: Transaction) {
        if (transaction.count > 0) {
            if (transaction.amount > 0) {
                updateTransactions(bids, transaction)
            } else {
                updateTransactions(asks, transaction)
            }
        } else if (transaction.count == 0) {
            when {
                transaction.amount == 1.0 -> bids.removeIf { it.price == transaction.price }
                transaction.amount == -1.0 -> asks.removeIf { it.price == transaction.price }
                else -> Log.w(TAG, "Count is zero and amount is neither 1 nor -1")
            }
        } else {
            Log.w(TAG, "Count is negative")
        }
    }

    /**
     * Generates a list of OrderBookItems based on the bids & asks. All the lists should have maximum 20 items.
     * An OrderBookItem might have null value for the bid or the ask
     */
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

    /**
     * Updates the ads/bids list. If a transaction with the same price already exists we update it by removing the
     * existing element and adding the new one in the front.
     */
    private fun updateTransactions(currentTransactions: MutableList<Transaction>, newTransaction: Transaction) {
        currentTransactions.removeIf {it.price == newTransaction.price}
        currentTransactions.add(0, newTransaction)
    }
}