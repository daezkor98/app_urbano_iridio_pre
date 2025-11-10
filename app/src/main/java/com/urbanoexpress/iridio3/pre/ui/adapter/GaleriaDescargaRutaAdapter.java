package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pre.databinding.GaleriaImagenRowBinding;
import com.urbanoexpress.iridio3.pre.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pre.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio3.pre.ui.model.GaleriaDescargaRutaItem;

/**
 * Created by mick on 19/07/16.
 */
public class GaleriaDescargaRutaAdapter extends RecyclerView.Adapter<GaleriaDescargaRutaAdapter.ViewHolder> {

    private final String TAG = GaleriaDescargaRutaAdapter.class.getSimpleName();

    private Context context;
    private List<GaleriaDescargaRutaItem> data;
    private LayoutInflater inflater;

    private OnClickItemGaleriaListener listener;

    private static final int ITEM_VIEW_TYPE_HEADER = 0;

    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private int totalSubtractForHeader = 0;

    private boolean visibilityBtnCamara = false;
    private boolean visibilityBtnFirma = false;
    private boolean visibilityBtnCargo = false;
    private boolean visibilityBtnVoucher = false;
    private boolean visibilityBtnGaleria = false;

    private String textButtonCargo;

    public GaleriaDescargaRutaAdapter(Context context, List<GaleriaDescargaRutaItem> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.textButtonCargo = ModelUtils.getNameLblCargoGuia(context);
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GaleriaImagenRowBinding binding = GaleriaImagenRowBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return data.size() + totalSubtractForHeader;
    }

    public void setListener(OnClickItemGaleriaListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GaleriaImagenRowBinding binding;

        public ViewHolder(GaleriaImagenRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.layoutBtnCamera.setOnClickListener(v -> listener.onClickCamera());
            binding.layoutBtnFirma.setOnClickListener(v -> listener.onClickFirma());
            binding.layoutBtnCargo.setOnClickListener(v -> listener.onClickCargo());
            binding.layoutBtnVoucher.setOnClickListener(v -> listener.onClickVoucher());
            binding.layoutBtnGaleria.setOnClickListener(v -> listener.onClickAddPhotoFromGalery());
            binding.imgGalery.setOnClickListener(v -> listener.onClickImage());
            binding.btnDelete.setOnClickListener(v ->
                    listener.onClickDeleteImage(getBindingAdapterPosition()));
        }

        public void bindView(int position) {
            Log.d(TAG, "onBindViewHolder");
            if (isHeader(position)) {
                binding.lblBtnCargo.setText(textButtonCargo);

                if (position == 0) {
                    if (visibilityBtnCamara) {
                        binding.layoutBtnCamera.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnFirma) {
                        binding.layoutBtnFirma.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnCargo) {
                        binding.layoutBtnCargo.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnVoucher) {
                        binding.layoutBtnVoucher.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnGaleria) {
                        binding.layoutBtnGaleria.setVisibility(View.VISIBLE);
                    }
                } else if (position == 1) {
                    if (visibilityBtnFirma) {
                        binding.layoutBtnFirma.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnCargo) {
                        binding.layoutBtnCargo.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnVoucher) {
                        binding.layoutBtnVoucher.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnGaleria) {
                        binding.layoutBtnGaleria.setVisibility(View.VISIBLE);
                    }
                } else if (position == 2) {
                    if (visibilityBtnCargo) {
                        binding.layoutBtnCargo.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnVoucher) {
                        binding.layoutBtnVoucher.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnGaleria) {
                        binding.layoutBtnGaleria.setVisibility(View.VISIBLE);
                    }
                } else if (position == 3) {
                    if (visibilityBtnVoucher) {
                        binding.layoutBtnVoucher.setVisibility(View.VISIBLE);
                    } else if (visibilityBtnGaleria) {
                        binding.layoutBtnGaleria.setVisibility(View.VISIBLE);
                    }
                } else if (position == 4 && visibilityBtnGaleria) {
                    binding.layoutBtnGaleria.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d(TAG, "POSITION: " + position);
                Log.d(TAG, "TOTALSUBTRACTFORHEADER: " + totalSubtractForHeader);
                GaleriaDescargaRutaItem item = data.get(position - totalSubtractForHeader);

                binding.layoutImage.setVisibility(View.VISIBLE);

                Log.d(TAG, "PATH IMAGE: " + item.getPathImage());

                Glide.with(context)
                        .load(item.getPathImage())
                        .centerCrop()
                        .into(binding.imgGalery);
            }
        }
    }

    private boolean isHeader(int position) {
        return position < totalSubtractForHeader;
    }

    public int getTotalSubtractForHeader() {
        return totalSubtractForHeader;
    }

    public void setTotalSubtractForHeader(int totalSubtractForHeader) {
        this.totalSubtractForHeader = totalSubtractForHeader;
        Log.d(TAG, "totalSubtractForHeader: " + totalSubtractForHeader);
    }

    public void setVisibilityButtonsGalery(boolean visibilityBtnCamara,
                                           boolean visibilityBtnFirma,
                                           boolean visibilityBtnCargo,
                                           boolean visibilityBtnVoucher,
                                           boolean visibilityBtnGaleria) {
        this.visibilityBtnCamara = visibilityBtnCamara;
        this.visibilityBtnFirma = visibilityBtnFirma;
        this.visibilityBtnCargo = visibilityBtnCargo;
        this.visibilityBtnVoucher = visibilityBtnVoucher;
        this.visibilityBtnGaleria = visibilityBtnGaleria;
    }
}
