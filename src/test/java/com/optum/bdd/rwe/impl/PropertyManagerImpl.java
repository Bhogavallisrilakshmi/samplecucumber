package com.optum.bdd.rwe.impl;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.optum.bdd.rwe.PropertyManager;



public class PropertyManagerImpl implements PropertyManager {

    private Properties envProperties = new Properties();
    private Logger logger = Logger.getLogger(PropertyManagerImpl.class);
    private static final String defaultProperties = "/DefaultEnvironment.properties";
    private static final String environmentProperties = "Environment.properties";
    /**
     * List of property keys used by supporting libraries. When
     * a property is added where the key matches a member of this list
     * the property is added to {@link System} properties. This
     * enables the user to maintain the value of the property
     * in the Environment.properties file.
     */
    private static final List<String> externalSystemProperties = new ArrayList<>();

    @PostConstruct
    public void loadEnvProperties() {
        externalSystemProperties.add(PropertyManager.BDD_SMP_WEBDRIVER_PATH);
        loadEnvProperties(defaultProperties);
        loadEnvProperties(environmentProperties);
    }

    private void loadEnvProperties(String propertiesFile) {
        addPropertiesToEnvironment(getPropertiesForResource(propertiesFile));
    }

    private Properties getPropertiesForResource(String resource) {
        BufferedReader reader = null;
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            inputStream = getInputStreamForResource(resource);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            if (reader != null) {
                properties = new Properties();
                try {
                    properties.load(reader);
                } catch (IOException e) {
                    logger.error("failed to load: " + resource);
                }
            }
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to load: " + resource + " :" + t.getLocalizedMessage());
            }
        } finally {
            for (Closeable closeable : new Closeable[] {reader, inputStream}) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return properties;
    }

    private InputStream getInputStreamForResource(String resource) {
        InputStream inputStream = null;
        inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            return getClass().getResourceAsStream(resource);
        }
        return inputStream;
    }

    private void addPropertiesToEnvironment(Properties newProperties) {
        for (Entry<Object, Object> property : newProperties.entrySet()) {
            setPropertyValue(property.getKey().toString(), property.getValue());
        }
    }

    @Override
    public String getPropertyValue(String key) {
        if (key == null) {
            return null;
        }
        Object value = System.getProperty(key);
        if (value != null) {
            return value.toString();
        }
        value = envProperties.get(key);
        if (value != null) {
            return value.toString();
        }
        value = System.getProperty(key);
        if (value != null) {
            setPropertyValue(key, value);
            return value.toString();
        }
        value = envProperties.getProperty(key);
        if (value != null) {
            setPropertyValue(key, value);
            return value.toString();
        }
        return (value == null ? null : value.toString());
    }

    public void setPropertyValue(String key, Object value) {
        if (key != null) {
            setPropertyValue(key, value.toString());
        } else {
            logger.warn("Null key for: " + value);
        }
    }

    @Override
    public void setPropertyValue(String key, String value) {
        envProperties.put(key, value);
        // Do not override existing system property
        if (System.getProperty(key) == null) {
            if (externalSystemProperties.contains(key)) {
                System.setProperty(key, value);
            }
        }
    }
}
