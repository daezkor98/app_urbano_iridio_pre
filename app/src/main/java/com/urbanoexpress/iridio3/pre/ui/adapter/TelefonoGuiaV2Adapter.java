package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pre.databinding.ItemTelefonoGuiaBinding;
import com.urbanoexpress.iridio3.pre.databinding.ItemTelefonoGuiaHeaderBinding;
import com.urbanoexpress.iridio3.pre.ui.model.TelefonoGuiaItem;
import com.urbanoexpress.iridio3.pre.util.AnimationUtils;

import java.util.List;

public class TelefonoGuiaV2Adapter extends RecyclerView.Adapter {

    private List<WrapperItem> data;
    private OnTelefonoGuiaListener listener;

    public TelefonoGuiaV2Adapter(List<WrapperItem> data, OnTelefonoGuiaListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WrapperItem.TYPE_HEADER:
                ItemTelefonoGuiaHeaderBinding headerBinding = ItemTelefonoGuiaHeaderBinding
                        .inflate(layoutInflater, parent, false);
                return new HeaderViewHolder(headerBinding);
            default:
                ItemTelefonoGuiaBinding telefonoGuiaBinding = ItemTelefonoGuiaBinding
                        .inflate(layoutInflater, parent, false);
                return new PhoneViewHolder(telefonoGuiaBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case WrapperItem.TYPE_HEADER:
                ((HeaderViewHolder) holder).bindView(position);
                break;
            case WrapperItem.TYPE_PHONE:
                ((PhoneViewHolder) holder).bindView(position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ItemTelefonoGuiaHeaderBinding binding;

        public HeaderViewHolder(ItemTelefonoGuiaHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(int position) {
            TelefonoGuiaHeaderItem item = (TelefonoGuiaHeaderItem) data.get(position);
            binding.lblTitle.setText(item.getTitle());
        }
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder {

        private ItemTelefonoGuiaBinding binding;

        public PhoneViewHolder(ItemTelefonoGuiaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.actionLlamarLayout.setOnClickListener(v ->
                    listener.onBtnLlamarClick(getAdapterPosition()));

            this.binding.actionLlamarLayout.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(),
                            this.binding.icon,
                            1.0f, 1.3f, 1.0f, 1.3f);
                } else if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(),
                            this.binding.icon,
                            1.3f, 1.0f, 1.3f, 1.0f);
                }
                return false;
            });
        }

        public void bindView(int position) {
            TelefonoGuiaItem item = (TelefonoGuiaItem) data.get(position);
            binding.lblNumero.setText(item.getTelefono());

            if (item.getContacto().isEmpty()) {
                binding.lblContacto.setVisibility(View.GONE);
            } else {
                binding.lblContacto.setVisibility(View.VISIBLE);
                binding.lblContacto.setText(item.getContacto());
            }
        }
    }

    public interface OnTelefonoGuiaListener {
        void onBtnLlamarClick(int position);
    }

    public interface WrapperItem {
        int TYPE_HEADER = 100;
        int TYPE_PHONE = 101;

        int getType();
    }

}