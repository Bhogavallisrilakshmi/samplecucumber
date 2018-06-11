package com.optum.bdd.core.selenium;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



/**
 * This Class contains methods written which would be specific to the requirements of HomeView-
 * template
 * project. The methods are like an added functionality to the Selenium Web Driver Framework.
 *
 * @author Mohan
 *
 */

public class WebDriverUtils {

    Logger logger = Logger.getLogger(WebDriverUtils.class);
    public static final String FF_WEBDRIVER_PATH = "src/test/resources/drivers/geckodriver.exe";
    public static final String CHROME_WEBDRIVER_PATH = "src/test/resources/drivers/chromedriver.exe";
    public static final String IE_WEBDRIVER_PATH = "src/test/resources/drivers/IEDriverServer.exe";
    public static final String HEADLESS = "bdd.enable.headless";
    public static final String WEBDRIVER = "com.motive.bdd.core.selenium.webdriver";
    String browser = "";
    String operatingSystem = "";
    String remoteHostIP = "";
    String remoteHostPort = "";
    boolean useRemoteWebDriver = false;


    public WebDriverUtils(String browser, String operatingSystem, String remoteHostIP,
            String remoteHostPort, boolean useRemoteWebDriver) {

        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.remoteHostIP = remoteHostIP;
        this.remoteHostPort = remoteHostPort;
        this.useRemoteWebDriver = useRemoteWebDriver;

    }

    /**
     * @return the browser type
     */
    public String getBrowser() {
        return browser;
    }


    /**
     * @param Browser : Browser type of the browser to set
     *
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * @return the operatingSystem type
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }


    /**
     * @param type of the operatingSystem to set
     */
    public void setOperatingSysytem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * @return Port of the remote host where selenium server running
     */
    public String getRemoteHostPort() {
        return remoteHostPort;
    }


    /**
     * @param Port of the remote host where selenium server running
     */
    public void setRemoteHostPort(String remoteHostPort) {
        this.remoteHostPort = remoteHostPort;
    }

    /**
     * @return Ip address of the remote host where selenium server running
     */
    public String getRemoteHostIP() {
        return remoteHostIP;
    }


    /**
     * @param Ip address of the remote host where selenium server running
     */
    public void setRemoteHostIP(String remoteHostIP) {
        this.remoteHostIP = remoteHostIP;
    }

    /**
     * @return boolean value indicates whether selenium server is running in remote machine
     */
    public boolean getUseRemoteWebDriver() {
        return useRemoteWebDriver;
    }


    /**
     * @param set value to indicates whether selenium server is running in remote machine
     */
    public void setUseRemoteWebDriver(boolean useRemoteWebDriver) {
        this.useRemoteWebDriver = useRemoteWebDriver;
    }


    /**
     * @param browser : Browser under Test. For eg., if the flow is launched in Firefox then value
     *        is "FF".
     * @return WebDriver for the Browser under Test / null in case of failure.
     * @throws MalformedURLException
     */
    public RemoteWebDriver getDriver(String baseURL) throws MalformedURLException {

        return getDriver(baseURL, true);

    }

    /**
     * @param browser : Browser under Test. For eg., if the flow is launched in Firefox then value
     *        is "FF".
     * @param browserProfile : firefox profile object (i.e.) If the firefox browser have to be
     *        launched
     *        with a specific profile then the profile object have to be sent.
     * @param enableBrowserLog : boolean to
     * @return WebDriver for the Browser under Test / null in case of failure.
     * @throws MalformedURLException
     */
    @SuppressWarnings("deprecation")
	public RemoteWebDriver getDriver(String baseURL, 
            boolean enableBrowserLog)
            throws MalformedURLException {
        logger.debug("********************** WebDriver-INITIALIZED *******************************************");
        RemoteWebDriver driver = null;
        DesiredCapabilities capability = null;

        OperatingSystem os = OperatingSystem.valueOf(operatingSystem.toUpperCase());
        Browsers br = Browsers.valueOf(browser.toUpperCase());
        switch (br) {

            case FIREFOX:
                capability = DesiredCapabilities.firefox();
                capability.setBrowserName("firefox");
                capability.setCapability(FirefoxDriver.PROFILE, getFirefoxProfiles());
                capability.setCapability("marionette", true);
                if (os.toString().equals("WINDOWS")) {
                    capability.setPlatform(Platform.WINDOWS);
                    setDriverSystemProperty("webdriver.gecko.driver",FF_WEBDRIVER_PATH);
                }
                if (os.toString().equals("MAC")) {
                    capability.setPlatform(Platform.MAC);
                    setDriverSystemProperty("webdriver.gecko.driver",FF_WEBDRIVER_PATH);
                }
                if (os.toString().equals("LINUX")) {
                    capability.setPlatform(Platform.LINUX);
                    setDriverSystemProperty("webdriver.gecko.driver",FF_WEBDRIVER_PATH);
                }
                if (useRemoteWebDriver) {
                    driver = new RemoteWebDriver(
                            new URL("http://" + remoteHostIP + ":" + remoteHostPort + "/wd/hub"),
                            capability);
                }
                if (enableBrowserLog) {
                    LoggingPreferences logs = new LoggingPreferences();
                    logs.enable(LogType.BROWSER, Level.ALL);
                    logs.enable(LogType.CLIENT, Level.ALL);
                    logs.enable(LogType.DRIVER, Level.ALL);
                    logs.enable(LogType.PERFORMANCE, Level.ALL);
                    logs.enable(LogType.PROFILER, Level.ALL);
                    logs.enable(LogType.SERVER, Level.ALL);
                    capability.setCapability(CapabilityType.LOGGING_PREFS, logs);
                    driver = new FirefoxDriver(capability);
                }else {
                    driver = new FirefoxDriver(capability);
                }
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                driver.get(baseURL);
                break;
            case IE:
                capability = DesiredCapabilities.internetExplorer();
                capability.setBrowserName("internet explorer");
                capability.setCapability("acceptSslCerts", true);
                capability.setCapability("ignoreProtectedModeSettings", true);
                capability.setCapability("unexpectedAlertBehaviour", "accept");
                capability.setCapability("AcceptUntrustedCertificates", true);
                capability.setPlatform(Platform.WINDOWS);
                setDriverSystemProperty("webdriver.ie.driver",IE_WEBDRIVER_PATH);
                if (useRemoteWebDriver) {
                    driver = new RemoteWebDriver(
                            new URL("http://" + remoteHostIP + ":" + remoteHostPort + "/wd/hub"),
                            capability);
                } else {
                    driver = new InternetExplorerDriver(capability);
                }
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                driver.get(baseURL);
                if (enableBrowserLog) {
                    LogEntries jserrorsB, jserrorsC, jserrorsD, jserrorsPer, jserrorsPro, jserrorsS;
                    jserrorsB = driver.manage().logs().get(LogType.BROWSER);
                    for (LogEntry error : jserrorsB) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }

                    jserrorsC = driver.manage().logs().get(LogType.CLIENT);

                    for (LogEntry error : jserrorsC) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }

                    jserrorsD = driver.manage().logs().get(LogType.DRIVER);

                    for (LogEntry error : jserrorsD) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }

                    jserrorsPer = driver.manage().logs().get(LogType.PERFORMANCE);

                    for (LogEntry error : jserrorsPer) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }

                    jserrorsPro = driver.manage().logs().get(LogType.PROFILER);

                    for (LogEntry error : jserrorsPro) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }

                    jserrorsS = driver.manage().logs().get(LogType.SERVER);

                    for (LogEntry error : jserrorsS) {
                        logger.debug(error.getLevel());
                        logger.debug(error.getMessage());
                        logger.debug(error.getClass());
                        logger.debug(error.getTimestamp());
                    }
                }
                break;
            case CHROME:
                capability = DesiredCapabilities.chrome();
                if (os.toString().equals("WINDOWS")) {
                    capability.setPlatform(Platform.WINDOWS);
                    setDriverSystemProperty("webdriver.chrome.driver",CHROME_WEBDRIVER_PATH);
                }
                if (os.toString().equals("MAC")) {
                    capability.setPlatform(Platform.MAC);
                    setDriverSystemProperty("webdriver.chrome.driver",CHROME_WEBDRIVER_PATH);
                }
                if (os.toString().equals("LINUX")) {
                    capability.setPlatform(Platform.LINUX);
                    setDriverSystemProperty("webdriver.chrome.driver",CHROME_WEBDRIVER_PATH);
                }
                capability.setCapability("acceptSslCerts", true);
                capability.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
                if (useRemoteWebDriver) {
                    driver = new RemoteWebDriver(
                            new URL("http://" + remoteHostIP + ":" + remoteHostPort + "/wd/hub"),
                            capability);
                } else {
                          driver = new ChromeDriver(capability);
                }
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                driver.get(baseURL);
                break;
            case SAFARI:
                capability = DesiredCapabilities.safari();
                capability.setBrowserName("safari");
                capability.setPlatform(Platform.MAC);
                driver = new SafariDriver();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                driver.get(baseURL);
                break;
            case GHOST:
                capability = new DesiredCapabilities();
                capability.setJavascriptEnabled(true);
                capability.setCapability("takeScreenshot", true);
                if (System.getProperty(WEBDRIVER) != null) {
                    capability.setCapability(
                            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                            System.getProperty(WEBDRIVER));
                } else {
                    setDriverSystemProperty("phantomjs.exe", "phantomjs.binary.path");
                }
                PhantomJSDriver pdriver = new PhantomJSDriver(capability);
                pdriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                pdriver.get(baseURL);
                return pdriver;
            default:
                logger.error("Browser not supported");
        }


        return driver;

    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        addArguments(options);
        return options;
    }

    private void addArguments(ChromeOptions options) {

        List<String> arguments = new ArrayList<String>();
        arguments.add("--ignore-certificate-errors");
        arguments.add("--disable-popup-blocking");
        arguments.add("--disable-plugins");
        arguments.add("--disable-extensions");
        if (System.getProperty(HEADLESS) != null) {
            if (System.getProperty(HEADLESS).toLowerCase().contentEquals("true")) {
                arguments.add("--headless");
            } else if (System.getProperty(HEADLESS).toLowerCase().contentEquals("false")) {
                // do nothing
            } else {
                logger.error("Provided value " + System.getProperty(HEADLESS)
                        + " is not supported for headless execution, so using default value as false");
            }
        }

        options.addArguments(arguments);

    }

    public FirefoxProfile getFirefoxProfiles() {
         FirefoxProfile profile = new FirefoxProfile();
         profile.setPreference(AtfConstants.PROFILE_FOLDER_LIST, 2);
         profile.setPreference(AtfConstants.PROFILE_SHOW_WHEN_STARTING, false);
      //   profile.setPreference(AtfConstants.PROFILE_DOWNLOAD_DIR, AtfSupport.getDownloadDirectoryPath());
         profile.setPreference(AtfConstants.PROFILE_NEVERASK_OPENFILE,
                 "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/zip");
         profile.setPreference(AtfConstants.PROFILE_NEVERASK_SAVETODISK,
                 "text/csv,application/zip,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
         profile.setPreference(AtfConstants.PROFILE_ALWAYSASK, false);
         profile.setPreference(AtfConstants.PROFILE_ALERT_ON_EXE_OPEN, false);
         profile.setPreference(AtfConstants.PROFILE_FOCUS_WHEN_STARTING, false);
         profile.setPreference(AtfConstants.PROFILE_USE_WINDOW, false);
         profile.setPreference(AtfConstants.PROFILE_SHOW_ALERT, false);
         profile.setPreference(AtfConstants.PROFILE_CLOSE_WHEN_DONE, false);
         profile.setPreference(AtfConstants.PROFILE_INSECURE_FIELD, false);
         return profile;
    }

    /*
     * Load the Selenium driver specified by the driver argument and
     * set the System property specified by propertyName to a
     * temporary file created from the content of the driver.
     */
    private void setDriverSystemProperty(String driver, String propertyName) {
                System.setProperty(driver, propertyName);
               return;
         }

    /**
     * @return String containing 10 digit Account Number
     */
    public static String getRandomAccountNumber() {

        String accountNumber = "";
        Random rn = new Random();

        while (accountNumber.length() < 10) {
            accountNumber = accountNumber + rn.nextInt(9);
        }

        return accountNumber;

    }

    /**
     * This is to wait for the element till it loaded in a page
     *
     * @param driver : Driver of the Browser Under Test.
     * @param webElement : Element that should be loaded.
     */
    public static int maxLoadTime = 60;

    public static boolean waitForElementToLoad(WebElement webElement, WebDriver driver)
            throws Exception {

        try {
            WebDriverWait wait = new WebDriverWait(driver, maxLoadTime);
            wait.until(ExpectedConditions.visibilityOf(webElement));
            // wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("")));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Wait till Element is Clickable
    public static boolean waitForElementClickable(WebElement webElement, WebDriver driver)
            throws Exception {

        try {
            WebDriverWait wait = new WebDriverWait(driver, maxLoadTime);
            wait.until(ExpectedConditions.elementToBeClickable(webElement));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * @param driver : Driver of the Browser Under Test.
     * @param fileName : Name of the snapshot file.
     */
    public static void screenCapture(RemoteWebDriver driver, String fileName) {

        File snapshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = fileName;

        try {
            FileUtils.copyFile(snapshot, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
