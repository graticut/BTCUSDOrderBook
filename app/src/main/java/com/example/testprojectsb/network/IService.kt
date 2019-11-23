package com.example.testprojectsb.network

import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Ticker
import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
interface IService {

    fun fetchData()
    fun stopData()
    fun subscribeToTickerUpdates(): Observable<Ticker>
    fun subscribeToBookOrderUpdates(): Observable<List<OrderBookItem>>
    fun subscribeToOutputUpdates(): Observable<String>
}