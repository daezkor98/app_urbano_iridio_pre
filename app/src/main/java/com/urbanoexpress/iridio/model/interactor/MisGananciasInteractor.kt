package com.urbanoexpress.iridio.model.interactor

import android.util.Log
import com.android.volley.VolleyError
import com.urbanoexpress.iridio.data.rest.ApiRequest
import com.urbanoexpress.iridio.data.rest.ApiRequest.ResponseListener
import com.urbanoexpress.iridio.data.rest.ApiRequest.TypeParams.FORM_DATA
import com.urbanoexpress.iridio.data.rest.ApiRest
import com.urbanoexpress.iridio.data.rest.ApiRest.Api.GET_MY_REVENUES
import com.urbanoexpress.iridio.data.rest.ApiRest.Api.GET_WEEK_DETAIL
import com.urbanoexpress.iridio.model.dto.GeneralRevenue
import com.urbanoexpress.iridio.model.dto.ResponseOf
import com.urbanoexpress.iridio.model.dto.RevenueDay
import com.urbanoexpress.iridio.model.dto.toInstance
import com.urbanoexpress.iridio.urbanocore.ifNull
import com.urbanoexpress.iridio.urbanocore.ifSafe
import com.urbanoexpress.iridio.util.network.volley.MultipartJsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
object MisGananciasInteractor {

    suspend fun uploadFacturaPDF(
        params: Map<String, String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean =
        suspendCoroutine<Boolean> { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().putData("file", data)
            ApiRequest.getInstance().request(ApiRest.Api.UPLOAD_FACTURA_MOTORIZADO,
                ApiRequest.TypeParams.MULTIPART, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        continuation.resume(true)
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resume(false)
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

                            val instance = response.toInstance<ResponseOf<GeneralRevenue>>()

                            instance?.data.ifSafe {
                                continuation.resume(it)
                            }.ifNull {
                                continuation.resumeWithException(Exception("No hay data"))//TODO
                            }

                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resumeWithException(error)
                    }
                })
        }

    suspend fun getSemanaDetail(params: Map<String, String>) =
        suspendCoroutine<List<RevenueDay>> { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().request(
                ApiRest.url(GET_WEEK_DETAIL), FORM_DATA, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            val instance = response.toInstance<ResponseOf<List<RevenueDay>>>()
                            instance?.data.ifSafe {
                                continuation.resume(it)
                            }.ifNull {
                                continuation.resumeWithException(Exception("No hay data"))//TODO
                            }

                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resumeWithException(error)
                    }
                })
        }
}

