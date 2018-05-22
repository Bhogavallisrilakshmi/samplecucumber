package com.optum.bdd.core.rest;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Mohan
 *
 */
public class XMLUtil {


    private static File file = null;
    private static Scanner input = null;
    private static StringBuilder content = null;
    private static Logger log = Logger.getLogger(XMLUtil.class.getName());

    public XMLUtil() {
        // TODO Auto-generated constructor stub
    }


    // Convert File to string
    // @SuppressWarnings("resource")
    public static String fileToString(String filePath) throws RestException {
        try {
            if (filePath != null) {
                file = new File(filePath);
                content = new StringBuilder();
                input = new Scanner(file);
                while (input.hasNextLine()) {
                    content.append(input.nextLine());
                }
            }
        } catch (Exception ex) {
            throw new RestException("RestException : " + ex.getMessage());
        }
        return content.toString();
    }

    // Formatting String to XML
    public static String formatToXml(String dirtyXML) throws RestException {
        String cleanXml = null;
        try {
            if (dirtyXML != null) {
                // if (FileHelper.exists(text)) return text; text =
                // text.replaceAll("<\\?xml.+?version.+?\\?>", "");

                if (dirtyXML.startsWith("null")) {
                    dirtyXML = dirtyXML.substring(4);
                }
                if (dirtyXML.startsWith("\n")) {
                    dirtyXML = dirtyXML.substring(1);
                } else if (dirtyXML.startsWith("\r\n")) {
                    dirtyXML = dirtyXML.substring(1);
                }

                cleanXml = dirtyXML.replaceAll("\r\n", "")
                        .replaceAll("\n", "")
                        .replaceAll("\t", "")
                        .replaceAll(">        <", "><")
                        .replaceAll(">  <", "><")
                        .replaceAll(">\\s+<", "><")
                        .replaceAll(">	", "")
                        .replaceAll(" <", "")
                        .replaceAll("><", ">\n<");
            }
        } catch (Exception ex) {
            throw new RestException("RestException : " + ex.getMessage());
        }

        return cleanXml;
    }

    // Decode string to xml
    public static String decodeToXML(String content) throws RestException {
        try {
            if (content != null) {
                content = content.replace("&quot;", "\"");
                content = content.replace("&gt;", ">");
                content = content.replace("&lt;", "<");
                content = content.replace("&eq;", "=");
                content = content.replace("&apos;", "'");
            }
        } catch (Exception ex) {
            throw new RestException("RestException : " + ex.getMessage());
        }

        return content;
    }



    // Pattern match and replace based on string
    public static String matchAndReplace(String string,
            String patternMatch,
            String replaceString) throws RestException {
        String result = null;
        try {
            if (string != null) {
                result = new String(string);
            }

            if (string.contains(".")) {
                string = string.replace(".", "\\.");
            }

            Pattern pattern = Pattern.compile(patternMatch);
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                // Get all %Key% as groups for this match
                String groupString = matcher.group();
                // log.debug("PatternMatch=" + groupString);
                // match and replace
                if (groupString != "" | groupString != null) {
                    result = result.replace(groupString, replaceString);
                }
            }
        } catch (Exception ex) {

            throw new RestException("RestException : " + ex.getMessage());
        }

        return result;
    }

    // Pattern match and replace based on 2D-Array
    public static String matchAndReplace(String string,
            String patternMatch,
            String[][] inputData) throws RestException {
        String result = null;
        try {
            if (string != null) {
                result = new String(string);
            }

            if (string.contains(".")) {
                string = string.replace(".", "\\.");
            }

            Pattern pattern = Pattern.compile(patternMatch);
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                String groupString = matcher.group();
                // log.debug("PatternMatch = " + groupString);
                // iterating over pattern and replace
                for (int i = 0; i < inputData.length; i++) {
                    if (groupString.contains(inputData[i][0])) {
                        String replacementWith = inputData[i][1];
                        result = result.replace(groupString, replacementWith);
                    }
                }
            }

        } catch (Exception ex) {

            throw new RestException("RestException : " + ex.getMessage());
        }

        return result;
    }

    // Pattern match and replace based on String input
    public static String matchAndReplace(String string,
            String patternMatch,
            String... inputData) throws RestException {
        String result = null;
        try {
            if (string != null) {
                result = new String(string);
            }

            if (string.contains(".")) {
                string = string.replace(".", "\\.");
            }

            Pattern pattern = Pattern.compile(patternMatch);
            Matcher matcher = pattern.matcher(result);

            // iterating over Input Data and replace for matched pattern
            for (int i = 0; i < inputData.length; i++) {
                // log.debug("inputData["+i+"] ="+ inputData[i]);
                while (matcher.find()) {
                    String groupString = matcher.group();
                    // log.debug("PatternMatch = " + groupString);
                    String replacementWith = inputData[i];
                    result = result.replace(groupString, replacementWith);
                    break;
                }
            }
        } catch (Exception ex) {

            throw new RestException("RestException : " + ex.getMessage());
        }

        return result;
    }



    // Get Value of XPath
    public static String getXPathValue(String text, String paramString) throws RestException {

        Document localDocument = null;
        paramString = getXPathString(paramString);
        XPath localXPath = null;
        String value = null;

        try {
            // log.debug("Dirty Text: " + text);
            String cleanText = cleanXml(text);
            // log.debug("Clean Text: " + cleanText);
            localDocument = getDocument(cleanText);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String msg = e.getMessage();

            if (msg == null) {
                msg = "null";
            }
            log.error("Error parsing Document " + msg + "\r\n" + paramString
                    + "\r\n" + text);
            throw new RestException("Error parsing(Document) " + msg + "\r\n"
                    + paramString + "\r\n" + text);
        }

        try {
            localXPath = newXPath(cleanXml(text));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String msg = e.getMessage();

            if (msg == null) {
                msg = "null";
            }
            log.error("Error parsing(xPath) " + msg + "\r\n" + paramString
                    + "\r\n" + text);
            throw new RestException("Error parsing(xPath) " + msg + "\r\n"
                    + paramString + "\r\n" + text);
        }

        try {
            localDocument = getDocument(cleanXml(text));

            localXPath = newXPath(cleanXml(text));
            // localXPath = newXPath(localDocument);
            // log.debug("getXPathValue(): " + paramString + "\r\n" + text);

            value = localXPath.evaluate(paramString, localDocument);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String msg = e.getMessage();

            if (msg == null) {
                msg = "null";
            }
            log.error("Error parsing(XPathExpressionException) " + msg + "\r\n"
                    + paramString + "\r\n" + text);
            throw new RestException("Error parsing(XPathExpressionException) "
                    + msg + "\r\n" + paramString + "\r\n" + text);
        }

        return value;
    }

    private static String getXPathString(String xPath) {

        if (!isNameSpaceAware()) {
            return stripNameSpaceFromXpath(xPath);
        }

        return xPath;
    }


    public static String stripNameSpaceFromXpath(String xpath) {
        String[] array = xpath.split("/");
        String newxPath = "/";

        for (String s : array) {
            if (s.matches("^.+?:.*")) {
                newxPath += s.replaceAll("^.+?:", "") + "/";
            } else {
                newxPath += s + "/";
            }
        }

        newxPath = newxPath.substring(1);
        newxPath = newxPath.substring(0, newxPath.length() - 1);

        return newxPath;
    }

    public static String cleanXml(String content) throws RestException {

        String cleanText = null;
        if (content != null) {
            content = content.replaceAll("<\\?xml.+?version.+?\\?>", "");
            if (content.startsWith("null")) {
                content = content.substring(4);
            }
            if (content.startsWith("\n")) {
                content = content.substring(1);
            } else if (content.startsWith("\r\n")) {
                content = content.substring(1);
            }

            cleanText = content.replaceAll("\r\n", " ")
                    .replaceAll("\n", " ")
                    .replaceAll("\t", "")
                    .replaceAll(">        <", "><")
                    .replaceAll(">  <", "><")
                    .replaceAll(">\\s+<", "><");
        }
        return cleanText;
    }


    @SuppressWarnings("deprecation")
    public static Document getDocument(String text) throws RestException {

        DocumentBuilder loader = null;
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            if (factory == null) {
                throw new ParserConfigurationException("factory is null");
            }

            factory.setNamespaceAware(isNameSpaceAware());
            factory.setValidating(false);

            try {
                loader = factory.newDocumentBuilder();

            } catch (Exception ex) {
                log.debug("Error parsing(ParserConfigurationException) "
                        + ex.getMessage() + "\r\n" + text);
            }

            // log.debug("Loader Created");
            if (loader == null) {
                throw new ParserConfigurationException("Loader is null");
            }

            if (text != null) {
                // document = loader.parse(text);
                document = loader.parse(new StringBufferInputStream(text));
            } else {
                document = loader.parse(new InputSource(new StringReader(text)));
            }

        } catch (ParserConfigurationException ex) {
            throw new RestException("Error parsing(ParserConfigurationException)" + ex.getMessage()
                    + "\r\n" + text);
        } catch (SAXException ex) {
            throw new RestException(
                    "Error parsing(SAXException)" + ex.getMessage() + "\r\n" + text);
        } catch (IOException ex) {
            throw new RestException("Error parsing(IOException)" + ex.getMessage() + "\r\n" + text);
        }

        return document;
    }

    private static XPath newXPath(String text) throws RestException {
        XPath localXPath = null;
        try {
            XPathFactory localXPathFactory = XPathFactory.newInstance();
            localXPath = localXPathFactory.newXPath();
            // if (isNameSpaceAware())
            // localXPath.setNamespaceContext(new SoapNameSpaceContext(cleanXml(text)));

        } catch (Exception ex) {
            throw new RestException("RestException" + ex.getMessage());
        }
        return localXPath;
    }

    private static boolean isNameSpaceAware = true;

    private static boolean isNameSpaceAware() {
        return isNameSpaceAware;
    }

    public static void setNameSpaceAware(boolean value) {
        isNameSpaceAware = value;
    }

}
