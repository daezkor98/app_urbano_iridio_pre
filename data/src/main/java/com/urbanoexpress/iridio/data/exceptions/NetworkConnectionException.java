package com.urbanoexpress.iridio.data.exceptions;

import androidx.annotation.Nullable;

import java.io.IOException;

public class NetworkConnectionException extends IOException {

    @Nullable
    @Override
    public String getMessage() {
        return "No connectivity exception";
    }
}
