package com.urbanoexpress.iridio3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio3.databinding.RowRevenueDayBinding


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
/*TODO: Remove*/
class DayRevenueAdapter : RecyclerView.Adapter<DayRevenueAdapter.ViewHolder>() {

    inner class ViewHolder(val bind: RowRevenueDayBinding) : RecyclerView.ViewHolder(bind.root) {

        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = RowRevenueDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 6
}