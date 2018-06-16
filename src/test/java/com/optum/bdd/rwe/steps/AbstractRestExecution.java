package com.motive.bdd.smp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motive.bdd.core.rest.RestException;
import com.motive.bdd.core.rest.RestResponse;

import cucumber.api.DataTable;

public abstract class AbstractRestExecution {

    static Logger logger = Logger.getLogger(AbstractRestExecution.class);

    public abstract boolean checkResponse(RestResponse rsp, List<String> outputList)
            throws JSONException;

    public abstract void execute(String name, DataTable dataInput) throws Throwable;

    /**
     * To get the input for rest execution
     *
     * @param dataInput DataTable
     * @return List<List<String>>
     */
    public static List<List<String>> getInput(DataTable dataInput) {
        List<String> inputList = new ArrayList<String>();
        List<String> outputList = new ArrayList<String>();
        List<List<String>> data = dataInput.raw();
        List<List<String>> result = new ArrayList<List<String>>();
        for (int i = 0; i < data.size(); i++) {
            switch (data.get(i).get(0).toLowerCase().trim()) {
                case RestConstants.REQUESTVALUE:
                    inputList.add(data.get(i).get(1).trim());
                    break;
                case RestConstants.VERIFYRESPONSE:
                    outputList.add(data.get(i).get(1).trim());
                    break;
                default:
                    logger.error(
                            "Datatable entry is not correct, it should be either setRequestValue or  verifyResponse");

            }
        }
        result.add(inputList);
        result.add(outputList);
        return result;
    }

    /**
     * To create the request for rest execution
     *
     * @param inputList
     * @param name : name of application
     * @param requestType : rest execution type model or overlay
     * @return requestContent
     */
    public static String createRequestContent(List<String> inputList, String name,
            String requestType) {
        String requestContent, tmpInputText1, tmpInputText2;
        int inputCharPos;
        requestContent = "{";
        for (int i = 0; i < inputList.size(); i++) {
            inputCharPos = inputList.get(i).indexOf("-");
            tmpInputText1 = inputList.get(i).substring(0, inputCharPos);
            tmpInputText2 = inputList.get(i).substring(inputCharPos + 1, inputList.get(i).length());
            switch (tmpInputText1.trim()) {
                case RestConstants.SUBSCRIBERID:
                    requestContent = requestContent + "\"subscriberId\":" + tmpInputText2 + ",";
                    break;
                case RestConstants.TIMEOUTINSEC:
                    requestContent = requestContent + "\"timeoutInSeconds\":" + tmpInputText2 + ",";
                    break;
                case RestConstants.INPUTS:
                    requestContent = requestContent + "\"inputs\":{" + tmpInputText2 + "},";
                    break;
                default:
                    logger.error("Provided input is not proper JSON object for Model");
            }
        }
        String uri = getUri(name, requestType);
        requestContent = requestContent + "\"uri\":" + "\"" + uri + "\"";
        requestContent = requestContent + "}";
        if (logger.isDebugEnabled()) {
            logger.debug("The request is " + requestContent);
        }
        return requestContent;
    }

    /**
     * To get the uri of the application
     *
     * @param name : name of application
     * @param requestType : rest execution type model or overlay
     * @return uri String
     */
    private static String getUri(String name, String requestType) {
        String uri = "";
        SqlRowSet rs = null;
        if (requestType.equals(RestConstants.OVERLAYREQ)) {
            rs = AtfSupport.executeModelingQuery(
                    "SELECT BASEURI FROM ECOMODEL WHERE DOCTYPE='" + RestConstants.ECOOVERLAY
                            + "' AND NAME = '"
                        + name
                        + "'");
        } else {
            rs = AtfSupport.executeModelingQuery(
                    "SELECT URI FROM ECOMODEL WHERE DOCTYPE='" + RestConstants.ECOMODEL
                            + "' AND NAME = '"
                            + name
                            + "'");
        }
        if (rs.next()) {
            if (requestType.equals(RestConstants.OVERLAYREQ))
                uri = rs.getString("BASEURI");
            else
                uri = rs.getString("URI");
        } else {
            logger.error("Unable to find the uri for " + name);
        }
        return uri;
    }

    /**
     * To get the response in json readable format
     *
     * @param rsp : RestResponse
     */
    public static void getNormalizedJson(RestResponse rsp) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Object jsonObject = mapper.readValue(rsp.getResponseBody(), Object.class);
                logger.debug("The response is "
                        + mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(jsonObject));
                System.out.println(mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(jsonObject));
            } catch (IOException | RestException e) {
                logger.debug("Response without formatting : " + rsp);
        }
    }

}
