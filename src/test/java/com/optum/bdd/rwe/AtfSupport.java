/**
 * Copyright (c) Nokia 2017 All rights reserved.
 *
 * Nokia, Inc. Proprietary/Trade Secret ; Information Not to be disclosed or used except in
 * accordance with applicable agreements.
 */
package com.optum.bdd.rwe;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.jdbc.support.rowset.SqlRowSet;


import com.optum.bdd.core.db.DataBaseUtil;
import com.optum.bdd.core.remoteaccess.RemoteAcessUtil;
import com.optum.bdd.core.rest.RestUtil;
import com.optum.bdd.core.soap.SoapException;
import com.optum.bdd.core.soap.SoapResponse;
import com.optum.bdd.core.soap.SoapUtil;
import com.optum.bdd.rwe.spring.AtfConfiguration;

import cucumber.api.DataTable;

// TODO: Auto-generated Javadoc
/**
 * Provides general purpose static methods to classes that support
 * Cucumber based tests with the ATF.
 */
public class AtfSupport {

    /**
     * The maximum default load time in seconds.
     * This is used by Selenium to provide a
     * maximum time allowed to wait for an
     * element.
     */
    private static int maxLoadTime = 30;

    /** The Constant BUFFER_SIZE used for dealing with stream input. */
    private static final int BUFFER_SIZE = 4096;


    /** The logger. */
    private static Logger logger = Logger.getLogger(AtfSupport.class);

    /**
     * Wait for element to load with custom timeout.
     *
     * @param webElement the web element to wait on
     * @param driver the {@link WebDriver} in which the web element will be loaded
     * @param timeout the custom timeout value to override default value
     * @return true, if the web element is loaded within defined maxLoadTime time
     */
    public static boolean waitForElementToLoad(WebElement webElement, WebDriver driver,
            int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.visibilityOf(webElement));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Wait for {@link WebElement}.
     *
     * @param webElement the {@link WebElement} to wait on.
     * @param driver the {@link WebDriver}
     * @param timeout maximum time in seconds to wait for the element
     * @return true, if successful
     */
    public static boolean waitForElement(WebElement webElement, WebDriver driver, int timeout) {
//        Assert.assertTrue(waitForElementToLoad(webElement, driver, timeout),
//                "failed to load: " + webElement);
      Assert.assertTrue(waitForElementToLoad(webElement, driver, timeout));
        return true;
    }

    /**
     * Wait for element to load with default timeout of {@link #maxLoadTime}.
     *
     * @param webElement - the web element for which the {@link WebDriver} have to wait
     * @param driver - the {@link WebDriver} in which the web element will be loaded
     * @return true, if the web element is loaded within defined maxLoadTime time
     */
    public static boolean waitForElementToLoad(WebElement webElement, WebDriver driver) {
        return waitForElementToLoad(webElement, driver, maxLoadTime);
    }

    /**
     * Wait for {@link WebElement} to be click enabled.
     *
     * @param webElement - the web element for which the {@link WebDriver} have to wait
     * @param driver - the {@link WebDriver} in which the web element will be loaded
     * @return true, if the web element is clickable within defined maxLoadTime time
     */
    public static boolean waitForElementClickable(WebElement webElement, WebDriver driver) {
        return waitForElementClickable(webElement, driver, maxLoadTime);
    }

    /**
     * Wait for a {@link WebElement} to be available and in a clickable state.
     *
     * @param webElement the web element
     * @param driver the {@link WebDriver}
     * @param timeToWait the time in seconds to wait
     * @return true, if successful
     */
    public static boolean waitForElementClickable(WebElement webElement, WebDriver driver,
            int timeToWait) {

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Waiting for :" + webElement);
            }
            WebDriverWait wait = new WebDriverWait(driver, timeToWait);
            wait.until(ExpectedConditions.elementToBeClickable(webElement));
            return true;
        } catch (TimeoutException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed waiting for: " + webElement);
            }
            return false;
        }
    }

    /**
     * Sets the clipboard data from the web application being tested.
     *
     * @param string the new clipboard data
     */
    public static void setClipboardData(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    /**
     * Execute List of commands on a remote SMP server. This will use the
     * values:
     * <p>
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_USER}
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_HOST}
     * </ol>
     * <p>
     * as the login credentials and machine on which to execute
     * the commands.
     *
     * @param commandList - List of commands that needs to be executed in remote machine
     * @return Output of the screen after the commands executed as String
     * @throws Exception the exception
     */
    public static String executeCommandsOnSmpServer(List<String> commandList) throws Exception {
        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_PASSWORD);
        String host = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_HOST);
        String newcommand = "";
        for (int i = 0; i < commandList.size(); i++) {
            if (i == commandList.size() - 1) {
                newcommand = newcommand + commandList.get(i);
            } else {
                newcommand = newcommand + commandList.get(i) + " && ";
            }
        }
        String result =
                new RemoteAcessUtil(userName, password, host).executeCommand(newcommand, 200);

        return result;
    }

    /**
     * Transfer files to a remote SMP server. This will use the
     * values:
     * <p>
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_USER}
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_RMT_MACHINE_HOST}
     * <li>{@link PropertyManager#BDD_SMP_REMOTE_DIR_PATH}
     * </ol>
     * <p>
     * as the login credentials and machine on which to execute
     * the commands. BDD_SMP_REMOTE_DIR_PATH is used for destination in remote machine.
     *
     * @param files - List of files that needs to be transfered to remote machine
     * @throws Exception the exception
     */
    public static String transferFilesToSmpServer(String[] files)
            throws Exception {
        String result = "";
        String remoteDirectoryPath = getRemoteDirectoryPath();
        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_PASSWORD);
        String host = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_HOST);
        for (String j : files) {
            String path = new File(j).getAbsolutePath();
            result =
                    new RemoteAcessUtil(userName, password, host).sendFile(
                            path, 50, remoteDirectoryPath);
        }
        return result;
    }

    /**
     * Checks if the input list sorted in ascending order.
     *
     * @param list the list
     * @return true, if is list sorted in ascending order
     */
    public static boolean isListSortedInAscendingOrder(List<String> list) {

        boolean isInAscendingOrder = false;
        String prev = list.get(0);
        String current = list.get(1);
        for (int i = 0; i < list.size(); i++) {

            if (prev.compareToIgnoreCase(current) <= 0) {
                prev = current;
                if (i != list.size() - 1) {
                    current = list.get(i + 1);
                }

                isInAscendingOrder = true;
                continue;
            } else {
                isInAscendingOrder = false;
                break;
            }

        }

        return isInAscendingOrder;

    }

    /**
     * Execute given query in SMP database. This requires the following
     * values to be set:
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_USER}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_URL}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_TYPE}
     * </ol>
     *
     * @param query - the sqlplus query as String
     * @return the sql row set
     */
    public static SqlRowSet executeModelingQuery(String query) {

        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_PASSWORD);
        String url = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_URL);
        String client =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_TYPE);
        DataBaseUtil databaseUtil = new DataBaseUtil(userName, password, url, client);
        SqlRowSet rs = databaseUtil.executeQuery(query);
        return rs;
    }


    /**
     * Execute query on reporting DB. This requires the following
     * values to be set:
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_USER}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_URL}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_TYPE}
     * </ol>
     *
     * @param query the query
     * @return the sql row set
     */
    public static SqlRowSet executeReportQuery(String query) {

        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_PASSWORD);
        String url = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_URL);
        String client =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_TYPE);
        DataBaseUtil databaseUtil = new DataBaseUtil(userName, password, url, client);
        SqlRowSet rs = databaseUtil.executeQuery(query);
        return rs;
    }

    /**
     * Description : This method is to update the table with sql query in modeling schema. This
     * requires the following
     * values to be set:
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_USER}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_URL}
     * <li>{@link PropertyManager#BDD_SMP_MOD_DATABASE_TYPE}
     * </ol>
     *
     * @param query : Sql query to be executed (Eg. update ACCOUNTS).
     * @return SqlRowSet : SqlRowset having result of executing above sql query which can be
     *         used to get desired result.
     */


    public static String updateModelingQuery(String query) {

        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_PASSWORD);
        String url = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_URL);
        String client =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_MOD_DATABASE_TYPE);
        DataBaseUtil databaseUtil = new DataBaseUtil(userName, password, url, client);
        String rs = databaseUtil.UpdateQuery(query);
        return rs;
    }

    /**
     * Description : This method is to update the table with sql query in reporting schema.
     * This requires the following
     * values to be set:
     * <ol>
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_USER}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_URL}
     * <li>{@link PropertyManager#BDD_SMP_REP_DATABASE_TYPE}
     * </ol>
     *
     * @param query : Sql query to be executed (Eg. update ACCOUNTS).
     * @return SqlRowSet : SqlRowset having result of executing above sql query which can be
     *         used to get desired result.
     */
    public static String updateReportQuery(String query) {

        String userName =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_PASSWORD);
        String url = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_URL);
        String client =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REP_DATABASE_TYPE);
        DataBaseUtil databaseUtil = new DataBaseUtil(userName, password, url, client);
        String rs = databaseUtil.UpdateQuery(query);
        return rs;
    }


    /**
     * Returns true if the {@link WebElement} is currently displayed
     * and false otherwise.
     *
     * @param webElement the web element
     * @return true, if is element present
     */
    public static boolean isElementPresent(WebElement webElement) {
        try {
            webElement.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isElementPresent(WebElement webElement, WebDriver driver, int timeOut) {
        try {
            webElement.getText();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the {@link WebElement} is currently not displayed
     * and false otherwise.
     *
     * @param webElement the web element
     * @return true, if is element not present
     */
    public static boolean isElementNotPresent(WebElement webElement) {
        try {
            webElement.isDisplayed();
            throw new RuntimeException();
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    /**
     * Checks if is list sorted in descending order.
     *
     * @param list the list
     * @return true, if is list sorted in descending order
     */
    public static boolean isListSortedInDescendingOrder(List<String> list) {

        boolean isInDescendingOrder = false;

        String prev = list.get(0);
        String current = list.get(1);
        for (int i = 0; i < list.size(); i++) {

            if (prev.compareToIgnoreCase(current) >= 0) {
                prev = current;
                if (i != list.size() - 1) {
                    current = list.get(i + 1);
                }

                isInDescendingOrder = true;
                continue;
            } else {
                isInDescendingOrder = false;
                break;
            }

        }

        return isInDescendingOrder;

    }

    /**
     * Gets the alert text
     * 
     * @param driver the {@link WebDriver}
     * @return the alert text
     */
    public static String getAlertText(WebDriver driver) {
        waitForMilliSeconds(2000);
        Alert alert = driver.switchTo().alert();
        return alert.getText();

    }

    /**
     * Click ok button in alert window.
     *
     * @param driver the {@link WebDriver}
     */
    public static void clickAlert(WebDriver driver) {
        waitForMilliSeconds(2000);
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    /**
     * Dismiss the alert window.
     *
     * @param driver the {@link WebDriver}
     */
    public static void alertDismiss(WebDriver driver) {
        Alert alert = driver.switchTo().alert();
        alert.dismiss();
    }

    /**
     * Mouse over the {@link WebElement}.
     *
     * @param element the element
     * @param driver the {@link WebDriver}
     */
    public static void mouseOver(WebElement element, WebDriver driver) {
        JavascriptExecutor js = null;
        if (driver instanceof JavascriptExecutor) {
            js = (JavascriptExecutor) driver;

            String mouseOverScript =
                    "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
            js.executeScript(mouseOverScript, element);
        } else {
            logger.error("Not able to do mouse over action");
        }

    }

    /**
     * Double click over the {@link WebElement}.
     *
     * @param element the element
     * @param driver the {@link WebElement}.
     */
    public static void doubleClick(WebElement element, WebDriver driver) {
        JavascriptExecutor js = null;
        if (driver instanceof JavascriptExecutor) {
            js = (JavascriptExecutor) driver;

            String doubleClick =
                    "if(document.createEvent){var evtObj = document.createEvent('MouseEvents');evtObj.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null); arguments[0].dispatchEvent(evtObj);}";
            js.executeScript(doubleClick, element);
        } else {
            logger.error("Not able to do double click action");
        }

    }

    /**
     * Unzip the zip file specified by the zipFilePath argument into
     * the directory specified by the destinationDirectory argument.
     *
     * @param zipFilePath the zip file path
     * @param destinationDirectory the dest directory
     */
    public static void unZip(String zipFilePath, String destinationDirectory) {

        File destDir = new File(destinationDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn;
        try {
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ZipEntry entry;
        try {
            entry = zipIn.getNextEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (zipIn != null) {
                try {
                    zipIn.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destinationDirectory + File.separator + entry.getName();
            File newfile = new File(filePath);
            new File(newfile.getParent()).mkdirs();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();

            }
            try {
                zipIn.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                entry = zipIn.getNextEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            zipIn.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract file.
     *
     * @param zipIn the zip in
     * @param filePath the file path
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
            bos.close();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Create Zip file from the contents available under specified folder.
     *
     * @param folder the folder where contents to zip are present
     * @param zipFilePath the file path where resulting zip file have to be placed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void zipContents(final Path folder, final Path zipFilePath) throws IOException {

        RemoteAcessUtil.zipContents(folder, zipFilePath);

    }

    /**
     * Delete directory.
     *
     * @param directory the directory
     * @return true, if successful
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    /**
     * Gets the file names in folder.
     *
     * @param folderpath the folderpath
     * @param filenames the filenames
     * @return the file names in folder
     */
    public static ArrayList<String> getFileNames(String folderpath,
            ArrayList<String> filenames) {
        File foldername = new File(folderpath);
        if (foldername.exists()) {
            File[] files = foldername.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFileNames(files[i].getAbsolutePath(), filenames);
                } else {
                    filenames.add(files[i].getName());
                }
            }
        }

        return (filenames);

    }

    /**
     * Checks if is string contains all expected values.
     *
     * @param rbody the rbody
     * @param expectedArray the expected array
     * @return true, if is string contains all expected values
     */
    public static boolean isStringContainsAllExpectedValues(String rbody,
            String[] expectedArray) {
        boolean flag = false;

        for (int i = 0; i < expectedArray.length; i++) {

            if (rbody.contains(expectedArray[i])) {
                flag = true;
                continue;
            } else {
                flag = false;
                break;
            }

        }
        return flag;
    }

    /**
     * Gets the folder names in folder.
     *
     * @param folderpath the folderpath
     * @param foldernames the foldernames
     * @return the folder names in folder
     */
    public static ArrayList<String> getListOfFolderNames(String folderpath,
            ArrayList<String> foldernames) {
        File foldername = new File(folderpath);

        if (foldername.exists()) {
            File[] files = foldername.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    foldernames.add(files[i].getName());
                } else {
                    continue;
                }
            }
        }

        return (foldernames);

    }

    /**
     * Return a {@link RestUtil}.
     *
     * @return the singelton {@link RestUtil}
     */
    public static RestUtil getRestUtility() {
        return AtfConfiguration.getApplicationContext().getBean(RestUtil.class);
    }

    /**
     * Drag and drop.
     *
     * @param source the source
     * @param driver the {@link WebDriver}
     */
    public static void dragAndDrop(WebElement source, WebDriver driver) {
        JavascriptExecutor js = null;

        if (driver instanceof JavascriptExecutor) {
            js = (JavascriptExecutor) driver;

            js.executeScript(
                    "function simulate(f,c,d,e){var b,a=null;for(b in eventMatchers)if(eventMatchers[b].test(c)){a=b;break}if(!a)return!1;document.createEvent?(b=document.createEvent(a),a==\"HTMLEvents\"?b.initEvent(c,!0,!0):b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null),f.dispatchEvent(b)):(a=document.createEventObject(),a.detail=0,a.screenX=d,a.screenY=e,a.clientX=d,a.clientY=e,a.ctrlKey=!1,a.altKey=!1,a.shiftKey=!1,a.metaKey=!1,a.button=1,f.fireEvent(\"on\"+c,a));return!0} var eventMatchers={HTMLEvents:/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/}; "
                            +
                            "simulate(arguments[0],\"mousedown\",0,0); simulate(arguments[0],\"mousemove\",arguments[1],arguments[2]); simulate(arguments[0],\"mouseup\",arguments[1],arguments[2]); ",
                    source, 500, 500);
        } else {
            logger.error("Not able to do drag and drop");
        }
    }

    /**
     * Wait for a button to become visible and when visible click it.
     *
     * @param driver the {@link WebDriver}
     * @param secondsToWait the seconds to wait for condition to be met
     * @param condition the condition
     * @param message the message to log if condition is met
     * @return true, if successful
     */
    public static boolean waitForButtonToClick(WebDriver driver, int secondsToWait, By condition,
            String message) {
        boolean buttonFoundAndClicked = false;
        try {
            WebElement button = waitForCondition(driver, secondsToWait, condition);
            if (button != null) {
                button.click();
                if (message != null) {
                    buttonFoundAndClicked = true;
                }
            }
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Proceeding after failing condition: " + condition);
            }
        }
        if (!buttonFoundAndClicked) {
            logger.info(message);
        }
        return buttonFoundAndClicked;
    }

    /**
     * Wait up to secondsToWait for the regExText to be seen in the client. The regExText parameter
     * name implies that the regExText can be a regular expression. Note that this method looks for
     * all text available using {@link WebDriver#getPageSource()}. The implication is that text that
     * may not be visible on a browser might be found because the 'getPageSource' returns the raw
     * text that is rendered by the browser.
     *
     * @param driver the {@link WebDriver}
     * @param regExText a regular expression
     * @param secondsToWait the seconds to wait
     */
    public static void waitForText(WebDriver driver, String regExText, int secondsToWait) {
        Pattern p = Pattern.compile(regExText);
        Calendar future = Calendar.getInstance();
        future.add(Calendar.SECOND, secondsToWait);
        while (true) {
            Calendar now = Calendar.getInstance();
            if (now.getTime().getTime() < future.getTime().getTime()) {
                Matcher m = p.matcher(driver.getPageSource());
                if (m.find()) {
                    return;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            } else {
                throw new RuntimeException("text: \"" + regExText + "\" was not found");
            }
        }
    }


    /**
     * Wait for element named "form" to be available and then submit.
     *
     * @param driver the {@link WebDriver}
     * @param secondsToWait the seconds to wait
     */
    public static void submitForm(WebDriver driver, int secondsToWait) {
        try {
            waitForCondition(driver, secondsToWait, By.tagName("form")).submit();
        } catch (Throwable e) {
            logger.warn("Could not find form", e);
        }
    }

    /**
     * Wait for the {@link By} condition to be met. When met it will return the link
     * {@WebElement} it was waiting for. If the wait time is exceeded it will return null.
     *
     * @param driver the {@link WebDriver}
     * @param secondsToWait the seconds to wait
     * @param condition the condition
     * @return the web element
     */
    private static WebElement waitForCondition(WebDriver driver, int secondsToWait, By condition) {
        WebDriverWait wait = new WebDriverWait(driver, secondsToWait);
        WebElement webElement =
                wait.until(ExpectedConditions.visibilityOfElementLocated(condition));
        return webElement;
    }

    /**
     * Wait for the specified number of seconds multiplied by
     * {@link PropertyManager#BDD_SMP_WEBDRIVER_COMPENSATION_FACTOR}.
     *
     * @param seconds the seconds
     */
    public static void waitForSeconds(int seconds) {
        waitForMilliSeconds(seconds * 1000);
    }

    /**
     * Wait for the specified number of milliseconds multiplied by
     * {@link PropertyManager#BDD_SMP_WEBDRIVER_COMPENSATION_FACTOR}.
     *
     * @param milliseconds the milliseconds
     * @return true, if successful
     */
    public static boolean waitForMilliSeconds(int milliseconds) {
        try {
            Thread.sleep((int) (milliseconds * getCompensationFactor()));
        } catch (Throwable t) {
            throw new RuntimeException("Wait time exceeded", t);
        }
        return true;
    }

    /**
     * Generate attribute string to append to a {@link URL}. The dataTable argument should contain
     * name / value pairs.
     *
     * @param dataTable the data table
     * @return the attributes to append to a URL
     */
    public static String generateAttribute(DataTable dataTable) {
        StringBuilder inputList = new StringBuilder();
        for (List<String> input : dataTable.raw()) {
            if (input.size() != 2) {
                throw new RuntimeException("dataTable input was not based on name value pairs");
            }
            inputList.append("&");
            inputList.append(input.get(0));
            inputList.append("=");
            inputList.append(input.get(1));
        }
        return inputList.toString();
    }

    /**
     * Gets the compensation factor.
     *
     * @return the compensation factor
     */
    private static double getCompensationFactor() {
        double compensationFactor = 1;
        try {
            compensationFactor = new Double(AtfConfiguration
                    .getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_COMPENSATION_FACTOR))
                            .doubleValue();
        } catch (Throwable t) {
            // use default value
        }
        return compensationFactor;
    }

    /**
     * Take a screenshot and save to file specified by 'savePath' parameter. While this is useful
     * for debugging the screenshot is typically not rendered as it would be seen during the test.
     * See {@link TakesScreenshot#getScreenshotAs(OutputType)} for details. In particular note that
     * it is a 'best effort' attempt to take a screenshot.
     *
     * @param driver the {@link WebDriver}
     * @param savePath the path to the file to save the screenshot
     */
    public static void takeScreenshot(WebDriver driver, String savePath) {
        try {
            if (driver != null) {
                if (driver instanceof TakesScreenshot) {
                    File saveDirectory = new File(savePath).getParentFile();
                    if (!saveDirectory.isDirectory() && !saveDirectory.mkdirs()) {
                    Assert.assertTrue("Cannot use directory: " + saveDirectory.getAbsolutePath(), saveDirectory.mkdirs());
                    }
                    File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    File saveFile = new File(savePath);
                    if (saveFile.exists()) {
                        saveFile.delete();
                    }
                    FileUtils.moveFile(screenShot, new File(savePath));
                    logger.info("Screen shot saved to: " + screenShot.getAbsolutePath());
                } else {
                    logger.info("Driver: " + driver + " does not support taking screenshots");;
                }
            }
        } catch (Throwable e) {
            logger.error(e);
        }
    }

    /**
     * Read content of the file.
     *
     * @param filePath - the absolute file path
     * @return the string - content of the file as string
     */
    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fin = new FileInputStream(filePath);
            InputStreamReader in = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(in);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return sb.toString();
    }

    /**
     * Execute a north bound interface (NBI) request.
     * This requires the following properties be set:
     * <p>
     * <ul>
     * <li>{@link PropertyManager#BDD_SMP_NBI_ENDPOINT}
     * <li>{@link PropertyManager#BDD_SMP_NBI_USER}
     * <li>{@link PropertyManager#BDD_SMP_NBI_PASSWORD}
     * <li>{@link PropertyManager#BDD_SMP_NBI_CONTENTTYPE}
     * </ul>
     *
     * @param requestXMLFilePath the path to an XML request file specifying a SOAP request
     * @return the SOAP response
     * @throws SoapException the soap exception
     */

    public static SoapResponse executeNBIService(String requestXMLFilePath) throws SoapException {

        String endpoint =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_NBI_ENDPOINT);
        String username =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_NBI_USER);
        String password =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_NBI_PASSWORD);
        String contentType =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_NBI_CONTENTTYPE);
        String requestFile = requestXMLFilePath;
        SoapUtil soapUtil = new SoapUtil(endpoint, username, password, contentType);
        SoapResponse response = soapUtil.executeRequest(requestFile);
        return response;


    }

    /**
     * Generate a random number.
     * 
     * @return a random number
     */
    public static int generateRandomNumber() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    /**
     * Verifies response body contains all expected values.
     *
     * @param responseBody the response body
     * @param expectedValues {@linkplain String} array of expected values
     * @return true, if is response body contains all expected values
     */
    public static boolean isResponseContainsAllExpectedValues(String responseBody,
            String[] expectedValues) {
        boolean result = true;
        for (String expectedValue : expectedValues) {
            if (!responseBody.contains(expectedValue))
                result = false;
            break;
        }
        return result;
    }

    /**
     * Switch to the window with the provided window handle.
     *
     * @param title the window title
     * @param driver the driver
     * @return true, if successfully switched to child window with provided title
     */
    public static boolean switchToWindow(String title, WebDriver driver) {

        String parentWindowhandler = null;
        try {
            parentWindowhandler = driver.getWindowHandle();
        } catch (NoSuchWindowException e) {
            logger.error("There is no window available to switch");
        }
        boolean childWindowFlag = false;
        for (String Child_Window : driver.getWindowHandles()) {
            driver.switchTo().window(Child_Window);
            if (driver.getTitle().toString().isEmpty()) {
                childWindowFlag = true;
                break;
            }
            if (driver.getTitle().contains(title)) {
                childWindowFlag = true;
                break;
            }
        }
        if (!childWindowFlag) {
            driver.switchTo().window(parentWindowhandler);
            logger.error("The window having title " + title + " is not available");
        }
        return childWindowFlag;
    }

    /**
     * Gets the download directory path specified by {@link PropertyManager#BDD_SMP_DOWNLOAD_DIR}.
     * If a value is not specified "target/downloads" is returned.
     *
     * @return the download directory path
     */
    public static String getDownloadDirectoryPath() {
        String downloadDir =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_DOWNLOAD_DIR);
        if (downloadDir == null) {
            downloadDir = "target" + File.separator + "downloads" + File.separator;
        }
        File tmpDir = new File(downloadDir);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return downloadDir;
    }

    /**
     * Creates the file with provided content and file name.
     *
     * @param filePath the filePath
     * @param content the content
     * @return true, if successful
     */
    public static String createFileWithContent(String filePath, String content) {
        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(content);
            out.close();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return filePath;
    }

    /**
     * Checks if text is present on current page.
     *
     * @param text the text
     * @param driver the driver
     * @param waitSeconds the wait seconds
     * @return true, if text is present on current page
     */
    public static boolean isTextPresentOnPage(String text, WebDriver driver, int waitSeconds) {
        try {
            waitForText(driver, text, waitSeconds);
            return driver.findElement(By.xpath("//*[text()='" + text + "']")).isDisplayed();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Login to all application through key cloak
     * 
     * @param username:username of keycloak application
     * @param password:password of keycloak application
     * @param driver
     * @return true if key cloak login is successful
     */
    public static boolean keyCloakAuthentication(String username, String password,
            WebDriver driver) {
        try {
            WebElement name = driver.findElement(By.id("target2"));
            AtfSupport.waitForElementToLoad(name, driver);
            name.clear();
            name.sendKeys(username);
            WebElement wepassword = driver.findElement(By.id("login-password"));
            AtfSupport.waitForElementToLoad(wepassword, driver);
            wepassword.clear();
            wepassword.sendKeys(password);
            WebElement logIn = driver.findElement(By.id("sbtbtn"));
            AtfSupport.waitForElementToLoad(logIn, driver);
            logIn.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * By default isKeyCloakEnabled set to true
     * If it's set to true keycloak authentication method is called otherwise native login method is
     * called
     * 
     * @return
     * @throws Exception
     */
    public static boolean isKeyCloakEnabled() {
        boolean flag = true;
        String isKeyCloakEnabled =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_KEYCLOAK_ENABLED);
        try {
            if (null != isKeyCloakEnabled) {
                Boolean isKeyCloakEnabledValue = Boolean.parseBoolean(isKeyCloakEnabled);
                if (!isKeyCloakEnabledValue) {
                    flag = false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    /**
     * To check keycloak username text box is displaying or not.
     *
     * @return true, if username text is displayed
     */
    public static boolean isKeyCloakUserExists(WebDriver driver) {
        try {
            WebElement name = driver.findElement(By.id("target2"));
            boolean keyCloakUserExists = isElementPresent(name);
            if (keyCloakUserExists)
                return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    public static void refresh(WebDriver driver) {
        driver.navigate().refresh();
    }

    public static void waitAndClick(WebElement element, WebDriver driver) {
        waitAndClick(element, driver, maxLoadTime);
    }

    public static void waitAndClick(WebElement element, WebDriver driver,
            int timeToWait) {
        try {
            waitForElementClickable(element, driver, timeToWait);
            element.click();
        } catch (Exception e) {
            logger.error("Unable to click " + element);
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify sent value's success.
     *
     * @param element is the element in which sent value has to be verified
     * @param value is the value which is to verified
     */
    public static void verifySentValue(WebElement element, String value) {
        String actual = element.getAttribute("value");
        if (actual.equals(value)) {
            if (logger.isDebugEnabled()) {
                logger.debug(value + " Value sent in WebElement: " + element);
            }
        } else {
            logger.error("Unable to send value: " + value + " in WebElement: " + element);
            throw new RuntimeException(
                    "Unable to send value: " + value + " in WebElement: " + element);
        }
    }

    /**
     * Gets the remote directory path specified by {@link PropertyManager#BDD_SMP_REMOTE_DIR_PATH}.
     * If a value is not specified "/home/${bdd.smp.rmt.machine.user}/remoteExecution" is returned.
     *
     * @return the download directory path
     */
    public static String getRemoteDirectoryPath() {
        String remoteDirPath =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_REMOTE_DIR_PATH);
        if (remoteDirPath == null) {
            String remoteMachineUser =
                    AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_RMT_MACHINE_USER);
            remoteDirPath =
                    "/home/" + remoteMachineUser + "/remoteExecution";
        }
        return remoteDirPath;
    }
}
