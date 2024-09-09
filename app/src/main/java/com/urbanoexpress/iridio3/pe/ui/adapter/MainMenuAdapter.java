package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pe.databinding.MainMenuRowBinding;
import com.urbanoexpress.iridio3.pe.model.NavigationMenuModel;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;

import java.util.List;

/**
 * Created by mick on 19/07/17.
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    private List<NavigationMenuModel> data;
    private LayoutInflater inflater;
    private Context context;
    private OnClickItemListener listener;

    public MainMenuAdapter(Context context, List<NavigationMenuModel> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainMenuRowBinding binding = MainMenuRowBinding.inflate(inflater, parent, false);
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

        private MainMenuRowBinding binding;

        public ViewHolder(MainMenuRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.boxItem.setOnClickListener(v -> listener.onClickItem(v, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            NavigationMenuModel item = data.get(position);
            binding.lblTitulo.setText(item.getTitle());
            binding.lblDescripcion.setText(item.getDescription());
            binding.lblBadgeNotification.setText(item.getBadgeText());

            if (CommonUtils.isAndroidLollipop()) {
                binding.boxContent.setClipToOutline(true);
            }

            if (item.getBadgeText().isEmpty() || item.getBadgeText().equals("0")) {
                binding.boxBadgeNotification.setVisibility(View.GONE);
            } else {
                binding.boxBadgeNotification.setVisibility(View.VISIBLE);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (position == 0) {
                layoutParams.setMargins(
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 5));
            } else if (position == getItemCount() - 1) {
                layoutParams.setMargins(
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 5),
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 10));
            } else {
                layoutParams.setMargins(
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 5),
                        MetricsUtils.dpToPx(context, 10),
                        MetricsUtils.dpToPx(context, 5));
            }

            binding.boxContent.setLayoutParams(layoutParams);

            Glide.with(context)
                    .load(item.getIcon())
                    .dontAnimate()
                    .into(binding.iconMenu);
        }
    }
}
