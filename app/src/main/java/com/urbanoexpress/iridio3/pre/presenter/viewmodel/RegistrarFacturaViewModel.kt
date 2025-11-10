package com.urbanoexpress.iridio3.pre.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio3.pre.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio3.pre.util.Preferences
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Brandon Quintanilla on March/21/2022.
 */
@HiltViewModel
class RegistrarFacturaViewModel @Inject constructor() : BaseViewModel() {

    val uploadFacturaResultLD: MutableLiveData<Boolean> = MutableLiveData()

    val gananciasInteractor by lazy { MisGananciasInteractor() }

    fun postFactura(
        numFact: String,
        fechaFact: String,
        montoFact: String,
        certID: String,
        imageBytes: ByteArray?
    ) = executeIO {

        val userID = Preferences.getInstance().getString("idUsuario", "")!!
        val codigoProvincia = Preferences.getInstance().getString("codigoProvincia", "")!!
        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val fechaFactFormat = fechaFact.replace("/", "-")

        val map = mapOf(
            "vp_fac_fecha" to fechaFact,
            "vp_fac_numero" to numFact,
            "vp_fac_sub_tot" to montoFact,
            "ext" to "pdf",
            "vp_id_user" to userID,
            "vp_prov_codigo" to codigoProvincia,
            "vp_cert_id" to certID,
            "vp_per_id" to idPer
        )

        val params = arrayOf(
            fechaFactFormat,
            numFact,
            montoFact,
            "pdf",
            userID,
            codigoProvincia,
            certID,
            idPer
        )

        val imagen =
            MultipartJsonObjectRequest.DataPart(
                "Factura",
                imageBytes,
                "application/pdf"
            )

        val isSuccess = gananciasInteractor.uploadFacturaPDF(params, imagen)
        uploadFacturaResultLD.postValue(isSuccess)
    }
}