package com.urbanoexpress.iridio3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio3.databinding.RowRevenuePeriodDetailBinding
import com.urbanoexpress.iridio3.model.dto.RevenueDay
import com.urbanoexpress.iridio3.urbanocore.extentions.*
import com.urbanoexpress.iridio3.urbanocore.values.weekDays

/**
 * Created by Brandon Quintanilla on March/17/2022.
 */
class RevenuePeriodDetailAdapter : RecyclerView.Adapter<RevenuePeriodDetailAdapter.ViewHolder>() {

    var revenueDays: List<RevenueDay> = ArrayList()
        set(value) {
            field = value
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

        item.notWorkingMessage.ifNull {
            holder.bind.tvEntregadosGuias.assertText("${item.entregas}") //=
            holder.bind.tvEntregadosMonto.assertText("S/ ${item.monto_entregas}")
            holder.bind.tvVisitadosGuias.assertText("${item.no_entregas}")
            holder.bind.tvVisitadosMonto.assertText("S/ ${item.monto_no_entregas}")
            holder.bind.tvNoTrabajo.gone()
            holder.bind.dividerHeaderH.visisble()
        }.ifNotNull { msg ->
            holder.bind.tvEntregadosGuias.gone()
            holder.bind.tvEntregadosMonto.gone()
            holder.bind.tvVisitadosGuias.gone()
            holder.bind.tvVisitadosMonto.gone()
            holder.bind.dividerHeaderH.gone()
            holder.bind.tvNoTrabajo.assertText(msg)
        }
    }

    override fun getItemCount(): Int = revenueDays.size
}