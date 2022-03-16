package com.urbanoexpress.iridio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio.databinding.RowRevenueWeekBinding
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.urbanocore.OnItemClick
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick

/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
class WeeksRevenueAdapter : RecyclerView.Adapter<WeeksRevenueAdapter.ViewHolder>() {

    var onItemClick: OnItemClick = null

    var periods: ArrayList<Period> = ArrayList()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    inner class ViewHolder(val bind: RowRevenueWeekBinding) : RecyclerView.ViewHolder(bind.root) {

        init {
            bind.itemParent.onExclusiveClick {
                onItemClick?.invoke(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val bind = RowRevenueWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val period = periods[position]
        holder.bind.tvDaysIntervalBegg.text = "Desde: ${period.beggningDay}"
        holder.bind.tvDaysIntervalEnd.text = "Hasta: ${period.endingDay}"
        holder.bind.tvRevenue.text = "S/ ${period.weekPeriodRevenue}"
    }
    override fun getItemCount(): Int = periods.size
}