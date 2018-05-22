package com.optum.bdd.rwe.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Login {
	
	@Given("^keycloak user is in Adminstator Page$")
	public void keycloak_user_is_in_Adminstator_Page() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    System.out.println("Demo");
	}

	@When("^keycloak login with username as \"([^\"]*)\" and password as \"([^\"]*)\"$")
	public void keycloak_login_with_username_as_and_password_as(String arg1, String arg2) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		 System.out.println("Demo1");
	}

	@Then("^verify keycloak title page$")
	public void verify_keycloak_title_page() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		 System.out.println("Demo2");
	}

	@Then("^LogOff from keycloak$")
	public void logoff_from_keycloak() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		 System.out.println("Demo3");
	}


}
