package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pe.databinding.ItemContenedorRecoleccionBinding;
import com.urbanoexpress.iridio3.pe.ui.model.ContenedorRecoleccionItem;

import java.util.List;

public class ContenedoresRecoleccionAdapter extends RecyclerView.Adapter<ContenedoresRecoleccionAdapter.ViewHolder> {

    private List<ContenedorRecoleccionItem> data;
    private OnContenedorRecoleccionListener listener;

    public ContenedoresRecoleccionAdapter(List<ContenedorRecoleccionItem> data,
                                          OnContenedorRecoleccionListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemContenedorRecoleccionBinding binding = ItemContenedorRecoleccionBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContenedorRecoleccionItem item = data.get(position);
        holder.binding.barraText.setText(item.getBarra());
        holder.binding.descriptionText.setText(item.getGuias() + " guías • " + item.getPiezas() + " piezas");
        holder.binding.selectCheckBox.setChecked(item.isSelected());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemContenedorRecoleccionBinding binding;

        public ViewHolder(ItemContenedorRecoleccionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                data.get(position).setSelected(!data.get(position).isSelected());
                notifyItemChanged(position);
                listener.onSelectionContenedorChanged(position);
            });
        }
    }

    public interface OnContenedorRecoleccionListener {
        void onSelectionContenedorChanged(int position);
    }
}