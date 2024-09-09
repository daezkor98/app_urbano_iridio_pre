package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalGaleriaDescargaBinding;
import com.urbanoexpress.iridio3.pe.presenter.GaleriaDescargaPresenter;
import com.urbanoexpress.iridio3.pe.presenter.GaleriaParadaProgramadaPresenter;
import com.urbanoexpress.iridio3.pe.ui.adapter.GaleriaDescargaRutaAdapter;
import com.urbanoexpress.iridio3.pe.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;
import com.urbanoexpress.iridio3.pe.view.GaleriaDescargaView;

import java.util.List;

public class GaleriaParadaProgramadaDialog extends DialogFragment implements GaleriaDescargaView {

    public static final String TAG = GaleriaParadaProgramadaDialog.class.getSimpleName();

    private ModalGaleriaDescargaBinding binding;
    private GaleriaParadaProgramadaPresenter presenter;

    public GaleriaParadaProgramadaDialog() { }

    public static GaleriaParadaProgramadaDialog newInstance(String idPlanDeViaje, String idParadaProgramada,
                                                            int paradaProgramadaEstadoLlegada){
        GaleriaParadaProgramadaDialog fragment = new GaleriaParadaProgramadaDialog();
        Bundle args = new Bundle();
        args.putString("idPlanDeViaje", idPlanDeViaje);
        args.putString("idParadaProgramada", idParadaProgramada);
        args.putInt("paradaProgramadaEstadoLlegada", paradaProgramadaEstadoLlegada);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalGaleriaDescargaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        if (presenter == null) {
            presenter = new GaleriaParadaProgramadaPresenter(GaleriaParadaProgramadaDialog.this,
                    getArguments().getString("idPlanDeViaje"),
                    getArguments().getString("idParadaProgramada"),
                    getArguments().getInt("paradaProgramadaEstadoLlegada"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RESULT");
        try {
            if (CameraUtils.validateOnActivityResult(requestCode, resultCode)) {
                presenter.onActivityResultImageFromCamera();
            } else if (requestCode == GaleriaDescargaPresenter.REQUEST_IMAGE_GALLERY &&
                    resultCode == Activity.RESULT_OK) {
                presenter.onActivityResultImageFromStorage(data);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(),
                    R.string.activity_resumen_ruta_message_error_al_tomar_foto,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showGaleria(List<GaleriaDescargaRutaItem> galeria) {
        try {
            GaleriaDescargaRutaAdapter adapter = new GaleriaDescargaRutaAdapter(getActivity(), galeria);
            adapter.setTotalSubtractForHeader(1);
            adapter.setVisibilityButtonsGalery(true, false, false, false, false);
            adapter.setListener(presenter);
            binding.lvGaleriaDescarga.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyGaleryItemChanged(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyGaleryItemInsert(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyGaleryItemRemove(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleryAllItemChanged() {
        binding.lvGaleriaDescarga.getAdapter().notifyDataSetChanged();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public Context getContextView() {
        return getActivity();
    }

    @Override
    public View baseFindViewById(int id) {
        return binding.getRoot().findViewById(id);
    }

    private void setupViews() {
        binding.lvGaleriaDescarga.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.lvGaleriaDescarga.setHasFixedSize(true);
        binding.lvGaleriaDescarga.addItemDecoration(
                new GridSpacingItemDecoration(3, MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.btnAceptar.setOnClickListener(v -> dismiss());
    }
}