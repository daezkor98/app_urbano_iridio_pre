package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalNoEntregoGuiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.presenter.NoEntregaGEPresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.GalleryAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.TipoEntregaGuiaAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;
import com.urbanoexpress.iridio3.pe.util.RecyclerTouchListener;
import com.urbanoexpress.iridio3.pe.view.DescargaNoEntregaView;

/**
 * Created by mick on 25/07/16.
 */
    public class NoEntregaGEDialog extends BaseDialogFragment implements DescargaNoEntregaView,
        GalleryAdapter.OnGalleryListener {

    public static final String TAG = NoEntregaGEDialog.class.getSimpleName();

    private ModalNoEntregoGuiaBinding binding;

    private NoEntregaGEPresenter presenter;

    public NoEntregaGEDialog() { }

    public static NoEntregaGEDialog newInstance(ArrayList<Ruta> rutas, int numVecesGestionado, int idMotivo) {
        NoEntregaGEDialog fragment = new NoEntregaGEDialog();
        Bundle args = new Bundle();
        args.putSerializable("guias", rutas);
        args.putInt("numVecesGestionado", numVecesGestionado);
        args.putInt("idMotivo",idMotivo);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalNoEntregoGuiaBinding.inflate(inflater, container, false);

        initUI();

        if (presenter == null) {
            presenter = new NoEntregaGEPresenter(
                    NoEntregaGEDialog.this,
                    (ArrayList<Ruta>) getArguments().getSerializable("guias"),
                    getArguments().getInt("numVecesGestionado"),
                    getArguments().getInt("idMotivo"));
            presenter.init();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            presenter.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            showToast(R.string.activity_resumen_ruta_message_error_al_tomar_foto);
        }
    }

    @Override
    public void setScreenTitle(String title) {
        binding.lblTitle.setText(title);
    }

    @Override
    public void setTextGuiaElectronica(String text) {
        binding.lblGuiaElectronica.setText(text);
    }

    @Override
    public void setTextBtnSiguiente(String text) {
        binding.btnSiguiente.setText(text);
    }

    @Override
    public String getTextComentarios() {
        return binding.txtComentarios.getText().toString().trim();
    }

    @Override
    public void showListaMotivos(List<MotivoDescargaItem> motivos) {
        try {
            TipoEntregaGuiaAdapter adapter = new TipoEntregaGuiaAdapter(motivos);
            binding.rvMotivos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleria.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showStepMotivos() {
        binding.motivoContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStepGaleria() {
        binding.galeriaContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideStepMotivos() {
        binding.motivoContentLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideStepGaleria() {
        binding.galeriaContentLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyGalleryItemChanged(int position) {
        binding.rvGaleria.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyGalleryItemInsert(int position) {
        binding.rvGaleria.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyGalleryItemRemove(int position) {
        binding.rvGaleria.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGalleryAllItemChanged() {
        binding.rvGaleria.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyMotivosAllItemChanged() {
        binding.rvMotivos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void hideKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), false, binding.btnSiguiente);
    }

    @Override
    public void showKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), true, null);
    }

    @Override
    public void showDialogEstadoShipper() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setIcon(R.drawable.ic_estado_cliente_critico)
                .setTitle(R.string.act_ruta_title_estado_shipper)
                .setMessage(R.string.act_ruta_msg_estado_shipper_critico_entrega)
                .setPositiveButton(R.string.text_gestionar, (dialog, which) -> {
                    if (presenter != null) presenter.onBtnGestionarFromValidationDialogClick();
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void showDialogUltimaGestionEfectiva(String latitude, String longitude) {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setIcon(R.drawable.ic_alert_circle_red)
                .setTitle(R.string.act_ruta_title_ultima_gestion_efectiva)
                .setMessage(R.string.act_ruta_msg_ultima_gestion_efectiva)
                .setPositiveButton(R.string.text_gestionar, (dialog, which) -> {
                    if (presenter != null) presenter.onBtnGestionarFromValidationDialogClick();
                })
                .setNeutralButton(R.string.text_cancelar, null)
                .setNegativeButton(R.string.text_ver_ubicacion, (dialog, which) -> {
                    String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude
                            + " (Ubicación de la entrega)";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                })
                .show();
    }

    @Override
    public void showMessageCantTakePhoto() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.text_advertencia)
                .setMessage(R.string.activity_detalle_ruta_message_no_puede_tomar_foto)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showWrongDateAndTimeMessage() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.text_configurar_fecha_hora)
                .setMessage(R.string.act_main_message_date_time_incorrect)
                .setPositiveButton(R.string.text_configurar, (dialog, which) ->
                        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void onButtonClick(int position) {
        if (presenter != null) presenter.onGalleryButtonClick(position);
    }

    @Override
    public void onDeleteImageClick(int position) {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_detalle_ruta_title_eliminar_galeria)
                .setMessage(R.string.activity_detalle_ruta_message_eliminar_galeria)
                .setPositiveButton(R.string.text_aceptar,
                        (dialog, which) -> {if (presenter != null) presenter.onGalleryDeleteImageClick(position);})
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private void initUI() {
        binding.rvMotivos.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvMotivos.setHasFixedSize(true);
        binding.rvMotivos.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(),
                        binding.rvMotivos, new RecyclerTouchListener.ClickListener() {
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

        binding.btnUpdateMotivos.setOnClickListener(view -> presenter.onClickUpdateMotivos());

        binding.btnSiguiente.setOnClickListener(v -> presenter.onBtnSiguienteClick());

        binding.rvGaleria.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleria.setHasFixedSize(true);
        binding.rvGaleria.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.txtComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.lblContadorComentario.setText(s.length() + "/160");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
