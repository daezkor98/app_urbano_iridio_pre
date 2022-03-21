package com.urbanoexpress.iridio.model

import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio.util.Preferences
import com.urbanoexpress.iridio.util.network.volley.MultipartJsonObjectRequest


/**
 * Created by Brandon Quintanilla on March/21/2022.
 */
class RegistrarFacturaViewModel : BaseViewModel() {

    fun postFactura(numFact: String, fechaFact: String, montoFact: String, imageBytes: ByteArray?) =
        executeIO {

/*            val map = mapOf(
                "vp_fac_fecha" to "04-03-2022",
                "vp_fac_numero" to "99999",
                "vp_id_user" to "1",
                "vp_prov_codigo" to "143"
            )*/

            val userID = Preferences.getInstance().getString("idUsuario", "")!!
            val codigoProvincia = Preferences.getInstance().getString("codigoProvincia", "")!!

            val map = mapOf(
                "vp_fac_fecha" to fechaFact,
                "vp_fac_numero" to numFact,
                "vp_id_user" to userID,
                "vp_prov_codigo" to codigoProvincia
            )

            val imagen = MultipartJsonObjectRequest.DataPart("file", imageBytes, "image/png")

            MisGananciasInteractor.uploadFacturaPDF(map, imagen)
        }
}