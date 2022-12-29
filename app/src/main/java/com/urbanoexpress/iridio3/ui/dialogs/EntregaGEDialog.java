package com.urbanoexpress.iridio3.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.databinding.ModalEntregarGuiaBinding;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.presenter.EntregaGEPresenter;
import com.urbanoexpress.iridio3.ui.adapter.GalleryAdapter;
import com.urbanoexpress.iridio3.ui.adapter.PiezasAdapter;
import com.urbanoexpress.iridio3.ui.adapter.PremiosAdapter;
import com.urbanoexpress.iridio3.ui.adapter.TipoEntregaGuiaAdapter;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.ui.model.PremioItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.util.MetricsUtils;
import com.urbanoexpress.iridio3.util.RecyclerTouchListener;
import com.urbanoexpress.iridio3.util.constant.Country;
import com.urbanoexpress.iridio3.view.DescargaEntregaView;

/**
 * Created by mick on 13/07/16.
 */
public class EntregaGEDialog extends BaseDialogFragment implements DescargaEntregaView,
        GalleryAdapter.OnGalleryListener {

    public static final String TAG = EntregaGEDialog.class.getSimpleName();

    private ModalEntregarGuiaBinding binding;
    private EntregaGEPresenter presenter;

    public EntregaGEDialog() { }

    /**
     * Factory method
     * */
    public static EntregaGEDialog newInstance(ArrayList<Ruta> rutas, int numVecesGestionado) {
        EntregaGEDialog fragment = new EntregaGEDialog();
        Bundle args = new Bundle();
        args.putSerializable("guias", rutas);
        args.putInt("numVecesGestionado", numVecesGestionado);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalEntregarGuiaBinding.inflate(inflater, container, false);

        initUI();

        presenter = new EntregaGEPresenter(
                EntregaGEDialog.this,
                (ArrayList<Ruta>) getArguments().getSerializable("guias"),
                getArguments().getInt("numVecesGestionado"));
        presenter.init();

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
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        presenter.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RESULT");
        try {
            presenter.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            showToast(R.string.activity_resumen_ruta_message_error_al_tomar_foto);
        }
    }

    @Override
    public void setErrorTxtNombre(String error) {
        binding.txtNombre.setError(error);
        binding.txtNombre.requestFocus();
    }

    @Override
    public void setErrorTxtDNI(String error) {
        binding.txtDocIdentificacion.setError(error);
        binding.txtDocIdentificacion.requestFocus();
    }

    @Override
    public void setTextGuiaElectronica(String text) {
        binding.lblGuiaElectronica.setText(text);
    }

    @Override
    public void setTextNombre(String text) {
        binding.txtNombre.setText(text);
    }

    @Override
    public void setTextDNI(String text) {
        binding.txtDocIdentificacion.setText(text);
    }

    @Override
    public void setTextComentarios(String text) {
        binding.txtComentarios.setText(text);
    }

    @Override
    public void setTextBtnSiguiente(String text) {
        binding.btnSiguiente.setText(text);
    }

    @Override
    public void setTitleStepFotoCargo(String title) {
        binding.lblTitleStepFotoCargo.setText(title);
    }

    @Override
    public String getTextNombre() {
        return binding.txtNombre.getText().toString().trim();
    }

    @Override
    public String getTextDNI() {
        return binding.txtDocIdentificacion.getText().toString().trim();
    }

    @Override
    public String getTextNumVoucher() {
        return binding.txtNumVoucherPOS.getText().toString().trim();
    }

    @Override
    public String getTextComentarios() {
        return binding.txtComentarios.getText().toString().trim();
    }

    @Override
    public void showListaMotivos(List<MotivoDescargaItem> motivos) {
        try {
            TipoEntregaGuiaAdapter adapter = new TipoEntregaGuiaAdapter(motivos);
            binding.lvMotivos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    //Items to display
    @Override
    public void showPiezas(List<PiezaItem> items) {
        try {
            PiezasAdapter adapter = new PiezasAdapter(items, presenter);
            binding.rvPiezas.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showPremios(List<PremioItem> items) {
        try {
            PremiosAdapter adapter = new PremiosAdapter(items, presenter);
            binding.rvPremios.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showFotosEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaFotos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showImagenFirmaEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaFirma.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showFotosCargoEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaCargo.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showFotosDomicilioEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaDomicilio.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showTipoDocIdentificacion(ArrayList<String> tipoDocIdentificacion) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item_text_black, tipoDocIdentificacion);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipoDocIdentificacion.setAdapter(adapter);
    }

    @Override
    public void showTipoDireccion(ArrayList<String> tipoDireccion) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item_text_black, tipoDireccion);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipoDireccion.setAdapter(adapter);
    }

    @Override
    public void showTipoMedioPago(ArrayList<String> tipoMedioPago) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item_text_black, tipoMedioPago);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipoMedioPago.setAdapter(adapter);
    }

    @Override
    public void setVisibilityContainerMsgEntregaParcial(int visible) {
        binding.containerMsgEntregaParcial.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputRecibidoPor(int visible) {
        binding.layoutInputRecibidoPor.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputTipoDocIndentidad(int visible) {
        binding.layoutInputTipoDocIndentidad.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputDocIndentidad(int visible) {
        binding.layoutInputDocIndentidad.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputTipoDireccion(int visible) {
        binding.layoutInputTipoDireccion.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputTipoMedioPago(int visible) {
        binding.layoutInputTipoMedioPago.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputObservarEntrega(int visible) {
        binding.layoutInputObservarEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityLayoutInputVoucher(int visible) {
        binding.layoutInputVoucher.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepPiezas(int visible) {
        binding.boxStepPiezas.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepProductosEntregados(int visible) {
        binding.boxStepProductosEntregados.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepTipoEntrega(int visible) {
        binding.boxStepTipoEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepDatosEntrega(int visible) {
        binding.boxStepDatosEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepFotosEntrega(int visible) {
        binding.boxStepFotosEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepFirmaEntrega(int visible) {
        binding.boxStepFirmaEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepFotoCargoEntrega(int visible) {
        binding.boxStepFotoCargoEntrega.setVisibility(visible);
    }

    @Override
    public void setVisibilityBoxStepFotosDomicilio(int visible) {
        binding.boxStepFotosDomicilio.setVisibility(visible);
    }

    @Override
    public void notifyPiezaItemChanged(int position) {
        RecyclerView.Adapter adapter = binding.rvPiezas.getAdapter();
        if (adapter != null) adapter.notifyItemChanged(position);
    }

    @Override
    public void notifyPiezasAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvPiezas.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyPremioItemChanged(int position) {
        RecyclerView.Adapter adapter = binding.rvPremios.getAdapter();
        if (adapter != null) adapter.notifyItemChanged(position);
    }

    @Override
    public void notifyPremiosAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvPremios.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyGaleriaFotosItemRemove(int position) {
        RecyclerView.Adapter adapter = binding.rvGaleriaFotos.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaFotosAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvGaleriaFotos.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyGaleriaFirmaItemRemove(int position) {
        RecyclerView.Adapter adapter = binding.rvGaleriaFirma.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaFirmaAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvGaleriaFirma.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyGaleriaCargoItemRemove(int position) {
        RecyclerView.Adapter adapter = binding.rvGaleriaCargo.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaCargoAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvGaleriaDomicilio.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyGaleriaDomicilioItemRemove(int position) {
        RecyclerView.Adapter adapter = binding.rvGaleriaDomicilio.getAdapter();
        if (adapter != null) adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaDomicilioAllItemChanged() {
        RecyclerView.Adapter adapter = binding.rvGaleriaDomicilio.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyMotivosAllItemChanged() {
        RecyclerView.Adapter adapter = binding.lvMotivos.getAdapter();
        if (adapter != null) adapter.notifyDataSetChanged();
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
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.text_configurar_fecha_hora)
                .setMessage(R.string.act_main_message_date_time_incorrect)
                .setPositiveButton(R.string.text_configurar, (dialog, which) ->
                        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)))
                .setNegativeButton(R.string.text_cancelar, null)
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
                .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                    if (presenter != null) presenter.onGalleryDeleteImageClick(position);
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private void initUI() {
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

        binding.btnUpdateMotivos.setOnClickListener(view -> presenter.onClickUpdateMotivos());

        binding.btnScanPCK.setOnClickListener(view -> presenter.onBtnScanPCKClick());

        binding.btnSiguiente.setOnClickListener(v -> presenter.onBtnSiguienteClick());

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical_recyclerview_1dp));

        ((SimpleItemAnimator) binding.rvPiezas.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvPiezas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPiezas.setHasFixedSize(true);
        binding.rvPiezas.addItemDecoration(itemDecoration);

        ((SimpleItemAnimator) binding.rvPremios.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvPremios.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPremios.setHasFixedSize(true);
        binding.rvPremios.addItemDecoration(itemDecoration);

        binding.rvGaleriaFotos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaFotos.setHasFixedSize(true);
        binding.rvGaleriaFotos.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.rvGaleriaFirma.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaFirma.setHasFixedSize(true);
        binding.rvGaleriaFirma.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.rvGaleriaCargo.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaCargo.setHasFixedSize(true);
        binding.rvGaleriaCargo.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.rvGaleriaDomicilio.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaDomicilio.setHasFixedSize(true);
        binding.rvGaleriaDomicilio.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.spinnerTipoDocIdentificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    configFormatTxtDocIdentidad();
                } else {
                    binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
                    binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                }
                binding.txtDocIdentificacion.setText("");
                presenter.onSelectedTipoDocIdentificacion(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.spinnerTipoDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                presenter.onSelectedTipoDireccion(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.spinnerTipoMedioPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                presenter.onSelectedTipoMedioPago(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

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

        binding.rBtnObservarEntrega.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onCheckedChangeObservarEntrega(isChecked);

            if (isChecked) {
                binding.layoutInputComentarios.setVisibility(View.GONE);
            } else {
                binding.layoutInputComentarios.setVisibility(View.VISIBLE);
            }
        });

        binding.layoutInputRecibidoPor.setOnClickListener(v -> {
            binding.txtNombre.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtNombre);
        });

        binding.layoutInputDocIndentidad.setOnClickListener(v -> {
            binding.txtDocIdentificacion.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtDocIdentificacion);
        });

        binding.layoutInputVoucher.setOnClickListener(v -> {
            binding.txtNumVoucherPOS.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtNumVoucherPOS);
        });

        binding.layoutInputComentarios.setOnClickListener(v -> {
            binding.txtComentarios.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtComentarios);
        });
    }

    private void configFormatTxtDocIdentidad() {
        int country = new PreferencesHelper(getActivity()).getCountry();
        switch (country) {
            case Country.CHILE:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_TEXT
                        |InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                break;
            case Country.ECUADOR:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Country.PERU:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

}
