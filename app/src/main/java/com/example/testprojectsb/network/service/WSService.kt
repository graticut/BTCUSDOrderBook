package com.example.testprojectsb.network.service

import android.util.Log
import com.example.testprojectsb.network.model.SubscribedItem
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.model.Transaction
import com.example.testprojectsb.network.requests.WSRequest
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import okhttp3.*
import okio.ByteString
import java.util.*

/**
 * Created by grati on 11/21/2019.
 */
class WSService: IService {

    private val TAG = javaClass.simpleName

    val NORMAL_CLOSURE_STATUS = 1000

    private var client: OkHttpClient? = OkHttpClient()
    private var ws: WebSocket? = null
    var listener: EchoWebSocketListener = EchoWebSocketListener()

    private var tickerSubject: ReplaySubject<Ticker> = ReplaySubject.createWithSize(1)
    private var transactionSubject: ReplaySubject<Transaction> = ReplaySubject.createWithSize(1)
    private var connectivitySubject: ReplaySubject<ConnectionState> = ReplaySubject.createWithSize(1)

    private var tickerChannelId = ""
    private var bookChannelId = ""

    private val orderBookBuilder = OrderBookBuilder()
    private val tickerBuilder = TickerBuilder()

    var connectionRetries = 0
    private val CONNECTION_RETRIES_LIMIT = 3

    override fun fetchData() {
        Log.d(TAG, "fetchData")
        connectionRetries = 0
        startConnection()
    }

    private fun startConnection() {
        Log.d(TAG, "startConnection")
        client = OkHttpClient()
        val request = Request.Builder().url("wss://api-pub.bitfinex.com/ws/2").build()
        ws = client!!.newWebSocket(request, listener)
        client!!.dispatcher().executorService().shutdown()
    }

    override fun stopData() {
        Log.d(TAG, "stopData")
        ws?.close(NORMAL_CLOSURE_STATUS, null)
    }

    override fun subscribeToConnectionUpdates(): Observable<ConnectionState> {
        return connectivitySubject
    }

    override fun subscribeToTickerUpdates(): Observable<Ticker> {
        return tickerSubject
    }

    override fun subscribeToTransactionUpdates(): Observable<Transaction> {
        return transactionSubject
    }

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            connectionRetries = 0

            val tickerRequest = WSRequest("ticker")
            val bookOrderRequest = WSRequest("book")

            webSocket!!.send(Gson().toJson(tickerRequest))
            webSocket.send(Gson().toJson(bookOrderRequest))
            connectivitySubject.onNext(ConnectionState(ConnectionType.CONNECTED))
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            text?.let {
                when (WSUtil.getMessageType(it, tickerChannelId, bookChannelId )) {
                    MessageType.SUBSCRIBED -> {
                        val element = Gson().fromJson(text, SubscribedItem::class.java)
                        if (element.event == "subscribed") {
                            when {
                                "ticker" == element.channel -> {
                                    tickerChannelId = element.chanId
                                    connectivitySubject.onNext(ConnectionState(ConnectionType.TICKER_SUBSCRIBED))
                                }
                                "book" == element.channel -> {
                                    bookChannelId = element.chanId
                                    connectivitySubject.onNext(ConnectionState(ConnectionType.ORDER_BOOK_SUBSCRIBED))
                                }
                                else -> Log.w(TAG, "Unknown channel")
                            }
                        } else {
                            Log.w(TAG, "probably a connection message")
                        }
                    }
                    MessageType.TICKER -> {
                        tickerSubject.onNext(
                            tickerBuilder.buildTicker(
                                text
                            )
                        )
                    }
                    MessageType.ORDERBOOK_SNAPSHOT -> {
                        emitOrderBookSnapshot(text)
                    }
                    MessageType.ORDERBOOK -> {
                        transactionSubject.onNext(orderBookBuilder.buildTransactionFromRawMessage(text))
                    }
                    else -> Log.w(TAG, "UNKNOWN data received: $text")
                }
            }
        }

        private fun emitOrderBookSnapshot(text: String) {
            orderBookBuilder.buildSnapshotTransactions(text).forEach{
                transactionSubject.onNext(it)
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            Log.d(TAG, "Receiving bytes : ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            connectivitySubject.onNext( ConnectionState(ConnectionType.CONNECTION_CLOSED, reason!!))
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            Log.d(TAG, "onFailure - ${t.message}")
            connectivitySubject.onNext(ConnectionState(ConnectionType.ERROR, error = t))
            if (connectionRetries < CONNECTION_RETRIES_LIMIT) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        connectionRetries++
                        stopData()
                        startConnection()
                    }
                }, 1000)
            }
        }
    }
}