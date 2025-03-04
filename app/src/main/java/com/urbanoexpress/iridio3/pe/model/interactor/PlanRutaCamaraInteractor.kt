package com.urbanoexpress.iridio3.pe.model.interactor

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest
import com.urbanoexpress.iridio3.pe.model.response.PlanRutaCamaraResponse
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.ERROR_400
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.ERROR_MSG
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.GENERIC_ERROR
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.RESPONSE_ERROR
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.SUCCESS
import com.urbanoexpress.iridio3.urbanocore.ST.gson
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
class PlanRutaCamaraInteractor(context: Context) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)


    suspend fun getRutaDetail(idRutaQr: String): PlanRutaCamaraResponse =
        suspendCoroutine { continuation ->

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                "https://api.geo.dev-urbano.dev/iridio/api/registro/datosRuta/" + "${idRutaQr}",
                null,
                { response ->

                    val successStatus = response.optBoolean(SUCCESS, false)
                    if (successStatus) {
                        val apiResponseResult = runCatching {
                            Gson().fromJson(response.toString(), PlanRutaCamaraResponse::class.java)
                        }
                        apiResponseResult.onSuccess { successResponse ->
                            continuation.resume(successResponse)
                        }.onFailure { exception ->
                            continuation.resumeWithException(exception)
                        }
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