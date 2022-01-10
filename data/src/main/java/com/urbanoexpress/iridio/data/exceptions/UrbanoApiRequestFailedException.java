package com.urbanoexpress.iridio.data.exceptions;

public class UrbanoApiRequestFailedException extends RuntimeException {

    private final ErrorType errorType;

    public enum ErrorType {
        GENERIC_REQUEST_ERROR
    }

    public UrbanoApiRequestFailedException(ErrorType errorType) {
        super("");
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public static final class Builder {

        private ErrorType errorType;

        public Builder(ErrorType errorType) {
            this.errorType = errorType;
        }

        public UrbanoApiRequestFailedException build() {
            switch (errorType) {
                case GENERIC_REQUEST_ERROR:
                    return new UrbanoApiRequestFailedException(errorType);
                default:
                    return new UrbanoApiRequestFailedException(null);
            }
        }
    }
}