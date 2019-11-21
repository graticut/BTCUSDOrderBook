package com.example.testprojectsb.network

/**
 * Created by grati on 11/20/2019.
 */
interface BTCUSDRequest
data class TickerRequest(var event: String, var channel: String, var symbol: String): BTCUSDRequest
data class BookOrderRequest(var event: String, var channel: String, var symbol: String, var freq: String): BTCUSDRequest