package com.urbanoexpress.iridio3.pe.model.interactor

import com.android.volley.VolleyError
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest.ResponseListener
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest.Api.*
import com.urbanoexpress.iridio3.pe.model.dto.GeneralRevenue
import com.urbanoexpress.iridio3.pe.model.dto.ResponseOf
import com.urbanoexpress.iridio3.pe.model.dto.RevenueDay
import com.urbanoexpress.iridio3.pe.model.dto.assertSuccess
import com.urbanoexpress.iridio3.pe.model.dto.toInstance
import com.urbanoexpress.iridio3.pe.model.dto.wasSuccessful
import com.urbanoexpress.iridio3.urbanocore.extentions.ifNull
import com.urbanoexpress.iridio3.urbanocore.extentions.ifSafe
import com.urbanoexpress.iridio3.urbanocore.extentions.logException
import com.urbanoexpress.iridio3.urbanocore.secureSuspendCoroutine
import com.urbanoexpress.iridio3.pe.util.network.volley.MultipartJsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
//@Keep
class MisGananciasInteractor {

    suspend fun uploadFacturaPDF(
        params: Map<String, String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean =
        secureSuspendCoroutine { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().putData("file", data)
            ApiRequest.getInstance().request(
                ApiRest.withEndpoint(UPLOAD_FACTURA_MOTORIZADO),
                ApiRequest.TypeParams.MULTIPART, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        response
                            .toInstance<ResponseOf<Any>>()
                            ?.wasSuccessful()
                        continuation.resume(true)
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        error.logException("volley: getMisGanancias")
                        continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                    }
                })
        }

    suspend fun getMisGanancias(params: Map<String, String?>) =
        secureSuspendCoroutine<GeneralRevenue> { continuation ->
            ApiRequest
                .getInstance()
                .putNewParams(params)
                .requestForm(GET_MY_REVENUES, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {

                        response
                            .toInstance<ResponseOf<GeneralRevenue>>()
                            ?.assertSuccess()
                            .ifSafe { data ->
                                continuation.resume(data)
                            }.ifNull {
                                continuation.resumeWithException(Exception("Sin datos disponibles"))
                            }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        error.logException("volley: getMisGanancias")
                        continuation.resumeWithException(error)
                    }
                })
        }

    suspend fun getSemanaDetail(params: Map<String, String>) =
        secureSuspendCoroutine<ArrayList<RevenueDay>> { continuation ->
            ApiRequest.getInstance().putNewParams(params)
            ApiRequest.getInstance().requestForm(
                GET_WEEK_DETAIL, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        response
                            .toInstance<ResponseOf<ArrayList<RevenueDay>>>()
                            ?.assertSuccess()
                            .ifSafe {
                                if (it.isEmpty()){
                                    continuation.resumeWithException(Exception("Sin datos disponibles"))
                                }else{
                                    continuation.resume(it)
                                }
                            }.ifNull {
                                continuation.resumeWithException(Exception("Sin datos disponibles"))
                            }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resumeWithException(error)
                    }
                })
        }
}

