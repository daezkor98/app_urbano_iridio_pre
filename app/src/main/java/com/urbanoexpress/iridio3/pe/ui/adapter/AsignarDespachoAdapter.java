package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.AsignarDespachoRowBinding;
import com.urbanoexpress.iridio3.pe.ui.model.AsignarDespachoItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;

import java.util.List;

/**
 * Created by mick on 30/11/16.
 */

public class AsignarDespachoAdapter extends RecyclerView.Adapter<AsignarDespachoAdapter.ViewHolder> {

    private List<AsignarDespachoItem> data;
    private LayoutInflater inflater;
    private Context context;

    public AsignarDespachoAdapter(Context context, List<AsignarDespachoItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AsignarDespachoRowBinding binding = AsignarDespachoRowBinding
                .inflate(inflater, parent, false);
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

        private AsignarDespachoRowBinding binding;

        public ViewHolder(AsignarDespachoRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(int position) {
            AsignarDespachoItem item = data.get(position);
            binding.lblFecha.setText(item.getFecha());
            binding.lblDespacho.setText(item.getIdDespacho());
            binding.lblOrigen.setText(item.getOrigen());
            binding.lblDestino.setText(item.getDestino());

            binding.imgArrow.setImageDrawable(
                    CommonUtils.changeColorDrawable(context,
                            item.getImgArrow(),
                            R.color.black));

            if (item.getSelected()) {
                binding.imgArrow.setVisibility(View.VISIBLE);
            } else {
                binding.imgArrow.setVisibility(View.GONE);
            }
        }
    }
}