package com.urbanoexpress.iridio3.model.interactor

import com.android.volley.VolleyError
import com.urbanoexpress.iridio3.data.rest.ApiRequest
import com.urbanoexpress.iridio3.data.rest.ApiRequest.ResponseListener
import com.urbanoexpress.iridio3.data.rest.ApiRequest.TypeParams.FORM_DATA
import com.urbanoexpress.iridio3.data.rest.ApiRest
import com.urbanoexpress.iridio3.data.rest.ApiRest.Api.*
import com.urbanoexpress.iridio3.model.dto.*
import com.urbanoexpress.iridio3.urbanocore.ifNull
import com.urbanoexpress.iridio3.urbanocore.ifSafe
import com.urbanoexpress.iridio3.urbanocore.logException
import com.urbanoexpress.iridio3.util.network.volley.MultipartJsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class MisGananciasInteractor {

    suspend fun uploadFacturaPDF(
        params: Map<String, String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean =
        suspendCoroutine { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().putData("file", data)
            ApiRequest.getInstance().request(ApiRest.url(UPLOAD_FACTURA_MOTORIZADO),
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
        suspendCoroutine<GeneralRevenue> { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().request(
                ApiRest.url(GET_MY_REVENUES), FORM_DATA, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {

                        try {
                            response
                                .toInstance<ResponseOf<GeneralRevenue>>()?.wasSuccessful()?.data
                                .ifSafe {
                                    continuation.resume(it)
                                }.ifNull {
                                    continuation.resumeWithException(Exception("No tiene información"))
                                }

                        } catch (e: Exception) {
                            e.logException("catch: getMisGanancias")
                            continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        error.logException("volley: getMisGanancias")
                        continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                    }
                })
        }

    suspend fun getSemanaDetail(params: Map<String, String>) =
        suspendCoroutine<ArrayList<RevenueDay>> { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().request(
                ApiRest.url(GET_WEEK_DETAIL), FORM_DATA, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            response
                                .toInstance<ResponseOf<ArrayList<RevenueDay>>>()?.wasSuccessful()?.data
                                .ifSafe {
                                    continuation.resume(it)
                                }.ifNull {
                                    continuation.resumeWithException(Exception("No tiene información"))
                                }

                        } catch (e: Exception) {
                            e.logException("catch: getSemanaDetail")
                            continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        error.logException("volley: getSemanaDetail")
                        continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                    }
                })
        }
}

