package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import com.urbanoexpress.iridio3.databinding.ParadaProgramadaRowBinding;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.ui.model.ParadaProgramadaItem;

/**
 * Created by mick on 25/05/16.
 */
public class ParadaProgramadaAdapter extends RecyclerView.Adapter<ParadaProgramadaAdapter.ViewHolder> {

    private Context context;
    private List<ParadaProgramadaItem> data;
    private LayoutInflater inflater;
    private OnClickItemListener listener;

    public ParadaProgramadaAdapter(Context context, List<ParadaProgramadaItem> data) {
        this.data = data;
        this.context = context;
        this.listener = (OnClickItemListener) context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ParadaProgramadaRowBinding binding = ParadaProgramadaRowBinding.inflate(inflater, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private ParadaProgramadaRowBinding binding;

        public ViewHolder(ParadaProgramadaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onClickItem(v, getBindingAdapterPosition());
            });
        }

        public void bindView(int position) {
            ParadaProgramadaItem item = data.get(position);
            binding.lblAgencia.setText(item.getRuta());
            binding.lblHoraEstimada.setText(item.getHoraEstimada());
            binding.lblHoraLlegada.setText(item.getHoraLlegada());
            binding.lblHoraLlegada.setTextColor(ContextCompat.getColor(context, item.getColorHoraLlegada()));
            binding.lblHoraSalida.setText(item.getHoraSalida());
        }
    }

}
