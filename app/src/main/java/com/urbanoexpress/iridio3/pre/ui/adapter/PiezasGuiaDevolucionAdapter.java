package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ItemPiezaGuiaDevolucionBinding;
import com.urbanoexpress.iridio3.pre.ui.model.PiezaItem;

import org.apache.commons.text.WordUtils;

import java.util.List;

public class PiezasGuiaDevolucionAdapter extends RecyclerView.Adapter<PiezasGuiaDevolucionAdapter.ViewHolder> {

    private List<PiezaItem> data;

    public PiezasGuiaDevolucionAdapter(List<PiezaItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPiezaGuiaDevolucionBinding binding = ItemPiezaGuiaDevolucionBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PiezaItem item = data.get(position);
        holder.binding.lblBarra.setText(item.getBarra());
        holder.binding.lblDescripcion.setText(
                String.format("%s %s %s", WordUtils.capitalize(item.getEstado(), new char[]{}),
                        holder.itemView.getContext().getString(R.string.text_dot),
                        item.getFecha()));

        holder.binding.iconNotAvailablePck.setVisibility(item.isOnMyRoute() ? View.GONE : View.VISIBLE);

        if (!item.isOnMyRoute()) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_package_not_avalible)
                    .dontAnimate()
                    .into(holder.binding.iconNotAvailablePck);
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.bg_list_even);
        } else {
            holder.itemView.setBackgroundResource(R.color.bg_list_odd);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemPiezaGuiaDevolucionBinding binding;

        public ViewHolder(ItemPiezaGuiaDevolucionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}