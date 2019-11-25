package com.example.testprojectsb.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.service.IService
import io.reactivex.Observable

/**
 * Registers as a listener for any TIcker updated and forwards them to any listeners of his own
 *
 * Created by grati on 11/21/2019.
 */
class TickerViewModel(val service: IService): ViewModel() {

    fun subscribeToTickerUpdates(): Observable<Ticker> {
        return service.subscribeToTickerUpdates()
    }
}