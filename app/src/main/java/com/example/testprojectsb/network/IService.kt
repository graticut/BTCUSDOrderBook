package com.example.testprojectsb.network

import io.reactivex.Observable

/**
 * Created by grati on 11/21/2019.
 */
interface IService {

    fun fetchData()
    fun subscribeToTickerUpdates(): Observable<String>
    fun subscribeToBookOrderUpdates(): Observable<String>
    fun subscribeToOutputUpdates(): Observable<String>
}