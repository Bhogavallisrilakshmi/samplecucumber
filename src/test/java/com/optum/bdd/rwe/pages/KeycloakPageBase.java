package com.optum.bdd.rwe.pages;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.optum.bdd.rwe.AbstractPageBase;
import com.optum.bdd.rwe.spring.AtfConfiguration;

@ContextConfiguration(classes = AtfConfiguration.class,
loader = AnnotationConfigContextLoader.class)

public class KeycloakPageBase extends AbstractPageBase{

    private static KeycloakPageBase instance = null;
    public static String APP_CONTEXT = "auth";

    private KeycloakPageBase() {

    }

    public static KeycloakPageBase getInstance() {
        if (instance == null) {
            instance = new KeycloakPageBase();
        }
        return instance;
    }

    public String getAppContext() {
        return APP_CONTEXT;
    }

}
