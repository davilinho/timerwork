package com.davidroid.worktimer.view

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davidroid.worktimer.R
import com.davidroid.worktimer.dateUtil.DateUtil
import com.davidroid.worktimer.model.AmountDay
import kotlinx.android.synthetic.main.item.view.*

/**
* Created by davidmartin on 5/12/17.
*/
class ItemAdapter(var data: MutableList<AmountDay> = mutableListOf()):
        RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(data[position], position)
    }

    override fun getItemCount() = data.size

    class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        @SuppressLint("SimpleDateFormat", "StringFormatInvalid")
        fun onBind(data: AmountDay, position: Int) {
            with(view) {
                with(data) {
                    action.text = context.getString(R.string.pause_info, position + 1)
                    time.text = DateUtil.getHoursAndMinutes(amount)
                }
            }
        }
    }
}