package com.optum.bdd.core.remoteaccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class RemoteAcessUtil {

    private static final Logger logger = LogManager.getLogger(RemoteAcessUtil.class);

    String username;
    String password;
    String remoteHostIP;
    int remoteHostPort;

    /**
     * Constructor which sets up remote session credentials with default port
     * value of 22
     * 
     * @param username
     *        - Credentials for establishing remote session
     * @param password
     *        - Credentials for establishing remote session
     * @param remoteHostIP
     *        - IP of the machine to which remote session is established.
     */
    public RemoteAcessUtil(String username, String password, String remoteHostIP) {

        this.username = username;
        this.password = password;
        this.remoteHostIP = remoteHostIP;
        this.remoteHostPort = 22;

    }

    /**
     * Constructor which sets up remote session credentials with dynamic port
     * value
     * 
     * @param username
     *        - Credentials for establishing remote session
     * @param password
     *        - Credentials for establishing remote session
     * @param remoteHostIP
     *        - IP of the machine to which remote session is established.
     * @param remoteHostPort
     *        - Port of the machine on which the remote session is to be
     *        established.
     */
    public RemoteAcessUtil(String username, String password, String remoteHostIP,
            int remoteHostPort) {

        this.username = username;
        this.password = password;
        this.remoteHostIP = remoteHostIP;
        this.remoteHostPort = remoteHostPort;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *        the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *        the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the remoteHostIP
     */
    public String getRemoteHostIP() {
        return remoteHostIP;
    }

    /**
     * @param remoteHostIP
     *        the remoteHostIP to set
     */
    public void setRemoteHostIP(String remoteHostIP) {
        this.remoteHostIP = remoteHostIP;
    }

    /**
     * @return the remoteHostPort
     */
    public int getRemoteHostPort() {
        return remoteHostPort;
    }

    /**
     * @param remoteHostPort
     *        the remoteHostPort to set
     */
    public void setRemoteHostPort(int remoteHostPort) {
        this.remoteHostPort = remoteHostPort;
    }

    /**
     * This method gives the ability to run scripts on remote machine. This
     * should mainly be used for executing autoIt script and batch files on
     * windows and shell/apple scripts on MAC. If the executed command does not
     * exit within the Timeout period then this function exits with a return
     * value of 2. However this does not kill the command on the remote machine.
     *
     * @param nodeUsername
     *        - user name of the Node Machine where the command need to be
     *        executed.
     * @param nodePassword
     *        - password of the Node Machine where the command need to be
     *        executed.
     * @param nodeIp
     *        - IP of the machine where the command need to be executed.
     * @param command
     *        - command to be executed
     *
     * @return - returns a string containing console output of the command.
     */

    public String executeCommand(String command, int timeOutInSeconds) {
        JSch jsch = new JSch();
        Session session = null;
        InputStream inStream;
        Channel channel;
        String result = "";

        try {
            session = jsch.getSession(username, remoteHostIP, remoteHostPort);
            logger.info("=======Session is Opened=======");
            session.setPassword(password);
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            session.setConfig(config);
            session.connect();
            logger.info("=======Session is Connected=======");

            channel = session.openChannel("exec");
            logger.info(command);
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            inStream = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while (timeOutInSeconds > 0) {
                while ((line = reader.readLine()) != null) {
                    result = result + line;
                }
                Thread.sleep(1000);
                timeOutInSeconds--;
            }
            int exitStatus = channel.getExitStatus();
            logger.info("-------exit status--------" + exitStatus);
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Zips the file.
     *
     * @param relativeFilePath
     *        the relative file path
     * @return the string
     */
    public static String zipFile(String relativeFilePath) {

        String downloadDir = "target" + File.separator + "downloads" + File.separator;
        File tmpDir = new File(downloadDir);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File localFile = new File(relativeFilePath);
        String outputFilePath = downloadDir + localFile.getName() + ".zip";
        String inputFilePath = new File(relativeFilePath).getAbsolutePath();
        try {
            zipContents(Paths.get(inputFilePath), Paths.get(outputFilePath));
        } catch (IOException e) {
            logger.error("Error creating config zip file : " + e);
            throw new RuntimeException(e);
        }
        return outputFilePath;
    }

    /**
     * Create Zip file from the contents available under specified folder.
     *
     * @param folder the folder where contents to zip are present
     * @param zipFilePath the file path where resulting zip file have to be placed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void zipContents(final Path folder, final Path zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    zos.putNextEntry(
                            new ZipEntry(folder.relativize(file).toString().replace("\\", "/")));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * This method gives the ability to transfer files from local to remote machine. This
     * should mainly be used for executing autoIt script and batch files on
     * windows and shell/apple scripts on MAC. If the file transfer does not
     * complete within the Timeout period then this function exits with a return
     * value of 2. However this does not kill the command on the remote machine. If you want to
     * transfer a folder, then it will transfer the folder as it is and it will also be available in
     * zip format.
     *
     * @param nodeUsername
     *        - user name of the Node Machine where the command need to be
     *        executed.
     * @param nodePassword
     *        - password of the Node Machine where the command need to be
     *        executed.
     * @param nodeIp
     *        - IP of the machine where the command need to be executed.
     * @param files
     *        - file to be transfered
     *
     * @return - returns a string containing console output of the execution.
     */
    public String sendFile(String files, int timeOutInSeconds,
            String remoteDirectoryPath) {
        JSch jsch = new JSch();
        Session session = null;
        String result = "";

        try {
            session = jsch.getSession(username, remoteHostIP, remoteHostPort);
            logger.info("=======Session is Opened=======");
            session.setPassword(password);
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            session.setConfig(config);
            session.connect();
            logger.info("=======Session is Connected=======");

            ChannelSftp channel = null;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            SftpATTRS attrs = null;

            try {
                attrs = channel.stat(remoteDirectoryPath);
            } catch (Exception e) {
                logger.info(remoteDirectoryPath + " not found");
            }
            if (attrs != null) {
                logger.info("Directory exists IsDir=" + attrs.isDir());
            } else {
                logger.info("Creating dir " + remoteDirectoryPath);
                try {
                channel.mkdir(remoteDirectoryPath);
                } catch (Exception e) {
                    logger.error("Not able to create directory");
                    throw new RuntimeException(e);
                }
            }

            String finalFilePath = null;
            String path = new File(files).getAbsolutePath();
            File file = new File(path);
            if (file.isDirectory()) {
                String zippedFile = zipFile(path);
                finalFilePath = new File(zippedFile).getAbsolutePath();
                File localFile = new File(finalFilePath);
                logger.info(localFile.getName());

                channel.cd(remoteDirectoryPath);
                channel.put(new FileInputStream(zippedFile), localFile.getName());
                Thread.sleep(2000);
                String getFileName = localFile.getName();
                executeCommand(
                        "unzip -o " + remoteDirectoryPath + "/" + getFileName + " -d "
                                + remoteDirectoryPath + "/" + getFileName.split("\\.")[0] + "/",
                        50);
            } else {
                finalFilePath = path;
                File localFile = new File(finalFilePath);
                logger.info(localFile.getName());

                channel.cd(remoteDirectoryPath);
                channel.put(new FileInputStream(localFile), localFile.getName());
            }
            int exitStatus = channel.getExitStatus();
            logger.info("-------exit status--------" + exitStatus);
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This method imports a log file from a remote machine and stores it in the
     * current project directory for further processing and analysis.
     *
     * @param logPath
     *        - Path of the file in remote machine
     * @destFileName name of the file to which teh log content have to be stored
     *               like destFileName
     * @throws RemoteAccessUtil_Exception
     */

    public void getLogFile(String logPath, String destFileName) throws RemoteAccessUtil_Exception {

        try {

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, remoteHostIP, remoteHostPort);
            logger.info("=======Session is Opened=======");
            session.setPassword(password);

            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            session.setConfig(config);
            session.connect();
            logger.info("=======Session is Connected=======");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            logger.info("=======Connected to Channel sftp=======");
            ChannelSftp c = (ChannelSftp) channel;

            logger.info("=======importing log file " + logPath);
            c.get(logPath, destFileName);
            logger.info("=======File was Imported=======");
            c.disconnect();
            session.disconnect();

        } catch (JSchException ex) {

            throw new RemoteAccessUtil_Exception("could not connect to server ", ex);

        } catch (SftpException ex) {

            throw new RemoteAccessUtil_Exception("log file transfer error ", ex);

        }
    }

    /**
     * This method reads the contents of the file in a remote machine and
     * returns its content as a string.
     * 
     * @param filePath
     *        - Path of the file to be read.
     * @return - String containing the contents of the file
     * @throws RemoteAccessUtil_Exception
     */

    public String readfromFile(String filePath) throws RemoteAccessUtil_Exception {

        try {

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, remoteHostIP, remoteHostPort);
            logger.info("=======Session is Opened=======");
            session.setPassword(password);

            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            session.setConfig(config);
            session.connect();
            logger.info("=======Session is Connected=======");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            logger.info("=======Connected to Channel sftp=======");
            ChannelSftp c = (ChannelSftp) channel;

            logger.info("=======Reading content from file " + filePath);

            InputStream out = c.get(filePath);
            String finalContent = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;

            while ((line = br.readLine()) != null) {

                finalContent = finalContent + line;
            }

            br.close();
            c.disconnect();
            session.disconnect();

            return finalContent;

        } catch (JSchException ex) {

            throw new RemoteAccessUtil_Exception("could not connect to server ", ex);

        } catch (SftpException ex) {

            throw new RemoteAccessUtil_Exception("log file transfer error ", ex);

        } catch (IOException ex) {

            throw new RemoteAccessUtil_Exception("Unable to open the file :  " + filePath, ex);
        }

    }

    /**
     * This method deletes the file on a remote machine.
     * 
     * @param logPath
     *        - Path of the file to be deleted.
     * @throws RemoteAccessUtil_Exception
     */
    public void deleteErrorLogFile(String logPath) throws RemoteAccessUtil_Exception {

        try {

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, remoteHostIP, remoteHostPort);
            logger.info("=======Session is Opened=======");
            session.setPassword(password);

            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            session.setConfig(config);
            session.connect();
            logger.info("=======Session is Connected=======");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            logger.info("=======Connected to Channel sftp=======");

            ChannelSftp c = (ChannelSftp) channel;

            logger.info("deleting log file " + logPath);
            c.rm(logPath);

            c.disconnect(); // closing session
            session.disconnect(); // closing session

        } catch (JSchException ex) {

            throw new RemoteAccessUtil_Exception("could not connect to server ", ex);

        } catch (SftpException ex) {

            if (!ex.getMessage().contains("No such file")) {
                throw new RemoteAccessUtil_Exception("configuration file transfer error ", ex);
            }
        }
    }

}
