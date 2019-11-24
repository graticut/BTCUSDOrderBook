package com.example.testprojectsb.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testprojectsb.R
import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.service.ConnectionState
import com.example.testprojectsb.network.service.WSService
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.ticker_layout.*
import kotlin.math.absoluteValue
import kotlin.math.sign

class MainFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var orderBookViewModel: OrderBookViewModel
    private lateinit var tickerViewModel: TickerViewModel
    private lateinit var serviceConnectionViewModel: ServiceConnectionViewModel
    private lateinit var subscriptions: CompositeDisposable
    private lateinit var  adapter: OrderBookAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val wsService = WSService()
        orderBookViewModel = ViewModelProviders.of(this, viewModelFactory { OrderBookViewModel(wsService) }).get(OrderBookViewModel::class.java)
        tickerViewModel = ViewModelProviders.of(this, viewModelFactory { TickerViewModel(wsService) }).get(TickerViewModel::class.java)
        serviceConnectionViewModel = ViewModelProviders.of(this, viewModelFactory { ServiceConnectionViewModel(wsService) }).get(ServiceConnectionViewModel::class.java)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderBookAdapter(context)
        order_book.adapter = adapter
        order_book.layoutManager = LinearLayoutManager(context)

        subscriptions = CompositeDisposable()
        subscriptions.add(orderBookViewModel.subscribeToBookOrderUpdates().subscribe {
            updateBookOutput(it)
        })
        subscriptions.add(tickerViewModel.subscribeToTickerUpdates().subscribe {
            updateTickerOutput(it)
        })
        subscriptions.add(orderBookViewModel.subscribeToOutputUpdates().subscribe {
            output(it)
        })
        start.setOnClickListener {
            serviceConnectionViewModel.fetchData()
        }
        stop.setOnClickListener {
            serviceConnectionViewModel.stopData()
        }

        observeServiceConnectionChanges()
        serviceConnectionViewModel.subscribeToConnectionUpdates()
        val connectionStateMonitor = ConnectionStateMonitor(context!!)
        connectionStateMonitor.observe(this, Observer<Boolean> {
            if (it) {
                serviceConnectionViewModel.fetchData()
            } else {
                serviceConnectionViewModel.stopData()
            }
        })
    }

    override fun onDestroyView() {
        subscriptions.dispose()
        subscriptions.clear()
        super.onDestroyView()
    }

    private fun observeServiceConnectionChanges() {
        serviceConnectionViewModel.getToConnectionUpdates().observe(this,  Observer<ConnectionState> {
            Log.d(TAG, "ConnectionState: " + it.state.toString())
        })
    }

    private fun updateTickerOutput(ticker: Ticker) {
        volume_content.text = String.format("%.0f", ticker.volume)
        low_content.text = String.format("%.1f", ticker.low)
        last_value.text = String.format("%.1f", ticker.lastPrice)
        daily_change.text = String.format("%.2f", ticker.dailyChange.absoluteValue)
        daily_change_percentage.text = getString(R.string.daily_change_percentage, String.format("%.2f", ticker.dailyChangePercentage.absoluteValue.times(100)) + "%")
        val dailyChangePositive = ticker.dailyChange.sign == 1.0
        val dailyChangeColor = if (dailyChangePositive) ContextCompat.getColor(context!!, R.color.positive_color) else ContextCompat.getColor(context!!, R.color.negative_color)
        daily_change.setTextColor(dailyChangeColor)
        daily_change_percentage.setTextColor(dailyChangeColor)
        arrow.visibility = View.VISIBLE
        arrow.setColorFilter(dailyChangeColor)
        arrow.rotation = if (dailyChangePositive) 180f else 0f
        high_content.text = String.format("%.1f", ticker.high)
    }

    private fun updateBookOutput(orderBookItems: List<OrderBookItem>) {
        adapter.updateElements(orderBookItems)
    }

    private fun output(txt: String) {
        output!!.text = output!!.text.toString() + txt + "\n"
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }
}