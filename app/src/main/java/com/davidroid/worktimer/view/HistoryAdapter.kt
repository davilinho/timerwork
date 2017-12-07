package com.davidroid.worktimer.view

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davidroid.worktimer.R
import com.davidroid.worktimer.model.AmountDay
import kotlinx.android.synthetic.main.item.view.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
* Created by davidmartin on 5/12/17.
*/
class HistoryAdapter(var data: MutableList<AmountDay> = mutableListOf()): RecyclerView.Adapter<HistoryAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount() = data.size

    class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        @SuppressLint("SimpleDateFormat")
        fun onBind(data: AmountDay) {
            with(view) {
                with(data) {
                    action.text = SimpleDateFormat("dd/MM/yyyy").format(date)
                    time.text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(amount.toLong()),
                            TimeUnit.MILLISECONDS.toMinutes(amount.toLong())
                                    - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(amount.toLong())))
                }
            }
        }
    }
}