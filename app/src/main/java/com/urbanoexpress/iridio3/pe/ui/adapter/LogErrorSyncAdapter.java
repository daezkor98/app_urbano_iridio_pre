package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pe.databinding.LogErrorSyncRowBinding;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.ui.model.LogErrorSyncItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Created by mick on 11/10/16.
 */

public class LogErrorSyncAdapter extends RecyclerView.Adapter<LogErrorSyncAdapter.ViewHolder> {

    private Context context;
    private List<LogErrorSyncItem> data;
    private LayoutInflater inflater;
    private OnClickItemListener listener;

    public LogErrorSyncAdapter(Context context, List<LogErrorSyncItem> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogErrorSyncRowBinding binding = LogErrorSyncRowBinding.inflate(inflater, parent, false);
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

    public void setListener(OnClickItemListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LogErrorSyncRowBinding binding;

        public ViewHolder(LogErrorSyncRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> listener.onClickItem(v, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            LogErrorSyncItem item = data.get(position);
            binding.titleText.setText(item.getTitulo());
            binding.descriptionText.setText(item.getMensaje());

            LocalDateTime ldt = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Long.parseLong(item.getFecha())), ZoneId.systemDefault());
            binding.dateText.setText(ldt.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));

            Glide.with(context).load(item.getResIcon()).into(binding.image);
        }
    }
}
