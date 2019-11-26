package com.example.testprojectsb.network.service

import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.model.Transaction
import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
interface IService {

    fun fetchData()
    fun stopData()
    fun subscribeToConnectionUpdates(): Observable<ConnectionState>
    fun subscribeToTickerUpdates(): Observable<Ticker>
    fun subscribeToTransactionUpdates(): Observable<Transaction>
}