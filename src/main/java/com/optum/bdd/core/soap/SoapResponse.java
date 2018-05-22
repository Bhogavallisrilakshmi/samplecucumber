package com.optum.bdd.core.soap;

import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SoapResponse extends ResponseEntity<String> {

    private static final int SUCCESS_STATUS = 200;

    private ResponseEntity<String> responseEntity = null;
    private HttpStatus httpStatus = null;
    private HttpHeaders httpHeaders = null;

    private String responseAsString = null;
    private String responseBody = null;
    private String responseStatus = null;
    private int statusCode = 0;
    private HttpStatus[] multipleStatus = null;
    private boolean isError = false;
    private String reasonPhrase = null;
    private String location = null;

    private String actualResponsebody = null;

    private static Logger log = Logger.getLogger(SoapResponse.class.getName());

    // initialize Response
    public SoapResponse(String sresponseString,
            ResponseEntity<String> sresponseEntity, HttpStatus shttpStatus) {
        super(sresponseString, shttpStatus);
        this.responseAsString = sresponseString;
        this.responseEntity = sresponseEntity;
        this.httpStatus = responseEntity.getStatusCode();
        this.httpHeaders = responseEntity.getHeaders();

    }

    public String getResponse() throws Exception {
        String responseContent = null;
        try {
            responseContent = XMLUtil.decodeToXML(responseAsString);
        } catch (Exception ex) {
            throw new Exception("Exception in getResponse():"
                    + ex.getStackTrace());
        }

        return responseContent;
    }


    public String getResponseBody() throws SoapException {
        String body = null;
        try {
            this.responseBody = responseEntity.getBody();
            body = XMLUtil.decodeToXML(responseBody);

        } catch (Exception ex) {
            throw new SoapException("Exception in getResponseBody():" + ex.getStackTrace());
        }
        return XMLUtil.formatToXml(body);
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
        if (this.httpStatus.value() == SUCCESS_STATUS) {
            URI uri = httpHeaders.getLocation();
            this.location = uri.toString();
        }

        return this.location;
    }


    // To get XPath from Response
    public String getValue(String xPath) throws Exception {
        String rawResponse = responseEntity.getBody();
        String formatedResponse = XMLUtil.decodeToXML(rawResponse);
        String value = null;
        if (xPath.startsWith("//")) {
            value = XMLUtil.getXPathValue(formatedResponse, xPath);
        }
        log.debug("Xpath Value : " + value);
        return value;
    }

}
