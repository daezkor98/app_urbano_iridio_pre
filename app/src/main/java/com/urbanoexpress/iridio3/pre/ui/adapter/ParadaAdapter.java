package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.RutaRowBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pre.util.OnTouchItemRutasListener;

import java.util.List;
import java.util.Map;


public class ParadaAdapter extends RecyclerView.Adapter<ParadaAdapter.ViewHolder>
        implements OnTouchItemRutasListener {

    private Context context;
    private List<Integer> idsParadas;
    private Map<Integer, List<RutaItem>> mapaParadas;
    private List<RutaItem> data;
    private OnParadaClickListener listener;
    private boolean isActionModeActive = false;

    public ParadaAdapter(Context context, OnParadaClickListener listener,
                         List<Integer> idsParadas, Map<Integer, List<RutaItem>> mapaParadas, List<RutaItem> data) {
        this.context = context;
        this.listener = listener;
        this.idsParadas = idsParadas;
        this.mapaParadas = mapaParadas;
        this.data = data;
    }

    public void setData(List<Integer> idsParadas, Map<Integer, List<RutaItem>> mapaParadas) {
        this.idsParadas = idsParadas;
        this.mapaParadas = mapaParadas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RutaRowBinding binding = RutaRowBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int paradaId = idsParadas.get(position);
        List<RutaItem> guias = mapaParadas.get(paradaId);

        if (data == null || data.isEmpty() || position >= data.size()) {
            return;
        }

        RutaItem item = data.get(position);

        if (guias != null && !guias.isEmpty()) {
            RutaItem primeraGuia = guias.get(0);

            if (guias.size() == 1) {
                holder.binding.txtGuia.setText(primeraGuia.getGuia());
            } else {
                holder.binding.txtGuia.setText("PARADA CON " + guias.size() + " GUÍAS");
            }
            holder.binding.txtDireccion.setText(primeraGuia.getDireccion());
            holder.binding.txtDistrito.setText(primeraGuia.getDistrito());

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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onParadaClick(paradaId, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idsParadas != null ? idsParadas.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSelect(View view, int position, boolean isSelected) {

    }

    @Override
    public void onItemSelectChanged(RecyclerView.ViewHolder view, int actionState) {

    }

    public void setActionModeActive(boolean active) {
        this.isActionModeActive = active;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RutaRowBinding binding;

        public ViewHolder(@NonNull RutaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnParadaClickListener {
        void onParadaClick(int paradaId, int position);
    }
}