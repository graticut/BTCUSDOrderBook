package com.example.testprojectsb.network.service

/**
 * Created by grati on 11/24/2019.
 */
data class ConnectionState(val state: ConnectionType, val data: String = "", val error: Throwable? = null)