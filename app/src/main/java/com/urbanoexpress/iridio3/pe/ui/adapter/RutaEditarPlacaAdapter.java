package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.urbanoexpress.iridio3.pe.databinding.RutaEditarPlacaRowBinding;
import com.urbanoexpress.iridio3.pe.ui.model.RutaEditarPlacaItem;

import java.util.List;

public class RutaEditarPlacaAdapter extends RecyclerView.Adapter<RutaEditarPlacaAdapter.ViewHolder> {

    private Context context;
    private List<RutaEditarPlacaItem> data;
    private LayoutInflater inflater;
    private OnCheckedChangeItemListener listener;

    private boolean onBind;

    public RutaEditarPlacaAdapter(Context context, List<RutaEditarPlacaItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RutaEditarPlacaRowBinding binding = RutaEditarPlacaRowBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(OnCheckedChangeItemListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RutaEditarPlacaRowBinding binding;

        public ViewHolder(RutaEditarPlacaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.rBtnChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!onBind) {
                    listener.onCheckedChanged(buttonView, isChecked, getBindingAdapterPosition());
                }
            });
        }

        public void bindView(int position) {
            RutaEditarPlacaItem item = data.get(position);
            binding.lblRuta.setText(item.getIdRuta());
            onBind = true;
            binding.rBtnChecked.setChecked(false);
            onBind = false;
        }
    }

    public interface OnCheckedChangeItemListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }

}