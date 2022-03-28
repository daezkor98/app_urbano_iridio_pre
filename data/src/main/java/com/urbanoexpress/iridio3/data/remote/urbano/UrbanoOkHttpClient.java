package com.urbanoexpress.iridio3.data.remote.urbano;

import android.content.Context;
import android.os.Build;

import com.urbanoexpress.iridio3.data.remote.ConnectivityInterceptor;
import com.urbanoexpress.iridio3.data.util.network.TLSSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class UrbanoOkHttpClient {

    private Context context;

    public UrbanoOkHttpClient(Context context) {
        this.context = context;
    }

    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                        getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" +
                            Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
                builder.sslSocketFactory(new TLSSocketFactory(), trustManager);
            } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex) {
                ex.printStackTrace();
            }
        }

        //Enable Full Body Logging
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        //Setting Timeout 60 Seconds
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);

        //Interceptor: Full Body Logger and ApiRequest Header
        builder.addInterceptor(logger);
        builder.addInterceptor(new ConnectivityInterceptor(context));

        return builder.build();
    }
}
