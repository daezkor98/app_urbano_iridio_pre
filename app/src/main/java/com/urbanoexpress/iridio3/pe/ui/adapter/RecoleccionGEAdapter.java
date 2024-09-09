package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.pe.databinding.RecoleccionGeRowBinding;
import com.urbanoexpress.iridio3.pe.ui.model.RecoleccionGEItem;

import java.util.List;

/**
 * Created by mick on 03/08/17.
 */

public class RecoleccionGEAdapter extends RecyclerView.Adapter<RecoleccionGEAdapter.ViewHolder> {

    private List<RecoleccionGEItem> data;
    private LayoutInflater inflater;
    private Context context;
    private OnRecoleccionGEItemClickListener listener;

    public RecoleccionGEAdapter(Context context, List<RecoleccionGEItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecoleccionGeRowBinding binding = RecoleccionGeRowBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecoleccionGEItem recoleccionGEItem = data.get(position);
        holder.binding.guiaText.setText(recoleccionGEItem.getGuiaElectronica());
        holder.binding.piezaText.setText(recoleccionGEItem.getPiezas());

        if (recoleccionGEItem.isSelected()) {
            holder.binding.chkSeleccionarGE.setChecked(true);
            holder.binding.chkSeleccionarGE.setSelected(true);
            holder.binding.guiaText.setTypeface(holder.binding.guiaText.getTypeface(), Typeface.BOLD);
            holder.binding.piezaText.setTypeface(holder.binding.piezaText.getTypeface(), Typeface.BOLD);
        } else if (recoleccionGEItem.isGuiaNoRecolectada()) {
            holder.binding.chkSeleccionarGE.setChecked(recoleccionGEItem.isSelected());
            holder.binding.guiaText.setTypeface(holder.binding.guiaText.getTypeface(), Typeface.BOLD);
            holder.binding.piezaText.setTypeface(holder.binding.piezaText.getTypeface(), Typeface.BOLD);
        } else {
            holder.binding.chkSeleccionarGE.setChecked(false);
            holder.binding.guiaText.setTypeface(null, Typeface.NORMAL);
            holder.binding.piezaText.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setListener(OnRecoleccionGEItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final RecoleccionGeRowBinding binding;

        public ViewHolder(RecoleccionGeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onLongClickGE(v, getAdapterPosition());
                }
                return false;
            });

            binding.chkSeleccionarGE.setOnCheckedChangeListener((buttonView, isChecked) -> {
                data.get(getAdapterPosition()).setSelected(isChecked);

                if (isChecked) {
                    binding.guiaText.setTypeface(binding.guiaText.getTypeface(), Typeface.BOLD);
                    binding.piezaText.setTypeface(binding.piezaText.getTypeface(), Typeface.BOLD);
                } else {
                    if (!data.get(getAdapterPosition()).isGuiaNoRecolectada()) {
                        binding.guiaText.setTypeface(null, Typeface.NORMAL);
                        binding.piezaText.setTypeface(null, Typeface.NORMAL);
                    }
                }

                if (listener != null) {
                    listener.onChkChangeGE(binding.chkSeleccionarGE, getAdapterPosition());
                }
            });
        }
    }

    public interface OnRecoleccionGEItemClickListener {
        void onChkChangeGE(View view, int position);
        void onLongClickGE(View view, int position);
    }

}