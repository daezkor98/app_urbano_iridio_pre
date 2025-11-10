package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.List;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ModalGaleriaDescargaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.presenter.GaleriaDescargaPresenter;
import com.urbanoexpress.iridio3.pre.ui.adapter.GaleriaDescargaRutaAdapter;
import com.urbanoexpress.iridio3.pre.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pre.util.CameraUtils;
import com.urbanoexpress.iridio3.pre.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pre.util.MetricsUtils;
import com.urbanoexpress.iridio3.pre.view.GaleriaDescargaView;

/**
 * Created by mick on 08/08/16.
 */
public class GaleriaGEDialog extends DialogFragment implements GaleriaDescargaView {

    public static final String TAG = GaleriaGEDialog.class.getSimpleName();

    private ModalGaleriaDescargaBinding binding;
    private GaleriaDescargaPresenter presenter;
    ActivityResultLauncher<Intent> galleryImageResultLauncher;

    public GaleriaGEDialog() { }

    public static GaleriaGEDialog newInstance(Ruta ruta){
        GaleriaGEDialog fragment = new GaleriaGEDialog();
        Bundle args = new Bundle();
        args.putSerializable("ruta", ruta);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalGaleriaDescargaBinding.inflate(inflater, container, false);
        galleryRegisterResult();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        if (presenter == null) {
            presenter = new GaleriaDescargaPresenter(GaleriaGEDialog.this,
                    (Ruta) getArguments().getSerializable("ruta"));
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
            adapter.setTotalSubtractForHeader(2);
            adapter.setVisibilityButtonsGalery(true, false, false, false,true);
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
    public void openGallery(Intent intent) {
        galleryImageResultLauncher.launch(intent);
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

    private void galleryRegisterResult(){
        galleryImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data!=null){
                        presenter.onActivityResultImageFromStorage(data);
                    }
                }
            }
        });
    }
}