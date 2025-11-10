package com.urbanoexpress.iridio3.pre.model.interactor

import com.android.volley.VolleyError
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest
import com.urbanoexpress.iridio3.pre.model.dto.ResponseOf
import com.urbanoexpress.iridio3.pre.model.dto.toInstance
import com.urbanoexpress.iridio3.pre.model.dto.validate
import com.urbanoexpress.iridio3.pre.model.dto.wasSuccessful
import com.urbanoexpress.iridio3.urbanocore.secureSuspendCoroutine
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Created by Brandon Quintanilla on March/24/2022.
 */
class MotorizadoInteractor {

    suspend fun uploadLicenciaMotorizado(
        params: Map<String, String>,
        data: MultipartJsonObjectRequest.DataPart?
    ): Boolean = secureSuspendCoroutine { continuation ->
        ApiRequest.getInstance().newParams()
        ApiRequest.getInstance().putAllParams(params)
        ApiRequest.getInstance().putData("file", data)
        ApiRequest.getInstance().request(
            ApiRest.withEndpoint(
                ApiRest.Api.UPLOAD_MOTORIZADO_LICENCE),
            ApiRequest.TypeParams.MULTIPART, object : ApiRequest.ResponseListener {
                override fun onResponse(response: JSONObject) {
                    response
                        .toInstance<ResponseOf<Any>>()
                        ?.wasSuccessful()
                    continuation.resume(true)
                }

                override fun onErrorResponse(error: VolleyError) {
                    continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                }
            })
    }

    suspend fun notifyUserIsNotMotorizado(
        params: Map<String, String>
    ): Boolean = secureSuspendCoroutine { continuation ->
        ApiRequest.getInstance().newParams()
        ApiRequest.getInstance().putAllParams(params)
        ApiRequest.getInstance().request(
            ApiRest.withEndpoint(
                ApiRest.Api.UPLOAD_MOTORIZADO_LICENCE),
            ApiRequest.TypeParams.FORM_DATA, object : ApiRequest.ResponseListener {
                override fun onResponse(response: JSONObject) {

                    response
                        .toInstance<ResponseOf<Any>>()
                        ?.validate(ifValid = { _ ->
                            continuation.resume(true)
                        }, ifNotValid = { t ->
                            continuation.resumeWithException(t)
                        })
                }

                override fun onErrorResponse(error: VolleyError) {
                    continuation.resumeWithException(Exception(ApiRequest.errorMessage))
                }
            })
    }
}