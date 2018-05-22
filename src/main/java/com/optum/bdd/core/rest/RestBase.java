package com.optum.bdd.core.rest;

import java.net.URI;
import java.util.Random;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


public class RestBase {


    protected String restUsername = null;
    protected String restPassword = null;
    protected String restUrl = null;
    protected String contentType = null;

    protected RestTemplate restTemplate = null;
    protected HttpHeaders httpHeader = null;

    protected HttpEntity<String> httpEntity = null;
    protected ResponseEntity<String> responseEntity = null;
    protected RestResponse restResponse = null;
    protected RestResponseByte restResponseByte = null;
    protected HttpStatus httpStatus = null;

    protected String userRequest = null;
    protected String responseString = null;
    protected byte[] responseBody = null;

    protected HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = null;
    protected LinkedMultiValueMap<String, Object> inputMap = null;

    private static Logger log = Logger.getLogger(RestBase.class.getName());


    protected RestBase() {

    }


    /**
     * Initialize RESTUtil
     * 
     * @param resturl
     *        - Base URL of rest : http://server:port/
     * @param username
     *        - User Name for REST
     * @param password
     *        - Password for REST
     * @param contentType
     *        - ContentType for REST
     */
    protected RestBase(String resturl, String username, String password, String contentType) {

        this(resturl, contentType);

        this.restUsername = username;
        this.restPassword = password;

        // Adding authenticate credentials to headers
        if ((this.restUsername != null && this.restPassword != null)
                || (!this.restUsername.isEmpty() && !this.restPassword.isEmpty())) {

            log.debug("Using Basic Authentication....");
            this.httpHeader.add("Authorization", "Basic " + RestRequestHandler
                    .getBase64Credentials(this.restUsername.trim(), this.restPassword.trim()));
        }



    }



    /**
     * Initialize RESTUtil
     * 
     * @param resturl - Base URL of rest : http://server:port/
     * @param contentType - ContentType for REST
     */
    protected RestBase(String resturl, String contentType) {

        this.restUrl = resturl;
        this.contentType = contentType;
        this.restTemplate = new RestTemplate();
        this.httpHeader = new HttpHeaders();

        // Set content-type
        RestRequestHandler.setContentType(this.httpHeader, this.contentType);

        // IMP-To Handle Server Error & Fault message
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });

    }

    public void setRestUser(String restUsername, String restPassword) {

        this.restUsername = restUsername;
        this.restPassword = restPassword;

        if ((this.restUsername != null && this.restPassword != null)
                || (!this.restUsername.isEmpty() && !this.restPassword.isEmpty())) {

            this.httpHeader.clear();
            log.debug("Setting Basic Authentication and ContentType");
            this.httpHeader = new HttpHeaders();

            this.httpHeader.add("Authorization", "Basic " + RestRequestHandler
                    .getBase64Credentials(this.restUsername.trim(), this.restPassword.trim()));
            // Set content-type
            RestRequestHandler.setContentType(this.httpHeader, this.contentType);
        }
    }

    public void setContentType(String contentType) {

        this.contentType = contentType;

        if ((this.restUsername != null && this.restPassword != null)
                || (!this.restUsername.isEmpty() && !this.restPassword.isEmpty())) {

            this.httpHeader.clear();
            log.debug("Setting Basic Authentication and ContentType");
            this.httpHeader = new HttpHeaders();

            this.httpHeader.add("Authorization", "Basic " + RestRequestHandler
                    .getBase64Credentials(this.restUsername.trim(), this.restPassword.trim()));
            // Set content-type
            RestRequestHandler.setContentType(this.httpHeader, this.contentType);

        }
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestUsername() {
        return restUsername;
    }


    public String getRestPassword() {
        return restPassword;
    }


    public String getRestUrl() {
        return restUrl;
    }

    public String getContentType() {
        return contentType;
    }


    /**
     * This method performs generalized REST POST/PUT operation.
     * 
     * @param httMethod
     *        - HttpMethod PUT or POST
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param Inputdata
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     */
    protected RestResponse execute(HttpMethod httpMethod, String context, String requestPath,
            String[][] Inputdata) throws RestException {

        try {
            if (context != null && Inputdata != null) {
                userRequest = RestRequestHandler.getRestRequest(requestPath, Inputdata);
            } else {
                userRequest = RestRequestHandler.getUserRequest(requestPath);
            }

            log.debug(
                    "********************** USER-INPUT *******************************************");
            log.debug("Context :" + context);
            log.debug("Request Path :" + requestPath);
            log.debug(
                    "******************************************************************************");

            log.debug(
                    "********************** REST-REQUEST ******************************************");
            log.debug(userRequest);
            log.debug(
                    "******************************************************************************");

            this.httpEntity = new HttpEntity<>(userRequest, this.httpHeader);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, httpMethod,
                    httpEntity, String.class);

            if (responseEntity != null) {
                // Get Response as String
                responseString = responseEntity.toString();

                log.debug(
                        "********************** REST-RESPONSE ******************************************");
                log.debug(responseString);
                log.debug(
                        "******************************************************************************");

                // Get status code
                this.httpStatus = responseEntity.getStatusCode();
                log.debug(
                        "********************** REST-RESPONSE-STATUSCODE *******************************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug(
                        "******************************************************************************");

                // Set RestResponse
                restResponse =
                        new RestResponse(responseString, this.responseEntity, this.httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }



    /**
     * Execute HTTP Method based on context
     * 
     * @param context
     * @return
     * @throws RestException
     */
    protected RestResponse execute(HttpMethod httpMethod, String context) throws RestException {

        try {
            log.debug(
                    "********************** USER-INPUT *******************************************");
            log.debug("Context :" + context);
            log.debug(
                    "******************************************************************************");

            this.httpEntity = new HttpEntity<>("parameters", httpHeader);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, httpMethod,
                    this.httpEntity, String.class);

            if (this.responseEntity != null) {

                responseString = responseEntity.toString();

                log.debug("********************** REST-RESPONSE **********************");
                log.debug(responseString);
                log.debug("**********************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("*********************************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }
        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }


        return restResponse;
    }



    /**
     * Execute HTTP Method based on Context and RequestPath
     * 
     * @param context
     * @param requestPath
     * @return
     * @throws RestException
     */
    public RestResponse execute(HttpMethod httpMethod, String context, String requestPath)
            throws RestException {

        try {
            log.debug(
                    "********************** USER-INPUT *******************************************");
            log.debug("Context :" + context);
            log.debug("Request Path :" + requestPath);
            log.debug(
                    "******************************************************************************");
            log.debug(
                    "********************** REST-REQUEST ******************************************");
            log.debug(userRequest);
            log.debug(
                    "******************************************************************************");

            if (context != null && requestPath != null) {
                this.userRequest = RestRequestHandler.getUserRequest(requestPath);
            }

            this.httpEntity = new HttpEntity<>(userRequest, this.httpHeader);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, httpMethod,
                    httpEntity, String.class);

            if (responseEntity != null) {

                // Get Response as String
                responseString = responseEntity.toString();

                log.debug(
                        "********************** REST-RESPONSE ******************************************");
                log.debug(responseString);
                log.debug(
                        "******************************************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug(
                        "********************** REST-RESPONSE-STATUSCODE *******************************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug(
                        "******************************************************************************");

                // Set RestResponse
                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }
        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }



    /**
     * This method performs REST DELETE operation with Request Body.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse executeDeleteWithRequestBody(String context, String requestPath)
            throws RestException {

        try {
            log.debug(
                    "********************** USER-INPUT *******************************************");
            log.debug("Context :" + context);
            log.debug("Request Path :" + requestPath);
            log.debug(
                    "******************************************************************************");
            log.debug(
                    "********************** REST-REQUEST ******************************************");
            log.debug(requestPath);
            log.debug(
                    "******************************************************************************");

            if (context != null && requestPath != null) {
                // Read request from filePath
                this.userRequest = RestRequestHandler.getUserRequest(requestPath);

                this.httpEntity = new HttpEntity<>(userRequest, httpHeader);

                // Very Imp to handle DELETE with Request Body
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory() {
                    @Override
                    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                        if (HttpMethod.DELETE == httpMethod) {
                            return new HttpEntityEnclosingDeleteRequest(uri);
                        }
                        return super.createHttpUriRequest(httpMethod, uri);
                    }
                });

            }
            this.responseEntity = restTemplate.exchange(this.restUrl + context, HttpMethod.DELETE,
                    httpEntity, String.class);

            if (this.responseEntity != null) {

                responseString = responseEntity.toString();

                log.debug("********************** REST-RESPONSE **********************");
                log.debug(responseString);
                log.debug("**********************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();

                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }



    /**
     * This method performs REST DELETE operation with Request Body and InputData.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param inputData
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse executeDeleteWithRequestBody(String context, String requestPath,
            String[][] inputData) throws RestException {

        try {
            log.debug(
                    "********************** USER-INPUT *******************************************");
            log.debug("Context :" + context);
            log.debug("Request Path :" + requestPath);
            log.debug(
                    "******************************************************************************");
            log.debug(
                    "********************** REST-REQUEST ******************************************");
            log.debug(requestPath);
            log.debug(
                    "******************************************************************************");

            if (context != null && requestPath != null && inputData != null) {
                // Read Request from FilePath with user-input-data
                this.userRequest = RestRequestHandler.getRestRequest(requestPath, inputData);

                this.httpEntity = new HttpEntity<>(userRequest, httpHeader);

                // Very Imp to handle DELETE with Request Body
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory() {
                    @Override
                    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                        if (HttpMethod.DELETE == httpMethod) {
                            return new HttpEntityEnclosingDeleteRequest(uri);
                        }
                        return super.createHttpUriRequest(httpMethod, uri);
                    }
                });

            }
            this.responseEntity = restTemplate.exchange(this.restUrl + context, HttpMethod.DELETE,
                    httpEntity, String.class);

            if (this.responseEntity != null) {

                responseString = responseEntity.toString();

                log.debug("********************** REST-RESPONSE **********************");
                log.debug(responseString);
                log.debug("**********************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();

                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }


    /**
     * This method performs UPLOADFILE operation with Input Data
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param inputData
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * @param method
     *        - any HttpMethod
     * 
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse uploadFileWithInputData(String context, String requestPath,
            String[][] inputData, HttpMethod method) throws RestException {

        String fileContent = null;

        try {

            if (context != null && requestPath != null) {
                final String fileName = requestPath.substring(requestPath.lastIndexOf("/") + 1);
                // final String fileName =
                // requestPath.substring(requestPath.lastIndexOf(File.pathSeparator)+1);
                fileContent = RestRequestHandler.getUserRequest(requestPath);


                log.debug(
                        "********************** USER-INPUT *******************************************");
                log.debug("Context :" + context);
                log.debug("Request Path :" + requestPath);
                log.debug("Uploading File Name : " + fileName);
                log.debug(
                        "******************************************************************************");

                this.inputMap = new LinkedMultiValueMap<>();


                ByteArrayResource byteArray = new ByteArrayResource(fileContent.getBytes()) {
                    @Override
                    public String getFilename() {

                        return fileName;
                    }
                };

                this.inputMap.add("fileupload", byteArray);
                for (int i = 0; i < inputData.length; i++) {
                    this.inputMap.add(inputData[i][0], inputData[i][1]);
                }
            }

            this.requestEntity = new HttpEntity<>(this.inputMap,
                    this.httpHeader);

            // this.responseEntity = restTemplate.exchange(this.restUrl + context, HttpMethod.POST,
            // this.requestEntity, String.class);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, method,
                    this.requestEntity, String.class); // Parameterized HTTP Method input

            if (this.responseEntity != null) {
                responseString = responseEntity.toString();
                log.debug("********************** REST-RESPONSE *********************************");
                log.debug(responseString);
                log.debug("**********************************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }


    /**
     * This method performs UPLOADFILE operation with Input Data
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param inputData
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * @param method
     *        - any HttpMethod
     * 
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse uploadBinaryFileWithInputData(String context, String requestPath,
            String[][] inputData, HttpMethod method) throws RestException {

        String fileContent = null;

        try {

            if (context != null && requestPath != null) {
                final String fileName = requestPath.substring(requestPath.lastIndexOf("/") + 1);
                // final String fileName =
                // requestPath.substring(requestPath.lastIndexOf(File.pathSeparator)+1);
                fileContent = RestRequestHandler.getUserRequest(requestPath);


                log.debug(
                        "********************** USER-INPUT *******************************************");
                log.debug("Context :" + context);
                log.debug("Request Path :" + requestPath);
                log.debug("Uploading File Name : " + fileName);
                log.debug(
                        "******************************************************************************");

                this.inputMap = new LinkedMultiValueMap<>();


                ByteArrayResource byteArray = new ByteArrayResource(fileContent.getBytes()) {
                    @Override
                    public String getFilename() {

                        return fileName;
                    }
                };

                this.inputMap.add("binaryfileupload", byteArray);
                for (int i = 0; i < inputData.length; i++) {
                    this.inputMap.add(inputData[i][0], inputData[i][1]);
                }
            }

            this.requestEntity = new HttpEntity<>(this.inputMap,
                    this.httpHeader);

            // this.responseEntity = restTemplate.exchange(this.restUrl + context, HttpMethod.POST,
            // this.requestEntity, String.class);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, method,
                    this.requestEntity, String.class); // Parameterized HTTP Method input

            if (this.responseEntity != null) {
                responseString = responseEntity.toString();
                log.debug("********************** REST-RESPONSE *********************************");
                log.debug(responseString);
                log.debug("**********************************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }


    /**
     * This method performs UPLOADFILE operation with PAYLOAD
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param payLoad
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse uploadFileWithPayLoad(String context, String requestPath,
            String[][] payLoad) throws RestException {

        Boolean booleanData = false;

        try {

            if (context != null && requestPath != null) {

                final String fileName = requestPath.substring(requestPath.lastIndexOf("/") + 1);
                String fileContent = RestRequestHandler.getUserRequest(requestPath);

                log.debug(
                        "********************** USER-INPUT *******************************************");
                log.debug("Context :" + context);
                log.debug("Request Path :" + requestPath);
                log.debug("Uploading File Name : " + fileName);
                log.debug(
                        "******************************************************************************");

                inputMap = new LinkedMultiValueMap<>();

                ByteArrayResource byteArray = new ByteArrayResource(fileContent.getBytes()) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                };


                // example
                // inputMap.add("update_duplicate", false);

                for (int i = 0; i < payLoad.length; i++) {
                    if (payLoad[i][1].equalsIgnoreCase("true")
                            || payLoad[i][1].equalsIgnoreCase("false")) {
                        booleanData = Boolean.valueOf(payLoad[i][1]);
                        this.inputMap.add(payLoad[i][0], booleanData);
                    } else {
                        this.inputMap.add(payLoad[i][0], payLoad[i][1]);
                    }
                }

                this.inputMap.add("uploadData", byteArray);

            }

            this.requestEntity =
                    new HttpEntity<>(inputMap, this.httpHeader);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, HttpMethod.POST,
                    requestEntity, String.class);

            if (this.responseEntity != null) {

                this.responseString = responseEntity.toString();
                log.debug("********************** REST-RESPONSE **********************");
                log.debug(responseString);
                log.debug("**********************************************************");

                // Get status code
                this.httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);

            }
        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }


    /**
     * This method performs UPLOADFILE operation with files more 64K
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be JSON or XML file path
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    protected RestResponse uploadFileWithoutUserInput(String filePath, String context,
            HttpMethod method) throws RestException {

        try {

            if (context != null && filePath != null) {
                final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                log.debug("Uploading File Name : " + fileName);

                this.inputMap = new LinkedMultiValueMap<>();
                this.inputMap.add("fileupload", new FileSystemResource(filePath));
            }

            this.requestEntity =
                    new HttpEntity<>(inputMap, this.httpHeader);
            this.responseEntity = restTemplate.exchange(this.restUrl + context, method,
                    requestEntity, String.class);

            if (this.responseEntity != null) {

                responseString = responseEntity.toString();
                log.debug("********************** REST-RESPONSE *********************");
                log.debug(responseString);
                log.debug("**********************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************");

                this.restResponse = new RestResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponse;
    }



    /**
     * @param context
     * @return
     * @throws RestException
     */
    public RestResponseByte fetchFile(String context) throws RestException {

        try {

            if (context != null) {
                this.httpEntity = new HttpEntity<>("parameters", httpHeader);
            }
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(this.restUrl + context,
                    HttpMethod.GET, this.httpEntity, byte[].class, "1");

            if (responseEntity != null) {

                this.responseBody = responseEntity.getBody();

                log.debug("********************** REST-RESPONSE **********************");
                log.debug(responseBody.toString());
                log.debug("**********************************************************");

                // Get status code
                this.httpStatus = responseEntity.getStatusCode();
                log.debug("********************** REST-RESPONSE-STATUSCODE **********************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug("**********************************************************");
                log.debug(responseEntity.getHeaders());

                this.restResponseByte =
                        new RestResponseByte(responseBody, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new RestException("RestException-1001 : " + ex.getMessage());
        }

        return restResponseByte;
    }



    /**
     * This method performs REST DELETE operation.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param request
     *        - request can be a dynamic JSON or XML
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     */
    /*
     * public RestResponse executeDelete(String context, String request) {
     * 
     * this.httpEntity = new HttpEntity<String>(request, httpHeader);
     * this.responseEntity = restTemplate.exchange(this.restUrl + context,
     * HttpMethod.DELETE, this.httpEntity, String.class); if
     * (this.responseEntity != null) {
     * 
     * responseString = responseEntity.toString();
     * 
     * log.debug("********************** REST-RESPONSE **********************");
     * log.debug(responseString);
     * log.debug("**********************************************************");
     * 
     * // Get status code httpStatus = responseEntity.getStatusCode();
     * log.debug(
     * "********************** REST-RESPONSE-STATUSCODE **********************"
     * ); log.debug("STATUSCODE :" + httpStatus.toString());
     * log.debug("**********************************************************");
     * 
     * restResponse = new RestResponse(responseString, responseEntity,
     * httpStatus); }
     * 
     * return restResponse; }
     */



    /**
     * Generate Random Number with user input
     * 
     * @param length of digit
     * @return Random Number
     */
    protected static long generateRandomDigit(long length) {
        Long random;
        Long rlength = (length - 1);

        if (rlength <= -1) {
            rlength = new Long(0);
        }

        Long math = (long) Math.pow(10, rlength);
        Long randomNumber = 9 * math;

        Random ran = new Random();
        random = Long.valueOf(new Integer(ran.nextInt(randomNumber.intValue())));

        return random;
    }


    /**
     * @author Girija
     *         Very Imp :
     *         Wrapper Class to handle DELETE with Request Body
     * 
     */
    public static class HttpEntityEnclosingDeleteRequest extends
            HttpEntityEnclosingRequestBase {

        public HttpEntityEnclosingDeleteRequest(final URI uri) {
            super();
            setURI(uri);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }

    }

}
