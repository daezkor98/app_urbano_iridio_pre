package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ItemGuiaRecolectadaBinding;
import com.urbanoexpress.iridio3.pre.ui.model.PiezaRecolectadaItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GuiasRecolectadasAdapter extends RecyclerView.Adapter<GuiasRecolectadasAdapter.ViewHolder> {

    private Context context;
    private List<PiezaRecolectadaItem> data;
    private LayoutInflater inflater;
    private OnGuiaRecolectadaListener listener;

    public GuiasRecolectadasAdapter(Context context, List<PiezaRecolectadaItem> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGuiaRecolectadaBinding binding = ItemGuiaRecolectadaBinding
                .inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PiezaRecolectadaItem item = data.get(position);
        holder.binding.barraText.setText(item.getBarra());
        holder.binding.selectCheckBox.setChecked(item.isSelected());
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

    public void setListener(OnGuiaRecolectadaListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemGuiaRecolectadaBinding binding;

        public ViewHolder(ItemGuiaRecolectadaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                data.get(position).setSelected(!data.get(position).isSelected());
                notifyItemChanged(position);
                listener.onSelectionPiezaChanged(position);
            });

            this.binding.moreActionsButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), this.binding.moreActionsButton);
                popup.inflate(R.menu.options_menu_piezas_recolectadas);
                popup.getMenu().findItem(R.id.action_delete).setVisible(false);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        listener.onEditarPiezaClick(getBindingAdapterPosition());
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }

    public interface OnGuiaRecolectadaListener {
        void onSelectionPiezaChanged(int position);
        void onEditarPiezaClick(int position);
    }
}