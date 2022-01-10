package com.urbanoexpress.iridio.data.remote;

import com.urbanoexpress.iridio.data.exceptions.UrbanoApiRequestFailedException;

public class UrbanoApiResponseValidator {

    public static <T> T validate(boolean isSuccess, T response) {
        if (!isSuccess) throw new UrbanoApiRequestFailedException
                .Builder(UrbanoApiRequestFailedException.ErrorType.GENERIC_REQUEST_ERROR).build();
        return response;
    }
}