package com.example.testprojectsb.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testprojectsb.R
import com.example.testprojectsb.network.model.OrderBookItem
import kotlinx.android.synthetic.main.order_book_viewholder.view.*
import java.util.*
import kotlin.math.absoluteValue

/**
 * Created by grati on 11/22/2019.
 */
class OrderBookAdapter(private val context: Context?): RecyclerView.Adapter<OrderBookAdapter.ViewHolder>() {

    private var elements: List<OrderBookItem> = ArrayList()

    override fun getItemCount(): Int {
        return elements.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(elements[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_book_viewholder, parent, false))
    }

    fun updateElements(mNewDataSet: List<OrderBookItem>) {
        elements = mNewDataSet
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: OrderBookItem) {
            item.ask?.let {
                itemView.price_ask.text = it.price.absoluteValue.toString()
                itemView.amount_ask.text = String.format("%.5f", it.amount.absoluteValue)
            }
            item.bid?.let {
                itemView.price_bid.text = it.price.absoluteValue.toString()
                itemView.amount_bid.text = String.format("%.5f", it.amount.absoluteValue)
            }
        }

    }
}