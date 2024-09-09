package com.urbanoexpress.iridio3.pe.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityDetalleRutaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.presenter.DetalleRutaRuralPresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.model.DetailsItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.DetailsAdapter;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.DetalleRutaRuralView;

import java.util.List;

public class DetalleRutaRuralActivity extends AppThemeBaseActivity implements DetalleRutaRuralView {

    private ActivityDetalleRutaBinding binding;
    private DetalleRutaRuralPresenter presenter;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            Bundle args = getIntent().getExtras().getBundle("args");
            presenter = new DetalleRutaRuralPresenter(this,
                    (Ruta) args.getSerializable("guias"), args.getInt("numVecesGestionado"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_ruta, menu);
        this.menu = menu;
        presenter.onMenuReady();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_galeria) {
            presenter.onClickActionGaleria();
            return true;
        } else if (item.getItemId() == R.id.action_volverConfirmarGestion) {
            presenter.onClickActionVolverConfirmarGestion();
            return true;
        } else if (item.getItemId() == R.id.action_agregar_guia) {
            presenter.onActionAgregarGuiaRecoleccion();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        presenter.onDestroy();
    }

    @Override
    public void setTextBarra(String text) {
        binding.lblBarra.setText(text);
    }

    @Override
    public void setTextDireccion(String text) {
        binding.lblDireccion.setText(text);
    }

    @Override
    public void setTextHorario(String text) {
        binding.lblHorario.setText(text);
    }

    @Override
    public void hideTextHorario() {
        binding.lblHorario.setVisibility(View.GONE);
    }

    @Override
    public void hideIconHorario() {
        binding.imgHorario.setVisibility(View.GONE);
    }

    @Override
    public void setColorTextHorario(int color) {
        binding.lblHorario.setTextColor(color);
    }

    @Override
    public void setColorIconHorario(int color) {
        ImageViewCompat.setImageTintList(binding.imgHorario, ColorStateList.valueOf(color));
    }

    @Override
    public void setTextMedioPago(String value) {
        binding.lblMedioPago.setText(value);
    }

    @Override
    public void setTextImporte(String value) {
        binding.lblImporte.setText(value);
    }

    @Override
    public void setTextBtnGestionar(String value) {
        binding.btnGestionar.setText(value);
    }

    @Override
    public void setTextMsgReqDevolucionShipper(String text) {
        binding.lblMensajeReqDevolucionShipper.setText(text);
    }

    @Override
    public void showAlertaReqDevolucionShipper() {
        binding.boxAlertaReqDevolucionShipper.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCobrarClienteContainer() {
        binding.cobrarClienteContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showActionPiezasContainer() {
        binding.actionPiezasContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showActionRequerimientoContainer() {
        binding.actionRequerimientoContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showActionDevolverContainer() {
        binding.actionDevolverContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBtnGestionar() {
        binding.btnGestionar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBtnGestionar() {
        binding.btnGestionar.setVisibility(View.GONE);
    }

    @Override
    public void showDetalleRuta(List<DetailsItem> items) {
        DetailsAdapter adapter = new DetailsAdapter(this, items);
        binding.rvDetalles.setAdapter(adapter);
    }

    @Override
    public void showAlertaEstadoShipper(int idResIcon, int bg, String msg) {
        binding.boxAlertaEstadoShipper.setVisibility(View.VISIBLE);
        binding.boxAlertaEstadoShipper.setBackgroundResource(bg);

        Glide.with(this)
                .load(idResIcon)
                .into(binding.imgEstadoShipper);

        binding.lblMensajeEstadoShipper.setText(msg);
    }

    @Override
    public void showMsgIniciarRuta() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.activity_detalle_ruta_title_ruta_no_iniciada)
                .setMessage(R.string.activity_detalle_ruta_message_debe_iniciar_ruta)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMsgRutaFinalizada() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.activity_detalle_ruta_title_ruta_finalizada)
                .setMessage(R.string.activity_detalle_ruta_message_ruta_finalizada)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMsgGaleriaNodisponible() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.text_accion_no_disponible)
                .setMessage(R.string.activity_detalle_ruta_message_no_hay_galeria)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMsgGestionNoPermitidaPorOrdenDescarga() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.text_accion_no_disponible)
                .setMessage(R.string.activity_detalle_ruta_message_orden_descarga_no_permitido)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showDialogDescargarMotivosGestionLlamada() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.activity_detalle_ruta_title_motivo_gestion_llamada_pendiente_descarga)
                .setMessage(R.string.activity_detalle_ruta_msg_motivo_gestion_llamada_pendiente_descarga)
                .setPositiveButton(R.string.text_descargar, (dialog, which) -> {
                    if (presenter != null) presenter.onBtnDescargarMotivosGestionLlamadaClick();
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void showObservationDialog(String observationBody) {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle("Observación")
                .setMessage(observationBody)
                .setPositiveButton("Entendido",(dialog,which)->{
                    manageGuia();
                })
                .show();
    }

    @Override
    public void showDialogSeleccionarResultadoLlamada(String[] resultados) {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle("Seleccione el resultado de la llamada realizada")
                .setCancelable(false)
                .setSingleChoiceItems(resultados, 0, (dialog, which) -> {
                    if (presenter != null) presenter.onMotivoGestionLlamadaSelected(which);
                })
                .setPositiveButton(R.string.text_continuar, (dialog, which) -> {
                    if (presenter != null) presenter.onBtnConfirmarResultadoGestionLlamadaClick();
                })
                .show();
    }

    @Override
    public void showDialogSeleccionarMotivoLlamadaNoContactada(String[] motivos) {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle("Seleccione el motivo por el cual la llamada no fue contactada")
                .setCancelable(false)
                .setSingleChoiceItems(motivos, 0, (dialog, which) -> {
                    if (presenter != null) presenter.onMotivoGestionLlamadaSelected(which);
                })
                .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                    if (presenter != null) presenter.onBtnConfirmarMotivoGestionLlamadaClick();
                })
                .show();
    }

    @Override
    public Menu getMenuToolbar() {
        return menu;
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void manageGuia(){
        binding.btnGestionar.setEnabled(false);
        presenter.onBtnGestionarClick();
        new Handler().postDelayed(() -> binding.btnGestionar.setEnabled(true), 1000);

    }
    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_detalle_ruta);

        binding.rvDetalles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDetalles.setHasFixedSize(true);

        binding.btnGestionar.setOnClickListener((v) -> {
            if(presenter.hasObservation()){
                presenter.displayRequiredObservation();
            }else{
                manageGuia() ;
            }
        });

        binding.actionIndicacionesContainerLayout.setOnClickListener((v) -> {
            binding.actionIndicacionesContainerLayout.setEnabled(false);
            presenter.onBtnIndicacionesClick();
            new Handler().postDelayed(() ->
                    binding.actionIndicacionesContainerLayout.setEnabled(true), 1000);
        });

        binding.actionLlamarContainerLayout.setOnClickListener((v) ->
                presenter.onBtnLlamarClick());

        binding.actionPiezasContainerLayout.setOnClickListener((v) ->
                presenter.onBtnPiezasClick());

        binding.actionRequerimientoContainerLayout.setOnClickListener((v) ->
                presenter.onBtnRequerimientoClick());

        binding.actionDevolverContainerLayout.setOnClickListener((v) ->
                presenter.onBtnDevolverClick());
    }
}