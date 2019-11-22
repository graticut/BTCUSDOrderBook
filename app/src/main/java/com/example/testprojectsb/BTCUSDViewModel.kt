package com.example.testprojectsb

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.IService
import com.example.testprojectsb.network.WSService
import com.example.testprojectsb.network.model.Ticker
import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
class BTCUSDViewModel: ViewModel() {

    private var service: IService =
        WSService()

    fun fetchData() {
        service.fetchData()
    }

    fun stopData() {
        service.stopData()
    }

    fun subscribeToTickerUpdates(): Observable<Ticker> {
        return service.subscribeToTickerUpdates()
    }

    fun subscribeToBookOrderUpdates(): Observable<String> {
        return service.subscribeToBookOrderUpdates()
    }

    fun subscribeToOutputUpdates(): Observable<String> {
        return service.subscribeToOutputUpdates()
    }
}