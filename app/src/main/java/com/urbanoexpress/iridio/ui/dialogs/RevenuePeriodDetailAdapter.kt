package com.urbanoexpress.iridio.ui.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.urbanoexpress.iridio.databinding.RowRevenuePeriodDetailBinding


/**
 * Created by Brandon Quintanilla on March/17/2022.
 */
class RevenuePeriodDetailAdapter : RecyclerView.Adapter<RevenuePeriodDetailAdapter.ViewHolder>() {

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

    }

    //TODO request data from sevice
    override fun getItemCount(): Int = 15
}