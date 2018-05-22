package com.optum.bdd.core.rest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

/**
 * @author Mohan
 *
 */
public class RestUtil extends RestBase {


    private static Logger log = Logger.getLogger(RestUtil.class.getName());

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
    public RestUtil(String resturl, String username, String password, String contentType) {

        super(resturl, username, password, contentType);

        log.debug(
                "********************** RestUtil-INITIALIZED *******************************************");
        log.debug("UserName : " + this.restUsername.trim() + "\n" + "	Password: "
                + this.restPassword.trim() + "\n" + "	Rest URL : " + this.restUrl
                + "\n" + "	ContentType : " + this.contentType);
        log.debug(
                "***************************************************************************************");
    }


    /**
     * Initialize RESTUtil
     * 
     * @param resturl - Base URL of rest : http://server:port/
     * @param contentType - ContentType for REST
     */
    public RestUtil(String resturl, String contentType) {

        super(resturl, contentType);

        log.debug(
                "********************** RestUtil-INITIALIZED Without Authentication*******************************************");
        log.debug("Rest URL : " + this.restUrl + "\n" +
                "	ContentType : " + this.contentType);
        log.debug(
                "******************************************************************************************************");
    }



    /***********************************************
     * HTTP Methods
     ***********************************************
     */

    /**
     * This method performs REST GET operation.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestException
     */

    public RestResponse executeGet(String context) throws RestException {

        return execute(HttpMethod.GET, context);
    }



    /**
     * This method performs REST POST operation.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     */

    public RestResponse executePost(String context, String requestPath) throws RestException {

        return execute(HttpMethod.POST, context, requestPath);
    }


    /**
     * This method performs REST PUT operation.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     */
    public RestResponse executePut(String context, String requestPath) throws RestException {

        return execute(HttpMethod.PUT, context, requestPath);
    }



    /**
     * This method performs REST DELETE operation.
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     */
    public RestResponse executeDelete(String context) throws RestException {

        return execute(HttpMethod.DELETE, context);
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
    public RestResponse executeDelete(String context, String requestPath) throws RestException {

        return executeDeleteWithRequestBody(context, requestPath);
    }



    /**
     * This method performs REST DELETE operation with Request Body and Input Data.
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
    public RestResponse executeDelete(String context, String requestPath, String[][] inputData)
            throws RestException {

        return executeDeleteWithRequestBody(context, requestPath, inputData);
    }



    /**
     * This method performs REST PUT operation.
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
     */

    public RestResponse executePut(String context, String requestPath, String[][] inputData)
            throws RestException {

        return execute(HttpMethod.PUT, context, requestPath, inputData);
    }



    /**
     * This method performs REST POST operation.
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
     */
    public RestResponse executePost(String context, String requestPath, String[][] inputData)
            throws RestException {

        return execute(HttpMethod.POST, context, requestPath, inputData);
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
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */
    public RestResponse uploadFile(String context, String requestPath, String[][] inputData)
            throws RestException {

        return uploadFileWithInputData(context, requestPath, inputData, HttpMethod.POST);
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
     * 
     * @param method
     *        - Any HttpMethod
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */

    public RestResponse uploadFile(String context, String requestPath, String[][] inputData,
            HttpMethod method) throws RestException {

        return uploadFileWithInputData(context, requestPath, inputData, method);
    }

    /**
     * This method performs UPLOAD FILE(*.bin) operation with Input Data
     * 
     * @param context
     *        - context of the url to be appended to the base url of
     *        http://server:port/
     * @param requestPath
     *        - request can be a dynamic JSON or XML file path
     * @param inputData
     *        - input data can be 2D-Array of {"key1","value1"} pairs
     * 
     * @param method
     *        - Any HttpMethod
     * @return RestResponse object containing status code, message/exception and
     *         the response body.
     * @throws RestClientException
     */

    public RestResponse uploadBinaryFile(String context, String requestPath, String[][] inputData,
            HttpMethod method) throws RestException {

        return uploadBinaryFileWithInputData(context, requestPath, inputData, method);
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
    public RestResponse uploadFileWithPayLoad(String context, String requestPath,
            String[][] payLoad) throws RestException {

        return super.uploadFileWithPayLoad(context, requestPath, payLoad);

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
    public RestResponse uploadFileWithoutUserInput(String filePath, String context,
            HttpMethod method) throws RestException {

        return super.uploadFileWithoutUserInput(filePath, context, method);
    }


    /**
     * @param context
     * @return
     * @throws RestException
     */
    public RestResponseByte fetchFile(String context) throws RestException {

        return super.fetchFile(context);
    }


    /**
     * Generate Random Number with user input
     * 
     * @param length of digit
     * @return Random Number
     */
    public static long generateRandomDigits(long length) {

        return generateRandomDigit(length);
    }


}
