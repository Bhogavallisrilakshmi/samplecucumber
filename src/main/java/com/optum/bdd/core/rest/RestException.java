package com.optum.bdd.core.rest;

@SuppressWarnings("serial")
public class RestException extends Exception {

    private String message = null;

    public RestException() {
        super();
    }

    public RestException(String message) {
        super(message);
        this.message = message;
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
