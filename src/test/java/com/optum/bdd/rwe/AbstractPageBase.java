/**
 * Copyright (c) Nokia 2017 All rights reserved.
 *
 * Nokia, Inc. Proprietary/Trade Secret ; Information Not to be disclosed or used except in
 * accordance with applicable agreements.
 */
package com.optum.bdd.rwe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;


import com.optum.bdd.core.selenium.AtfConstants;
import com.optum.bdd.core.selenium.Browsers;
import com.optum.bdd.core.selenium.WebDriverUtils;
import com.optum.bdd.rwe.spring.AtfConfiguration;

/**
 * Implementation of {@link PageBase}
 */
public abstract class AbstractPageBase implements PageBase {

    /** A {@link Logger}. */
    private Logger logger = Logger.getLogger(AbstractPageBase.class);

    /** {@link WebDriver} for Web UI automation. */
    private WebDriver driver;

    private FirefoxProfile profile = null;

    /** The page map keyed by {@link #getAppContext()} */
    private static Map<String, AbstractPageBase> pageMap = new HashMap<>();

    private boolean isActive = false;

    private List<Browsers> browsers = Arrays.asList(Browsers.values());

    /**
     * Get the application name used in the URL. For example Configuration Manager uses 'config' and
     * Workflow Launcher uses 'wf'.
     *
     * @return application name used in URL
     */
    public abstract String getAppContext();

    /**
     * Gets the default browser. Default value is null
     * which means that there are no browser restrictions
     * for the application.
     *
     * @return null if all browsers supported or List of supported browsers
     */
    public List<Browsers> getSupportedBrowsers() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#getDriver()
     */
    public WebDriver getDriver() {
        if (driver == null) {
            driver = createDriver(null);
        }
        AbstractPageBase.pageMap.put(getAppContext(), this);
        return driver;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#getDriver(java.lang.String)
     */
    public WebDriver getDriver(String appendToUrl) {
        if (driver == null) {
            driver = createDriver(appendToUrl);
        }
        AbstractPageBase.pageMap.put(getAppContext(), this);
        return driver;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#createDriver(java.lang.String)
     */
    public WebDriver createDriver(String appendToUrl) {

        String url = formatUrl(appendToUrl);
        try {
            String operatingSystem =
                    AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_OS);
            String remoteHostIP =
                    AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_HOST);
            String remoteHostPort =
                    AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_PORT);
            String useRemote =
                    AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_REMOTE);

            WebDriverUtils webUtil = new WebDriverUtils(getBrowserType(), operatingSystem,
                    remoteHostIP, remoteHostPort, Boolean.valueOf(useRemote));
            profile = adjustDriverForApplication();
            if (profile == null) {
                driver = webUtil.getDriver(url.toString());
            } else {
                driver = webUtil.getDriver(url.toString());
            }
            maximizeBrowserWindow(driver);
        } catch (Throwable t) {
            if (driver != null) {
                driver.close();
            }
            logger.error(t, t);
            throw new RuntimeException("Failed to access: " + url);
        }
        isActive = true;
        AbstractPageBase.pageMap.put(getAppContext(), this);
        return driver;
    }

    private void maximizeBrowserWindow(WebDriver driver) {
        try {
            driver.manage().window().maximize();
        } catch (Throwable t) {
            logger.warn("Failed to maximize window : " + t.getLocalizedMessage());
        }
    }

    private String formatUrl(String appendToUrl) {

        StringBuilder appUrl = new StringBuilder();
        appUrl.append(getProtocol() + AtfConstants.COLON + AtfConstants.DOUBLE_SLASH
                + getHost());
        String port = getPort();
        if (port != null) {
            appUrl.append(AtfConstants.COLON + port);
        }
        appUrl.append(AtfConstants.SLASH);
        appUrl.append(getAppContext());
        if (appendToUrl != null) {
            appUrl.append(appendToUrl);
        }
        return appUrl.toString();
    }

    /**
     * Get the browser to be used to launch the application (see: @{link {@link Browsers}).
     *
     * @return the name of the {@link Browsers} in which application have to open
     */
    public String getBrowserType() {

        String applicationBrowserType = getApplicationBrowserType();
        String defaultBrowserType = getDefaultBrowserType();
        if (applicationBrowserType != null) {
            return applicationBrowserType;
        }
        if (defaultBrowserType != null && applicationBrowserType==null) {
            return defaultBrowserType;
        }
        return getAtfBrowserType();
    }

    /**
     * Gets the protocol value to launch the application (see:
     * {@link PropertyManager#getPropertyValue}), otherwise return http.
     *
     * @return the protocol value as String (http or https)
     */
    public String getProtocol() {
        String protocolValue =
                AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_TEST_PROTOCOL);
        if (protocolValue == null) {
            return AtfConstants.HTTP;
        } else {
            return protocolValue;
        }
    }


    /**
     * Gets the port value to launch the application (see: {@link PropertyManager#getPropertyValue})
     *
     * @return the port as String
     */
    public String getPort() {
        String port = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_TEST_SERVER_PORT);
        return port;
    }

    /**
     * Gets the hostname value to launch the application (see: {@link PropertyManager#getPropertyValue})
     *
     * @return the hostname as String
     */
    public String getHost() {
        String host = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_TEST_SERVER);
        return host;
    }

    /**
     * Gets the name of the default browser for the application or null if there is no default
     * browser type.
     *
     * @return the default browser type
     */
    private String getDefaultBrowserType() {
        if ((getSupportedBrowsers() != null) && (getSupportedBrowsers().size() > 0)) {
            return getSupportedBrowsers().get(0).name();
        }
        return null;
    }

    /**
     * Gets the browser type specified for a particular application. Property values
     * <p>
     * <code>
     * bdd.smp.webdriver.&lt;Application Context&gt;.browser.type
     * </code>
     * <p>
     * are used for this purpose. For example:
     * <p>
     * <code>
     * bdd.smp.webdriver.soc.browser.type
     * </code>
     * <p>
     * can be used to set a browser to be used for the Service Operations Console
     * @return the application browser type
     */
    private String getApplicationBrowserType() {
        String appBrowserType = AtfConfiguration
                .getPropertyValue(AtfConstants.WEBDRIVER + getAppContext() + AtfConstants.BROWSER);
        String globalBrowserType = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_BROWSER);
        if (appBrowserType != null) {
            try {
                Browsers.valueOf(appBrowserType);
            } catch (Throwable t) {
                logger.error(appBrowserType + " is not a supported browser type. Supported types are: "
                        + browsers);
                return null;
            }
        }
        else {
             appBrowserType=globalBrowserType;
        }
        return appBrowserType;
    }

    private String getAtfBrowserType() {
        return AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_BROWSER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#checkDriver()
     */
    public WebDriver checkDriver() {
        AbstractPageBase.pageMap.put(getAppContext(), this);
        return driver;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#killDriver()
     */
    public void killDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        isActive = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#maximize()
     */
    public void maximize() {
        if (driver != null) {
            driver.manage().window().maximize();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.motive.bdd.smp.PageBase#close()
     */
    public void close() {
        if (driver != null) {
            driver.close();
        }
        isActive = false;
    }

    /**
     * Returns a {@link PageBase} for the specified applicationContext.
     * For example if the applicationContext is "wf" the Page
     * base will be for the Workflow Launcher"
     *
     * @param applicationContext the application context
     * @return the page base
     */
    public static PageBase getPageBase(String applicationContext) {
        return pageMap.get(applicationContext);
    }

    /**
     * Adjust the {@link WebDriver} based on requirements
     * of application supported by AbstractPageBase implementation.
     * 
     * @return
     */
    protected FirefoxProfile adjustDriverForApplication() {
        return null;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return isActive;
    }



    /**
     * Returns {@link Collection} of AbstractPageBases from the
     * {@link #pageMap}.
     *
     * @return the page bases
     */
    public static Collection<AbstractPageBase> getPageBases() {
        return pageMap.values();
    }

    /**
     * Returns {@link #pageMap} of current running applications and its page base.
     *
     * @return the page map
     */
    public static Map<String, AbstractPageBase> getPageMap() {
        return pageMap;
    }
    
    /**
     * Gets the IP Address to config proxy for SecurityScan  (see: {@link PropertyManager#getPropertyValue})
     *
     * @return the IPAddress as String
     */
    public String getProxyHost() {
        String host = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_PROXY_IPADDRESS);
        return host;
    }
    
    /**
     * Gets the port value to config proxy for Security Scan (see: {@link PropertyManager#getPropertyValue})
     *
     * @return the port as String
     */
    public String getProxyPort() {
        String port = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_PROXY_PORT);
        return port;
    }





}
