package com.urbanoexpress.iridio3.pe.presenter.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.databinding.ModalInputClaveForzarCierreRutaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.Despacho;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pe.model.entity.Pieza;
import com.urbanoexpress.iridio3.pe.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.TrackLocation;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.FileUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by mick on 28/11/16.
 */

public class ForzarCierreRutaHelper {

    private ModalInputClaveForzarCierreRutaBinding binding;
    private Activity activity;
    private AlertDialog dialog;

    private final int ACTION_CONTEXT;

    public interface ActionContext {
        int RUTA_DEL_DIA = 1;
        int PLAN_DE_VIAJE = 2;
    }

    public ForzarCierreRutaHelper(Context context, int actionContext) {
        this.activity = (Activity) context;
        this.ACTION_CONTEXT = actionContext;
    }

    public void init() {
        int resIdMsg;
        if (ACTION_CONTEXT == ActionContext.RUTA_DEL_DIA) {
            resIdMsg = R.string.activity_ruta_message_advertencia_forzar_cierre_ruta;
        } else { // ActionContext.PLAN_DE_VIAJE
            resIdMsg = R.string.act_plan_de_viaje_msg_advertencia_forzar_cierre_ruta;
        }

        BaseModalsView.showAlertDialog(activity,
                R.string.activity_ruta_title_advertencia_forzar_cierre_ruta,
                resIdMsg,
                R.string.text_continuar, (dialog, which) -> showDialogInputCredenciales(),
                R.string.text_cancelar, null);
    }

    private void showDialogInputCredenciales() {
        binding = ModalInputClaveForzarCierreRutaBinding.inflate(
                activity.getLayoutInflater(), null, false);

        binding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

        binding.txtPassword.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handleCerrarRuta();
            }
            return false;
        });

        binding.btnCerrarRuta.setOnClickListener(v -> handleCerrarRuta());

        dialog = new AlertDialog.Builder(activity).setView(binding.getRoot()).create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void handleCerrarRuta() {
        if (binding.txtPassword.getText().toString().length() > 0 &&
                binding.txtUserName.getText().toString().length() > 0) {
            dialog.dismiss();
            BaseModalsView.showProgressDialog(activity, R.string.text_validando_clave);
            validateCredencial(binding.txtUserName.getText().toString(), binding.txtPassword.getText().toString());
        } else {
            BaseModalsView.showToast(activity,
                    R.string.activity_ruta_message_clave_forzar_cierre_ruta_incorrecto, Toast.LENGTH_SHORT);
        }
    }

    public static void deleteAllDataRuta(Context context) {
        Log.d("ACTIVITY", "ELIMINANDO ARCHIVOS");
        File file = new File(FileUtils.getBaseDirectoryPath(context));
        FileUtils.deleteRecursively(file);

        Ruta.deleteAll(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        Pieza.deleteAll(Pieza.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        Imagen.deleteAll(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Imagen.Tipo.GESTION_GUIA + "");
        EstadoRuta.deleteAll(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "(" + NamingHelper.toSQLNameDefault("tipoRuta") + " is null or " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?)",
                Preferences.getInstance().getString("idUsuario", ""),
                EstadoRuta.TipoRuta.RUTA_DEL_DIA + "");
        DescargaRuta.deleteAll(DescargaRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        GuiaGestionada.deleteAll(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        GestionLlamada.deleteAll(GestionLlamada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        TrackLocation.deleteAll(TrackLocation.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        SecuenciaRuta.deleteAll(SecuenciaRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        LogErrorSync.deleteAll(LogErrorSync.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));

        Preferences.getInstance().edit().putInt("idRuta", 0).apply();
    }

    public static void deleteAllDataParadaProgramada(Context context) {
        File file = new File(FileUtils.getBaseDirectoryPath(context));
        FileUtils.deleteRecursively(file);

        PlanDeViaje.deleteAll(PlanDeViaje.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        ParadaProgramada.deleteAll(ParadaProgramada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        Despacho.deleteAll(Despacho.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
        Imagen.deleteAll(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Imagen.Tipo.PARADA_PROGRAMADA + "");
        IncidenteRuta.deleteAll(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + "");
    }

    private void forzarCierreRuta() {
        BaseModalsView.showProgressDialog(activity, R.string.text_forzar_cierre_ruta);

        if (ACTION_CONTEXT == ActionContext.RUTA_DEL_DIA) {
            new Thread(() -> {
                deleteAllDataRuta(activity);
                activity.runOnUiThread(() -> {
                    sendOnRutaFinalizadaReceiver();
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showToast(activity,
                            R.string.activity_ruta_forzar_cierre_ruta_completado,
                            Toast.LENGTH_SHORT);
                });
            }).start();
        } else {
            new Thread(() -> {
                deleteAllDataParadaProgramada(activity);
                activity.runOnUiThread(() -> {
                    sendOnForzarCierreRutaReceiver();
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showToast(activity,
                            R.string.activity_ruta_forzar_cierre_ruta_completado,
                            Toast.LENGTH_SHORT);
                });
            }).start();
        }
    }

    private void validateCredencial(String username, String password) {
        if (CommonUtils.validateConnectivity(activity)) {
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("username",      username);
            ApiRequest.getInstance().putParams("password",      CommonUtils.getSHA1(password));
            ApiRequest.getInstance().putParams("imei",          Session.getUser().getDevicePhone());
            ApiRequest.getInstance().putParams("vp_id_user",    Preferences.getInstance().getString("idUsuario", ""));
            ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                            ApiRest.Api.VALIDATE_CLAVE_CIERRE_RUTA,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    BaseModalsView.hideProgressDialog();
                                    forzarCierreRuta();
                                } else {
                                    BaseModalsView.hideProgressDialog();
                                    BaseModalsView.showToast(activity,
                                            response.getString("msg_error"), Toast.LENGTH_LONG);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                BaseModalsView.hideProgressDialog();
                                BaseModalsView.showToast(activity,
                                        R.string.json_object_exception,
                                        Toast.LENGTH_LONG);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            BaseModalsView.hideProgressDialog();
                            BaseModalsView.showToast(activity,
                                    R.string.volley_error_message,
                                    Toast.LENGTH_LONG);
                        }
                    });
        }
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#rutaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#rutaFinalizadaReceiver}
     */
    private void sendOnRutaFinalizadaReceiver() {
        Intent intent = new Intent("OnRutaFinalizada");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    /**
     * Receiver
     *
     * {@link PlanDeViajePresenter#forzarCierreRutaReceiver}
     */
    private void sendOnForzarCierreRutaReceiver() {
        Intent intent = new Intent(LocalAction.FORZAR_CIERRE_RUTA_ACTION);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

}
