package com.example.testprojectsb.ui

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.service.IService
import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
class TickerViewModel(val service: IService): ViewModel() {

    fun subscribeToTickerUpdates(): Observable<Ticker> {
        return service.subscribeToTickerUpdates()
    }
}