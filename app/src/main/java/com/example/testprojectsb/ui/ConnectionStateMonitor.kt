package com.example.testprojectsb.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData


/**
 * Created by grati on 11/23/2019.
 */
class ConnectionStateMonitor(mContext: Context) : LiveData<Boolean>() {
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager: ConnectivityManager?

    init {
        connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = NetworkCallback(this)
    }

    override fun onActive() {
        super.onActive()
        updateConnection()
        connectivityManager!!.registerDefaultNetworkCallback(networkCallback!!)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager!!.unregisterNetworkCallback(networkCallback!!)
    }

    private fun updateConnection() {
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
                postValue(true)
            } else {
                postValue(false)
            }
        }
    }

    internal inner class NetworkCallback(private val mConnectionStateMonitor: ConnectionStateMonitor) :
        ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            mConnectionStateMonitor.value?.let {
                if (!it) mConnectionStateMonitor.postValue(true)
            }
        }

        override fun onLost(network: Network) {
            mConnectionStateMonitor.value?.let {
                if (it) mConnectionStateMonitor.postValue(false)
            }
        }
    }
}