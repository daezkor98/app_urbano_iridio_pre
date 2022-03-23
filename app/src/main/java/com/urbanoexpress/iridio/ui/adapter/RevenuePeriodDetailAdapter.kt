package com.urbanoexpress.iridio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio.databinding.RowRevenuePeriodDetailBinding
import com.urbanoexpress.iridio.model.dto.RevenueDay
import com.urbanoexpress.iridio.model.dto.completeDays
import com.urbanoexpress.iridio.urbanocore.values.weekDays

/**
 * Created by Brandon Quintanilla on March/17/2022.
 */
class RevenuePeriodDetailAdapter : RecyclerView.Adapter<RevenuePeriodDetailAdapter.ViewHolder>() {

    var revenueDays: ArrayList<RevenueDay> = ArrayList()
        set(value) {
            field = value
            revenueDays.completeDays()
            this.notifyDataSetChanged()
        }

    inner class ViewHolder(val bind: RowRevenuePeriodDetailBinding) :
        RecyclerView.ViewHolder(bind.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = RowRevenuePeriodDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = revenueDays[position]
        holder.bind.tvDay.text = "${weekDays[item.dia_semana]}"
        holder.bind.tvEntregadosGuias.text = "${item.entregas}"
        holder.bind.tvEntregadosMonto.text = "S/ ${item.monto_entregas}"
        holder.bind.tvVisitadosGuias.text = "${item.no_entregas}"
        holder.bind.tvVisitadosMonto.text = "S/ ${item.monto_no_entregas}"
    }

    override fun getItemCount(): Int = revenueDays.size
}