package com.example.testprojectsb.network

import android.util.Log
import com.example.testprojectsb.network.model.Ticker
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import okhttp3.*
import okio.ByteString

/**
 * Created by grati on 11/21/2019.
 */
class WSService: IService {

    private var client: OkHttpClient? = OkHttpClient()
    private var ws: WebSocket? = null

    private var tickerSubject: ReplaySubject<String> = ReplaySubject.createWithSize(1)
    private var bookOrderSubject: ReplaySubject<String> = ReplaySubject.createWithSize(1)
    private var outputSubject: ReplaySubject<String> = ReplaySubject.createWithSize(1)

    override fun subscribeToTickerUpdates(): Observable<String> {
        return tickerSubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun subscribeToBookOrderUpdates(): Observable<String> {
        return bookOrderSubject.observeOn(AndroidSchedulers.mainThread())
    }
    override fun subscribeToOutputUpdates(): Observable<String> {
        return outputSubject.observeOn(AndroidSchedulers.mainThread())
    }

    private val TAG = javaClass.simpleName

    private var tickerChannelId = ""
    private var bookChannelId = ""

    val NORMAL_CLOSURE_STATUS = 1000

    override fun fetchData() {
        ws?.cancel()
        ws?.close(NORMAL_CLOSURE_STATUS, null)
        ws = null

        val request = Request.Builder().url("wss://api-pub.bitfinex.com/ws/2").build()
        ws = client!!.newWebSocket(request, EchoWebSocketListener())

        client!!.dispatcher().executorService().shutdown()
    }

    private inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            outputSubject.onNext("onOpen Receiving : " + response!!)

            val tickerRequest = TickerRequest("subscribe", "ticker", "tBTCUSD")
            val bookOrderRequest = BookOrderRequest("subscribe", "book", "tBTCUSD", "F1")

            webSocket!!.send(Gson().toJson(tickerRequest))
//            webSocket.send(Gson().toJson(bookOrderRequest))
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            if (text?.startsWith("[")!!) {
                val tempList = text.split(",").map {it.replace("[", "").replace("]","")}.toMutableList()
                val channelId = tempList.first()
                tempList.remove(channelId)

                when {
                    tempList.first() == "\"hb\"" -> return
                    channelId == tickerChannelId -> {
                        Log.d(TAG,"onMessage ticker - " + text)
                        val lastPrice= tempList[6].toDouble()
                        val volume  = tempList[7].toDouble()
                        val high = tempList[8].toDouble()
                        val low = tempList[9].toDouble()
                        val dailyChange = tempList[4].toDouble()
                        val dailyChangePercentage = tempList[5].toDouble()
                        val volumeValue = lastPrice.toBigDecimal().multiply(volume.toBigDecimal())
                        val ticker = Ticker(volumeValue, lastPrice, low, high, dailyChange, dailyChangePercentage)
                        tickerSubject.onNext("lastPrice: ${ticker.lastPrice} \n" +
                                "low: ${ticker.low} \n" +
                                "high: ${ticker.high} \n" +
                                "volume: ${ticker.volume}\n" +
                                "dailyChange: ${ticker.dailyChange}\n" +
                                "dailyChangePercentage: ${ticker.dailyChangePercentage}")
                    }
                    else -> bookOrderSubject.onNext(text + " -- ${tempList.size}")
                }
            } else {
                outputSubject.onNext("Receiving : $text")
                try {
                    val element = Gson().fromJson(text, Element::class.java)
                    if (element.event == "subscribed") {
                        if ("ticker" == element.channel) {
                            tickerChannelId = element.chanId
                        } else if ("book" == element.channel) {
                            bookChannelId = element.chanId
                        }
                    }
                } catch (exception: IllegalStateException) {
                    Log.d(TAG, "event elements are over")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            outputSubject.onNext("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            outputSubject.onNext("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            outputSubject.onNext("Error : " + t.message)
        }
    }
}