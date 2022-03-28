package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.databinding.GestionGuiaHabilitantesRowBinding;

import java.util.List;

/**
 * Created by zerocul on 13/11/17.
 */

public class GestionGEHabilitanteAdapter extends RecyclerView.Adapter<GestionGEHabilitanteAdapter.ViewHolder> {

    private final String TAG = ParadaProgramadaAdapter.class.getSimpleName();
    private Context context;
    private List<String> data;
    private LayoutInflater inflater;

    public GestionGEHabilitanteAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GestionGuiaHabilitantesRowBinding binding =
                GestionGuiaHabilitantesRowBinding.inflate(inflater, parent, false);
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

        private GestionGuiaHabilitantesRowBinding binding;

        public ViewHolder(GestionGuiaHabilitantesRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(int position) {
            String descripcion = data.get(position);
            binding.descriptionText.setText(descripcion);
        }
    }
}