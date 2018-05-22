package com.optum.bdd.core.soap;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.JSchException;

/**
 * @author girija.panda@nokia.com
 *
 */
public class SoapBase {

    protected String endPoint = null;
    protected String userName = null;
    protected String password = null;
    protected String passwordType = null;
    protected String contentType = null;
    protected boolean isSecure = false;

    protected RestTemplate restTemplate = null;
    protected HttpHeaders httpHeader = null;
    protected HttpEntity<String> httpEntity = null;
    protected ResponseEntity<String> responseEntity = null;
    protected HttpStatus httpStatus = null;

    protected SoapResponse soapResponse = null;
    protected String userRequest = null;
    protected String responseString = null;

    protected AutoTunnel autotunnel = null;

    private static Logger log = Logger.getLogger(SoapBase.class.getName());

    public SoapBase() {

    }


    /**
     * Initialize SoapBase
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param contentType
     * @throws SoapException
     */
    protected SoapBase(String endpoint, String username, String password, String contentType)
            throws SoapException {

        // Initialize
        this.endPoint = endpoint;
        this.userName = username;
        this.password = password;
        this.contentType = contentType;

        // HttpHeader
        this.httpHeader = new HttpHeaders();

        // Set content-type
        RequestHandler.setContentType(this.httpHeader, this.contentType);
        // Add to headers and authenticate credentials
        this.httpHeader.add("Authorization",
                "Basic " + RequestHandler.getBase64Credentials(this.userName, this.password));


        // Check for https
        if (this.endPoint.toLowerCase().contains("https")) {
            isSecure = true;
        }

        // FOR REST_TEMPLATE
        this.restTemplate = new RestTemplate();

        // VERY IMP : To Handle SERVER_ERROR & FAULT_MESSAGE
        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });


        log.debug(
                "********************** SoapUtil-INITIALIZED *******************************************");
        log.debug("End Point : " + this.endPoint + "\n" +
                "	UserName : " + this.userName + "\n" +
                "	Password: " + this.password + "\n" +
                "	ContentType : " + this.contentType);
        log.debug(
                "***************************************************************************************");


    }

    /**
     * Initialize SoapBase
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param passwordType
     * @param contentType
     * @throws SoapException
     */

    protected SoapBase(String endpoint, String username, String password, String passwordType,
            String contentType) throws SoapException {

        // Initialize
        this.endPoint = endpoint;
        this.userName = username;
        this.password = password;
        this.passwordType = passwordType;
        this.contentType = contentType;

        // HttpHeader
        this.httpHeader = new HttpHeaders();

        // Set content-type
        RequestHandler.setContentType(this.httpHeader, this.contentType);

        // Check for https
        if (this.endPoint.toLowerCase().contains("https")) {
            isSecure = true;
        }

        // FOR REST_TEMPLATE
        this.restTemplate = new RestTemplate();

        // VERY IMP : To Handle SERVER_ERROR & FAULT_MESSAGE
        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });

        log.debug(
                "********************** SoapUtil-INITIALIZED *******************************************");
        log.debug("End Point : " + this.endPoint + "\n" +
                "	UserName : " + this.userName + "\n" +
                "	Password : " + this.password + "\n" +
                "	Password Type: " + this.passwordType + "\n" +
                "	ContentType : " + this.contentType);
        log.debug(
                "***************************************************************************************");
    }



    /**
     * Initialize SoapBase
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param contentType
     * @param tunnelHost
     * @param tunnelUserName
     * @param tunnelPassword
     * @param tunnelPort
     * @param tunnelRemoteHost
     * @param tunnelRemotePort
     * @param tunnelLocalPort
     * @throws SoapException
     */
    protected SoapBase(String endpoint, String username, String password, String contentType,
            String tunnelHost, String tunnelUserName, String tunnelPassword, String tunnelPort,
            String tunnelRemoteHost, String tunnelRemotePort, String tunnelLocalPort)
            throws SoapException {


        // Initialize
        this(endpoint, username, password, contentType);

        if (tunnelHost != null || tunnelUserName != null || tunnelPassword != null
                || tunnelRemoteHost != null || tunnelRemotePort != null
                || tunnelLocalPort != null) {
            this.autotunnel = initAutoTunnel(tunnelHost, tunnelUserName, tunnelPassword, tunnelPort,
                    tunnelRemoteHost, tunnelRemotePort, tunnelLocalPort);
        }

        log.debug(
                "********************** SoapUtil-INITIALIZED *******************************************");
        log.debug("End Point : " + this.endPoint + "\n" +
                "	UserName : " + this.userName + "\n" +
                "	Password: " + this.password + "\n" +
                "	ContentType : " + this.contentType);
        log.debug(
                "***************************************************************************************");


    }


    /**
     * Initialize SoapBase
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param passwordType
     * @param contentType
     * @param tunnelHost
     * @param tunnelUserName
     * @param tunnelPassword
     * @param tunnelPort
     * @param tunnelRemoteHost
     * @param tunnelRemotePort
     * @param tunnelLocalPort
     * @throws SoapException
     */
    protected SoapBase(String endpoint, String username, String password, String passwordType,
            String contentType, String tunnelHost, String tunnelUserName, String tunnelPassword,
            String tunnelPort, String tunnelRemoteHost, String tunnelRemotePort,
            String tunnelLocalPort) throws SoapException {


        // Initialize
        this(endpoint, username, password, passwordType, contentType);

        if (tunnelHost != null || tunnelUserName != null || tunnelPassword != null
                || tunnelRemoteHost != null || tunnelRemotePort != null
                || tunnelLocalPort != null) {

            this.autotunnel = initAutoTunnel(tunnelHost, tunnelUserName, tunnelPassword, tunnelPort,
                    tunnelRemoteHost, tunnelRemotePort, tunnelLocalPort);

        }

        log.debug(
                "********************** SoapUtil-INITIALIZED *******************************************");
        log.debug("End Point : " + this.endPoint + "\n" +
                "	UserName : " + this.userName + "\n" +
                "	Password : " + this.password + "\n" +
                "	Password Type: " + this.passwordType + "\n" +
                "	ContentType : " + this.contentType);
        log.debug(
                "***************************************************************************************");

    }



    /**
     * Execute Soap-Request and Generate Response
     * 
     * @param request : XML request file path
     * @return SoapResponse
     */
    public SoapResponse execute(String request) throws SoapException {

        try {
            if (request != null) {
                userRequest = RequestHandler.getUserRequest(request);
            }

            // For WSSE-SOAP-Request
            if (this.passwordType != null
                    && (this.passwordType.toLowerCase().trim().equals("passwordtext")
                            || this.passwordType.toLowerCase().trim().equals("passworddigest"))) {
                userRequest = RequestHandler.getSoapRequest(userRequest, userName, password,
                        passwordType, endPoint, isSecure);
            }

            log.debug(
                    "********************** SOAP-REQUEST ******************************************");
            log.debug(userRequest);
            log.debug(
                    "******************************************************************************");

            this.httpEntity = new HttpEntity<>(userRequest, this.httpHeader);
            this.responseEntity =
                    restTemplate.exchange(this.endPoint, HttpMethod.POST, httpEntity, String.class);

            if (responseEntity != null) {

                // Get Response as String
                responseString = responseEntity.toString();
                responseString = XMLUtil.decodeToXML(responseString);

                log.debug(
                        "********************** SOAP-RESPONSE ******************************************");
                log.debug(XMLUtil.formatToXml(XMLUtil.decodeToXML(responseString)));
                log.debug(
                        "******************************************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();
                log.debug(
                        "********************** SOAP-RESPONSE-STATUSCODE *******************************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug(
                        "******************************************************************************");

                // Set SoapResponse
                soapResponse = new SoapResponse(responseString, responseEntity, httpStatus);
            }

            // Closing AutoTunnel Session
            if (this.autotunnel != null) {
                this.autotunnel.closeSession();
                this.autotunnel = null;
            }

        } catch (Exception ex) {

            // Closing AutoTunnel Session
            if (this.autotunnel != null) {
                this.autotunnel.closeSession();
                this.autotunnel = null;
            }

            throw new SoapException("SoapException-1001 : " + ex.getMessage());
        }

        return soapResponse;
    }


    /**
     * Execute Soap-Request with input data and Generate Response
     * 
     * @param request : XML request file path
     * @param request : String[][] inputData
     * @return SoapResponse
     */
    public SoapResponse execute(String request, String[][] inputData) throws SoapException {

        try {
            if (request != null) {
                // return Input Data-bound-Request as String
                userRequest = RequestHandler.getSoapRequest(request, inputData);
            }

            // For WSSE-SOAP-Request
            if (this.passwordType != null
                    && (this.passwordType.toLowerCase().trim().equals("passwordtext")
                            || this.passwordType.toLowerCase().trim().equals("passworddigest"))) {
                userRequest = RequestHandler.getSoapRequest(userRequest, userName, password,
                        passwordType, endPoint, isSecure);
            }

            log.debug(
                    "********************** SOAP-REQUEST ******************************************");
            log.debug(userRequest);
            log.debug(
                    "******************************************************************************");

            this.httpEntity = new HttpEntity<>(userRequest, this.httpHeader);
            this.responseEntity =
                    restTemplate.exchange(this.endPoint, HttpMethod.POST, httpEntity, String.class);

            if (responseEntity != null) {
                // Get Response as String
                responseString = responseEntity.toString();
                responseString = XMLUtil.decodeToXML(responseString);

                log.debug(
                        "********************** SOAP-RESPONSE ******************************************");
                log.debug(XMLUtil.formatToXml(XMLUtil.decodeToXML(responseString)));
                log.debug(
                        "******************************************************************************");

                // Get status code
                httpStatus = responseEntity.getStatusCode();

                log.debug(
                        "********************** SOAP-RESPONSE-STATUSCODE *******************************");
                log.debug("STATUSCODE :" + httpStatus.toString());
                log.debug(
                        "******************************************************************************");

                // Set SoapResponse
                soapResponse = new SoapResponse(responseString, responseEntity, httpStatus);
            }

        } catch (Exception ex) {
            throw new SoapException("SoapException-1001 : " + ex.getMessage());
        }

        return soapResponse;
    }



    // Initialize AutoTunnel and Open Session
    protected AutoTunnel initAutoTunnel(String tunnelHost, String tunnelUserName,
            String tunnelPassword, String tunnelPort, String tunnelRemoteHost,
            String tunnelRemotePort, String tunnelLocalPort) throws SoapException {

        AutoTunnel myAutotunnel = null;

        try {
            // myAutotunnel = new AutoTunnel(tunnelHost, tunnelUserName, tunnelPassword,
            // Integer.valueOf( tunnelPort ), tunnelRemoteHost, Integer.valueOf( tunnelRemotePort ),
            // Integer.valueOf( tunnelLocalPort ) );
            myAutotunnel = new AutoTunnel(tunnelHost, tunnelUserName, tunnelPassword, tunnelPort,
                    tunnelRemoteHost, tunnelRemotePort, tunnelLocalPort);

            myAutotunnel.openSesssion();
        } catch (JSchException ex) {

            throw new SoapException("SoapException-AutoTunnel: " + ex.getMessage());
        }

        return myAutotunnel;
    }


}
