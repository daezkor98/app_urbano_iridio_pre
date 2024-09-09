package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pe.databinding.ItemPremioBinding;
import com.urbanoexpress.iridio3.pe.ui.model.PremioItem;

import java.util.List;

public class PremiosAdapter extends RecyclerView.Adapter<PremiosAdapter.ViewHolder> {

    private List<PremioItem> data;
    private OnPremioListener listener;

    public PremiosAdapter(List<PremioItem> data, OnPremioListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPremioBinding binding = ItemPremioBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PremioItem item = data.get(position);
        holder.binding.lblDescripcion.setText(item.getProducto());
        holder.binding.lblPiezas.setText(item.getPiezas());
        holder.binding.chkPieza.setChecked(item.isSelected());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemPremioBinding binding;

        public ViewHolder(ItemPremioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.itemView.setOnClickListener(v -> listener.onPremioClick(getAdapterPosition()));
        }
    }

    public interface OnPremioListener {
        void onPremioClick(int position);
    }
}