package com.urbanoexpress.iridio.data.remote;

import android.content.Context;

import androidx.annotation.NonNull;

import com.urbanoexpress.iridio.data.exceptions.NetworkConnectionException;
import com.urbanoexpress.iridio.data.util.network.Connectivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {

    private Context context;

    public ConnectivityInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        if (!Connectivity.isConnected(context)) {
            throw new NetworkConnectionException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}