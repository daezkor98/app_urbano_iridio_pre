package com.urbanoexpress.iridio.model.interactor

import com.android.volley.VolleyError
import com.google.gson.reflect.TypeToken
import com.urbanoexpress.iridio.data.rest.ApiRequest
import com.urbanoexpress.iridio.data.rest.ApiRequest.ResponseListener
import com.urbanoexpress.iridio.data.rest.ApiRequest.TypeParams.FORM_DATA
import com.urbanoexpress.iridio.data.rest.ApiRest
import com.urbanoexpress.iridio.data.rest.ApiRest.Api.GET_MY_REVENUES
import com.urbanoexpress.iridio.data.rest.ApiRest.Api.GET_WEEK_DETAIL
import com.urbanoexpress.iridio.model.dto.GeneralRevenue
import com.urbanoexpress.iridio.model.dto.ResponseOf
import com.urbanoexpress.iridio.model.dto.WeekRevenue
import com.urbanoexpress.iridio.urbanocore.ST
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
object MisGananciasInteractor {

    suspend fun getMisGanancias(params: HashMap<String, Any>) =
        suspendCoroutine<ResponseOf<GeneralRevenue>> { continuation ->
            ApiRequest.getInstance().newParams()
//        ApiRequest.getInstance().putParams("username", params[0])
            ApiRequest.getInstance().request(
                ApiRest.url(GET_MY_REVENUES), FORM_DATA, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {

                        try {
                            /*Gson*/

                            val empMapType: Type =
                                object : TypeToken<ResponseOf<GeneralRevenue>>() {}.type
                            val instance: ResponseOf<GeneralRevenue> =
                                ST.gson.fromJson(response.toString(), empMapType)

/*                            val respoaanse = response
                                .transformTo<ResponseOf<GeneralRevenue>>()*/

                            continuation.resume(instance)

                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        continuation.resumeWithException(error)
                    }
                })
        }

    suspend fun getSemanaDetail(param: HashMap<String, Any>) =
        suspendCoroutine<ResponseOf<WeekRevenue>> { continuation ->
            ApiRequest.getInstance().newParams()
//        ApiRequest.getInstance().putParams("username", params[0])
            ApiRequest.getInstance().request(
                ApiRest.url(GET_WEEK_DETAIL), FORM_DATA, object : ResponseListener {
                    override fun onResponse(response: JSONObject) {

                        try {
                            /*Gson*/
                            val empMapType: Type = object : TypeToken<ResponseOf<WeekRevenue>>() {}.type
                            val instance: ResponseOf<WeekRevenue> = ST.gson.fromJson(response.toString(), empMapType)

/*                            val respoaanse = response
                                .transformTo<ResponseOf<GeneralRevenue>>()*/

                            continuation.resume(instance)

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