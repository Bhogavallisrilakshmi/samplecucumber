package com.optum.bdd.rwe.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.optum.bdd.rwe.AtfSupport;

public class KeycloakLogin {

private static final Logger logger = Logger.getLogger( KeycloakLogin.class);
    // private static PageBase instance = null;
    private WebDriver driver;
    
    public static final String wf_UserName = "username";
    @FindBy(id = wf_UserName)
    @CacheLookup
    public WebElement txt_UserName;


    public static final String wf_Password = "password";
    @FindBy(id = wf_Password)
    @CacheLookup
    public WebElement txt_Password;

    public static final String wf_Logon = "//input[@id='kc-login']";
    @FindBy(xpath = wf_Logon)
    @CacheLookup
    public WebElement btn_Logon;
    
    public static final String admin_Link = "//div[@class='wrapper']/div[@class='content']//a[@href='admin/']";
    @FindBy(xpath=admin_Link)
    @CacheLookup
    public WebElement adminLink;
    
    public  KeycloakLogin(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void typeUsername(String usernameText) {
        logger.debug("Entering  Username.");
        AtfSupport.waitForElementToLoad(txt_UserName, driver);
        txt_UserName.clear();
        txt_UserName.sendKeys(usernameText);

    }

    public void typePassword(String password) {
        logger.debug("Entering  Password.");
        AtfSupport.waitForElementToLoad(txt_Password, driver);
        txt_Password.clear();
        txt_Password.sendKeys(password);
    }

    public WebDriver clickLogOn() {
        logger.debug("Clicking Logon button");
        AtfSupport.waitForElementToLoad(btn_Logon, driver);
        btn_Logon.click();
        return this.driver;
    }

    public boolean isUsernameTxtBoxDisplayed() {
        return AtfSupport.isElementPresent(txt_UserName);
    }
    public boolean isWFlauncherLoginButtonDisplayed() {
        return btn_Logon.isDisplayed();
    }


}
