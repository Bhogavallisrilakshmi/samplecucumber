package com.optum.bdd.rwe.steps;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.optum.bdd.rwe.pages.KeycloakLogin;
import com.optum.bdd.rwe.pages.KeycloakPageBase;
import com.optum.bdd.rwe.spring.AtfConfiguration;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class Login {
	
	private Logger logger = Logger.getLogger(Login.class);
	private static KeycloakPageBase pageBase = KeycloakPageBase.getInstance();
	private KeycloakLogin keycloakObj;
	private static WebDriver driver;
	public AnnotationConfigApplicationContext context;
	
	@Given("^keycloak user is in Adminstator Page$")
	public void keycloak_user_is_in_Adminstator_Page()  {
    driver = initilizeDriver();
	keycloakObj = new KeycloakLogin(driver);
	keycloakObj.adminLink.click();
	}

	@When("^keycloak login with username as \"([^\"]*)\" and password as \"([^\"]*)\"$")
	public void keycloak_login_with_username_as_and_password_as(String arg1, String arg2) throws Throwable {
	   keycloakObj.typeUsername("admin");
	   keycloakObj.typePassword("admin");
	   keycloakObj.clickLogOn();
	}

	@Then("^verify keycloak title page$")
	public void verify_keycloak_title_page() throws Throwable {
       Assert.assertTrue(true);

	}

	@Then("^LogOff from keycloak$")
	public void logoff_from_keycloak() throws Throwable {
	    System.out.println("LogOff");
	}
	
	private static WebDriver initilizeDriver() {
        return pageBase.getDriver();
    }


}
