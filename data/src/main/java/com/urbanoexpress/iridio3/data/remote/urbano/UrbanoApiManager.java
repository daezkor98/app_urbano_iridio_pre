package com.urbanoexpress.iridio3.data.remote.urbano;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.urbanoexpress.iridio3.data.remote.BaseUrl;
import com.urbanoexpress.iridio3.data.remote.urbano.services.UserService;
import com.urbanoexpress.iridio3.data.util.gson.DoubleGsonTypeAdapter;
import com.urbanoexpress.iridio3.data.util.gson.IntegerGsonTypeAdapter;
import com.urbanoexpress.iridio3.data.util.gson.LongGsonTypeAdapter;
import com.urbanoexpress.iridio3.data.util.gson.StringGsonTypeAdapter;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UrbanoApiManager {

    private static String apiBaseUrl;
    private static Retrofit retrofit;
    private static UserService userApi;

    public UrbanoApiManager(Context context) {
        createService(context);
    }

    private static void init() {
        userApi = createApi(UserService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    private static void createService(Context context) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Double.class, new DoubleGsonTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntegerGsonTypeAdapter())
                .registerTypeAdapter(Long.class, new LongGsonTypeAdapter())
                .registerTypeAdapter(String.class, new StringGsonTypeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(new UrbanoOkHttpClient(context).getOkHttpClient())
                .build();
        init();
    }

    public static void setApiBaseUrl(BaseUrl url) {
        if (url != null) {
            apiBaseUrl = url.getBaseUrl();
        }
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public UserService getUserApi() {
        return userApi;
    }
}
