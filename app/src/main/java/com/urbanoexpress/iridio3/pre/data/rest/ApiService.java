package com.urbanoexpress.iridio3.pre.data.rest;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONObject;

import okhttp3.ConnectionPool;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Servicio API usando Retrofit
 */
public class ApiService {

    private static final String TAG = ApiService.class.getSimpleName();
    private static final String BASE_URL = "https://bkd-tms.urbanoexpress.com.pe/iridio/";
    //private static final String BASE_URL = "https://api.geo.dev-urbano.dev/iridio/";

    private static ApiService apiService;
    private RetrofitApiInterface apiInterface;
    private RetrofitApiInterface apiInterfaceLongTimeout;

    public interface TypeParams {
        int FORM_DATA = 0;
        int MULTIPART = 1;
    }

    public interface RetrofitApiInterface {
        @HTTP(method = "POST", hasBody = true)
        Call<ResponseBody> requestFormData(
                @Url String url,
                @Body Map<String, String> params
        );

        @HTTP(method = "POST", hasBody = true)
        Call<ResponseBody> requestFormDataJson(
                @Url String url,
                @Body Object jsonObject
        );

        @Multipart
        @HTTP(method = "POST", hasBody = true)
        Call<ResponseBody> requestMultipart(
                @Url String url,
                @PartMap Map<String, RequestBody> params,
                @Part MultipartBody.Part filePart
        );
    }

    private ApiService() {
        initRetrofit();
    }

    public static synchronized ApiService getInstance() {
        if (apiService == null) {
            apiService = new ApiService();
        }
        return apiService;
    }

    private void initRetrofit() {
        OkHttpClient normalClient = buildNormalClient();

        OkHttpClient longTimeoutClient = buildLongTimeoutClient();

        // Retrofit normal
        Retrofit retrofitNormal = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(normalClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Retrofit con timeout largo
        Retrofit retrofitLongTimeout = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(longTimeoutClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofitNormal.create(RetrofitApiInterface.class);
        apiInterfaceLongTimeout = retrofitLongTimeout.create(RetrofitApiInterface.class);
    }

    private OkHttpClient buildNormalClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .pingInterval(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder();

                    String bearerToken = Preferences.getInstance().getString("auth_token", "");
                    if (!bearerToken.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + bearerToken);
                    }

                    String contentType = original.header("Content-Type");
                    if (contentType == null) {
                        requestBuilder.header("Content-Type", "application/json; charset=UTF-8");
                    }

                    okhttp3.Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();
    }

    private OkHttpClient buildLongTimeoutClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(3, 60, TimeUnit.SECONDS))
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder();

                    String bearerToken = Preferences.getInstance().getString("auth_token", "");
                    if (!bearerToken.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + bearerToken);
                    }

                    String contentType = original.header("Content-Type");
                    if (contentType == null) {
                        requestBuilder.header("Content-Type", "application/json; charset=UTF-8");
                    }

                    okhttp3.Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();
    }

    public void requestForm(String endpoint, final ResponseListener responseListener) {
        request(ApiRest.withEndpoint(endpoint), TypeParams.FORM_DATA, responseListener);
    }

    public void request(String url, int typeParams, final ResponseListener responseListener) {
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "PARAMS: " + requestParams.toString());

        switch (typeParams) {
            case TypeParams.FORM_DATA:
                requestFormData(url, responseListener);
                break;
            case TypeParams.MULTIPART:
                requestMultiPart(url, responseListener);
                break;
        }
    }

    private void requestFormData(String url, final ResponseListener responseListener) {
        Call<ResponseBody> call = apiInterface.requestFormData(url, requestParams);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                        } finally {
                            response.body().close();
                        }
                        //String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        responseListener.onResponse(jsonResponse);
                    } else {
                        String errorBodyContent = null;
                        try {
                            errorBodyContent = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error";
                        } finally {
                            if (response.errorBody() != null) {
                                response.errorBody().close();
                            }
                        }
                        VolleyError error = createVolleyError(response);
                        responseListener.onErrorResponse(error);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    VolleyError error = new VolleyError("Error processing response: " + e.getMessage());
                    responseListener.onErrorResponse(error);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                VolleyError error = new VolleyError(t.getMessage(), t);
                responseListener.onErrorResponse(error);
            }
        });
    }

    private void requestMultiPart(String url, final ResponseListener responseListener) {
        OkHttpClient longTimeoutClient = new OkHttpClient.Builder()
                .connectTimeout(420, TimeUnit.SECONDS)
                .readTimeout(420, TimeUnit.SECONDS)
                .writeTimeout(420, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(3, 60, TimeUnit.SECONDS))
                .build();

        Retrofit retrofitWithLongTimeout = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(longTimeoutClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApiInterface apiInterfaceWithLongTimeout = retrofitWithLongTimeout.create(RetrofitApiInterface.class);

        // Convertir parámetros normales a RequestBody
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            RequestBody body = RequestBody.create(okhttp3.MultipartBody.FORM, entry.getValue());
            params.put(entry.getKey(), body);
        }

        // Convertir DataPart a MultipartBody.Part
        MultipartBody.Part filePart = null;
        if (!requestParamsData.isEmpty()) {
            // Asumimos que solo hay un archivo por request para simplificar
            Map.Entry<String, MultipartJsonObjectRequest.DataPart> entry =
                    requestParamsData.entrySet().iterator().next();
            MultipartJsonObjectRequest.DataPart dataPart = entry.getValue();

            RequestBody fileRequestBody = RequestBody.create(
                    okhttp3.MediaType.parse(dataPart.getType()),
                    dataPart.getContent()
            );
            filePart = MultipartBody.Part.createFormData(
                    entry.getKey(),
                    dataPart.getFileName(),
                    fileRequestBody
            );
        }

        Call<ResponseBody> call = apiInterfaceWithLongTimeout.requestMultipart(url, params, filePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                        } finally {
                            response.body().close();
                        }
                        //String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        responseListener.onResponse(jsonResponse);
                    } else {
                        String errorBodyContent = null;
                        try {
                            errorBodyContent = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error";
                        } finally {
                            if (response.errorBody() != null) {
                                response.errorBody().close();
                            }
                        }
                        VolleyError error = createVolleyError(response);
                        responseListener.onErrorResponse(error);
                    }
                } catch (Exception e) {
                    VolleyError error = new VolleyError("Error processing response: " + e.getMessage());
                    responseListener.onErrorResponse(error);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                VolleyError error = new VolleyError(t.getMessage(), t);
                responseListener.onErrorResponse(error);
            }
        });
    }

    public void requestWithLongTimeout(String url, int typeParams, final ResponseListener responseListener) {
        Log.d(TAG, "URL (LongTimeout): " + url);
        Log.d(TAG, "PARAMS: " + requestParams.toString());

        switch (typeParams) {
            case TypeParams.FORM_DATA:
                requestFormDataWithLongTimeout(url, responseListener);
                break;
            case TypeParams.MULTIPART:
                requestMultiPartWithLongTimeout(url, responseListener);
                break;
        }
    }

    private void requestFormDataWithLongTimeout(String url, final ResponseListener responseListener) {
        Call<ResponseBody> call = apiInterfaceLongTimeout.requestFormData(url, requestParams);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                        } finally {
                            response.body().close();
                        }
                        JSONObject jsonResponse = new JSONObject(responseString);
                        responseListener.onResponse(jsonResponse);
                    } else {
                        VolleyError error = createVolleyError(response);
                        responseListener.onErrorResponse(error);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    VolleyError error = new VolleyError("Error processing response: " + e.getMessage());
                    responseListener.onErrorResponse(error);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                VolleyError error = new VolleyError(t.getMessage(), t);
                responseListener.onErrorResponse(error);
            }
        });
    }

    private void requestMultiPartWithLongTimeout(String url, final ResponseListener responseListener) {
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            RequestBody body = RequestBody.create(okhttp3.MultipartBody.FORM, entry.getValue());
            params.put(entry.getKey(), body);
        }

        MultipartBody.Part filePart = null;
        if (!requestParamsData.isEmpty()) {
            Map.Entry<String, MultipartJsonObjectRequest.DataPart> entry =
                    requestParamsData.entrySet().iterator().next();
            MultipartJsonObjectRequest.DataPart dataPart = entry.getValue();

            RequestBody fileRequestBody = RequestBody.create(
                    okhttp3.MediaType.parse(dataPart.getType()),
                    dataPart.getContent()
            );
            filePart = MultipartBody.Part.createFormData(
                    entry.getKey(),
                    dataPart.getFileName(),
                    fileRequestBody
            );
        }

        Call<ResponseBody> call = apiInterfaceLongTimeout.requestMultipart(url, params, filePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                        } finally {
                            response.body().close();
                        }
                        JSONObject jsonResponse = new JSONObject(responseString);
                        responseListener.onResponse(jsonResponse);
                    } else {
                        VolleyError error = createVolleyError(response);
                        responseListener.onErrorResponse(error);
                    }
                } catch (Exception e) {
                    VolleyError error = new VolleyError("Error processing response: " + e.getMessage());
                    responseListener.onErrorResponse(error);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                VolleyError error = new VolleyError(t.getMessage(), t);
                responseListener.onErrorResponse(error);
            }
        });
    }

    private VolleyError createVolleyError(Response<ResponseBody> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";

            // Crear un VolleyError con el código de estado y mensaje
            return new VolleyError("HTTP " + response.code() + ": " + errorBody) {
                @Override
                public long getNetworkTimeMs() {
                    return 0;
                }
            };
        } catch (Exception e) {
            return new VolleyError("HTTP " + response.code() + ": Error parsing error response");
        }
    }

    private Map<String, String> requestParams = new HashMap<>();
    private Map<String, MultipartJsonObjectRequest.DataPart> requestParamsData = new HashMap<>();

    public void newParams() {
        requestParams = new HashMap<>();
        requestParamsData = new HashMap<>();
    }

    @Deprecated
    public void putAllParams(Map<String, String> params) {
        requestParams.putAll(params);
    }

    public ApiService putNewParams(Map<String, String> params) {
        newParams();
        requestParams.putAll(params);
        return this;
    }

    public void putParams(String key, String value) {
        requestParams.put(key, value);
    }

    public void putData(String key, MultipartJsonObjectRequest.DataPart data) {
        requestParamsData.put(key, data);
    }

    public interface ResponseListener {
        void onResponse(org.json.JSONObject response);
        void onErrorResponse(VolleyError error);
    }
}