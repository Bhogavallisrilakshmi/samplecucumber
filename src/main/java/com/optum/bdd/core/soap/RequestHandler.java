package com.optum.bdd.core.soap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RequestHandler {

    private static final String MATCHPATTERN = "(%.+?%)";
    private static final String SOAPHEADER_MATCHPATTERN =
            "(<.+?:Header\\/>)|(<.+?:Header>)(<.*\\n+(<?.*\\n)+)(<.+?:Header\\/>)";
    private static final String WSSE_HEADER_MATCHPATTERN = "(\\{.+?\\})";

    private static long milliseconds = 0;
    private static Date date = null;
    private static Calendar calender = null;
    private static TimeZone timezone = null;
    private static URL endPointUrl = null;
    private static HttpURLConnection httpConnection = null;
    private static HttpsURLConnection httpSconnection = null;

    // private static Object lockObject = new Object();
    private static Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler() {

    }


    // START : Get SOAP-REQUEST = <WSSE-SOAP-HEADER + USER-SOAP-REQUEST>
    public static String getSoapRequest(String request, String userName, String password,
            String passwordType, String endPoint, boolean isSecure)
            throws SoapException {

        String soapWSSEHeader = null;
        String soapRequest = null;

        try {
            if (request != null) {
                // Construct WSSE-SOAP-HEADER
                soapWSSEHeader =
                        getWSSESoapHeader(userName, password, passwordType, endPoint, isSecure);
                soapWSSEHeader = XMLUtil.formatToXml(soapWSSEHeader);

                /*
                 * log.debug("soapWSSEHeader :");
                 * log.debug("================================");
                 * log.debug(soapWSSEHeader);
                 * log.debug(
                 * "************************************************************************************************"
                 * );
                 */
                // Add above WSSE-SOAP-HEADER to user input Soap-Request
                soapRequest = getSoapRequestWithHeader(request, soapWSSEHeader);

                /*
                 * log.debug("Actual Request :");
                 * log.debug("================================");
                 * log.debug(XMLUtil.formatToXml(soapRequest));
                 * log.debug(
                 * "************************************************************************************************"
                 * );
                 */
            }

        } catch (Exception ex) {
            // TODO Auto-generated catch block
            throw new SoapException("SoapException-201 : " + ex.getMessage());
        }

        return soapRequest;
    }


    // 1. Construct WSSE-SOAP-HEADER = <SOAP-Header + WSSE-Token>
    public static String getWSSESoapHeader(String userName, String password, String passwordType,
            String endPoint, boolean isSecure) throws SoapException {

        String wsseSoapHeader = null;
        try {
            if (userName != null && password != null && passwordType != null && endPoint != null) {

                // Construct Custom-SOAP-Header
                wsseSoapHeader = XMLUtil.matchAndReplace(getWSSEHeader(passwordType),
                        WSSE_HEADER_MATCHPATTERN,
                        userName,
                        password,
                        BASE64Encoder.encode(getNounce()),
                        getSoapDate(getServerDate(endPoint, false)));
            }
        } catch (Exception ex) {
            throw new SoapException("SoapException-202 : " + ex.getMessage());
        }

        return wsseSoapHeader;
    }


    // 2. Delete any SOAP-HEADER from USER-SOAP-REQUEST
    public static String getSoapRequestWithHeader(String request, String customSoapHeader)
            throws SoapException {

        String headerBindRequest = null;

        try {
            if (request != null) {
                headerBindRequest = XMLUtil.matchAndReplace(request,
                        SOAPHEADER_MATCHPATTERN,
                        customSoapHeader);
            }
        } catch (Exception ex) {
            throw new SoapException("SoapException-203 : " + ex.getMessage());
        }

        return headerBindRequest;
    }

    /*
     * Get Nounce
     */
    public static String getNounce() {
        Random generator = new Random();
        String nonceString = String.valueOf(generator.nextInt(999999999));
        return nonceString;
    }

    // Set WSSE Header
    public static String getWSSEHeader(String passwordType) {
        String WSSE_PASSWORDTYPE_TAG = null;

        if (passwordType != null) {
            // For PasswordType:PasswordText
            if (passwordType.toLowerCase().trim().equals("passwordtext")
                    || passwordType.toLowerCase().trim().equals("text")) {
                WSSE_PASSWORDTYPE_TAG =
                        "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">";
            }

            // For PasswordType:PasswordDigest
            if (passwordType.toLowerCase().trim().equals("passworddigest")
                    || passwordType.toLowerCase().trim().equals("digest")) {
                WSSE_PASSWORDTYPE_TAG =
                        "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">";
            }
        }

        String securityHeader = "<soap:Header>"
                + "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
                + "<wsse:UsernameToken xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                + "<wsse:Username>{0}</wsse:Username>"
                + WSSE_PASSWORDTYPE_TAG + "{1}</wsse:Password>"
                + "<wsse:Nonce>{2}</wsse:Nonce>"
                + "<wsu:Created>{3}</wsu:Created>" + "</wsse:UsernameToken>"
                + "</wsse:Security>" + "</soap:Header>";

        return securityHeader;
    }

    /**
     * @throws MalformedURLException
     *         Get Server Date
     */
    public static Date getServerDate(String endPoint, boolean secure) throws SoapException {


        try {
            endPointUrl = new URL(endPoint);
            if (!secure) {
                httpConnection = (HttpURLConnection) endPointUrl.openConnection();
                milliseconds = httpConnection.getDate();
                httpConnection.disconnect();
            } else {
                httpSconnection = (HttpsURLConnection) endPointUrl.openConnection();
                milliseconds = httpSconnection.getDate();
                httpSconnection.disconnect();
            }

            if (milliseconds == 0) {
                return null;
            }

            date = new Date(milliseconds);
            calender = Calendar.getInstance();
            calender.setTime(date);
            timezone = calender.getTimeZone();
            Integer offset = timezone.getOffset(milliseconds);
            calender.add(Calendar.MILLISECOND, offset * -1);

        } catch (MalformedURLException ex) {
            throw new SoapException("SoapException-205 :" + ex.getMessage());
        } catch (IOException ex) {
            throw new SoapException("SoapException-206 :" + ex.getMessage());
        }

        return calender.getTime();
    }


    // Get Date as SOAP-Format
    public static String getSoapDate(Date serverDate) {

        if (serverDate == null) {
            return "";
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = dateFormat.format(serverDate);
        return date;
    }


    // Get User Request
    public static String getUserRequest(String request) throws SoapException {

        String userRequest = null;
        try {
            if (request != null && request.endsWith(".xml")) {
                userRequest = XMLUtil.fileToString(request);
                userRequest = XMLUtil.formatToXml(userRequest);
            }
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            throw new SoapException("SoapException-200 : " + ex.getMessage());
        }
        return userRequest;
    }

    public static String getSoapRequest(String request, String[][] inputData)
            throws SoapException {

        String dataBoundRequest = null;

        if (request != null && request.endsWith(".xml")) {
            try {
                dataBoundRequest = XMLUtil.fileToString(request);
                dataBoundRequest =
                        XMLUtil.matchAndReplace(dataBoundRequest, MATCHPATTERN, inputData);
                dataBoundRequest = XMLUtil.formatToXml(dataBoundRequest.toString());
            } catch (Exception ex) {
                throw new SoapException("SoapException-204 : " + ex.getMessage());
            }
        }

        return dataBoundRequest;
    }


    // To set ContentType
    public static void setContentType(HttpHeaders httpHeader, String contentType) {
        if (contentType.contains("text/xml")) {
            httpHeader.setContentType(MediaType.TEXT_XML);
            return;
        }
        if (contentType.contains("application/json")) {
            // Also working
            // this.httpHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            httpHeader.setContentType(MediaType.APPLICATION_JSON);
            return;
        }
        if (contentType.contains("application/xml")) {
            httpHeader.setContentType(MediaType.APPLICATION_XML);
            return;
        }

        if (contentType.contains("application/soap+xml;charset=UTF-8")) {
            // httpHeader.add("Accept-Encoding", "gzip,deflate");
            httpHeader.add("Content-Type", contentType);
            return;
        }
        httpHeader.add("Content-Type", contentType);
    }


    // To set Base64 Credentials
    public static String getBase64Credentials(String userName, String password) {
        String plainCreds = userName + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }

}
