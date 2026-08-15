package com.urbanoexpress.iridio3.pre.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.UrbanoPayInput;
import com.urbanoexpress.iridio3.pre.model.entity.UrbanoPayResult;
import com.urbanoexpress.iridio3.pre.presenter.UrbanoPayPresenter;
import com.urbanoexpress.iridio3.pre.util.MetricsUtils;
import com.urbanoexpress.iridio3.pre.util.QRCode;
import com.urbanoexpress.iridio3.pre.view.UrbanoPayView;

/**
 * Pantalla dedicada al pago QR (Urbano Pay). Se lanza desde flujos que necesiten cobrar
 * (EntregaGEDialog, RecoleccionValija, etc.) via {@code UrbanoPayContract} y retorna el
 * resultado con {@link UrbanoPayResult}.
 *
 * <p>Estados: INICIAL (guía + botón Generar QR) → QR (código QR + escanear) → PAGADO (comprobante).
 * El motorizado también puede cambiar a efectivo en cualquier momento.
 */
public class UrbanoPayActivity extends BaseActivity implements UrbanoPayView {

    public static final String EXTRA_INPUT = "input";
    public static final String EXTRA_RESULT = "result";

    // Views
    private TextView lblTituloEntrega;
    private LinearLayout boxEstadoInicial;
    private LinearLayout boxEstadoQR;
    private LinearLayout boxEstadoPagado;
    private TextView lblGuiaInicial;
    private ImageView qrCodeImage;
    private TextView lblMontoQR;
    private TextView lblRqIdCode;
    private TextView lblProgresoPago;
    private TextView lblPagadoGuia;
    private TextView lblPagadoMonto;
    private TextView lblPagadoDoc;
    private TextView lblPagadoNombre;
    private TextView btnEfectivo;
    private TextView btnGenerarQr;
    private TextView lblEscaneaPagarCta;
    private LinearLayout boxWatermark;
    private ProgressDialog progressDialog;

    private UrbanoPayPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urbano_pay);

        UrbanoPayInput input = (UrbanoPayInput) getIntent().getSerializableExtra(EXTRA_INPUT);
        if (input == null) {
            // No debería ocurrir — retornar cancelado
            Intent res = new Intent();
            res.putExtra(EXTRA_RESULT, new UrbanoPayResult(
                    UrbanoPayResult.Status.CANCELADO, 0, 0, null, "", ""));
            setResult(RESULT_OK, res);
            finish();
            return;
        }

        bindViews();
        presenter = new UrbanoPayPresenter(this, input);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (presenter != null) {
            presenter.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void bindViews() {
        lblTituloEntrega = findViewById(R.id.lbl_titulo_entrega);
        boxEstadoInicial = findViewById(R.id.box_estado_inicial);
        boxEstadoQR = findViewById(R.id.box_estado_qr);
        boxEstadoPagado = findViewById(R.id.box_estado_pagado);
        lblGuiaInicial = findViewById(R.id.lbl_guia_inicial);
        qrCodeImage = findViewById(R.id.qr_code_image);
        lblMontoQR = findViewById(R.id.lbl_monto_qr);
        lblRqIdCode = findViewById(R.id.lbl_rq_id_code);
        lblProgresoPago = findViewById(R.id.lbl_progreso_pago);
        lblPagadoGuia = findViewById(R.id.lbl_pagado_guia);
        lblPagadoMonto = findViewById(R.id.lbl_pagado_monto);
        lblPagadoDoc = findViewById(R.id.lbl_pagado_doc);
        lblPagadoNombre = findViewById(R.id.lbl_pagado_nombre);
        btnEfectivo = findViewById(R.id.btn_efectivo);
        btnGenerarQr = findViewById(R.id.btn_generar_qr);
        lblEscaneaPagarCta = findViewById(R.id.lbl_escanea_pagar_cta);
        boxWatermark = findViewById(R.id.box_watermark);

        btnGenerarQr.setOnClickListener(v -> presenter.onBtnGenerarQRClick());
        findViewById(R.id.btn_continuar_pago).setOnClickListener(v -> presenter.onBtnContinuarPagoClick());
        btnEfectivo.setOnClickListener(v -> presenter.onBtnEfectivoClick());
    }

    // ==================== UrbanoPayView ====================

    @NonNull
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showEstadoInicial() {
        lblTituloEntrega.setVisibility(View.GONE);
        boxEstadoInicial.setVisibility(View.VISIBLE);
        boxEstadoQR.setVisibility(View.GONE);
        boxEstadoPagado.setVisibility(View.GONE);
        btnEfectivo.setVisibility(View.VISIBLE);
        btnGenerarQr.setVisibility(View.VISIBLE);
        lblEscaneaPagarCta.setVisibility(View.GONE);
        boxWatermark.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEstadoQR() {
        lblTituloEntrega.setVisibility(View.VISIBLE);
        boxEstadoInicial.setVisibility(View.GONE);
        boxEstadoQR.setVisibility(View.VISIBLE);
        boxEstadoPagado.setVisibility(View.GONE);
        btnEfectivo.setVisibility(View.VISIBLE);
        btnGenerarQr.setVisibility(View.GONE);
        lblEscaneaPagarCta.setVisibility(View.VISIBLE);
        boxWatermark.setVisibility(View.GONE);
    }

    @Override
    public void showEstadoPagado() {
        lblTituloEntrega.setVisibility(View.VISIBLE);
        boxEstadoInicial.setVisibility(View.GONE);
        boxEstadoQR.setVisibility(View.GONE);
        boxEstadoPagado.setVisibility(View.VISIBLE);
        btnEfectivo.setVisibility(View.GONE);
        btnGenerarQr.setVisibility(View.GONE);
        lblEscaneaPagarCta.setVisibility(View.GONE);
        boxWatermark.setVisibility(View.GONE);
    }

    @Override
    public void setTextTituloEntrega(String titulo) {
        lblTituloEntrega.setText(titulo);
    }

    @Override
    public void setTextGuiaInicial(String guia) {
        lblGuiaInicial.setText(guia);
    }

    @Override
    public void displayQR(String hash) {
        // Usa el mismo patrón que EntregaGEDialog para consistencia (QRCode.Builder + Glide)
        Bitmap qrBitmap = new QRCode.Builder(this)
                .setValue(hash)
                .setSize(MetricsUtils.dpToPx(this, 240))
                .build();
        Glide.with(this).load(qrBitmap).into(qrCodeImage);
    }

    @Override
    public void setTextMonto(String montoFmt) {
        lblMontoQR.setText(montoFmt);
    }

    @Override
    public void setTextRqIdCode(String rqIdText) {
        lblRqIdCode.setText(rqIdText);
    }

    @Override
    public void setTextProgresoPago(String progreso) {
        lblProgresoPago.setText(progreso);
    }

    @Override
    public void setVisibilityProgreso(boolean visible) {
        lblProgresoPago.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDatosComprobante(String estado, String guia, String monto, String docNumero, String nombre) {
        // Estado ya está en el layout como "¡PAGADO!" hardcoded, no lo cambiamos
        lblPagadoGuia.setText(guia);
        lblPagadoMonto.setText(monto);
        lblPagadoDoc.setText(docNumero);
        lblPagadoNombre.setText(nombre);
    }

    @Override
    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void closeWithResult(UrbanoPayResult result) {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT, result);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }
}
