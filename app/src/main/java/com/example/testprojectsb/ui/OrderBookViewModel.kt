package com.example.testprojectsb.ui

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.service.IService
import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
class OrderBookViewModel(val service: IService): ViewModel() {

    fun subscribeToBookOrderUpdates(): Observable<List<OrderBookItem>> {
        return service.subscribeToBookOrderUpdates()
    }

    fun subscribeToOutputUpdates(): Observable<String> {
        return service.subscribeToOutputUpdates()
    }
}