package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.databinding.ItemGalleryButtonBinding;
import com.urbanoexpress.iridio3.databinding.ItemGalleryPhotoBinding;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter {

    private static final String TAG = "GalleryAdapter";

    private Context context;
    private List<GalleryWrapperItem> data;
    private LayoutInflater inflater;
    private OnGalleryListener listener;

    public GalleryAdapter(Context context, List<GalleryWrapperItem> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case GalleryWrapperItem.TYPE_BUTTON:
                ItemGalleryButtonBinding buttonBinding = ItemGalleryButtonBinding
                        .inflate(inflater, parent, false);
                return new ButtonViewHolder(buttonBinding, listener);
            default:
                ItemGalleryPhotoBinding photoBinding = ItemGalleryPhotoBinding
                        .inflate(inflater, parent, false);
                return new PhotoViewHolder(photoBinding, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case GalleryWrapperItem.TYPE_BUTTON:
                ((ButtonViewHolder) holder).bindView(position);
                break;
            case GalleryWrapperItem.TYPE_PHOTO:
                ((PhotoViewHolder) holder).bindView(position);
                break;
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

    public void setListener(OnGalleryListener listener) {
        this.listener = listener;
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {

        public ItemGalleryButtonBinding binding;
        public OnGalleryListener listener;

        public ButtonViewHolder(ItemGalleryButtonBinding binding, OnGalleryListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            this.binding.btnCamera.setOnClickListener(v ->
                    listener.onButtonClick(getBindingAdapterPosition()));
        }

        void bindView(int position) {
            GalleryButtonItem photo = (GalleryButtonItem) data.get(position);

            Glide.with(context)
                    .load(photo.getResourceId())
                    .into(binding.btnCamera);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ItemGalleryPhotoBinding binding;
        public OnGalleryListener listener;

        public PhotoViewHolder(ItemGalleryPhotoBinding binding, OnGalleryListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            this.binding.btnDelete.setOnClickListener(v ->
                    listener.onDeleteImageClick(getBindingAdapterPosition()));
        }

        void bindView(int position) {
            GalleryPhotoItem photo = (GalleryPhotoItem) data.get(position);

            Glide.with(context)
                    .load(photo.getPathImage())
                    .into(binding.imgPhoto);
        }
    }

    public interface OnGalleryListener {
        void onButtonClick(int position);
        void onDeleteImageClick(int position);
    }
}
