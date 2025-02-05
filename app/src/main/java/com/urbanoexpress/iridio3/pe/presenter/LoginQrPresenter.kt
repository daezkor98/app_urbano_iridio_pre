package com.urbanoexpress.iridio3.pe.presenter

import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.google.firebase.messaging.FirebaseMessaging
import com.urbanoexpress.iridio3.pe.model.entity.MenuApp
import com.urbanoexpress.iridio3.pe.model.entity.MotivoDescarga
import com.urbanoexpress.iridio3.pe.model.entity.TipoDireccion
import com.urbanoexpress.iridio3.pe.model.entity.Usuario
import com.urbanoexpress.iridio3.pe.model.interactor.SplashLogInInteractor
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback
import com.urbanoexpress.iridio3.pe.util.CommonUtils
import com.urbanoexpress.iridio3.pe.util.Preferences
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Logger


/**
 * Created by Brandon Quintanilla on Febrero/03/2025.
 */
class LoginQrPresenter(private var view: LoginQrView) : RequestCallback {

    private var splashLogInInteractor: SplashLogInInteractor = SplashLogInInteractor(view.viewContext)
    private var firebaseToken = ""
    private var devicePhone: String

    init {

        Preferences.getInstance().init(view.viewContext, "GlobalConfigApp")
        devicePhone = Preferences.getInstance().getString("phone", "") ?: ""

        Preferences.getInstance().init(view.viewContext, "UserProfile")

        FirebaseMessaging.getInstance().token.addOnSuccessListener { s: String ->
            firebaseToken = s
        }
    }


    fun logIn(userName: String, passWord: String?) {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {

            val params = arrayOf<String>(
                userName,
                CommonUtils.getSHA1(passWord),
                firebaseToken,
                devicePhone
            )

            splashLogInInteractor.logIn(params, this)
        }
    }

    override fun onSuccess(response: JSONObject?) {

        try {
            val success = response!!.getBoolean("success")

            if (success) {
                ProcessLogInDataTask(devicePhone).execute<Any>(response)
            } else {
                Log.d("Hola", "error");
            }
        } catch (ex: JSONException) {
            Log.d("Hola", "error" + ex.message);

        } catch (ex: NullPointerException) {
            Log.d("Hola", "error" + ex.message);

        }
    }

    override fun onError(error: VolleyError?) {

        Log.d("Hola", "error" + error?.message);
    }

    class ProcessLogInDataTask(private val devicePhone: String) :
        AsyncTaskCoroutine<JSONObject?, Boolean?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* view.showProgressDialog(
                 R.string.act_login_login_title_iniciando_sesion,
                 R.string.act_login_login_msg_procesando_datos
             )*/
        }

        override fun doInBackground(vararg params: JSONObject?): Boolean {
            try {
                params[0]?.let {
                    val data = it.getJSONObject("data")
                    saveUserProfile(data.getJSONObject("userProfile"))
                }

            } catch (ex: JSONException) {
                ex.printStackTrace()
                //TODO show messages on background
                // view.showToast(R.string.act_login_login_msg_error_datos_user_profile)
                return false
            }

            try {
                params[0]?.let {
                    val data = it.getJSONObject("data")
                    saveAppDataDefault(data.getJSONObject("appDataDefault"))
                }

            } catch (ex: JSONException) {
                ex.printStackTrace()
                CommonUtils.deleteUserData()
                //view.showToast(R.string.act_login_login_msg_error_datos_app_data_default)
                return false
            }

            try {
                params[0]?.let {
                    val data = it.getJSONObject("data")
                    saveUserMenu(data.getJSONArray("userMenu"))
                }

            } catch (ex: JSONException) {
                ex.printStackTrace()
                CommonUtils.deleteUserData()
                // view.showToast(R.string.act_login_login_msg_error_datos_app_menu_user)
                return false
            }

            return true
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)
            //view.dismissProgressDialog()

            // Validar si todos los datos se proceso correctamente
            if (aBoolean!!) {
                /*  view.showToast(R.string.act_login_login_success)
                  view.getViewContext().startActivity(
                      Intent(view.getViewContext(), MainActivity::class.java)
                  )
                  view.finishActivity()*/
            }
        }

        @Throws(JSONException::class, java.lang.NullPointerException::class)
        fun saveUserProfile(data: JSONObject) {
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
                .putBoolean(
                    "menuAppAvailable",
                    true
                ) //.putBoolean("courierDisponibleRutaExpress", false)
                .putInt("idRuta", 0)
                .apply()

            Usuario.deleteAll(Usuario::class.java)
            val usuario = Usuario(
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
            )
            usuario.save()
        }

        @Throws(JSONException::class)
        fun saveAppDataDefault(data: JSONObject) {
            MotivoDescarga.deleteAll(MotivoDescarga::class.java)
            TipoDireccion.deleteAll(TipoDireccion::class.java)

            val dataMotivos = data.getJSONObject("motivos")
            val motivosDBMain = dataMotivos.getJSONObject("dbMain")

            if (motivosDBMain.has("entrega")) {
                saveMotivos(
                    motivosDBMain.getJSONArray("entrega"),
                    MotivoDescarga.Tipo.ENTREGA,
                    "\n- Entrega"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("entrega_parcial"),
                    MotivoDescarga.Tipo.ENTREGA_PARCIAL,
                    "\n- Entrega Parcial"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("no_entrega"),
                    MotivoDescarga.Tipo.NO_ENTREGA,
                    "\n- No Entrega"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("recolecta"),
                    MotivoDescarga.Tipo.RECOLECTA,
                    "\n- Recolecta"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("no_recolecta"),
                    MotivoDescarga.Tipo.NO_RECOLECTA,
                    "\n- No Recolecta"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("entrega_devolucion"),
                    MotivoDescarga.Tipo.ENTREGA_DEVOLUCION,
                    "\n- Entrega Devolución"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("entrega_devolucion_parcial"),
                    MotivoDescarga.Tipo.ENTREGA_DEVOLUCION_PARCIAL,
                    "\n- Entrega Devolución Parcial"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("entrega_liquidacion"),
                    MotivoDescarga.Tipo.ENTREGA_LIQUIDACION,
                    "\n- Entrega Liquidación"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("no_entrega_liq_dev"),
                    MotivoDescarga.Tipo.NO_ENTREGA_LIQ_DEV,
                    "\n- No Entrega Liquidación/Devolución"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("gestion_con_llamada"),
                    MotivoDescarga.Tipo.GESTION_CON_LLAMADA,
                    "\n- No Gestión con Llamada"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("observacion_entrega"),
                    MotivoDescarga.Tipo.OBSERVACION_ENTREGA,
                    "\n- No Observación Entrega"
                )

                saveMotivos(
                    motivosDBMain.getJSONArray("no_hubo_tiempo"),
                    MotivoDescarga.Tipo.NO_HUBO_TIEMPO,
                    "\n- No Hubo Tiempo"
                )

                val tipoDireccion = motivosDBMain.getJSONArray("tipo_direccion")

                for (i in 0 until tipoDireccion.length()) {
                    val jsonObject = tipoDireccion.getJSONObject(i)
                    val tipoDir = TipoDireccion(
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonObject.getString("id_elemento"),
                        jsonObject.getString("descripcion")
                    )
                    tipoDir.save()
                }
            }

        }

        @Throws(JSONException::class)
        fun saveMotivos(motivos: JSONArray, tipoMotivo: Int, msgError: String?) {
            if (motivos.length() > 0) {
                for (i in 0 until motivos.length()) {
                    val jsonObject = motivos.getJSONObject(i)
                    val motivo = MotivoDescarga(
                        Preferences.getInstance().getString("idUsuario", ""),
                        tipoMotivo,
                        jsonObject.getString("mot_id"),
                        jsonObject.getString("codigo"),
                        jsonObject.getString("descri"),
                        jsonObject.getString("linea")
                    )
                    motivo.save()
                }
            } /* else {
                msgErrorMotivos += msgError;
            }*/
        }

        @Throws(JSONException::class)
        fun saveUserMenu(data: JSONArray) {
            var jsonObject: JSONObject

            var menuAppDB: MenuApp
            MenuApp.deleteAll(MenuApp::class.java)

           /* menuAppDB = MenuApp(
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonObject.getString("id_menu"),
                        jsonObject.getString("padre"),
                        jsonObject.getString("nivel"),
                        jsonObject.getString("nombre"),
                        jsonObject.getString("menu_class"),
                        jsonObject.getString("orden")
                    )
                    menuAppDB.save()

            */


        }
    }

}