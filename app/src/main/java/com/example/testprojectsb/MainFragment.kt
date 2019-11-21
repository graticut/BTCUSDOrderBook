package com.example.testprojectsb

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {


    private val TAG = javaClass.simpleName

    private lateinit var viewModel: BTCUSDViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(requireActivity())[BTCUSDViewModel::class.java]

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        start!!.setOnClickListener {
//            start()
            viewModel.subscribeToBookOrderUpdates().subscribe {
                updateBookOutput(it)
            }
            viewModel.subscribeToTickerUpdates().subscribe {
                updateTickerOutput(it)
            }
            viewModel.subscribeToOutputUpdates().subscribe {
                output(it)
            }
            viewModel.fetchData()
        }
    }

    private fun updateTickerOutput(txt: String) {
        activity?.runOnUiThread { ticker_output!!.text = txt }
    }

    private fun updateBookOutput(txt: String) {
        activity?.runOnUiThread { book_output!!.text = txt }
    }

    private fun output(txt: String) {
        activity?.runOnUiThread { output!!.text = output!!.text.toString() + "\n\n" + txt }
    }
}