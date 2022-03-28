package com.urbanoexpress.iridio3.data.remote.urbano.services;

import com.urbanoexpress.iridio3.data.entity.VerifyUserSessionEntity;
import com.urbanoexpress.iridio3.data.remote.urbano.UrbanoApiEndPoints;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST(UrbanoApiEndPoints.VERIFY_USER_SESSION)
    Call<VerifyUserSessionEntity> verifyUserSession(@Body RequestBody body);
}
