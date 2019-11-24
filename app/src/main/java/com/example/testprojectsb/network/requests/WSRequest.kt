package com.example.testprojectsb.network.requests

/**
 * Created by grati on 11/20/2019.
 */
data class WSRequest(var channel: String, var event: String = "subscribe", var symbol: String = "tBTCUSD", var freq: String = "F1")