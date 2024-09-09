package com.urbanoexpress.iridio3.pe.presenter;

import android.content.Intent;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.entity.MenuApp;
import com.urbanoexpress.iridio3.pe.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pe.model.entity.TipoDireccion;
import com.urbanoexpress.iridio3.pe.model.entity.Usuario;
import com.urbanoexpress.iridio3.pe.model.interactor.SplashLogInInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.ui.MainActivity;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.SplashLogInView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mick on 19/05/16.
 */
public class SplashLogInPresenter implements RequestCallback {

    private SplashLogInView view;
    private SplashLogInInteractor splashLogInInteractor;

    private String firebaseToken = "";
    private String devicePhone = "";

    public SplashLogInPresenter(SplashLogInView view) {
        this.view = view;
        splashLogInInteractor = new SplashLogInInteractor(view.getViewContext());

        Preferences.getInstance().init(view.getViewContext(), "GlobalConfigApp");
        devicePhone = Preferences.getInstance().getString("phone", "");

        Preferences.getInstance().init(view.getViewContext(), "UserProfile");

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);

        view.animateSplashScreen();
    }

    public void initPreLoading() {
        if (Preferences.getInstance().getString("idUsuario", "").length() > 0) {
            if (view.getViewContext() != null) {
                view.getViewContext().startActivity(
                        new Intent(view.getViewContext(), MainActivity.class));
                view.finishActivity();
            }
        } else {
            view.setUserName(Preferences.getInstance().getString("usuario", ""));
            view.showFormLogin();
            view.showOptionsMenu();
        }
    }

    public void logIn(String userName, String passWord) {
        if (validateUserAndPassword(userName, passWord)) {
            if (CommonUtils.validateConnectivity(view.getViewContext())) {
                view.showProgressDialog(R.string.act_login_login_title_iniciando_sesion,
                        R.string.text_espere_un_momento);

                String[] params = new String[]{
                        userName,
                        CommonUtils.getSHA1(passWord),
                        firebaseToken,
                        devicePhone
                };

                splashLogInInteractor.logIn(params, this);
            } else {
                view.setEnabledBtnLogIn(true);
            }
        } else {
            view.setEnabledBtnLogIn(true);
        }
    }

    private boolean validateUserAndPassword(String userName, String passWord) {
        if (userName.length() == 0 || passWord.length() == 0) {
            view.showToast(R.string.act_login_error_datos_usuario);
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(JSONObject response) {
        try {
            boolean success = response.getBoolean("success");

            view.setEnabledBtnLogIn(true);

            if (success) {
                new ProcessLogInDataTask().execute(response);
            } else {
                view.dismissProgressDialog();
                view.showToast(response.getString("msg_error"));
            }
        } catch (JSONException | NullPointerException ex) {
            ex.printStackTrace();
            view.dismissProgressDialog();
            view.setEnabledBtnLogIn(true);
            view.showToast(R.string.json_object_exception);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
        }
    }

    @Override
    public void onError(VolleyError error) {
        error.printStackTrace();
        view.dismissProgressDialog();
        view.setEnabledBtnLogIn(true);
        view.showToast(R.string.volley_error_message);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
    }

    private class ProcessLogInDataTask extends AsyncTaskCoroutine<JSONObject, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog(R.string.act_login_login_title_iniciando_sesion,
                    R.string.act_login_login_msg_procesando_datos);
        }

        @Override
        public Boolean doInBackground(JSONObject... jsonObjects) {
            try {
                JSONObject data = jsonObjects[0].getJSONObject("data");
                saveUserProfile(data.getJSONObject("userProfile"));
            } catch (JSONException ex) {
                ex.printStackTrace();
                //TODO show messages on background
                view.showToast(R.string.act_login_login_msg_error_datos_user_profile);
                return false;
            }

            try {
                JSONObject data = jsonObjects[0].getJSONObject("data");
                saveAppDataDefault(data.getJSONObject("appDataDefault"));
            } catch (JSONException ex) {
                ex.printStackTrace();
                CommonUtils.deleteUserData();
                view.showToast(R.string.act_login_login_msg_error_datos_app_data_default);
                return false;
            }

            try {
                JSONObject data = jsonObjects[0].getJSONObject("data");
                saveUserMenu(data.getJSONArray("userMenu"));
            } catch (JSONException ex) {
                ex.printStackTrace();
                CommonUtils.deleteUserData();
                view.showToast(R.string.act_login_login_msg_error_datos_app_menu_user);
                return false;
            }

            return true;
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            view.dismissProgressDialog();

            // Validar si todos los datos se proceso correctamente
            if (aBoolean) {
                view.showToast(R.string.act_login_login_success);
                view.getViewContext().startActivity(
                        new Intent(view.getViewContext(), MainActivity.class));
                view.finishActivity();
            }
        }

        private void saveUserProfile(JSONObject data) throws JSONException, NullPointerException {

            Preferences.getInstance().edit()
                    .putString("idUsuario", data.getString("id_user"))
                    .putString("idPer", data.getString("per_id"))
                    .putString("mostrarEncuesta", data.getString("mostrar_encuesta"))
                    .putString("usuario", data.getString("usuario"))
                    .putString("nombre", data.getString("nombre"))
                    .putString("tipoUsuario", data.getString("usr_tipo"))
                    .putString("codigoProvincia", data.getString("prov_codigo"))
                    .putString("nombreProvincia", data.getString("prov_nombre"))
                    .putString("siglaProvincia", data.getString("prov_sigla"))
                    .putString("perfil", data.getString("perfil"))
                    .putString("tiempoRequestGPS", data.getString("time_session"))
                    .putString("tiempoRequestDatos", data.getString("time_sincro"))
                    .putString("lineaPostal", data.getString("postal"))
                    .putString("lineaValores", data.getString("valores"))
                    .putString("lineaLogistica", data.getString("logistica"))
                    .putString("lineaLogisticaEspecial", data.getString("log_especial"))
                    .putLong("inicioSerieRecoleccion", 0)
                    .putBoolean("menuAppAvailable", true)
                    //.putBoolean("courierDisponibleRutaExpress", false)
                    .putInt("idRuta", 0)
                    .apply();

            Usuario.deleteAll(Usuario.class);
            Usuario usuario = new Usuario(
                    data.getString("id_user"),
                    data.getString("usuario"),
                    data.getString("nombre"),
                    data.getString("usr_tipo"),
                    data.getString("prov_codigo"),
                    data.getString("prov_nombre"),
                    data.getString("prov_sigla"),
                    data.getString("perfil"),
                    data.getString("time_session"),
                    data.getString("time_sincro"),
                    data.getString("postal"),
                    data.getString("valores"),
                    data.getString("logistica"),
                    data.getString("log_especial"),
                    devicePhone,
                    true,
                    "0",
                    0
            );
            usuario.save();
        }

        private void saveAppDataDefault(JSONObject data) throws JSONException {
            MotivoDescarga.deleteAll(MotivoDescarga.class);
            TipoDireccion.deleteAll(TipoDireccion.class);

            JSONObject dataMotivos = data.getJSONObject("motivos");
            JSONObject motivosDBMain = dataMotivos.getJSONObject("dbMain");

            if (motivosDBMain.has("entrega")) {
                saveMotivos(motivosDBMain.getJSONArray("entrega"),
                        MotivoDescarga.Tipo.ENTREGA,
                        "\n- Entrega");

                saveMotivos(motivosDBMain.getJSONArray("entrega_parcial"),
                        MotivoDescarga.Tipo.ENTREGA_PARCIAL,
                        "\n- Entrega Parcial");

                saveMotivos(motivosDBMain.getJSONArray("no_entrega"),
                        MotivoDescarga.Tipo.NO_ENTREGA,
                        "\n- No Entrega");

                saveMotivos(motivosDBMain.getJSONArray("recolecta"),
                        MotivoDescarga.Tipo.RECOLECTA,
                        "\n- Recolecta");

                saveMotivos(motivosDBMain.getJSONArray("no_recolecta"),
                        MotivoDescarga.Tipo.NO_RECOLECTA,
                        "\n- No Recolecta");

                saveMotivos(motivosDBMain.getJSONArray("entrega_devolucion"),
                        MotivoDescarga.Tipo.ENTREGA_DEVOLUCION,
                        "\n- Entrega Devolución");

                saveMotivos(motivosDBMain.getJSONArray("entrega_devolucion_parcial"),
                        MotivoDescarga.Tipo.ENTREGA_DEVOLUCION_PARCIAL,
                        "\n- Entrega Devolución Parcial");

                saveMotivos(motivosDBMain.getJSONArray("entrega_liquidacion"),
                        MotivoDescarga.Tipo.ENTREGA_LIQUIDACION,
                        "\n- Entrega Liquidación");

                saveMotivos(motivosDBMain.getJSONArray("no_entrega_liq_dev"),
                        MotivoDescarga.Tipo.NO_ENTREGA_LIQ_DEV,
                        "\n- No Entrega Liquidación/Devolución");

                saveMotivos(motivosDBMain.getJSONArray("gestion_con_llamada"),
                        MotivoDescarga.Tipo.GESTION_CON_LLAMADA,
                        "\n- No Gestión con Llamada");

                saveMotivos(motivosDBMain.getJSONArray("observacion_entrega"),
                        MotivoDescarga.Tipo.OBSERVACION_ENTREGA,
                        "\n- No Observación Entrega");

                saveMotivos(motivosDBMain.getJSONArray("no_hubo_tiempo"),
                        MotivoDescarga.Tipo.NO_HUBO_TIEMPO,
                        "\n- No Hubo Tiempo");

                JSONArray tipoDireccion = motivosDBMain.getJSONArray("tipo_direccion");

                for (int i = 0; i < tipoDireccion.length(); i++) {
                    JSONObject jsonObject = tipoDireccion.getJSONObject(i);
                    TipoDireccion tipoDir = new TipoDireccion(
                            Preferences.getInstance().getString("idUsuario", ""),
                            jsonObject.getString("id_elemento"),
                            jsonObject.getString("descripcion")
                    );
                    tipoDir.save();
                }
            }

            /*if (msgErrorMotivos.length() > 0) {
                if (msgErrorMotivos.contains("Recolecta") ) {
                    if (Preferences.getInstance().getString("lineaLogistica", "").equals("1")) {
                        showToast(splashLogInView.getViewContext(),
                                "Su usuario, no tiene datos de motivos (CHK) de:" + msgErrorMotivos + "",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    showToast(splashLogInView.getViewContext(),
                            "Su usuario, no tiene datos de motivos (CHK) de:" + msgErrorMotivos + "",
                            Toast.LENGTH_LONG);
                }
            }*/
        }

        private void saveMotivos(JSONArray motivos, int tipoMotivo, String msgError) throws JSONException {
            if (motivos.length() > 0) {
                for (int i = 0; i < motivos.length(); i++) {
                    JSONObject jsonObject = motivos.getJSONObject(i);
                    MotivoDescarga motivo = new MotivoDescarga(
                            Preferences.getInstance().getString("idUsuario", ""),
                            tipoMotivo,
                            jsonObject.getString("mot_id"),
                            jsonObject.getString("codigo"),
                            jsonObject.getString("descri"),
                            jsonObject.getString("linea")
                    );
                    motivo.save();
                }
            }/* else {
                msgErrorMotivos += msgError;
            }*/
        }

        private void saveUserMenu(JSONArray data) throws JSONException {
            JSONObject jsonObject;

            MenuApp menuAppDB;
            MenuApp.deleteAll(MenuApp.class);

            for (int i = 0; i < data.length(); i++) {
                jsonObject = data.getJSONObject(i);

                if (validarNivelMenu(jsonObject.getInt("nivel"))) {
                    menuAppDB = new MenuApp(
                            Preferences.getInstance().getString("idUsuario", ""),
                            jsonObject.getString("id_menu"),
                            jsonObject.getString("padre"),
                            jsonObject.getString("nivel"),
                            jsonObject.getString("nombre"),
                            jsonObject.getString("menu_class"),
                            jsonObject.getString("orden"));
                    menuAppDB.save();
                }
            }
        }

        private boolean validarNivelMenu(int nivel) {
            // Validar que los menus solo sean mayores a 0
            // Es decir, los menus principales no seran contados en el menu de la aplicacion.
            return nivel > 0;
        }
    }

}