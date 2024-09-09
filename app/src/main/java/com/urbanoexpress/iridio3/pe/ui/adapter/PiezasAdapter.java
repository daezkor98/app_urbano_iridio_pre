package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ItemPiezaBinding;
import com.urbanoexpress.iridio3.pe.ui.model.PiezaItem;

import org.apache.commons.text.WordUtils;

import java.util.List;

public class PiezasAdapter extends RecyclerView.Adapter<PiezasAdapter.ViewHolder> {

    private List<PiezaItem> data;
    private OnPiezaListener listener;

    public PiezasAdapter(List<PiezaItem> data, OnPiezaListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPiezaBinding binding = ItemPiezaBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PiezaItem item = data.get(position);
        holder.binding.lblBarra.setText(item.getBarra());
        holder.binding.lblEstado.setText(WordUtils.capitalize(item.getEstado(), new char[]{}));
        holder.binding.lblFecha.setText(item.getFecha());
        holder.binding.chkPieza.setChecked(item.isSelected());
        holder.binding.iconNotAvailablePck.setVisibility(item.isOnMyRoute() ? View.GONE : View.VISIBLE);

        if (!item.isOnMyRoute()) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_package_not_avalible)
                    .dontAnimate()
                    .into(holder.binding.iconNotAvailablePck);
        }

        if (item.isSelectable()) {
            holder.binding.chkPieza.setEnabled(true);
        } else {
            holder.binding.chkPieza.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemPiezaBinding binding;

        public ViewHolder(ItemPiezaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPiezaClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnPiezaListener {
        void onPiezaClick(int position);
    }
}