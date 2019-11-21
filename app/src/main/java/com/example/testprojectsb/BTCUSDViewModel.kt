package com.example.testprojectsb

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.IService
import com.example.testprojectsb.network.WSService
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

    fun subscribeToTickerUpdates(): Observable<String> {
        return service.subscribeToTickerUpdates()
    }

    fun subscribeToBookOrderUpdates(): Observable<String> {
        return service.subscribeToBookOrderUpdates()
    }

    fun subscribeToOutputUpdates(): Observable<String> {
        return service.subscribeToOutputUpdates()
    }
}