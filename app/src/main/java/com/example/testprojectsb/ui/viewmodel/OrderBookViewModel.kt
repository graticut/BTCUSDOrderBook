package com.example.testprojectsb.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.service.IService
import io.reactivex.Observable

/**
 * Registers as a listener for any OrderBook updated and forwards them to any listeners of his own
 *
 * Created by grati on 11/21/2019.
 */
class OrderBookViewModel(val service: IService): ViewModel() {

    fun subscribeToBookOrderUpdates(): Observable<List<OrderBookItem>> {
        return service.subscribeToBookOrderUpdates()
    }
}