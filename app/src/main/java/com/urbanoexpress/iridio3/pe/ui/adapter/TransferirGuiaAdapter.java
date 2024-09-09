package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pe.databinding.TransferirGuiaRowBinding;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.ui.model.TransferirGuiaItem;

import java.util.List;

/**
 * Created by mick on 12/06/17.
 */

public class TransferirGuiaAdapter extends RecyclerView.Adapter<TransferirGuiaAdapter.ViewHolder> {

    private Context context;
    private List<TransferirGuiaItem> data;
    private LayoutInflater inflater;
    private OnClickItemListener listener;

    public TransferirGuiaAdapter(Context context, List<TransferirGuiaItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = (OnClickItemListener) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TransferirGuiaRowBinding binding = TransferirGuiaRowBinding.inflate(inflater, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TransferirGuiaRowBinding binding;

        public ViewHolder(TransferirGuiaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(view -> listener.onClickItem(view, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            TransferirGuiaItem item = data.get(position);
            binding.lblGuia.setText(item.getGuiaElectronica());
            binding.bgLinearLayout.setBackgroundColor(item.getBackgroundColor());

            if (item.getIdResIconLinea() == -1) {
                binding.imgLinea.setVisibility(View.GONE);
            } else {
                binding.imgLinea.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(item.getIdResIconLinea())
                        .centerCrop()
                        .into(binding.imgLinea);
            }
        }
    }

}