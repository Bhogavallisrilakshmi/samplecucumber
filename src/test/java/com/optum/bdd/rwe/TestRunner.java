package com.optum.bdd.rwe;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(

       strict = true,
       dryRun = false,
        monochrome = true,
        snippets = SnippetType.CAMELCASE,
        features = "src/test/resources/features",
        format = {"html:target/site/cucumber-pretty","json:target/cucumber.json",
        "junit:target/junit/all_tests.xml" }
      //  glue = "com.optum.bdd.rwe.steps"

) 
public class TestRunner {


}
