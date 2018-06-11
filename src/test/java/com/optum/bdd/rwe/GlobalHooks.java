package com.optum.bdd.rwe;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.optum.bdd.rwe.spring.AtfConfiguration;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class GlobalHooks {

    public AnnotationConfigApplicationContext context;
    
    private Logger logger = Logger.getLogger(GlobalHooks.class);

    /**
     * Load the environment property files. For more information refer PropertyManagerImpl class.
     */
    @Before
    public void initialize() {
           logger.info("********** Initialize the Ennvironment Properties ***********************");
           context = new AnnotationConfigApplicationContext(AtfConfiguration.class);
    }

    /**
     * Return the {@link AtfConfiguration} used
     * to support ATF testing.
     *
     * @return the context
     */
    public ApplicationContext getContext() {
        return context;
    }
    
    /**
     * Takes a Screenshot for failed Scenario's and close the browser.
     * @param scenario
     * @throws IOException
     */

    @After
    public void closeAppBrowser(Scenario scenario) throws IOException {
    for (AbstractPageBase pageBase : AbstractPageBase.getPageBases()) {
           if(scenario.isFailed()) {
            logger.info("********** TakeScreenshot for Failed Scenario ***********************");
             scenario.embed(((TakesScreenshot)pageBase.checkDriver()).getScreenshotAs(OutputType.BYTES), "image/png");
             logger.info("********** Close the Browser ***********************");
            pageBase.killDriver();
           }else {
           logger.info("********** Close the Browser ***********************");
           if(pageBase.isActive()) {
           pageBase.killDriver();
           }
           }
       }
    }
}
