package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.databinding.CodigoBarraRowBinding;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.ui.model.CodigoBarraItem;

/**
 * Created by mick on 12/08/16.
 */

public class CodigoBarraAdapter extends RecyclerView.Adapter<CodigoBarraAdapter.ViewHolder> {

    private List<CodigoBarraItem> data;
    private LayoutInflater inflater;
    private Context context;
    private OnClickItemListener listener;

    public CodigoBarraAdapter(Context context, List<CodigoBarraItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CodigoBarraRowBinding binding = CodigoBarraRowBinding.inflate(inflater, parent, false);
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

    public void setListener(OnClickItemListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CodigoBarraRowBinding binding;

        public ViewHolder(CodigoBarraRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> listener.onClickItem(v, getBindingAdapterPosition()));

            binding.containerImgIcon.setOnClickListener(v ->
                    listener.onClickIcon(v, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            CodigoBarraItem item = data.get(position);
            binding.lblCodigoBarra.setText(item.getCodigo());
            binding.bgLinearLayout.setBackgroundColor(item.getBackgroundColor());
            Glide.with(context)
                    .load(item.getIcon())
                    .into(binding.imgIcon);
        }
    }

}
