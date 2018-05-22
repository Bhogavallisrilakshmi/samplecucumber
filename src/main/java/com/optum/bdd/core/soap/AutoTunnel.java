package com.optum.bdd.core.soap;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * @author girija.panda@nokia.com
 *
 *         AutoTunnel : Handles SSH Tunneling
 *         Means Connecting to Remote SSH Servers
 *         AND Mapping both Local and Remote Ports for Tunneling by using "setPortForwardingL()"
 *
 *         setPortForwardingL() :
 *         Data sent to port "tunnelLocalPort" of any machine(localhost)
 *         should be forwarded to port "tunnelRemotePort" of the machine with host
 *         "tunnelRemoteHost" and viceversa
 */

public class AutoTunnel {

    private String tunnelHost = null;
    private String tunnelUserName = null;
    private String tunnelPassword = null;
    private Integer tunnelPort = 22;

    private String tunnelRemoteHost = null;
    private Integer tunnelRemotePort = null;
    private Integer tunnelLocalPort = null;

    private JSch jsch = null;
    private Session session = null;
    private LocalUserInfo luserInfo = null;

    private static Logger log = Logger.getLogger(AutoTunnel.class.getName());

    /**
     * Initialize AutoTunnel
     * 
     * @param autoTunnelEnable
     * @param tunnelHost
     * @param tunnelUserName
     * @param tunnelPassword
     * @param tunnelPort
     * @param tunnelRemoteHost
     * @param tunnelRemotePort
     * @param tunnelLocalPort
     */
    public AutoTunnel(String tunnelHost, String tunnelUserName, String tunnelPassword,
            String tunnelPort, String tunnelRemoteHost, String tunnelRemotePort,
            String tunnelLocalPort) {
        // Initialize
        this.tunnelHost = tunnelHost;
        this.tunnelUserName = tunnelUserName;
        this.tunnelPassword = tunnelPassword;
        if (tunnelPort != "" || tunnelPort != null) {
            this.tunnelPort = Integer.valueOf(tunnelPort);
        }

        this.tunnelRemoteHost = tunnelRemoteHost;
        this.tunnelRemotePort = Integer.valueOf(tunnelRemotePort);
        this.tunnelLocalPort = Integer.valueOf(tunnelLocalPort);

        log.debug("AutoTunnel Initialized...");
    }



    /**
     * Open and PortForward SSH Session
     * 
     * @throws JSchException
     */
    public void openSesssion() throws JSchException {
        try {
            JSch.setConfig("StrictHostKeyChecking", "no");

            jsch = new JSch();
            this.session = jsch.getSession(this.tunnelUserName, this.tunnelHost,
                    this.tunnelPort.intValue());
            this.session.setPassword(this.tunnelPassword);

            luserInfo = new LocalUserInfo(this.tunnelPassword);
            this.session.setUserInfo(luserInfo);

            // Connect to Remote Host
            this.session.connect();

            // Forwarding data from "tunnelLocalPort" to "tunnelRemotePort" with host
            // "tunnelRemoteHost"
            this.session.setPortForwardingL(this.tunnelLocalPort.intValue(), this.tunnelRemoteHost,
                    this.tunnelRemotePort.intValue());

            log.debug(
                    "********************** AutoTunnel-OpenSession *******************************************");
            log.debug("Tunnel-Host : " + this.tunnelHost + "\n" +
                    "	Tunnel-UserName : " + this.tunnelUserName + "\n" +
                    "	Tunnel-Password : " + this.tunnelPassword + "\n" +
                    "	Tunnel-Port : " + this.tunnelPort.toString() + "\n" +
                    "	Tunnel-RemoteHost : " + this.tunnelRemoteHost + "\n" +
                    "	Tunnel-RemotePort : " + this.tunnelRemotePort.toString() + "\n" +
                    "	Tunnel-LocalPort : " + this.tunnelLocalPort.toString());
            log.debug(
                    "***************************************************************************************");

            Thread.sleep(3000);

        } catch (Exception ex) {
            log.error("AutoTunnel:openSesssion():" + ex.getMessage());
        }

    }


    // Close SSH Session
    public void closeSession() {

        try {
            log.debug("Disconnecting AutoTunnel Session....");
            // this.session.delPortForwardingL(this.tunnelHost, this.tunnelLocalPort.intValue());
            this.session.delPortForwardingL(this.tunnelLocalPort.intValue());
            this.session.disconnect();
            this.session = null;

            log.debug("AutoTunnel Session Disconnected....");

        } catch (JSchException ex) {
            log.error("AutoTunnel:closeSession():" + ex.getMessage());
        }


    }



    class LocalUserInfo implements UserInfo {

        String password;

        public LocalUserInfo(String pwd) {
            this.password = pwd;
        }

        public String getPassword() {
            return password;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {}
    }

}
