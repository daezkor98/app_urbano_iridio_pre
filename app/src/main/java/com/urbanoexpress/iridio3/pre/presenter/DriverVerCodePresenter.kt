package com.urbanoexpress.iridio3.pre.presenter

import android.util.Log
import com.urbanoexpress.iridio3.pre.R
import com.urbanoexpress.iridio3.pre.model.entity.GrupoMotivo
import com.urbanoexpress.iridio3.pre.model.entity.MenuApp
import com.urbanoexpress.iridio3.pre.model.entity.MotivoDescarga
import com.urbanoexpress.iridio3.pre.model.entity.TipoDireccion
import com.urbanoexpress.iridio3.pre.model.entity.Usuario
import com.urbanoexpress.iridio3.pre.model.interactor.DriverVerCodeInteractor
import com.urbanoexpress.iridio3.pre.util.CommonUtils
import com.urbanoexpress.iridio3.pre.util.Preferences
import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine
import com.urbanoexpress.iridio3.pre.util.constant.DriverVerificationConstants.GENERIC_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
/**
 * Created by Brandon Quintanilla on Febrero/03/2025.
 */
class DriverVerCodePresenter(
    private var view: DriverContract.DriverVerificationView?,
    private val interactor: DriverVerCodeInteractor,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DriverContract.DriverVerificationPresenter {

    private var job: Job? = null

    override fun loginDriverUser(idRutaQR: String, verCodeQR: String, driverPhone: String) {
        view?.let { view ->
            view.showLoginProgressDialog()
          job = CoroutineScope(mainDispatcher).launch {
                try {
                    val users = withContext(ioDispatcher) {
                        interactor.loginQResponse(
                            idRuta = idRutaQR,
                            verificationCode = verCodeQR
                        )
                    }
                    ProcessLogInDataTask(driverPhone, view).execute<Any>(users)

                } catch (e: Exception) {
                    view.dismissLoginProgressDialog()
                    view.showError(e.message ?: GENERIC_ERROR)
                }
            }
        }
    }

    override fun detachView() {
        job?.cancel()
    }
}

class ProcessLogInDataTask(
    private val devicePhone: String,
    private val view: DriverContract.DriverVerificationView
) :
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
                val data: JSONObject = it.getJSONObject("data")
                saveToken(data.getString("access_token"))
            }

        } catch (ex: JSONException) {
            view.showError(R.string.act_login_login_msg_error_datos_user_profile.toString())
            return false
        }
        try {
            params[0]?.let {
                val data = it.getJSONObject("data")
                saveUserProfile(data.getJSONObject("userProfile"))
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
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
        if (aBoolean == true) {
            view.fromLoginToMainMenu()
        } else {
            Log.d("Hola", "ocurrio un error al validar el json");
        }
    }

    @Throws(JSONException::class, java.lang.NullPointerException::class)
    private fun saveUserProfile(data: JSONObject) {
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
            0,
            "0"
        )
        usuario.save()
    }

    @Throws(JSONException::class)
    private fun saveAppDataDefault(data: JSONObject) {
        GrupoMotivo.deleteAll(
            GrupoMotivo::class.java)
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

            saveMotivos2(
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
    private fun saveMotivos(motivos: JSONArray, tipoMotivo: Int, msgError: String?) {
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
    private fun saveMotivos2(motivos: JSONArray, tipoMotivo: Int, msgError: String) {
        if (motivos.length() > 0) {
            for (i in 0 until motivos.length()) {
                val jsonObject = motivos.getJSONObject(i)
                val grupoMotivo =
                    GrupoMotivo(
                        jsonObject.getInt("gru_id"),
                        jsonObject.getString("gru_descri")
                    )
                grupoMotivo.save()

                val submotivosJson = jsonObject.getJSONArray("submotivos")
                for (j in 0 until submotivosJson.length()) {
                    val jsonObjectSubMotivo = submotivosJson.getJSONObject(j)
                    val submotivo = MotivoDescarga(
                        Preferences.getInstance().getString("idUsuario", ""),
                        tipoMotivo,
                        jsonObjectSubMotivo.getString("mot_id"),
                        jsonObjectSubMotivo.getString("codigo"),
                        jsonObjectSubMotivo.getString("descri"),
                        jsonObjectSubMotivo.getString("linea"),
                        jsonObjectSubMotivo.getInt("gru_id")
                    )
                    submotivo.save()
                }
            }
        }
    }

    @Throws(JSONException::class)
    private fun saveUserMenu(data: JSONArray) {

        var jsonObject: JSONObject

        var menuAppDB: MenuApp
        MenuApp.deleteAll(MenuApp::class.java)

        for (i in 0 until data.length()) {
            jsonObject = data.getJSONObject(i)

            if (validarNivelMenu(jsonObject.getInt("nivel"))) {
                menuAppDB = MenuApp(
                    Preferences.getInstance().getString("idUsuario", ""),
                    jsonObject.getString("id_menu"),
                    jsonObject.getString("padre"),
                    jsonObject.getString("nivel"),
                    jsonObject.getString("nombre"),
                    jsonObject.getString("menu_class"),
                    jsonObject.getString("orden")
                )
                menuAppDB.save()
            }
        }

    }


    private fun saveToken(token: String) {
        Preferences.getInstance().edit()
            .putString("auth_token", token).apply()
    }
}

private fun validarNivelMenu(nivel: Int): Boolean {
    return nivel > 0
}