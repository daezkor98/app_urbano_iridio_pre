package com.urbanoexpress.iridio.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ModalObservarGestionEntregaBinding;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.presenter.ObservarGestionEntregaPresenter;
import com.urbanoexpress.iridio.ui.adapter.GaleriaDescargaRutaAdapter;
import com.urbanoexpress.iridio.ui.adapter.MotivoDescargaAdapter;
import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio.util.MetricsUtils;
import com.urbanoexpress.iridio.util.RecyclerTouchListener;
import com.urbanoexpress.iridio.view.ObservarGestionEntregaView;

import java.util.List;

public class ObservarGestionEntregaDialog extends DialogFragment
        implements ObservarGestionEntregaView {

    public static final String TAG = ObservarGestionEntregaDialog.class.getSimpleName();

    private ModalObservarGestionEntregaBinding binding;
    private ObservarGestionEntregaPresenter presenter;

    public ObservarGestionEntregaDialog() { }

    public static ObservarGestionEntregaDialog newInstance(Ruta guia) {
        ObservarGestionEntregaDialog fragment = new ObservarGestionEntregaDialog();
        Bundle args = new Bundle();
        args.putSerializable("guia", guia);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalObservarGestionEntregaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        presenter = new ObservarGestionEntregaPresenter(
                ObservarGestionEntregaDialog.this,
                (Ruta) getArguments().getSerializable("guia"));
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
                presenter.onActivityResultImage();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(),
                    R.string.activity_resumen_ruta_message_error_al_tomar_foto,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setGuiaElectronica(String guiaElectronica) {
        binding.lblGuiaElectronica.setText(guiaElectronica);
    }

    @Override
    public void showListaMotivos(List<MotivoDescargaItem> motivos) {
        try {
            MotivoDescargaAdapter adapter = new MotivoDescargaAdapter(getActivity(), motivos);
            binding.lvMotivos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showGaleria(List<GaleriaDescargaRutaItem> galeria) {
        Log.d(TAG, "showGaleria");
        try {
            GaleriaDescargaRutaAdapter adapter = new GaleriaDescargaRutaAdapter(getActivity(), galeria);
            adapter.setTotalSubtractForHeader(1); // Solo boton de camara
            adapter.setVisibilityButtonsGalery(true, false, false, false, false);
            adapter.setListener(presenter);
            binding.lvGaleria.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyGaleryItemChanged(int position) {
        binding.lvGaleria.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyGaleryItemInsert(int position) {
        binding.lvGaleria.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyGaleryItemRemove(int position) {
        binding.lvGaleria.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleryAllItemChanged() {
        binding.lvGaleria.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyMotivosAllItemChanged() {
        binding.lvMotivos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public EditText getViewTxtComentarios() {
        return binding.txtComentarios;
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
        binding.lvMotivos.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.lvMotivos.setHasFixedSize(true);
        binding.lvMotivos.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(),
                        binding.lvMotivos, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        view.setFocusable(true);
                        view.setFocusableInTouchMode(true);
                        presenter.onClickItemMotivo(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        binding.btnUpdateMotivos.setOnClickListener(v -> presenter.onClickUpdateMotivos());

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnAgregar.setOnClickListener(v -> presenter.onClickAceptar());

        binding.lvGaleria.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.lvGaleria.setHasFixedSize(true);
        binding.lvGaleria.addItemDecoration(
                new GridSpacingItemDecoration(3, MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.txtComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.lblContadorComentario.setText(charSequence.length() + "/160");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}