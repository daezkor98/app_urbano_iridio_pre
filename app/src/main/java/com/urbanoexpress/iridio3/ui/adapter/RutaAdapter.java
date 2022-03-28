package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.RutaRowBinding;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.ui.model.RutaItem;
import com.urbanoexpress.iridio3.util.OnTouchItemRutasListener;

/**
 * Created by mick on 22/06/16.
 */
public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.ViewHolder>
        implements OnTouchItemRutasListener {

    private Context context;
    private List<RutaItem> data;
    private LayoutInflater inflater;
    private OnClickGuiaItemListener listener;

    public RutaAdapter(Context context, OnClickGuiaItemListener listener, List<RutaItem> data) {
        this.data = data;
        this.context = context;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RutaRowBinding binding = RutaRowBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RutaItem item = data.get(position);
        holder.binding.txtGuia.setText(item.getGuia());
        holder.binding.txtDistrito.setText(item.getDistrito());
        holder.binding.txtDireccion.setText(item.getDireccion());
        holder.binding.txtHoraLlegadaEstimada.setText(item.getHoraLlegadaEstimada());
        holder.binding.lblPiezas.setText(item.getTipoRuta());
        holder.binding.bgLinearLayout.setBackgroundColor(item.getBackgroundColor());
        holder.binding.txtHoraLlegadaEstimada.setTextColor(item.getLblColorHorario());

        if (item.isShowIconGestionGuia()) {
            switch (item.getGestionEfectiva()) {
                case Ruta.ResultadoGestion.NO_DEFINIDO:
                    holder.binding.imgCheckGestionEfectiva.setVisibility(View.GONE);
                    break;
                case Ruta.ResultadoGestion.EFECTIVA_COMPLETA:
                    holder.binding.imgCheckGestionEfectiva.setVisibility(View.VISIBLE);
                    holder.binding.imgCheckGestionEfectiva.setBackgroundResource(
                            R.drawable.bg_circle_checkpoint_entrega);
                    break;
                case Ruta.ResultadoGestion.EFECTIVA_PARCIAL:
                    holder.binding.imgCheckGestionEfectiva.setVisibility(View.VISIBLE);
                    holder.binding.imgCheckGestionEfectiva.setBackgroundResource(
                            R.drawable.bg_circle_checkpoint_entrega_parcial);
                    break;
                case Ruta.ResultadoGestion.NO_EFECTIVA:
                    holder.binding.imgCheckGestionEfectiva.setVisibility(View.VISIBLE);
                    holder.binding.imgCheckGestionEfectiva.setBackgroundResource(
                            R.drawable.bg_circle_checkpoint_no_entrega);
                    break;
            }
        }

        if (item.isShowCounterItem()) {
            holder.binding.boxCounterItem.setVisibility(View.VISIBLE);
            holder.binding.lblCounterItem.setText(item.getCounterItem());
        } else {
            holder.binding.boxCounterItem.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getIcon())
                .into(holder.binding.imgLinea);

        if (item.getIconTipoEnvio() > 0) {
            holder.binding.imgTipoEnvio.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getIconTipoEnvio())
                    .into(holder.binding.imgTipoEnvio);
        } else {
            holder.binding.imgTipoEnvio.setVisibility(View.GONE);
        }

        if (item.isShowImportePorCobrar()) {
            holder.binding.btnImportePorCobrar.setVisibility(View.VISIBLE);
            holder.binding.lblSimboloMoneda.setText(item.getSimboloMoneda());
        } else {
            holder.binding.btnImportePorCobrar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(data, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(data, i, i - 1);
//            }
//        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSelect(View view, int position, boolean isSelected) {

    }

    @Override
    public void onItemSelectChanged(RecyclerView.ViewHolder view, int actionState) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RutaRowBinding binding;

        public ViewHolder(RutaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onClickGuiaItem(v, getBindingAdapterPosition());
            });

            binding.containerImgStatusLocation.setOnClickListener(view -> {
                if (listener != null)
                    listener.onClickGuiaIconLinea(view, getBindingAdapterPosition());
            });

            binding.btnImportePorCobrar.setOnClickListener(view -> {
                if (listener != null)
                    listener.onClickGuiaIconImporte(view, getBindingAdapterPosition());
            });

            binding.imgTipoEnvio.setOnClickListener(view -> {
                if (listener != null)
                    listener.onClickGuiaIconTipoEnvio(view, getBindingAdapterPosition());
            });
        }
    }

    public interface OnClickGuiaItemListener {
        void onClickGuiaItem(View view, int position);
        void onClickGuiaIconLinea(View view, int position);
        void onClickGuiaIconImporte(View view, int position);
        void onClickGuiaIconTipoEnvio(View view, int position);
    }

}