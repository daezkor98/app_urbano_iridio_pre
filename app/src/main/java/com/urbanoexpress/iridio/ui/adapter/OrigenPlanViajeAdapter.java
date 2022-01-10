package com.urbanoexpress.iridio.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.OrigenPlanViajeRowBinding;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio.ui.model.OrigenPlanViajeItem;

import java.util.List;

/**
 * Created by mick on 27/04/17.
 */

public class OrigenPlanViajeAdapter extends RecyclerView.Adapter<OrigenPlanViajeAdapter.ViewHolder> {

    private Context context;
    private List<OrigenPlanViajeItem> data;
    private LayoutInflater inflater;
    private OnClickItemListener listener;

    public OrigenPlanViajeAdapter(Context context, List<OrigenPlanViajeItem> data,
                                  OnClickItemListener listener) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrigenPlanViajeRowBinding binding = OrigenPlanViajeRowBinding.inflate(inflater, parent, false);
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

        private OrigenPlanViajeRowBinding binding;

        public ViewHolder(OrigenPlanViajeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> listener.onClickItem(v, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            OrigenPlanViajeItem item = data.get(position);
            binding.txtOrigen.setText(item.getOrigen());
            binding.txtDescripcion.setText(item.getDescripcionRuta());
            binding.txtFechaCreacion.setText(item.getFecha());

            binding.bgLinearLayout.setBackgroundColor(item.getBackgroundColor());

            Glide.with(context)
                    .load(R.drawable.ic_car_plan_viaje)
                    .into(binding.imgCar);
        }
    }
}
