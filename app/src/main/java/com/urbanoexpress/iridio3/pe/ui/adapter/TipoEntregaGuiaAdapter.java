package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pe.databinding.TipoEntregaGuiaRowBinding;
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem;

import java.util.List;

public class TipoEntregaGuiaAdapter extends RecyclerView.Adapter<TipoEntregaGuiaAdapter.ViewHolder> {

    private List<MotivoDescargaItem> data;

    public TipoEntregaGuiaAdapter(List<MotivoDescargaItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TipoEntregaGuiaRowBinding binding = TipoEntregaGuiaRowBinding.inflate(
                LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MotivoDescargaItem item = data.get(position);
        holder.binding.lblDescripcion.setText(item.getDescripcion());
        if (data.get(position).isSelected()) {
            holder.binding.bgLinearLayout.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            holder.binding.bgLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TipoEntregaGuiaRowBinding binding;

        public ViewHolder(TipoEntregaGuiaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
