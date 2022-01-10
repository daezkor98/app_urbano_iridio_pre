package com.urbanoexpress.iridio.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ItemPiezaRecolectadaBinding;
import com.urbanoexpress.iridio.ui.model.PiezaRecolectadaItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PiezasRecolectadasAdapter extends RecyclerView.Adapter<PiezasRecolectadasAdapter.ViewHolder> {

    private List<PiezaRecolectadaItem> data;
    private OnPiezaRecolectadaListener listener;

    public PiezasRecolectadasAdapter(List<PiezaRecolectadaItem> data, OnPiezaRecolectadaListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPiezaRecolectadaBinding binding = ItemPiezaRecolectadaBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PiezaRecolectadaItem item = data.get(position);
        holder.binding.barraText.setText(item.getBarra());
        if (item.getType() == PiezaRecolectadaItem.Type.WYB) {
            String description, fechaSS;
            try {
                fechaSS = LocalDate.parse(item.getFechaSS(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .format(DateTimeFormatter.ofPattern("dd MMM"));
            } catch (DateTimeParseException ex) {
                ex.printStackTrace();
                fechaSS = item.getFechaSS();
            }
            if (item.getFechaSS().isEmpty()) {
                description = item.getEstado() + " • " + item.getPiezas() + " piezas";
            } else {
                description = item.getEstado() + " (" + fechaSS + ") • " + item.getPiezas() + " piezas";
            }
            holder.binding.descriptionText.setText(description);
        } else {
            holder.binding.descriptionText.setText(item.getEstado());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPiezaRecolectadaBinding binding;

        public ViewHolder(ItemPiezaRecolectadaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.moreActionsButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                PiezaRecolectadaItem piezaItem = data.get(position);

                PopupMenu popup = new PopupMenu(itemView.getContext(), this.binding.moreActionsButton);
                popup.inflate(R.menu.options_menu_piezas_recolectadas);
                popup.getMenu().findItem(R.id.action_edit)
                        .setVisible(piezaItem.getType() == PiezaRecolectadaItem.Type.WYB);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        listener.onEditarPiezaClick(position);
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        listener.onEliminarPiezaClick(position);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }

    public interface OnPiezaRecolectadaListener {
        void onEditarPiezaClick(int position);
        void onEliminarPiezaClick(int position);
    }
}