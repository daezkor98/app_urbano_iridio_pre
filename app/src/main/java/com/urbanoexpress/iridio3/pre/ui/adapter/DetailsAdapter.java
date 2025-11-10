package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ItemDetailsBinding;
import com.urbanoexpress.iridio3.pre.ui.model.DetailsItem;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private List<DetailsItem> data;
    private LayoutInflater inflater;

    public DetailsAdapter(Context context, List<DetailsItem> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailsBinding binding = ItemDetailsBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailsItem item = data.get(position);
        holder.binding.titleText.setText(item.getTitle());
        holder.binding.descriptionText.setText(item.getDescription());

        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.bg_list_even);
        } else {
            holder.itemView.setBackgroundResource(R.color.bg_list_odd);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemDetailsBinding binding;

        public ViewHolder(ItemDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}