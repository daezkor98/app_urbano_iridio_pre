package com.urbanoexpress.iridio3.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.databinding.RowRecoleccionGuiasSinAlistarItemBinding;
import com.urbanoexpress.iridio3.ui.model.PiezaRecolectadaItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RecolecioneGuiasSinAlistarAdapter extends
        RecyclerView.Adapter<RecolecioneGuiasSinAlistarAdapter.ViewHolder> {

    private List<PiezaRecolectadaItem> data;

    public RecolecioneGuiasSinAlistarAdapter(List<PiezaRecolectadaItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowRecoleccionGuiasSinAlistarItemBinding binding =
                RowRecoleccionGuiasSinAlistarItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PiezaRecolectadaItem item = data.get(position);
        holder.binding.barraText.setText(item.getBarra());

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
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RowRecoleccionGuiasSinAlistarItemBinding binding;

        public ViewHolder(RowRecoleccionGuiasSinAlistarItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}