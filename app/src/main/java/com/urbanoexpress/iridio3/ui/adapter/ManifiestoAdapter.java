package com.urbanoexpress.iridio3.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ItemEliminarManifiestoBinding;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.ui.model.ManifiestoItem;
import com.urbanoexpress.iridio3.util.AnimationUtils;
import com.urbanoexpress.iridio3.util.CommonUtils;

import java.util.List;

/**
 * Created by mick on 12/10/16.
 */

public class ManifiestoAdapter extends RecyclerView.Adapter<ManifiestoAdapter.ViewHolder> {

    private List<ManifiestoItem> data;
    private OnClickItemListener listener;

    public ManifiestoAdapter(List<ManifiestoItem> data, OnClickItemListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemEliminarManifiestoBinding binding = ItemEliminarManifiestoBinding
                .inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ManifiestoItem item = data.get(position);
        holder.binding.lblManifiesto.setText(item.getIdManifiesto());
        if (item.getDescripcion().isEmpty()) {
            holder.binding.lblDescripcion.setVisibility(View.GONE);
        } else {
            holder.binding.lblDescripcion.setText(item.getDescripcion());
        }
        holder.binding.imgDelete.setBackground(CommonUtils.changeColorDrawable(
                holder.itemView.getContext(), R.drawable.ic_delete_white, R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemEliminarManifiestoBinding binding;

        public ViewHolder(ItemEliminarManifiestoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickIcon(v, getAdapterPosition());
                }
            });

            binding.imgDelete.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(), v,
                                1.0f, 1.2f, 1.0f, 1.2f);
                    } else if (event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL) {
                        AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(), v,
                                1.2f, 1.0f, 1.2f, 1.0f);
                    }
                    return false;
                }
            });
        }
    }
}