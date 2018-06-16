package com.motive.bdd.smp.internal;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.motive.bdd.core.rest.RestException;
import com.motive.bdd.core.rest.RestResponse;
import com.motive.bdd.core.rest.RestUtil;
import com.motive.bdd.smp.AtfSupport;
import com.motive.bdd.smp.PropertyManager;
import com.motive.bdd.smp.RestConstants;
import com.motive.bdd.spring.AtfConfiguration;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;

public class ServicePortalRest {

    static RestUtil restUtil = AtfConfiguration.getApplicationContext().getBean(RestUtil.class);
    RestResponse response;
    public static final Logger logger = LogManager.getLogger(ServicePortalRest.class);


    /**
     * Execute the rest api with the provided HTTP method and check whether the response has
     * the expected response code and values.
     * An example usage is:
     * <pre>
    Given RST execute "POST" "portalweb/rest/18.0/sites"
    *|setRequestXML|src/main/resources/apidocJson/siteDefinition.json|
    *|verifyStatus|200|
    *|verifyJsonResponse|metadata-metadata_info1=123|
    *|verifyResponseContains|30|
     * </pre>
     * <pre>
     * setRequestXML - the value for request body is placed in a location and path of location is set.
     * verifyJsonResponse - the expected value in response, the JSON object till the attribute have to
     * be specified with "-" as delimiter.
     * Example : To retrieve value of FollowOnTestName in response
     {
    *"name": "ssp",
    *"description": "Sample Self Service Portal",
    *"guid": "d2eb4f9a-37f1-48c7-bd52-9a540d640e3c",
    *"realm": "smp",
    *"sessionTimeoutMinutes": 30,
    *"pollingFrequencyMilliseconds": 3000,
    *"metadata": {
    * "metadata_info1": "123",
    *"metadata_info2": "abc"}
     * </pre>
     * metadata-metadata_info1=123 Note: all the verifyResponse contents in above
     * example is for above response.
     * @param httpMethod - GET/POST/PUT/DELETE whichever is required for execution must be passed as
     *        a input parameter
     * @param url - Relative URL for the api Should be passed.
     * @param dataInput necessary inputs in Cucumber Data table format.
     * @return
     */
    @Given("^RST execute \"([^\"]*)\" \"([^\"]*)\"$")
    public void rst_execute(String httpMethod, String url, DataTable dataInput)
            throws RestException, JSONException
    {
        List<String> outputJsonList = new ArrayList<String>();
        List<String> outputList = new ArrayList<String>();
        String expectedStatusCode = null;
        List<String> authentication = new ArrayList<String>();
        RestResponse rsp1 = null;
        String requestPath = null;
        List<List<String>> data = dataInput.raw();
        for (int i = 0; i < data.size(); i++) {
            switch (data.get(i).get(0).trim()) {
                case RestConstants.SETAUTHENTICATION:
                    authentication.add(data.get(i).get(1).trim());
                    break;
                case RestConstants.REQUESTINPUT:
                    requestPath = data.get(i).get(1).trim();
                    break;
                case RestConstants.VERIFYJSONRESPONSE:
                    outputJsonList.add(data.get(i).get(1).trim());
                    break;
                case RestConstants.VERIFYRESPONSECONATINS:
                    outputList.add(data.get(i).get(1).trim());
                    break;
                case RestConstants.VERIFYSTATUS:
                    expectedStatusCode = data.get(i).get(1).trim();
                    break;
                default:
                    logger.error(
                            "Datatable entry is not correct, it should be either setRequestValue or  verifyResponse");
            }
        }
        if (!authentication.isEmpty())
            for (int i = 0; i < authentication.size(); i++) {
                String[] str1 = authentication.get(i).trim().split(RestConstants.HYPHEN);
                restUtil.setRestUser(str1[0], str1[1]);
            }
        if (httpMethod.equals(RestConstants.POST)) {
                rsp1 = postExecution(url, requestPath);
                if (!((expectedStatusCode) == null)) {
                    checkStatusCode(rsp1, expectedStatusCode);
            }
            }
        if (httpMethod.equals(RestConstants.GET)) {
                rsp1 = getExecution(url, requestPath);
                if (!((expectedStatusCode) == null)) {
                    checkStatusCode(rsp1, expectedStatusCode);
                }
                }
        if (httpMethod.equals(RestConstants.PUT)) {
                rsp1 = putExecution(url, requestPath);
                if (!((expectedStatusCode) == null)) {
                    checkStatusCode(rsp1, expectedStatusCode);
            }
            }
        if (httpMethod.equals(RestConstants.DELETE)) {
                rsp1 = deleteExecution(url, requestPath);
                if (!(expectedStatusCode == null)) {
                checkStatusCode(rsp1, expectedStatusCode);
            }
        }
        boolean result = false;
        if (outputList.isEmpty() && outputJsonList.isEmpty()) {
            result = true;
        }
        if (!outputList.isEmpty()) {
            result = verifyResponseContains(rsp1, outputList);
        }
        if (!outputJsonList.isEmpty()) {
            result = verifyResponse(rsp1, outputJsonList);
            }
        if (!authentication.isEmpty()) {
        restUtil.setRestUser(AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_USER),
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_PASSWORD));
        }
        }

    public boolean verifyResponseContains(RestResponse rsp1, List<String> outputList) {
        boolean result = false;
        String[] str1 = outputList.get(0).trim().split(RestConstants.HYPHEN);
        for (int i = 0; i < str1.length; i++) {
            Assert.assertEquals(rsp1.toString().contains(str1[i]), true);
        }
        return result;
    }
    public void checkStatusCode(RestResponse rsp1, String expectedStatusCode) {
        Assert.assertEquals(rsp1.getStatusCode().toString(), expectedStatusCode);
    }
    public RestResponse postExecution(String arg2, String requestPath)
            throws RestException {
        File filePath = new File(requestPath);
        RestResponse getResponse = restUtil.executePost(arg2, filePath.getAbsolutePath());
        return getResponse;
    }
    public RestResponse putExecution(String url, String requestPath) throws RestException {
        String Path = requestPath;
        File filePath = new File(Path);
        RestResponse getResponse = restUtil.executePut(url, filePath.getAbsolutePath());
        return getResponse;
    }
    public RestResponse getExecution(String url, String requestPath) throws RestException {
        RestResponse response = null;
        if (requestPath == null) {
            RestResponse getResponse = restUtil.executeGet(url);
            return getResponse;
        }
        if (!(requestPath == null)) {
            RestResponse getResponse = restUtil.executeGet(url);
            return getResponse;
        }
        return response;
    }
    public RestResponse deleteExecution(String url, String requestPath) throws RestException {
        if (requestPath == null) {
            RestResponse getResponse = restUtil.executeDelete(url);
            return getResponse;
        }
        if (!(requestPath == null)) {
            String Path = requestPath;
            File filePath = new File(Path);
            RestResponse getResponse = restUtil.executeDelete(url, filePath.getAbsolutePath());
            return getResponse;
        }
        return response;
    }
    public boolean verifyResponse(RestResponse getResponse,
            List<String> outputJsonList) throws JSONException {
        String responseJson;
        int inputCharPos;
        boolean executionResult = false;
        inputCharPos = getResponse.toString().indexOf("{");
        responseJson =
                getResponse.toString().substring(inputCharPos, getResponse.toString().length());
        JSONObject jsonObj = new JSONObject(responseJson);
        for (int i = 0; i < outputJsonList.size(); i++) {
            String[] str1 = outputJsonList.get(i).trim().split(RestConstants.EQUAL);
            if (str1[0].trim().contains(RestConstants.HYPHEN)) {
                String tmpStr = str1[0].toString().trim();
                String[] tmpStr3 = tmpStr.split(RestConstants.HYPHEN);
                JSONArray tmpResult = null;
                JSONObject tmpValue = null;
                int finalPost = tmpStr3.length;
                if (finalPost == 2) {
                    verify: for (int arrVal = 0; arrVal < finalPost; arrVal++) {
                        tmpResult = (JSONArray) jsonObj.get(tmpStr3[0]);
for (int k = 0; k < tmpResult.length(); k++) {
    String tmpStr1 = tmpResult.getString(k);
                            if (tmpStr1.contains(tmpStr3[arrVal + 1]))
                            {
                                JSONObject innerJson = new JSONObject(tmpStr1);
                                String finalResult = (String) innerJson.get(tmpStr3[arrVal + 1]);
                                Assert.assertEquals(finalResult, str1[1].trim());
                                break verify;
                            }
                    }
                    }
                }
                if (finalPost == 3) {
                    verify: {
                    for (int arrVal = 1; arrVal < tmpStr3.length; arrVal++) {
                        tmpResult = (JSONArray) jsonObj.get(tmpStr3[0]);
                        for (int k = 0; k < tmpResult.length(); k++) {
                            String tmpStr1 = tmpResult.getString(k);
                            if (tmpStr1.contains(tmpStr3[arrVal]))
                            {
                                tmpValue = new JSONObject(tmpStr1);
                                    tmpResult = (JSONArray) tmpValue.get(tmpStr3[arrVal]);
                            int finalVal = arrVal + 1;
                            String finalResult = null;
                            for (int e = 0; e < tmpResult.length(); e++) {
                                String tempstr = tmpResult.getString(e);
                                if (tmpStr1.contains(tmpStr3[finalVal])) {
                                    tmpValue = new JSONObject(tempstr);
                                    finalResult = (String) tmpValue.get(tmpStr3[finalVal]);
                            Assert.assertEquals(finalResult, str1[1].trim());
                                            break verify;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!str1[0].trim().contains(RestConstants.HYPHEN)) {
                String finalResult = (String) jsonObj.get(str1[0]);
                Assert.assertEquals(finalResult, str1[1].trim());
            }
            }
        return executionResult;
    }

    @Given("^RST execute \"([^\"]*)\" for initialized site\"([^\"]*)\" with username\"([^\"]*)\"and password\"([^\"]*)\"$")
    public void rstExecuteForInitializedSiteWithUsernameAndPassword(String httpMethod, String Site,
            String username, String password)
            throws RestException, JSONException {
        restUtil.setRestUser(username, password);

        RestResponse Json = ServicePortalRest.getAuthenticationConfiguration(Site);
        JSONObject jsonObj = new JSONObject(Json);
        String finalResult = (String) jsonObj.get("smpSessionId");

        restUtil.setRestUser(AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_USER),
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_PASSWORD));
    }


    private static RestResponse InitializeSite(String Site) throws RestException {
        String URL = ("portalweb/rest/8.0/sites/" + Site + "/initializedsite");
        File filePath = new File("src/main/resources/apidocJson/initializeSite.json");
        RestResponse getResponse = restUtil.executePost(URL, filePath.getAbsolutePath());
        return getResponse;
    }

private static RestResponse getAuthenticationConfiguration(String Site) throws RestException {
        String URL = ("portalweb/rest/8.0/authenticationconfigurations?siteName=" + Site + "");
        RestResponse getResponse = restUtil.executeGet(URL);
        String URL2 = ("portalweb/rest/8.0/sites/" + Site + "/initializedsite");
        File filePath2 = new File("src/main/resources/apidocJson/initializeSite.json");
        RestResponse getResponse2 = restUtil.executePost(URL2, filePath2.getAbsolutePath());
        return getResponse2;
    }
@Given("^SMP check datatype for number column of table shoould be INT$")
public void smpCheckDatatypeForNumberColumnOfTableShoouldBeINT() throws SQLException  {
    SqlRowSet rs = AtfSupport.executeModelingQuery("select MAX(ID) from ECOMODEL where name='AccountStatus'");
    if (rs.next())
    {
        SqlRowSetMetaData rsmd =rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            Assert.assertEquals(rsmd.getColumnTypeName(i), "BIGINT");}
        }}
@Given("^SMP check datatype for date column of table should be TIMESTAMP$")
public void smpCheckDatatypeForDateColumnOfTableShouldBeTIMESTAMP() throws Throwable {
       SqlRowSet rs = AtfSupport.executeModelingQuery("select MAX(CREATIONDATE) from ECOMODEL where name='AccountStatus'");
       if (rs.next()) {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                Assert.assertEquals(rsmd.getColumnTypeName(i), "TIMESTAMP");
            }
        }
}
@Given("^SMP check datatype for deleted column of table should be TINYINT$")
public void smpCheckDatatypeForDeletedColumnOfTableShouldBeTINYINT() throws Throwable {
        SqlRowSet rs = AtfSupport.executeModelingQuery(
                "select DELETED from ACCOUNTS where EXTERNALACCOUNTID='ACCOUNT 12345'");
        if (rs.next()) {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                Assert.assertEquals(rsmd.getColumnTypeName(i), "TINYINT");
            }
        }
    }
@Given("^SMP check SIZE of columns have been increased in workflow step report table$")
public void smpCheckSIZEOfColumnsHaveBeenIncreasedInWorkflowStepReportTable() throws Throwable {
        SqlRowSet rs = AtfSupport.executeReportQuery(
                "SELECT STEP_NAME from WORKFLOW_STEP_REPORT where flow_name='probres'");
        int size = 65535;
        if (rs.next()) {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            Assert.assertEquals(rsmd.getColumnDisplaySize(i),size);
            } }}
@Given("^RST get dictionary details with process instance id$")
public void rstGetDictionaryDetailsWithProcessInstanceId() throws Throwable {
        SqlRowSet rs = AtfSupport.executeModelingQuery(
                "SELECT ID from WORKFLOW_EXECUTIONS where workflow_module='Startup'");
        int id = 0;
        if (rs.next()) {
            id = rs.getInt("ID");
            restUtil.setRestUser("workflowuser", "workflowuser1");
            String URL="/wfe/rest/7.0/instances/workflows/dictionarys?reloadDictionary=false";
            JSONObject jsonString = new JSONObject()
                    .put("id", "application")
                    .put("executionId", id);
  String json=jsonString.toString();
  String filepath="target/json";
  String path=AtfSupport.createFileWithContent(filepath, json);
  File newFile = new File(path);
RestResponse postResponse = restUtil.executePost(URL, newFile.getAbsolutePath());
  String ResponseCode = Integer.toString(postResponse.getstatusCode());
  Assert.assertEquals(ResponseCode, "200");
 String responseBody = postResponse.getBody();
 Assert.assertEquals(responseBody.contains("executionId"), true);
            if (logger.isDebugEnabled())
                logger.debug("Execution is successfull");
restUtil.setRestUser(AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_USER),AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REST_PASSWORD));}}}
