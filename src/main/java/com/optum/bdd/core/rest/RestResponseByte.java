package com.optum.bdd.core.rest;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class RestResponseByte extends ResponseEntity<byte[]> {

    private static final int SUCCESS_STATUS = 200;
    private static final int CREATED_STATUS = 201;

    private ResponseEntity<byte[]> responseEntity = null;
    private HttpStatus httpStatus = null;
    private HttpHeaders httpHeaders = null;

    private String responseStatus = null;
    private int statusCode = 0;
    private HttpStatus[] multipleStatus = null;
    private boolean isError = false;
    private String reasonPhrase = null;
    // private String location = null;
    private byte[] responseBody = null;

    // initialize Response
    public RestResponseByte(byte[] sresponseByte, ResponseEntity<byte[]> sresponseEntity,
            HttpStatus shttpStatus) {
        super(sresponseByte, shttpStatus);
        this.responseEntity = sresponseEntity;
        this.httpStatus = responseEntity.getStatusCode();
        this.httpHeaders = responseEntity.getHeaders();

    }

    public byte[] getResponseBody() throws RestException {

        try {
            this.responseBody = responseEntity.getBody();

        } catch (Exception ex) {
            throw new RestException("Exception in getResponseBody():" + ex.getMessage());
        }
        return this.responseBody;
    }

    public String getStatus() {
        this.responseStatus = this.httpStatus.toString();
        return responseStatus;
    }

    public int getstatusCode() {
        this.statusCode = this.httpStatus.value();
        return statusCode;
    }

    public HttpStatus[] getMultipleStatus() {
        this.multipleStatus = HttpStatus.values();
        return multipleStatus;
    }

    public boolean isError() {
        if (httpStatus.value() != SUCCESS_STATUS) {
            this.isError = true;
        }
        return this.isError;
    }

    public String getErrorMessage() {
        if (this.httpStatus.value() != SUCCESS_STATUS) {
            this.reasonPhrase = httpStatus.getReasonPhrase();
        }
        return this.reasonPhrase;
    }

    public String getLocation() {
        URI uri = null;
        String location = null;
        try {
            if (this.httpStatus.value() == SUCCESS_STATUS
                    || this.httpStatus.value() == CREATED_STATUS) {
                uri = httpHeaders.getLocation();
                location = uri.toString();
            }
        } catch (Exception ex) {
            // throw new RestException("RestException : "+ex.getMessage());
            location = null;
        }
        return location;
    }

}
