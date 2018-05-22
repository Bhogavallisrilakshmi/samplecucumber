package com.optum.bdd.rwe;

import org.openqa.selenium.WebDriver;

public interface PageBase {

    WebDriver getDriver();

    WebDriver getDriver(String appendToUrl);

    WebDriver createDriver(String appendToUrl);

    WebDriver checkDriver();

    void killDriver();

    void maximize();

    void close();

}
