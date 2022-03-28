package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.urbanoexpress.iridio3.databinding.DespachoRowBinding;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemDespachoListener;
import com.urbanoexpress.iridio3.ui.model.DespachoItem;

/**
 * Created by mick on 02/06/16.
 */
public class DespachoAdapter extends RecyclerView.Adapter<DespachoAdapter.ViewHolder> {

    private List<DespachoItem> data;
    private LayoutInflater inflater;
    private Context context;
    private OnClickItemDespachoListener listener;

    private int menuActionMode;

    public DespachoAdapter(Context context, List<DespachoItem> data, int menuActionMode) {
        this.data = data;
        this.context = context;
        this.menuActionMode = menuActionMode;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DespachoRowBinding binding = DespachoRowBinding.inflate(inflater, parent, false);
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

    public void setListener(OnClickItemDespachoListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private DespachoRowBinding binding;

        public ViewHolder(DespachoRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v ->
                    listener.onClickItem(v, getBindingAdapterPosition(), menuActionMode));
        }

        public void bindView(int position) {
            DespachoItem item = data.get(position);
            binding.lblDespacho.setText(item.getDespacho());
            binding.lblOrigen.setText(item.getOrigen());
            binding.lblDestino.setText(item.getDestino());
            binding.lblPiezas.setText(item.getPiezas());
            binding.lblGuia.setText(item.getGuia());

            binding.imgArrow.setImageDrawable(ContextCompat.getDrawable(context, item.getImgArrow()));

            if (item.getSelected()) {
                binding.imgArrow.setVisibility(View.VISIBLE);
            } else {
                binding.imgArrow.setVisibility(View.GONE);
            }
        }
    }
}