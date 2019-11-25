package com.example.testprojectsb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testprojectsb.network.service.ConnectionState
import com.example.testprojectsb.network.service.ConnectionType
import com.example.testprojectsb.network.service.IService
import io.reactivex.disposables.CompositeDisposable

/**
 * Keeps the view updated with the service's connection state
 *
 * Created by grati on 11/21/2019.
 */
class ServiceConnectionViewModel(val service: IService): ViewModel() {
    private val TAG = javaClass.simpleName

    private val connectionType = MutableLiveData<ConnectionState>()
    private var subscriptions: CompositeDisposable? = null

    init {
        subscriptions = CompositeDisposable()
    }

    fun subscribeToConnectionUpdates() {
        subscriptions?.add(service.subscribeToConnectionUpdates().subscribe ({
            connectionType.postValue(it)
        }, {
            connectionType.postValue(ConnectionState(ConnectionType.ERROR, error = it))
            Log.e(TAG, "Error when receiving connection updates: $it")
        }))
    }

    fun getToConnectionUpdates(): MutableLiveData<ConnectionState> {
        return connectionType
    }

    fun fetchData() {
        service.fetchData()
    }

    fun stopData() {
        service.stopData()
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions?.clear()
        subscriptions = null
    }
}