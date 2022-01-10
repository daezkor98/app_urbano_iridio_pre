package com.urbanoexpress.iridio.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio.databinding.ItemCountryBottomSheetBinding;
import com.urbanoexpress.iridio.ui.model.PaisItem;

import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {

    private List<PaisItem> data;
    private LayoutInflater inflater;
    private Context context;
    private OnCountriesListener listener;

    public CountriesAdapter(Context context, List<PaisItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCountryBottomSheetBinding binding = ItemCountryBottomSheetBinding
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

    public void setListener(OnCountriesListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCountryBottomSheetBinding binding;

        public ViewHolder(ItemCountryBottomSheetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> listener.onCountryClick(getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            PaisItem item = data.get(position);
            binding.lblCountry.setText(item.getNombre());
            Glide.with(context)
                    .load(item.getIcon())
                    .into(binding.iconFlag);
        }
    }

    public interface OnCountriesListener {
        void onCountryClick(int position);
    }

}