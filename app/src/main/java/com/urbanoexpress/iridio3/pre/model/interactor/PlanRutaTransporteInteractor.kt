package com.urbanoexpress.iridio3.pre.model.interactor

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest
import com.urbanoexpress.iridio3.pre.model.response.PlanRutaDetallesResponse
import com.urbanoexpress.iridio3.pre.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pre.util.Exception.BaseException
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.EMPTY_VALUE
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.ERROR_CODE_400
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.ERROR_MSG
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.RESPONSE_CODE_DETAIL
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.RESPONSE_DEFAULT_ERROR
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.RESPONSE_MSG_ERROR
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.SUCCESS
import com.urbanoexpress.iridio3.urbanocore.ST.gson
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
class PlanRutaTransporteInteractor(context: Context) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
//    "https://api.geo.dev-urbano.dev/iridio/api/rutas/grabarRuta"


    suspend fun validateRoad(road: PlacaGeoModel): PlanRutaDetallesResponse =
        suspendCoroutine { continuation ->
            val jsonString = gson.toJson(road)
            val jsonRequestBody = JSONObject(jsonString)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                ApiRest.getInstance().apiBaseUrl + ApiRest.Api.VALIDATE_DATOS_RUTA,
                jsonRequestBody,
                { response ->
                    val successStatus = response.optBoolean(SUCCESS, false)
                    if (successStatus) {
                        val apiResponseResult = runCatching {
                            Gson().fromJson(
                                response.toString(),
                                PlanRutaDetallesResponse::class.java
                            )
                        }
                        apiResponseResult.onSuccess { successResponse ->
                            continuation.resume(successResponse)
                        }.onFailure { exception ->
                            continuation.resumeWithException(BaseException(cause = exception))
                        }
                    } else {
                        continuation.resumeWithException(getException(response))
                    }
                },
                { error ->
                    continuation.resumeWithException(BaseException(cause = error))
                }
            )

            requestQueue.add(jsonObjectRequest)
        }

    private fun getException(json: JSONObject): BaseException {
        val errorCode = json.optString(RESPONSE_CODE_DETAIL, EMPTY_VALUE)
        val message = getMessageError(json.optString(RESPONSE_MSG_ERROR, RESPONSE_DEFAULT_ERROR))
        return BaseException(errorCode = errorCode, message = message)
    }

    private fun getMessageError(message: String): String {
        if (message.contains(ERROR_CODE_400)) {
            try {
                val msgJsonString = message.split(ERROR_CODE_400)[1]
                val msgJsonObject = gson.fromJson(msgJsonString, JsonObject::class.java)
                return (msgJsonObject.get(ERROR_MSG).toString())
            } catch (e: Exception) {
                return message
            }

        } else {
            return message
        }
    }
}

