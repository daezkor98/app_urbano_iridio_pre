package com.urbanoexpress.iridio3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.FragmentRecolectarValijaExpressPhotoValijaBinding;
import com.urbanoexpress.iridio3.model.RecolectarValijaExpressViewModel;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.presenter.RVEPhotoValijaPresenter;
import com.urbanoexpress.iridio3.ui.adapter.GalleryAdapter;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.util.MetricsUtils;
import com.urbanoexpress.iridio3.util.MultimediaManager;
import com.urbanoexpress.iridio3.view.RVEPhotoValijaView;

import java.util.ArrayList;
import java.util.List;

public class RVEPhotoValijaFragment extends BaseFragment implements RVEPhotoValijaView,
        GalleryAdapter.OnGalleryListener {

    public static final String TAG = "RVEPhotoValijaFragment";

    private FragmentRecolectarValijaExpressPhotoValijaBinding binding;
    private RVEPhotoValijaPresenter presenter;
    private RecolectarValijaExpressViewModel model;
    private MultimediaManager multimediaManager;

    public RVEPhotoValijaFragment() {}

    public static RVEPhotoValijaFragment newInstance() {
        return new RVEPhotoValijaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecolectarValijaExpressPhotoValijaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.progressLayout.progressLayout.getVisibility() != View.VISIBLE) {
                    ModalHelper.getBuilderAlertDialog(getContext())
                            .setTitle("Cancelar recolección")
                            .setMessage("¿Estás seguro de cancelar la recolección de valija?")
                            .setPositiveButton(R.string.text_continuar, null)
                            .setNegativeButton(R.string.text_cancelar, (dialog, which) -> finishActivity())
                            .show();
                }
            }
        });

        multimediaManager = new MultimediaManager(requireContext());
        model = new ViewModelProvider(requireActivity()).get(RecolectarValijaExpressViewModel.class);
        presenter = new RVEPhotoValijaPresenter(this, model);
        presenter.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showProgressDialog(int messageId) {
        binding.progressLayout.progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressDialog(String message) {
        binding.progressLayout.progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        binding.progressLayout.progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyGalleryItemInserted(int position) {
        try {
            binding.galleryRecyclerView.getAdapter().notifyItemInserted(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyGalleryItemRemoved(int position) {
        try {
            binding.galleryRecyclerView.getAdapter().notifyItemRemoved(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setGallery(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.galleryRecyclerView.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void openCamera(String directoryNameParent, String imageNamePrefix) {
        multimediaManager.generateFileForTakePicture(directoryNameParent, imageNamePrefix);
        startImageCaptureForResult.launch(multimediaManager.getIntentImageCapture());
    }

    @Override
    public void showConfirmDeletePhotoModal(int position) {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_detalle_ruta_title_eliminar_galeria)
                .setMessage(R.string.activity_detalle_ruta_message_eliminar_galeria)
                .setPositiveButton(R.string.text_eliminar, (dialog, which) ->
                        presenter.onConfirmDeleteImageClick(position))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void showMsgError(String msg) {
        binding.msgErrorText.setText(msg);
        binding.msgErrorText.setVisibility(View.VISIBLE);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void sendBroadcastGestionValijaFinalizada(ArrayList<Ruta> rutas) {
        Intent intent = new Intent("OnDescargaFinalizada");
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", rutas);
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    @Override
    public void onButtonClick(int position) {
        binding.msgErrorText.setVisibility(View.GONE);
        presenter.onTakePhotoClick(position);
    }

    @Override
    public void onDeleteImageClick(int position) {
        presenter.onDeletePhotoClick(position);
    }

    private void setupViews() {
        binding.galleryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.galleryRecyclerView.setHasFixedSize(true);
        binding.galleryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.msgErrorText.setOnClickListener(v -> v.setVisibility(View.GONE));

        binding.saveButton.setOnClickListener(v -> {
            binding.msgErrorText.setVisibility(View.GONE);
            presenter.onRegisterButtonClick();
        });
    }

    private final ActivityResultLauncher<Intent> startImageCaptureForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    presenter.subscribeToCompletableProcessDataImage(
                            multimediaManager.onSuccessResultTakingPicture());
                }
            });
}