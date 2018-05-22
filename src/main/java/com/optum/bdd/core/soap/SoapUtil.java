package com.optum.bdd.core.soap;

import org.apache.log4j.Logger;

/**
 * @author girija.panda@nokia.com
 *         SoapUtil : Handles SOAP-WebService call
 *         AND Generates Soap-Response
 *
 */
public class SoapUtil extends SoapBase {

    private static Logger log = Logger.getLogger(SoapUtil.class.getName());


    /**
     * Initialize SoapUtil
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param contentType
     * @throws SoapException
     */
    public SoapUtil(String endpoint, String username, String password, String contentType)
            throws SoapException {

        // Initialize
        super(endpoint, username, password, contentType);
    }

    /**
     * Initialize SoapUtil WSSE-Security
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param passwordType
     * @param contentType
     * @throws SoapException
     */

    public SoapUtil(String endpoint, String username, String password, String passwordType,
            String contentType) throws SoapException {

        // Initialize
        super(endpoint, username, password, passwordType, contentType);
    }


    /**
     * Initialize SoapUtil with AutoTunnel
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param contentType
     * @param tunnelHost
     * @param tunnelUserName
     * @param tunnelPassword
     * @param tunnelPort
     * @param tunnelRemoteHost
     * @param tunnelRemotePort
     * @param tunnelLocalPort
     * @throws SoapException
     */
    public SoapUtil(String endpoint, String username, String password, String contentType,
            String tunnelHost, String tunnelUserName, String tunnelPassword, String tunnelPort,
            String tunnelRemoteHost, String tunnelRemotePort, String tunnelLocalPort)
            throws SoapException {

        // Initialize
        super(endpoint, username, password, contentType, tunnelHost, tunnelUserName, tunnelPassword,
                tunnelPort, tunnelRemoteHost, tunnelRemotePort, tunnelLocalPort);
    }


    /**
     * Initialize SoapUtil WSSE-Security and AutoTunnel
     * 
     * @param endpoint
     * @param username
     * @param password
     * @param passwordType
     * @param contentType
     * @param tunnelHost
     * @param tunnelUserName
     * @param tunnelPassword
     * @param tunnelPort
     * @param tunnelRemoteHost
     * @param tunnelRemotePort
     * @param tunnelLocalPort
     * @throws SoapException
     */
    public SoapUtil(String endpoint, String username, String password, String passwordType,
            String contentType, String tunnelHost, String tunnelUserName, String tunnelPassword,
            String tunnelPort, String tunnelRemoteHost, String tunnelRemotePort,
            String tunnelLocalPort) throws SoapException {

        // Initialize
        super(endpoint, username, password, passwordType, contentType, tunnelHost, tunnelUserName,
                tunnelPassword, tunnelPort, tunnelRemoteHost, tunnelRemotePort, tunnelLocalPort);
    }



    /**
     * Execute Soap-Request and Generate Response
     * 
     * @param request : XML request file path
     * @return SoapResponse
     */
    public SoapResponse executeRequest(String request) throws SoapException {

        return execute(request);
    }



    /**
     * Execute Soap-Request with input data and Generate Response
     * 
     * @param request : XML request file path
     * @param request : String[][] inputData
     * @return SoapResponse
     */
    public SoapResponse executeRequest(String request, String[][] inputData) throws SoapException {

        return execute(request, inputData);
    }

}
