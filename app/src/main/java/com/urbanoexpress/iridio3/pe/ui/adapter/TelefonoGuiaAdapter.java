package com.urbanoexpress.iridio3.pe.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.TelefonoGuiaRowBinding;
import com.urbanoexpress.iridio3.pe.ui.model.TelefonoGuiaItem;
import com.urbanoexpress.iridio3.pe.util.AnimationUtils;

import java.util.List;

/**
 * Created by mick on 30/06/17.
 */

public class TelefonoGuiaAdapter extends RecyclerView.Adapter<TelefonoGuiaAdapter.ViewHolder> {

    private static final String TAG = TelefonoGuiaAdapter.class.getSimpleName();

    private Context context;
    private List<TelefonoGuiaItem> data;
    private LayoutInflater inflater;
    private OnClickIconPhoneListener listener;
    private int type;
    private boolean showBtnContacto, showBtnEditar;

    public TelefonoGuiaAdapter(Context context, List<TelefonoGuiaItem> data, int type,
                               boolean showBtnContacto, boolean showBtnEditar) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.type = type;
        this.showBtnContacto = showBtnContacto;
        this.showBtnEditar = showBtnEditar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TelefonoGuiaRowBinding binding = TelefonoGuiaRowBinding.inflate(inflater, parent, false);
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

    public void setListener(TelefonoGuiaAdapter.OnClickIconPhoneListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TelefonoGuiaRowBinding binding;

        public ViewHolder(TelefonoGuiaRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v ->
                    listener.onClickPhoneContent(v, getBindingAdapterPosition(), type));

            binding.btnLlamar.setOnClickListener(v ->
                    listener.onClickBtnPhone(v, getBindingAdapterPosition(), type));

            binding.btnContacto.setOnClickListener(v ->
                    listener.onClickBtnContacto(v, getBindingAdapterPosition(), type));

            binding.btnEditar.setOnClickListener(v ->
                    listener.onClickBtnEdit(v, getBindingAdapterPosition(), type));

            binding.btnLlamar.setOnTouchListener(touchListener);

            binding.btnContacto.setOnTouchListener(touchListener);

            binding.btnEditar.setOnTouchListener(touchListener);
        }

        public void bindView(int position) {
            TelefonoGuiaItem item = data.get(position);
            binding.lblTelefono.setText(item.getTelefono());

            binding.btnContacto.setVisibility(showBtnContacto ? View.VISIBLE : View.GONE);
            binding.btnEditar.setVisibility(showBtnEditar ? View.VISIBLE : View.GONE);

            if (type == 3) {
                binding.boxChip.setBackgroundResource(R.drawable.bg_chip_telefono_color_black);
                binding.btnLlamar.setImageResource(R.drawable.ic_phone_circle_green);
            } else {
                binding.boxChip.setBackgroundResource(R.drawable.bg_chip_telefono_color_grey);
                binding.btnLlamar.setImageResource(R.drawable.ic_phone_circle_red);
            }
        }

        private View.OnTouchListener touchListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "Button Press");
                AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(), v,
                        1.0f, 1.2f, 1.0f, 1.2f);
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                Log.d(TAG, "Button Release");
                AnimationUtils.animationScale(150, new AccelerateDecelerateInterpolator(), v,
                        1.2f, 1.0f, 1.2f, 1.0f);
            } else {
                Log.d(TAG, "Event: " + event.getAction());
            }
            return false;
        };
    }

    public interface OnClickIconPhoneListener {
        void onClickPhoneContent(View view, int position, int type);
        void onClickBtnPhone(View view, int position, int type);
        void onClickBtnContacto(View view, int position, int type);
        void onClickBtnEdit(View view, int position, int type);
    }

}