package com.example.testprojectsb

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.testprojectsb.network.model.Ticker
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.math.absoluteValue
import kotlin.math.sign

class MainFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var viewModel: BTCUSDViewModel
    private lateinit var subscriptions: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(requireActivity())[BTCUSDViewModel::class.java]

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscriptions = CompositeDisposable()
        subscriptions.add(viewModel.subscribeToBookOrderUpdates().subscribe {
            updateBookOutput(it)
        })
        subscriptions.add(viewModel.subscribeToTickerUpdates().subscribe {
            updateTickerOutput(it)
        })
        subscriptions.add(viewModel.subscribeToOutputUpdates().subscribe {
            output(it)
        })
        start.setOnClickListener {
            viewModel.fetchData()
        }
        stop.setOnClickListener {
            viewModel.stopData()
        }
    }

    override fun onDestroyView() {
        subscriptions.dispose()
        subscriptions.clear()
        super.onDestroyView()
    }

    private fun updateTickerOutput(ticker: Ticker) {
        volume_content.text = String.format("%.0f", ticker.volume)
        low_content.text = String.format("%.1f", ticker.low)
        last_value.text = String.format("%.1f", ticker.lastPrice)
        daily_change.text = String.format("%.2f", ticker.dailyChange.absoluteValue)
        daily_change_percentage.text = getString(R.string.daily_change_percentage, String.format("%.2f", ticker.dailyChangePercentage.absoluteValue.times(100)) + "%")
        val dailyChangePositive = ticker.dailyChange.sign == 1.0
        val dailyChangeColor = if (dailyChangePositive) Color.GREEN else Color.RED
        daily_change.setTextColor(dailyChangeColor)
        daily_change_percentage.setTextColor(dailyChangeColor)
        arrow.visibility = View.VISIBLE
        arrow.setColorFilter(dailyChangeColor)
        arrow.rotation = if (dailyChangePositive) 180f else 0f
        high_content.text = String.format("%.1f", ticker.high)
    }

    private fun updateBookOutput(txt: String) {
        book_output!!.text = txt
    }

    private fun output(txt: String) {
        output!!.text = output!!.text.toString() + "\n\n" + txt
    }
}