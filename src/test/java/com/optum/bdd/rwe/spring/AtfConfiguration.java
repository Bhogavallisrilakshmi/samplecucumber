package com.optum.bdd.rwe.spring;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.optum.bdd.core.selenium.AtfConstants;
import com.optum.bdd.core.selenium.WebDriverUtils;
import com.optum.bdd.rwe.PropertyManager;
import com.optum.bdd.rwe.impl.PropertyManagerImpl;


@Configuration
public class AtfConfiguration implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private Logger logger = Logger.getLogger(AtfConfiguration.class);


    @Bean
    @Autowired
    public PropertyManager getPropertyManager() {
        logger.info("Loading the Environment Properties");
        return new PropertyManagerImpl();
    }

    @Bean
    public WebDriverUtils getWebDriverConfig(PropertyManager propertyManager)
            throws MalformedURLException, IllegalStateException {

        WebDriverUtils webDriverUtility = new WebDriverUtils(
                propertyManager.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_BROWSER),
                propertyManager.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_OS),
                propertyManager.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_HOST),
                propertyManager.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_PORT),
                Boolean.parseBoolean(propertyManager.getPropertyValue(PropertyManager.BDD_SMP_WEBDRIVER_REMOTE)));
                return webDriverUtility;
    }


    public static String getPropertyValue(String key) {
        return applicationContext.getBean(PropertyManager.class).getPropertyValue(key);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AtfConfiguration.applicationContext = applicationContext;
    }

    public static void setPropertyValue(String key, String value) {
        applicationContext.getBean(PropertyManager.class).setPropertyValue(key, value);
    }

    /**
     * Gets the protocol value to launch the application (see:
     * {@link PropertyManager#getPropertyValue}), otherwise return http.
     *
     * @return the protocol value as String (http or https)
     */
    public String getProtocol() {
        String protocolValue =   AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_TEST_PROTOCOL);
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
     * Gets the hostname value to launch the application (see:
     * {@link PropertyManager#getPropertyValue})
     *
     * @return the hostname as String
     */
    public String getHost() {
        String host = AtfConfiguration.getPropertyValue(PropertyManager.BDD_SMP_TEST_SERVER);
        return host;
    }

}
