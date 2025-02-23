package com.urbanoexpress.iridio3.pe.model.interactor

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.ERROR_400
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.ERROR_MSG
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.GENERIC_ERROR
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.RESPONSE_ERROR
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.SUCCESS
import com.urbanoexpress.iridio3.urbanocore.ST.gson
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class DriverVerCodeInteractor(private val context: Context?) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)


    suspend fun loginQResponse(idRuta: String, verificationCode: String): JSONObject =
        suspendCoroutine { continuation ->

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
                ApiRest.getInstance()
                    .getNewApiBaseUrl(context) + ApiRest.Api.LOGIN_QR + idRuta + "/${verificationCode}",
                null,
                { response ->
                    val successStatus = response.optBoolean(SUCCESS, false)

                    if (successStatus) {
                        continuation.resume(response)
                    } else {
                        val msgError = response.optString(RESPONSE_ERROR, GENERIC_ERROR)
                        continuation.resumeWithException(getExceptionMessage(msgError))
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )
            requestQueue.add(jsonObjectRequest)
        }

    private fun getExceptionMessage(message: String): Exception {
        if (message.contains(ERROR_400)) {
            try {
                val msgJsonString = message.split(ERROR_400)[1]
                val msgJsonObject = gson.fromJson(msgJsonString, JsonObject::class.java)
                return Exception(msgJsonObject.get(ERROR_MSG).toString())
            } catch (e: Exception) {
                return Exception(message)
            }

        } else {
            return Exception(message)
        }

    }
}