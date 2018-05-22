package com.optum.bdd.core.soap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {

    private static final String LOG_CONFIGURATION_PROPERTY = "log4j.properties";
    private static Logger log = Logger.getLogger(LogUtil.class.getName());

    public LogUtil() {
        // TODO Auto-generated constructor stub
    }


    public static void loadConfig() throws SoapException {

        Properties properties;
        String value = null;
        try {
          InputStream reader =
                    LogUtil.class.getClassLoader().getResourceAsStream(LOG_CONFIGURATION_PROPERTY);
            properties = new Properties();
            properties.load(reader);
            PropertyConfigurator.configure(properties);
            } catch (FileNotFoundException ex) {
            log.error("Log4j configuration FileNotFoundException : " + ex.getMessage());
            System.exit(0);
        } catch (IOException ex) {
            log.error("Log4j configuration IOException : " + ex.getMessage());
            System.exit(0);
        }

    }


}
