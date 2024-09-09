package com.urbanoexpress.iridio3.pe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio3.pe.databinding.RowRevenueWeekBinding
import com.urbanoexpress.iridio3.pe.model.dto.Period
import com.urbanoexpress.iridio3.pe.model.dto.certEstadosMap
import com.urbanoexpress.iridio3.urbanocore.OnItemClick
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick

/**
 * Created by Brandon Quintanilla on February/28/2022.
 */

class PeriodsRevenueAdapter : RecyclerView.Adapter<PeriodsRevenueAdapter.ViewHolder>() {

    var onItemClick: OnItemClick = null

    var periods: List<Period> = ArrayList()
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
        holder.bind.tvDaysIntervalBegg.text = "Desde: ${period.fecha_inicio}"
        holder.bind.tvDaysIntervalEnd.text = "Hasta: ${period.fecha_fin}"
        holder.bind.tvEstado.text = "${certEstadosMap[period.cert_estado]?.stateName}"
        holder.bind.tvRevenue.text = "S/ ${period.monto}"
    }

    override fun getItemCount(): Int = periods.size
}