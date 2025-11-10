package com.urbanoexpress.iridio3.pre.model.interactor

import com.android.volley.VolleyError
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest.ResponseListener
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest.Api.GET_MY_REVENUES
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest.Api.GET_WEEK_DETAIL
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest.Api.UPLOAD_FACTURA_MOTORIZADO
import com.urbanoexpress.iridio3.pre.data.rest.ApiService
import com.urbanoexpress.iridio3.pre.model.dto.GeneralRevenue
import com.urbanoexpress.iridio3.pre.model.dto.ResponseOf
import com.urbanoexpress.iridio3.pre.model.dto.RevenueDay
import com.urbanoexpress.iridio3.pre.model.dto.assertSuccess
import com.urbanoexpress.iridio3.pre.model.dto.toInstance
import com.urbanoexpress.iridio3.pre.model.dto.wasSuccessful
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest.DataPart
import com.urbanoexpress.iridio3.urbanocore.extentions.ifNull
import com.urbanoexpress.iridio3.urbanocore.extentions.ifSafe
import com.urbanoexpress.iridio3.urbanocore.extentions.logException
import com.urbanoexpress.iridio3.urbanocore.secureSuspendCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
//@Keep
class MisGananciasInteractor {

//    suspend fun uploadFacturaPDF(
//        params: Map<String, String>,
//        data: MultipartJsonObjectRequest.DataPart?
//    ): Boolean =
//        secureSuspendCoroutine { continuation ->
//            ApiRequest.getInstance().newParams()
//            ApiRequest.getInstance().putAllParams(params)
//            ApiRequest.getInstance().putData("file", data)
//            ApiRequest.getInstance().request(
//                ApiRest.withEndpoint(UPLOAD_FACTURA_MOTORIZADO),
//                ApiRequest.TypeParams.MULTIPART, object : ResponseListener {
//                    override fun onResponse(response: JSONObject) {
//                        response
//                            .toInstance<ResponseOf<Any>>()
//                            ?.wasSuccessful()
//                        continuation.resume(true)
//                    }
//
//                    override fun onErrorResponse(error: VolleyError) {
//                        error.logException("volley: getMisGanancias")
//                        continuation.resumeWithException(Exception(ApiRequest.errorMessage))
//                    }
//                })
//        }

    suspend fun uploadFacturaPDF(
        params: Array<String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean = secureSuspendCoroutine { continuation ->
        ApiService.getInstance().newParams()

        ApiService.getInstance().putParams("vp_fac_fecha", params[0])
        ApiService.getInstance().putParams("vp_fac_numero", params[1])
        ApiService.getInstance().putParams("vp_fac_sub_tot", params[2])
        ApiService.getInstance().putParams("ext", params[3])
        ApiService.getInstance().putParams("vp_id_user", params[4])
        ApiService.getInstance().putParams("vp_prov_codigo", params[5])
        ApiService.getInstance().putParams("vp_cert_id", params[6])
        ApiService.getInstance().putParams("vp_per_id", params[7])
        ApiService.getInstance().putData("file", data)

        ApiService.getInstance().request(
            ApiRest.getInstance().apiBaseUrl +
                    UPLOAD_FACTURA_MOTORIZADO,
            ApiService.TypeParams.MULTIPART,
            object : ApiService.ResponseListener {
                override fun onResponse(response: JSONObject) {
                    response
                        .toInstance<ResponseOf<Any>>()
                        ?.wasSuccessful()
                    continuation.resume(true)
                }

                override fun onErrorResponse(error: VolleyError) {
                    error.logException("volley: uploadFacturaPDF")
                    continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                }
            })
    }

//    suspend fun getMisGanancias(params: Map<String, String?>) =
//        secureSuspendCoroutine<GeneralRevenue> { continuation ->
//            ApiRequest
//                .getInstance()
//                .putNewParams(params)
//                .requestForm(GET_MY_REVENUES, object : ResponseListener {
//                    override fun onResponse(response: JSONObject) {
//
//                        response
//                            .toInstance<ResponseOf<GeneralRevenue>>()
//                            ?.assertSuccess()
//                            .ifSafe { data ->
//                                continuation.resume(data)
//                            }.ifNull {
//                                continuation.resumeWithException(Exception("Sin datos disponibles"))
//                            }
//                    }
//
//                    override fun onErrorResponse(error: VolleyError) {
//                        error.logException("volley: getMisGanancias")
//                        continuation.resumeWithException(error)
//                    }
//                })
//        }

    suspend fun getMisGanancias(perId : String?) =
        secureSuspendCoroutine<GeneralRevenue> { continuation ->
            ApiService.getInstance().request(
                ApiRest.getInstance().apiBaseUrl +
                        GET_MY_REVENUES + (perId),
                ApiService.TypeParams.FORM_DATA,
                object : ApiService.ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            val generalRevenue = response.toInstance<GeneralRevenue>()

                            if (generalRevenue?.success == true) {
                                continuation.resume(generalRevenue)
                            } else {
                                continuation.resumeWithException(Exception("Sin datos disponibles"))
                            }

                        } catch (e: Exception) {
                            continuation.resumeWithException(Exception("Error parsing: ${e.message}"))
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        error.logException("volley: getMisGanancias")
                        continuation.resumeWithException(error)
                    }
                }
            )
        }

//    suspend fun getSemanaDetail(params: Map<String, String>) =
//        secureSuspendCoroutine<ArrayList<RevenueDay>> { continuation ->
//            ApiRequest.getInstance().putNewParams(params)
//            ApiRequest.getInstance().requestForm(
//                GET_WEEK_DETAIL, object : ResponseListener {
//                    override fun onResponse(response: JSONObject) {
//                        response
//                            .toInstance<ResponseOf<ArrayList<RevenueDay>>>()
//                            ?.assertSuccess()
//                            .ifSafe {
//                                if (it.isEmpty()){
//                                    continuation.resumeWithException(Exception("Sin datos disponibles"))
//                                }else{
//                                    continuation.resume(it)
//                                }
//                            }.ifNull {
//                                continuation.resumeWithException(Exception("Sin datos disponibles"))
//                            }
//                    }
//
//                    override fun onErrorResponse(error: VolleyError) {
//                        continuation.resumeWithException(error)
//                    }
//                })
//        }

    suspend fun getSemanaDetail(idPer: String, fechaInicio: String, fechaFin: String, certID: String) =
        secureSuspendCoroutine<ArrayList<RevenueDay>> { continuation ->
            ApiService.getInstance().newParams()
            ApiService.getInstance().putParams("vp_per_id", idPer)
            ApiService.getInstance().putParams("vp_fecha_inicio", fechaInicio)
            ApiService.getInstance().putParams("vp_fecha_fin", fechaFin)
            ApiService.getInstance().putParams("vp_cert_id", certID)
            ApiService.getInstance().request(
                ApiRest.getInstance().apiBaseUrl +
                        GET_WEEK_DETAIL,
                ApiService.TypeParams.FORM_DATA,
                object : ApiService.ResponseListener {
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

