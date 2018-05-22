package com.optum.bdd.core.rest;

import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.JsonPath;

/**
 * @author gpanda
 *
 */
public class RestResponse extends ResponseEntity<String> {

    private static final int SUCCESS_STATUS = 200;
    private static final int CREATED_STATUS = 201;

    private ResponseEntity<String> responseEntity = null;
    private HttpStatus httpStatus = null;
    private HttpHeaders httpHeaders = null;

    private String responseStatus = null;
    private int statusCode = 0;
    private HttpStatus[] multipleStatus = null;
    private boolean isError = false;
    private String reasonPhrase = null;
    // private String location = null;
    private String responseBody = null;

    private static Logger log = Logger.getLogger(RestResponse.class.getName());

    // initialize Response
    public RestResponse(String sresponseString, ResponseEntity<String> sresponseEntity,
            HttpStatus shttpStatus) {
        super(sresponseString, shttpStatus);
        this.responseEntity = sresponseEntity;
        this.httpStatus = responseEntity.getStatusCode();
        this.httpHeaders = responseEntity.getHeaders();

    }

    public String getResponseBody() throws RestException {

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

    /**
     * Get JSON Value from JSON response
     * 
     * @param jsonPath
     * @return
     * @throws RestException
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/
     *      jayway/JsonPath</a>
     * 
     */
    public String getJSONValue(String jsonPath) throws RestException {

        String restResponse = responseEntity.getBody();
        String jsonValue = null;
        try {
            log.debug("JSON PATH : " + jsonPath);
            if (jsonPath.startsWith("$")) {
                jsonValue = JsonPath.read(restResponse, jsonPath).toString();
                log.debug("JSON VALUE : " + jsonValue);
            } else {
                log.error("Invalid in JSON Path :" + jsonPath);
            }

        } catch (Exception ex) {
            throw new RestException("Error getting JSON value : " + ex.getMessage());
        }

        return jsonValue;

    }


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
