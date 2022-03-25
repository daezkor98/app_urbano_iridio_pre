package com.urbanoexpress.iridio.model.interactor

import com.android.volley.VolleyError
import com.urbanoexpress.iridio.data.rest.ApiRequest
import com.urbanoexpress.iridio.data.rest.ApiRest
import com.urbanoexpress.iridio.util.network.volley.MultipartJsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on March/24/2022.
 */
class MotorizadoInteractor {

    suspend fun uploadLicenciaMotorizado(
        params: Map<String, String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean = suspendCoroutine { continuation ->
            ApiRequest.getInstance().newParams()
            ApiRequest.getInstance().putAllParams(params)
            ApiRequest.getInstance().putData("file", data)
            ApiRequest.getInstance().request(ApiRest.url(ApiRest.Api.UPLOAD_MOTORIZADO_LICENCE),
                ApiRequest.TypeParams.MULTIPART, object : ApiRequest.ResponseListener {
                    override fun onResponse(response: JSONObject) {
                        continuation.resume(true)
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resume(false)
                    }
                })
        }
}