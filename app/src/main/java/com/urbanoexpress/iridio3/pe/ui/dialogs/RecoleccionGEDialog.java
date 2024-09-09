package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalRecoleccionGeBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.presenter.RecoleccionGEPresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.model.PiezaRecolectadaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.GalleryAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.GuiasRecolectadasAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.PiezasRecolectadasAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pe.ui.widget.AdderButton;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.RecoleccionGEView;

/**
 * Created by mick on 03/08/17.
 */

public class RecoleccionGEDialog extends BaseDialogFragment implements RecoleccionGEView,
        GalleryAdapter.OnGalleryListener,
        PiezasRecolectadasAdapter.OnPiezaRecolectadaListener,
        GuiasRecolectadasAdapter.OnGuiaRecolectadaListener {

    public static final String TAG = RecoleccionGEDialog.class.getSimpleName();

    private ModalRecoleccionGeBinding binding;

    private RecoleccionGEPresenter presenter;

    public RecoleccionGEDialog() { }

    public static RecoleccionGEDialog newInstance(ArrayList<Ruta> rutas, int numVecesGestionado,
                                                  boolean guiasElectronicas) {
        RecoleccionGEDialog fragment = new RecoleccionGEDialog();
        Bundle args = new Bundle();
        args.putSerializable("guias", rutas);
        args.putInt("numVecesGestionado", numVecesGestionado);
        args.putBoolean("guiasElectronicas", guiasElectronicas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getViewContext())
                .registerReceiver(confirmPiezasRecolectadasReceiver,
                        new IntentFilter(LocalAction.CONFIRM_PIEZAS_RECOLECTADAS_ACTION));

        if (getArguments() != null) {
            presenter = new RecoleccionGEPresenter(this,
                    (ArrayList<Ruta>) getArguments().getSerializable("guias"),
                    getArguments().getInt("numVecesGestionado"),
                    getArguments().getBoolean("guiasElectronicas"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalRecoleccionGeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        if (presenter != null) {
            presenter.init();
        }
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
        LocalBroadcastManager.getInstance(getViewContext())
                .unregisterReceiver(confirmPiezasRecolectadasReceiver);
        presenter.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (CameraUtils.validateOnActivityResult(requestCode, resultCode)) {
                presenter.onActivityResultImage();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            showToast(R.string.activity_resumen_ruta_message_error_al_tomar_foto);
        }
    }

    @Override
    public void clearBarra() {
        binding.barraEditText.setText("");
        binding.barraEditText.requestFocus();
    }

    @Override
    public void setEnabledFormSobre(boolean enabled) {
        binding.sobresAdderButton.setEnabled(enabled);
    }

    @Override
    public void setEnabledFormPaquete(boolean enabled) {
        binding.paquetesAdderButton.setEnabled(enabled);
    }

    @Override
    public void setEnabledFormValija(boolean enabled) {
        binding.valijasAdderButton.setEnabled(enabled);
    }

    @Override
    public void setEnabledFormOtros(boolean enabled) {
        binding.otrosAdderButton.setEnabled(enabled);
    }

    @Override
    public void setErrorBarra(String error) {
        binding.barraEditText.setError(error);
        binding.barraEditText.requestFocus();
    }

    @Override
    public void setErrorFormGuiaRecoleccion(String error) {
        binding.txtGuiaRecoleccion.setError(error);
        binding.txtGuiaRecoleccion.requestFocus();
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void setTextGuiaElectronica(String text) {
        binding.lblGuiaElectronica.setText(text);
    }

    @Override
    public void setTextTotalRecolectados(String text) {
        binding.totalPiezasRecolectadasText.setText(text);
    }

    @Override
    public void setTextFormGuiaRecoleccion(String text) {
        binding.txtGuiaRecoleccion.setText(text);
    }

    @Override
    public void setTextBtnSiguiente(String text) {
        binding.nextButton.setText(text);
    }

    @Override
    public void setValueFormPaquete(int value) {
        binding.paquetesAdderButton.setValue(value);
    }

    @Override
    public void setVisibilityMoreActionsMenu(boolean visibility) {
        binding.moreActionsButton.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelectionModeScan() {
        binding.inputBarraContainer.setVisibility(View.VISIBLE);
        binding.selectAllGuiasCheckBox.setVisibility(View.GONE);
        binding.piezasRecyclerView.setAdapter(null);
        binding.selectAllGuiasCheckBox.setChecked(false);
    }

    @Override
    public void setSelectionModeCheck() {
        binding.inputBarraContainer.setVisibility(View.GONE);
        binding.selectAllGuiasCheckBox.setVisibility(View.VISIBLE);
        binding.piezasRecyclerView.setAdapter(null);
        binding.msgSuccessText.setVisibility(View.GONE);
        binding.msgErrorText.setVisibility(View.GONE);
        binding.selectAllGuiasCheckBox.setChecked(false);
    }

    @Override
    public String getTextFormGuiaRecoleccion() {
        return binding.txtGuiaRecoleccion.getText().toString().trim();
    }

    @Override
    public String getTextComentarios() {
        return binding.txtComentarios.getText().toString().trim();
    }

    @Override
    public int getValueFormSobre() {
        return binding.sobresAdderButton.getValue();
    }

    @Override
    public int getValueFormPaquete() {
        return binding.paquetesAdderButton.getValue();
    }

    @Override
    public int getValueFormValija() {
        return binding.valijasAdderButton.getValue();
    }

    @Override
    public int getValueFormOtros() {
        return binding.otrosAdderButton.getValue();
    }

    @Override
    public void showGallery(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.galleryRecyclerView.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showPiezasRecolectadas(List<PiezaRecolectadaItem> items) {
        try {
            PiezasRecolectadasAdapter adapter = new PiezasRecolectadasAdapter(items, this);
            binding.piezasRecyclerView.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showGuiasRecolectadas(List<PiezaRecolectadaItem> items) {
        try {
            GuiasRecolectadasAdapter adapter = new GuiasRecolectadasAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.piezasRecyclerView.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showMsgError(String msg) {
        binding.msgErrorText.setText(msg);
        binding.msgErrorText.setVisibility(View.VISIBLE);
        binding.msgSuccessText.setVisibility(View.GONE);
    }

    @Override
    public void showMsgSuccess(String msg) {
        binding.msgSuccessText.setText(msg);
        binding.msgSuccessText.setVisibility(View.VISIBLE);
        binding.msgErrorText.setVisibility(View.GONE);
    }

    @Override
    public void showStepGuias() {
        binding.stepGuiasContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStepFormulario() {
        binding.stepFormularioContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStepFotosProducto() {
        binding.stepFotosProductoContainerLayout.setVisibility(View.VISIBLE);
        binding.galleryTitleText.setText("FOTOS DEL PRODUCTO");
    }

    @Override
    public void showStepFirmaCliente() {
        binding.galleryTitleText.setText("FIRMA DEL CLIENTE");
    }

    @Override
    public void showStepFotosCargo() {
        binding.galleryTitleText.setText("FOTOS DEL CARGO");
    }

    @Override
    public void showStepFotosDomicilio() {
        binding.galleryTitleText.setText("FOTOS DEL DOMICILIO");
    }

    @Override
    public void hideStepGuias() {
        binding.stepGuiasContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideStepFormulario() {
        binding.stepFormularioContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyGalleryAllItemChanged() {
        RecyclerView.Adapter adapter = binding.galleryRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyGalleryItemInserted(int position) {
        RecyclerView.Adapter adapter = binding.galleryRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyItemInserted(position);
    }

    @Override
    public void notifyGalleryItemRemoved(int position) {
        RecyclerView.Adapter adapter = binding.galleryRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyPiezasAllItemChanged() {
        RecyclerView.Adapter adapter = binding.piezasRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyPiezasItemChanged(int position) {
        RecyclerView.Adapter adapter = binding.piezasRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyItemChanged(position);
    }

    @Override
    public void notifyPiezasItemInserted(int position) {
        RecyclerView.Adapter adapter = binding.piezasRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyItemInserted(position);
    }

    @Override
    public void notifyPiezasItemRemoved(int position) {
        RecyclerView.Adapter adapter = binding.piezasRecyclerView.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void hideKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), false, binding.nextButton);
    }

    @Override
    public void showKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), true, null);
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
    public void showMessageCantTakeSigning() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.text_advertencia)
                .setMessage(R.string.activity_detalle_ruta_message_no_puede_firmar)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showWrongDateAndTimeMessage() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.text_configurar_fecha_hora)
                .setMessage(R.string.act_main_message_date_time_incorrect)
                .setPositiveButton(R.string.text_configurar, (dialog, which) ->
                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void showModalConfirmarEliminacionPieza(String title, String message, int position) {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.text_eliminar, (dialog, which) -> {
                    if (presenter != null) presenter.onConfirmarEliminacionPiezaClick(position);
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void showModalConfirmarPiezasRecolectadas(PiezaRecolectadaItem item, int position) {
        ConfirmPiezasRecolectadasDialog dialog = ConfirmPiezasRecolectadasDialog
                .newInstance(item, position);
        dialog.show(getActivity().getSupportFragmentManager(), ConfirmPiezasRecolectadasDialog.TAG);
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

    @Override
    public void onEditarPiezaClick(int position) {
        presenter.onEditarPiezaClick(position);
    }

    @Override
    public void onEliminarPiezaClick(int position) {
        presenter.onEliminarPiezaClick(position);
    }

    @Override
    public void onSelectionPiezaChanged(int position) {
        presenter.onSelectionPiezaChanged(position);
    }

    /*private boolean touchActive = false;
    int total = 0;
    int bgColorMinus = 0;
    int bgColorPlus = 0;*/

    private void setupViews() {
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getViewContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(
                ContextCompat.getDrawable(getViewContext(), R.drawable.divider_vertical_recyclerview_1dp));
        binding.piezasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.piezasRecyclerView.setHasFixedSize(true);
        binding.piezasRecyclerView.addItemDecoration(itemDecoration);

        binding.galleryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.galleryRecyclerView.setHasFixedSize(true);
        binding.galleryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.paquetesAdderButton.setOnValueChangeListener(adderButtonValueChangedListener);
        binding.sobresAdderButton.setOnValueChangeListener(adderButtonValueChangedListener);
        binding.valijasAdderButton.setOnValueChangeListener(adderButtonValueChangedListener);
        binding.otrosAdderButton.setOnValueChangeListener(adderButtonValueChangedListener);

        binding.barraEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.msgErrorText.setVisibility(View.GONE);
                binding.msgSuccessText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        binding.barraEditText.setOnKeyListener((v, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                hideKeyboard();
                presenter.processBarra(binding.barraEditText.getText().toString());
            }
            return false;
        });

        /*binding.actionPlusPaquetesLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("ACTIVITY", "TOUCH ACTION_DOWN");
                    touchActive = true;

                    total = getTextFormPaquete().isEmpty()
                            ? 0 : Integer.parseInt(getTextFormPaquete());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (touchActive) {
                                SystemClock.sleep(100);

                                total++;

                                if (total <= 0) {
                                    total = 0;
                                    bgColorMinus = ContextCompat.getColor(getContext(), R.color.gris_9);
                                    bgColorPlus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                                } else if (total < 99999) {
                                    bgColorMinus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                                    bgColorPlus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                                } else {
                                    total = 99999;
                                    bgColorMinus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                                    bgColorPlus = ContextCompat.getColor(getContext(), R.color.gris_9);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTextFormPaquete(String.valueOf(total));
                                        setBackgroundBtnMinusPaquetes(bgColorMinus);
                                        setBackgroundBtnPlusPaquetes(bgColorPlus);
                                    }
                                });
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("ACTIVITY", "TOUCH ACTION_UP");
                    touchActive = false;
                }
                return false;
            }
        });*/

        binding.inputGuiaRecoleccionLayout.setOnClickListener(v -> {
            binding.txtGuiaRecoleccion.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtGuiaRecoleccion);
        });

        binding.inputComentariosLayout.setOnClickListener(v -> {
            binding.txtComentarios.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtComentarios);
        });

        binding.scanBarcodeButton.setOnClickListener(v -> presenter.onCLickBtnScanBarCode());

        binding.actionScanBarcodeContainerLayout.setOnClickListener(v ->
                presenter.onCLickBtnScanBarCode());

        binding.moreActionsButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getViewContext(), this.binding.moreActionsButton);
            popup.inflate(R.menu.menu_recoleccion_guia_electronica);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_selection_mode_scan) {
                    presenter.onSelectionModeClick(RecoleccionGEPresenter.SELECTION_MODE.SCAN);
                    return true;
                } else if (item.getItemId() == R.id.action_selection_mode_check) {
                    presenter.onSelectionModeClick(RecoleccionGEPresenter.SELECTION_MODE.CHECK);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        binding.selectAllGuiasCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onSelectAllGuiasCheckedChange(isChecked);
        });

        binding.nextButton.setOnClickListener(v -> presenter.onBtnSiguienteClick());
    }

    private final AdderButton.OnValueChangeListener adderButtonValueChangedListener = value ->
            binding.lblTotalRecolectado.setText(String.valueOf(binding.paquetesAdderButton.getValue()
                + binding.sobresAdderButton.getValue() + binding.valijasAdderButton.getValue()
                + binding.otrosAdderButton.getValue()));

    /**
     * Broadcast
     *
     * {@link ConfirmPiezasRecolectadasDialog#sendConfirmPiezasRecolectdasAction}
     */
    private final BroadcastReceiver confirmPiezasRecolectadasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onConfirmPiezasRecolectadasClick(intent.getParcelableExtra("pieza"),
                    intent.getIntExtra("position", -1));
        }
    };

}