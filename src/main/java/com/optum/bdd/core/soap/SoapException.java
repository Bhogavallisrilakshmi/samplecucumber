package com.optum.bdd.core.soap;

@SuppressWarnings("serial")
public class SoapException extends Exception {

    private String message = null;

    public SoapException() {
        super();
    }

    public SoapException(String message) {
        super(message);
        this.message = message;
    }

    public SoapException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public SoapException(Throwable cause) {
        super(cause);
    }

    public SoapException(String message, Throwable cause, boolean enableSuppression,
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
