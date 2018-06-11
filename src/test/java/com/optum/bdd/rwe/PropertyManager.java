package com.optum.bdd.rwe;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

/**
 * PropertyManager is responsible for maintaining property values for those
 * properties used by the ATF. All required properties have default values. The
 * default values enable out of the box testing when using the SMP reference
 * configuration. The default value of a property can be overridden by:
 * <ul>
 * <li>Setting a <code>-D</code> argument when running the <code>mvn</code>
 * command. For example:
 * <p>
 *
 * <pre>
*  mvn test -Dbdd.smp.test.protocol=https
 * </pre>
 *
 * <li>Setting the value of a property in
 * <code>src/test/resources/Environment.properties</code>. For example this line
 * can be added:
 * <p>
 *
 * <pre>
 * bdd.smp.test.protocol = https
 * </pre>
 * </ul>
 */

@Service
public interface PropertyManager {

    /**
     * The supported protocol.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.test.protocol
     * </code>
     * <p>
     * Valid protocol values are:
     * <ul>
     * <li>http
     * <li>https
     * </ul>
     */
    public static final String BDD_SMP_TEST_PROTOCOL = "bdd.smp.test.protocol";

    /**
     * The name or IP address of the SMP server to test against.
     * <p>
     * Property key:<code>
     * bdd.smp.test.server
     * </code>
     */
    public static final String BDD_SMP_TEST_SERVER = "bdd.smp.test.server";
    /**
     * The port of the SMP server to test against.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.test.server.port
     * </code>
     */
    public static final String BDD_SMP_TEST_SERVER_PORT = "bdd.smp.test.server.port";
    /**
     * The port number of the Selenium server.
     * <p>
     * Property key: <code>
     * bdd.smp.webdriver.port
     * </code>
     */
    public static final String BDD_SIM_TEST_SERVER_PORT = "bdd.sim.test.server.port";
    /**
     * The name or IP address of the Soapsimulator server to use.
     * <p>
     * Property key: <code>
     * bdd.smp.selenium.host
     * </code>
     */
    public static final String BDD_SMP_WEBDRIVER_HOST = "bdd.smp.webdriver.host";
    /**
     * The name or IP address of the Selenium server to use.
     * <p>
     * Property key: <code>
     * bdd.smp.selenium.host
     * </code>
     */
    public static final String BDD_SMP_TEST_SERVER_HOST = "bdd.smp.test.server.host";
    /**
     * The port number of the Selenium server.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.port
     * </code>
     */
    public static final String BDD_SIM_TEST_SERVER_HOST = "bdd.sim.test.server.host";
    /**
     * The host name of the sim server.
     * <p>
     * Property key: <code>
     * bdd.smp.webdriver.port
     * </code>
     */
    public static final String BDD_SMP_WEBDRIVER_PORT = "bdd.smp.webdriver.port";
    /**
     * The path to the Selenium driver used for handling web
     * interfaces.
     * <p>
     * Property key: <code>
     * com.motive.bdd.core.selenium.webdriver
     * </code>
     * <p>
     * The ATF includes drivers for the Windows operating system but users are encouraged to obtain
     * the latest available drivers for the browser and OS being tested. See: <a href=
     * "http://docs.seleniumhq.org/projects/webdriver"
     * >http://docs.seleniumhq.org/projects/webdriver/</a> for details on obtaining Selenium
     * drivers.
     */
    public static final String BDD_SMP_WEBDRIVER_PATH = "com.motive.bdd.core.selenium.webdriver";
    /**
     * The Browser to use. See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_BROWSER = "bdd.smp.webdriver.browser.type";



    /**
     * The path used to download the contents from the browser. If not specified, the
     * <code>target/downloads</code> folder is used.
     * Property key:
     * <p>
     * <code>
     * bdd.smp.download.dir
     * </code>
     */
    public static final String BDD_SMP_DOWNLOAD_DIR = "bdd.smp.download.dir";

    /**
     * The Browser to use for config application (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.config.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_CONFIG_BROWSER =
            "bdd.smp.webdriver.config.browser.type";

    /**
     * The Browser to use for Workflow Builder (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.workflow-builder.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_WFB_BROWSER =
            "bdd.smp.webdriver.workflow-builder.browser.type";

    /**
     * The Browser to use for the apidocs application (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.apidocs.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_APIDOCS_BROWSER =
            "bdd.smp.webdriver.apidocs.browser.type";

    /**
     * The Browser to use for bptools (if not specified the value of bdd.smp.webdriver.browser.type
     * will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.bptools.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_BPTOOLS_BROWSER =
            "bdd.smp.webdriver.bptools.browser.type";

    /**
     * The Browser to use for CSC (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.csc.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_CSC_BROWSER = "bdd.smp.webdriver.csc.browser.type";

    /**
     * The Browser to use for EAC (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.eac.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_EAC_BROWSER = "bdd.smp.webdriver.eac.browser.type";

    /**
     * The Browser to use for the Jasper reporting application (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.jasperserver-pro.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_JASPER_BROWSER =
            "bdd.smp.webdriver.jasperserver-pro.browser.type";

    /**
     * The Browser to use for SSC (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.ssc.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_SSC_BROWSER = "bdd.smp.webdriver.ssc.browser.type";

    /**
     * The Browser to use for Workflow Laucnher (if not specified the value of
     * bdd.smp.webdriver.browser.type will be used). See
     * <a href="http://www.seleniumhq.org/about/platforms.jsp#browsers"
     * >Selenium Web Drivers</a>.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.wf.browser.type
     * </code>
     * <p>
     * Valid browser type values are:
     * <ul>
     * <li>OLDFIREFOX - For Firefox version older than 47
     * <li>CHROME
     * <li>FIREFOX - For Firefox version 47 and later
     * <li>IE
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_WF_BROWSER = "bdd.smp.webdriver.wf.browser.type";

    /**
     * The operating system on which the Selenium server is running.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.browser.os
     * </code>
     * <p>
     * Valid operating system values are:
     * <ul>
     * <li>Windows
     * <li>Linux
     * </ul>
     */
    public static final String BDD_SMP_WEBDRIVER_OS = "bdd.smp.webdriver.os";

    /**
     * Boolean value indicating whether the Selenium server is operating on the same
     * machine where the tests are run.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.remote
     * </code>
     */
    public static final String BDD_SMP_WEBDRIVER_REMOTE = "bdd.smp.webdriver.remote";

    /**
     * The database type of the SMP Database. Valid values are 'oracle' or
     * 'mariadb'. This is not used when testing SMP web applications.
     */
    public static final String BDD_SMP_DATABASE_TYPE = "bdd.smp.database.type";
    /**
     * The URL SMP Database. This is not used when testing SMP web applications.
     */
    public static final String BDD_SMP_DATABASE_URL = "bdd.smp.database.url";
    /**
     * The user name of the SMP Database. This is not used when testing SMP web
     * applications.
     */
    public static final String BDD_SMP_DATABASE_USER = "bdd.smp.database.user";
    /**
     * The password of the SMP Database. This is not used when testing SMP web
     * applications.
     */
    public static final String BDD_SMP_DATABASE_PASSWORD = "bdd.smp.database.password";

    /**
     * The database type of the SMP Modeling Database. Valid values are 'oracle'
     * or 'mariadb'. This is not used when testing SMP web applications.
     */
    public static final String BDD_SMP_MOD_DATABASE_TYPE = "bdd.smp.mod.database.type";
    /**
     * The URL of SMP Modeling Database. This is not used when testing SMP web
     * applications.
     */
    public static final String BDD_SMP_MOD_DATABASE_URL = "bdd.smp.mod.database.url";
    /**
     * The user name of the SMP Modeling Database. This is not used when testing
     * SMP web applications.
     */
    public static final String BDD_SMP_MOD_DATABASE_USER = "bdd.smp.mod.database.user";
    /**
     * The password of the SMP Modeling Database. This is not used when testing
     * SMP web applications.
     */
    public static final String BDD_SMP_MOD_DATABASE_PASSWORD = "bdd.smp.mod.database.password";

    /**
     * The database type of the SMP Reporting Database. Valid values are
     * 'oracle' or 'mariadb'. This is not used when testing SMP web
     * applications.
     */
    public static final String BDD_SMP_REP_DATABASE_TYPE = "bdd.smp.rep.database.type";
    /**
     * The URL of SMP Reporting Database. This is not used when testing SMP web
     * applications.
     */
    public static final String BDD_SMP_REP_DATABASE_URL = "bdd.smp.rep.database.url";
    /**
     * The user name of the SMP Reporting Database. This is not used when
     * testing SMP web applications.
     */
    public static final String BDD_SMP_REP_DATABASE_USER = "bdd.smp.rep.database.user";
    /**
     * The password of the SMP Reporting Database. This is not used when testing
     * SMP web applications.
     */
    public static final String BDD_SMP_REP_DATABASE_PASSWORD = "bdd.smp.rep.database.password";

    /**
     * The IP address of the remote machine to which SSH connection to be
     * established.
     */
    public static final String BDD_SMP_RMT_MACHINE_HOST = "bdd.smp.rmt.machine.host";
    /**
     * The user name of the remote machine to which SSH connection to be
     * established.
     */
    public static final String BDD_SMP_RMT_MACHINE_USER = "bdd.smp.rmt.machine.user";
    /**
     * The password of the remote machine to which SSH connection to be
     * established.
     */
    public static final String BDD_SMP_RMT_MACHINE_PASSWORD = "bdd.smp.rmt.machine.password";


    /**
     * The NBI endpoint of the SMP NBI Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.nbi.endpoint
     * </code>
     * <p>
     * Property value: http://&lt;host name&gt;:&lt;port&gt;/nbi/Workflows
     */

    public static final String BDD_SMP_NBI_ENDPOINT = "bdd.smp.nbi.endpoint";



    /**
     * The NBI user of the SMP NBI Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.nbi.user
     * </code>
     */

    public static final String BDD_SMP_NBI_USER = "bdd.smp.nbi.user";

    /**
     * The NBI password of the SMP NBI Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.nbi.password
     * </code>
     */

    public static final String BDD_SMP_NBI_PASSWORD = "bdd.smp.nbi.password";

    /**
     * The password for Workflow Builder, Workflow Launcher, and Workflow Engine.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.wf.password
     * </code>
     */

    public static final String BDD_SMP_WF_PASSWORD = "bdd.smp.wf.password";

    /**
     * The user for Workflow Builder, Workflow Launcher and Workflow Engine.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.wf.user
     * </code>
     */

    public static final String BDD_SMP_WF_USER = "bdd.smp.wf.user";

    /**
     * The NBI CONTENTTYPE of the SMP NBI Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.nbi.contenttype
     * </code>
     * <p>
     * Property value: &lt;text/xml; charset=UTF-8&gt;
     */
    public static final String BDD_SMP_NBI_CONTENTTYPE = "bdd.smp.nbi.contenttype";
    /**
     * Boolean value indicating if keycloak authentication is used
     * <p>
     * Property key:
     * <code>
     * bdd.smp.keycloak.enabled
     * </code>
     * <p>
     * Default value is true.
     * Property value: true indicates keycloak login available otherwise native method of login
     * available
     */
    public static final String BDD_SMP_KEYCLOAK_ENABLED = "bdd.smp.keycloak.enabled";
    /**
     * Numeric value used to adjust the wait time
     * ({@link Thread#sleep(long)}). It can be used to increase or decrease the wait time when
     * interfacing with a {@link WebDriver}.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.webdriver.compensation_factor
     * </code>
     * <p>
     * The value must be a non-negative numeric value where a value of 1 means
     * no increase or decrease in the default wait time. A value of 2 indicates
     * the wait times should be doubled. A value of .75 indicates the wait times
     * should be 75% of the default value. Care should be taken to use
     * reasonable values. Typically values greater than 1 will be needed to
     * adjust for slow environments.
     */
    public static final String BDD_SMP_WEBDRIVER_COMPENSATION_FACTOR =
            "bdd.smp.webdriver.compensation_factor";

    /**
     * The REST base URL of the SMP REST Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.rest.baseurl
     * </code>
     * <p>
     * Property value: http://&lt;host name&gt;:&lt;port&gt;/configweb/rest/7.0/serviceoperations
     */

    public static final String BDD_SMP_REST_BASEURL = "bdd.smp.rest.baseurl";



    /**
     * The REST user name of the SMP REST Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.rest.user
     * </code>
     */

    public static final String BDD_SMP_REST_USER = "bdd.smp.rest.user";

    /**
     * The REST password of the SMP REST Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.rest.password
     * </code>
     */

    public static final String BDD_SMP_REST_PASSWORD = "bdd.smp.rest.password";


    /**
     * The REST contenttype of the SMP REST Services.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.rest.contenttype
     * </code>
     * <p>
     * Property value: &lt;text/xml; charset=UTF-8&gt;
     */
    public static final String BDD_SMP_REST_CONTENTTYPE = "bdd.smp.rest.contenttype";

    /**
     * The port in which NODE-Red server is running.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.test.nodered.port
     * </code>
     */
    public static final String BDD_SMP_TEST_NODERED_PORT = "bdd.smp.test.nodered.port";

    /**
     * The port number where BOT is running.
     * <p>
     * Property key: <code>
     * bdd.smp.webdriver.port
     * </code>
     */
    public static final String BDD_BOT_TEST_SERVER_PORT = "bdd.bot.test.server.port";

    /**
     * The hostwhere BOT is running.
     * <p>
     * Property key: <code>
     * bdd.smp.webdriver.port
     * </code>
     */
    public static final String BDD_BOT_TEST_SERVER_HOST = "bdd.bot.test.server.host";

    /**
     * Property to specify if chrome browser have to be instantiated in headless mode.
     * <p>
     * Property key: <code>
     * bdd.enable.headless
     * </code>
     */
    public static final String BDD_ENABLE_HEADLESS = "bdd.enable.headless";

    /**
     * The path is used to upload the contents from the local to remote machine. If not specified,
     * the
     * <code>/home/${bdd.smp.rmt.machine.user}/remoteExecution</code> folder is used.
     * Property key:
     * <p>
     * <code>
     * bdd.smp.remote.dir.path
     * </code>
     */
    public static final String BDD_SMP_REMOTE_DIR_PATH = "bdd.smp.remote.dir.path";

    /**
     * The path is used to upload the contents from the repo/forgery to remote machine. If not
     * specified,
     * and it is used, RunTimeException will be invoked. Kindly specify the whole path just before
     * External folder.
     *
     * Example:
     * bdd.smp.contents=
     * https://repo.lab.pl.alcatel-lucent.com/smp-dropzone/18.4.0.0-SNAPSHOT/master_20180307-0226
     *
     * Property key:
     * <p>
     * <code>
     * bdd.smp.contents
     * </code>
     */
    public static final String BDD_SMP_CONTENTS = "bdd.smp.contents";

    /**
     * Boolean value indicating to setup proxy for security scan on the same
     * machine where the tests are run.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.proxy.enable
     * </code>
     */
    public static final String BDD_SMP_WEBDRIVER_PROXY = "bdd.smp.proxy.enable";
 
    /**
    * IPAddresss of the machine to setup proxy for security scan on the same
    * machine where the tests are run.
    * <p>
    * Property key:
    * <code>
    * bdd.smp.proxy.ipaddress
    * </code>
    */
    public static final String BDD_SMP_PROXY_IPADDRESS = "bdd.smp.proxy.ipaddress";
    
    /**
     * Port value to setup proxy for security scan on the same
     * machine where the tests are run.
     * <p>
     * Property key:
     * <code>
     * bdd.smp.proxy.port
     * </code>
     */
     public static final String BDD_SMP_PROXY_PORT = "bdd.smp.proxy.port";

    /**
     * Gets the property value.
     *
     * @param key the key
     * @return the property value
     */
    String getPropertyValue(String key);

    /**
     * Sets the property value.
     *
     * @param key the key
     * @param value the value
     */
    void setPropertyValue(String key, String value);

}
