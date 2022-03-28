package com.urbanoexpress.iridio3.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.databinding.NotificacionRutaRowBinding;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.ui.model.NotificacionRutaItem;

import java.util.List;

public class NotificacionesRutaAdapter extends RecyclerView.Adapter<NotificacionesRutaAdapter.ViewHolder> {

    private List<NotificacionRutaItem> data;
    private LayoutInflater inflater;
    private Context context;
    private OnClickItemListener listener;

    public NotificacionesRutaAdapter(Context context, List<NotificacionRutaItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificacionRutaRowBinding binding = NotificacionRutaRowBinding.inflate(inflater, parent, false);
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

        private NotificacionRutaRowBinding binding;

        public ViewHolder(NotificacionRutaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> listener.onClickItem(v, getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            NotificacionRutaItem item = data.get(position);
            binding.lblTituloNotificacion.setText(item.getTitulo());
            binding.lblMsgNotificacion.setText(item.getMensaje());
            binding.lblFechaCHK.setText(item.getFechaCHK());
            binding.bgIcon.setBackgroundResource(item.getBgIcon());
            binding.bgLinearLayout.setBackgroundColor(item.getBackgroundColor());

            if (item.getGestion().equals("1")) {
                binding.boxCheckNotificacionGestionada.setVisibility(View.VISIBLE);
            } else {
                binding.boxCheckNotificacionGestionada.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(item.getIconNotify())
                    .dontAnimate()
                    .into(binding.imgIcon);
        }
    }
}