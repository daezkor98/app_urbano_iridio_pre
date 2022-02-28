package com.urbanoexpress.iridio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio.databinding.RowRevenueWeekBinding


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
class RevenueWeeksAdapter : RecyclerView.Adapter<RevenueWeeksAdapter.ViewHolder>() {

    inner class ViewHolder(val bind: RowRevenueWeekBinding) : RecyclerView.ViewHolder(bind.root) {

        init {
            bind.itemParent.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val bind = RowRevenueWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 4 //TODO replace
}