package com.optum.bdd.core.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RestRequestHandler {

    private static final String MATCHPATTERN = "(%.+?%)";
    private static Logger log = Logger.getLogger(RestRequestHandler.class.getName());

    public RestRequestHandler() {}

    // To set ContentType
    public static void setContentType(HttpHeaders httpHeader, String contentType) {
        if (contentType.contains("text/plain")) {

            httpHeader.setContentType(MediaType.TEXT_PLAIN);
        } else if (contentType.contains("application/json")) {

            httpHeader.setContentType(MediaType.APPLICATION_JSON);
        } else if (contentType.contains("application/xml")) {

            httpHeader.setContentType(MediaType.APPLICATION_XML);
        } else {

            httpHeader.add("Content-Type", contentType);
        }

    }

    // To set Base64 Credentials
    public static String getBase64Credentials(String userName, String password) {
        String plainCreds = userName + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }

    // @SuppressWarnings("resource")
    public static String getUserRequest(String requestPath) {

        File file = new File(requestPath);

        if (!file.exists()) {
            log.debug("***********Dynamic JSON or XML request");
            return requestPath;
        } else if (file.exists()) {

            log.debug("***********Reading the request from " + requestPath + "file.*************");
            Scanner input = null;
            StringBuilder string = new StringBuilder();
            try {
                input = new Scanner(file);
                while (input.hasNextLine()) {
                    string.append(input.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return string.toString();

        } else {

            return null;
        }

    }

    public static String getRestRequest(String request, String[][] inputData) throws RestException {

        String dataBoundRequest = null;

        if (request != null) {
            try {
                dataBoundRequest = getUserRequest(request);
                dataBoundRequest = matchAndReplace(dataBoundRequest, MATCHPATTERN, inputData);
            } catch (Exception ex) {
                throw new RestException("RestException-2004 : " + ex.getMessage());
            }
        }

        return dataBoundRequest;
    }

    // Pattern match and replace based on 2D-Array
    public static String matchAndReplace(String string, String patternMatch, String[][] inputData)
            throws RestException {

        String result = null;
        try {

            if (string != null) {
                result = new String(string);
            }

            if (string.contains(".")) {
                string = string.replace(".", "\\.");
            }

            Pattern pattern = Pattern.compile(patternMatch);
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                String groupString = matcher.group();
                for (int i = 0; i < inputData.length; i++) {
                    if (groupString.contains(inputData[i][0])) {
                        String replacementWith = inputData[i][1];
                        result = result.replace(groupString, replacementWith);
                    }
                }
            }

        } catch (Exception ex) {

            throw new RestException("RestException : " + ex.getMessage());
        }

        return result;
    }

}
