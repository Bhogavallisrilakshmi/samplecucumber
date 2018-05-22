/**
 * Copyright (c) Nokia 2017 All rights reserved.
 *
 * Nokia, Inc. Proprietary/Trade Secret ; Information Not to be disclosed or used except in
 * accordance with applicable agreements.
 */
package com.optum.bdd.rwe;

import java.io.File;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.jdbc.support.rowset.SqlRowSet;


import com.optum.bdd.core.selenium.AtfConstants;
import com.optum.bdd.core.soap.SoapResponse;
import com.optum.bdd.core.soap.SoapUtil;
import com.optum.bdd.rwe.spring.AtfConfiguration;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;


public class AtfSteps {


    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger(AtfSteps.class);

    /** The Constant MODELING_SCHEMA. */
    private static final String MODELING_SCHEMA = "ModelingDB";

    /** The Constant REPORTING_SCHEMA. */
    private static final String REPORTING_SCHEMA = "ReportingDB";


    /**
     * Add a property value. See {@link PropertyManager}.
     * <p>
     * Example usage:
     * <p>
     * Then ATF add property with key "bdd.smp.download.dir" and value "/tmp"
     * </p>
     *
     *
     * @param key key value
     * @param value property value
     */
    @Given("^ATF add property with key \"([^\"]*)\" and value \"([^\"]*)\"$")
    public void addPropertyValue(String key, String value) {
        AtfConfiguration.setPropertyValue(key, value);
    }

    /**
     * Wait for seconds.
     * <p>
     * Example usage:
     * <p>
     *
     * <pre>
     * And ATF wait for 3 seconds.
     * </pre>
     *
     *
     * @param seconds the seconds to wait
     */
    @And("^ATF wait for (\\d+) seconds$")
    public void waitForSeconds(int seconds) {
        AtfSupport.waitForSeconds(seconds);
    }

    /**
     * Wait for milliseconds.
     * <p>
     * Example usage:
     * <p>
     *
     * <pre>
     * And ATF wait for 500 milliseconds.
     * </pre>
     *
     *
     * @param seconds the milliseconds to wait
     */
    @And("^ATF wait for (\\d+) milliseconds$")
    public void waitForMilliSeconds(int seconds) {
        AtfSupport.waitForMilliSeconds(seconds);
    }

    /**
     * Log text to standard output.
     * <p>
     * Example usage:
     * <p>
     *
     * <pre>
     * And ATF log text "Something interesting is about to happen."
     * </pre>
     *
     * @param logText the text to log
     */
    @And("^ATF log (\".*\")$")
    public void log(String logText) {
        logger.info(logText);
    }

    /**
     * Execute query and check column has specified value using specified schema. Only valid values
     * are supported.
     *
     * An example usage is:
     *
     * <pre>
     *   And DBS Execute query "SELECT VERSION FROM POEM_CONTENT WHERE CREATED=(SELECT MAX(CREATED) FROM POEM_CONTENT)" and check column "VERSION" has values "2" using "ModelingDB" schema
     * </pre>
     *
     * Valid schema values are:
     * <ul>
     * <li>ModelingDB
     * <li>ReportingDB
     * </ul>
     *
     * @param query the query
     * @param columnName the column name
     * @param value the value
     * @param schema the schema
     */
    @Given("^DBS execute query \"([^\"]*)\" and check column \"([^\"]*)\" has value \"([^\"]*)\" using \"([^\"]*)\" schema$")
    public void executeDbQuery(String query,
            String columnName, String value, String schema) {
        String str = null;
        boolean flag = false;
        SqlRowSet rs = null;
        if (schema.equalsIgnoreCase(MODELING_SCHEMA)) {
            rs = AtfSupport.executeModelingQuery(query);
        } else if (schema.equalsIgnoreCase(REPORTING_SCHEMA)) {
            rs = AtfSupport.executeReportQuery(query);
        } else {
            throwInvalidSchemaException(schema);
        }
        while (rs.next()) {
            str = rs.getString(columnName);
            if (str.contains(value)) {
                flag = true;
                break;
            }
        }
       Assert.assertEquals(flag, true);
    }

    /**
     * Check that SMP data exists using the specified query. Valid
     * values for
     * schema are:
     * <ol>
     * <li>ReportingDB
     * <li>ModelingDB
     * </ol>
     *
     * @param query the query
     * @param schema the database name
     * @return true, if successful
     */
    private boolean checkSmpData(String query, String schema) {
        boolean resultValue = false;
        switch (schema) {
            case REPORTING_SCHEMA:
                SqlRowSet reportRowSet = AtfSupport.executeReportQuery(query);
                if (reportRowSet.next()) {
                    resultValue = true;
                }
                break;
            case MODELING_SCHEMA:
                SqlRowSet modelRowSet = AtfSupport.executeModelingQuery(query);
                if (modelRowSet.next()) {
                    // if (modelRowSet.next()) {
                    resultValue = true;
                    // }
                }
                break;
            default:
                throwInvalidSchemaException(schema);
                break;
        }
        return resultValue;
    }

    /**
     * Dbs execute query on database and check result has content.
     *
     * An example usage is:
     *
     * <pre>
     *   And DBS execute query "SELECT VERSION FROM POEM_CONTENT WHERE CREATED=(SELECT MAX(CREATED) FROM POEM_CONTENT)" on database "ModelingDB" and check result has content
     * </pre>
     *
     * Valid values for schema are:
     * <ul>
     * <li>ReportingDB
     * <li>ModelingDB
     * </ul>
     *
     * @param query the query
     * @param schema the database name
     */
    @Given("^DBS execute query \"([^\"]*)\" on \"([^\"]*)\" database and check result has content$")
    public void dbResultExists(String query,
            String schema) {
        Assert.assertEquals(checkSmpData(query, schema), true);

    }

    /**
     * Execute the specified query and check the result has no content.
     *
     * An example usage is:
     *
     * <pre>
     *   And DBS execute query "SELECT VERSION FROM POEM_CONTENT WHERE CREATED=(SELECT MAX(CREATED) FROM POEM_CONTENT)" on database MODELING_SCHEMA and check result has no content
     * </pre>
     *
     * Valid values for schema are:
     * <ul>
     * <li>ReportingDB
     * <li>ModelingDB
     * </ul>
     *
     * @param query the query
     * @param schema the database name
     */
    @Given("^DBS execute query \"([^\"]*)\" on \"([^\"]*)\" database and check result has no content$")
    public void dbResultNotExist(String query,
            String schema) {
        Assert.assertEquals(checkSmpData(query, schema), false);
    }

    /**
     * Throw invalid schema exception.
     *
     * @param schema the schema
     */
    public static void throwInvalidSchemaException(String schema) {
        throw new RuntimeException(schema
                + "is not supported. Supported dataseNames are: " + MODELING_SCHEMA
                + " and " + REPORTING_SCHEMA);
    }

    /**
     * Search for key and replace value in request XML content available in provided path.
     *
     * @param setrRequestXml the set of key value pair in the format (Key1=value1,key2=vaue2)
     * @param requestXmlPath the request xml path
     * @return the updated requestXmlPath
     */
    private String replaceContentInRequestXml(String setrRequestXml, String requestXmlPath) {
        String xml = AtfSupport.readFile(requestXmlPath);
        String stringToReplace = null;
        String stringReplace = null;
        String[] value = setrRequestXml.split(AtfConstants.COMMA);
        for (int i = 0; i < value.length; i++) {
            String[] valueParam = value[i].split(AtfConstants.EQUAL);
            stringToReplace = "#" + valueParam[0] + "#";
            stringReplace = valueParam[1];
            xml = xml.replace(stringToReplace, stringReplace);
        }
        requestXmlPath =
                AtfSupport.createFileWithContent(AtfSupport.getDownloadDirectoryPath()
                        + "request.xml", xml);
        return requestXmlPath;
    }

    /**
     * This implementation is to execute soap request for the corresponding End point URL
     * with attributes and to verify the response contains expected result
     * <p>
     * An example usage is:
     *
     * <pre>
     *          Then SOAP execute request "http://isco.idc.devlab.motive.com:1234/Billing46" with username "" and password ""
     *          |requestXmlPath|src/main/resources/reqXML/billingRequest.xml|
     *          |responseContains|2000|
     * </pre>
     * <ul>
     * <li>requestXmlPath* - The file path location where the request xml content is present</li>
     * <li>responseContains - The response value which will be retrieved from request</li>
     * <li>* - Mandatory Inputs</li>
     * </ul>
     *
     * @param endpoint the endPoint
     * @param userName the user name
     * @param password the password
     * @param inputData the input data
     */
    @Then("^SOAP execute request \"([^\"]*)\" with username \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void soapExecuteRequestWithUsernameAndPassword(String endpoint, String userName,
            String password, DataTable inputData) {
        String requestXmlPath = "";
        String responseContains = "";
        String setRequestXml = null;
        SoapResponse responseContent = null;
        String contentType =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_NBI_CONTENTTYPE);
        List<List<String>> data = inputData.raw();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(0).toLowerCase().equals(AtfConstants.REQUESTPATH)) {
                requestXmlPath = data.get(i).get(1);
            }
            if (data.get(i).get(0).toLowerCase().equals(AtfConstants.RESPONSECHECK)) {
                responseContains = data.get(i).get(1);
            }
            if (data.get(i).get(0).toLowerCase().equals(AtfConstants.SETREQUESTXML)) {
                setRequestXml = data.get(i).get(1);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("requestXml path is " + requestXmlPath);
        }
        if (setRequestXml != null) {
            requestXmlPath = replaceContentInRequestXml(setRequestXml, requestXmlPath);
        }
        try {
            SoapUtil soapReq = new SoapUtil(endpoint, userName, password, contentType);
            if (logger.isDebugEnabled())
                logger.debug("The request XML is " + AtfSupport.readFile(requestXmlPath));
            File f1 = new File(requestXmlPath);
            requestXmlPath = f1.getAbsolutePath();
            responseContent = soapReq.execute(AtfSupport.readFile(requestXmlPath));
        } catch (Exception e) {
            logger.error("Error executing the soap request");
            throw new RuntimeException(e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Soap response is " + responseContent.getBody());
        }
        if (!responseContains.isEmpty())
        Assert.assertTrue(responseContent.getBody().contains(responseContains));
        }
    }
