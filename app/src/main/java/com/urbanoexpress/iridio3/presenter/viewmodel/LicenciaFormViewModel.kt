package com.urbanoexpress.iridio3.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio3.model.interactor.MotorizadoInteractor
import com.urbanoexpress.iridio3.util.Preferences
import com.urbanoexpress.iridio3.util.network.volley.MultipartJsonObjectRequest

/**
 * Created by Brandon Quintanilla on March/24/2022.
 */
class LicenciaFormViewModel : BaseViewModel() {

    private val motorizadoInteractor = MotorizadoInteractor()
    val onRegisterSuccess = MutableLiveData<Boolean>()

    fun postLicence(imageBytes: ByteArray, licenseEmition: String, licenseExp: String) = executeIO {

        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val idUsuario = Preferences.getInstance().getString("idUsuario", "")!!

        val params = mapOf(
            "vp_id_user" to idUsuario,
            "vp_per_id" to idPer,
            "vp_doc_emision" to licenseEmition,
            "vp_doc_caduca" to licenseExp,
            "vp_tdoc_id" to "1",//Tipo licencia = 1
            "vp_und_id" to "0",//Mock for future use
            "hasImage" to "1",
            "vp_doc_estado" to "1"//Enabled
        )

        val imagen = MultipartJsonObjectRequest
            .DataPart(
                "file",
                imageBytes,
                "image/jpg"
            )

        val success = motorizadoInteractor.uploadLicenciaMotorizado(params, imagen)
        onRegisterSuccess.postValue(success)
    }

    fun notifyUserIsNotMotorizado() = executeIO {

        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val idUsuario = Preferences.getInstance().getString("idUsuario", "")!!

        val params = mapOf(
            "vp_id_user" to idUsuario,
            "vp_per_id" to idPer,
            "vp_tdoc_id" to "0",
            "vp_und_id" to "0",
            "hasImage" to "0",
            "vp_doc_estado" to "1"
        )

        val success = motorizadoInteractor.notifyUserIsNotMotorizado(params)
        onRegisterSuccess.postValue(success)
    }
}