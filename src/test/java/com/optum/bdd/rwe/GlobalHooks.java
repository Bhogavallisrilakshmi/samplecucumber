package com.optum.bdd.rwe;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.optum.bdd.rwe.spring.AtfConfiguration;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class GlobalHooks {

    public AnnotationConfigApplicationContext context;

    /**
     * Load the environment property files. For more information refer PropertyManagerImpl class.
     */
    @Before
    public void initialize() {
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

    @After
    public void closeAppBrowser() throws IOException {
        for (AbstractPageBase pageBase : AbstractPageBase.getPageBases()) {
            if (pageBase.isActive()) {
                pageBase.killDriver();
            }
        }
    }

}
