package com.optum.bdd.core.soap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;

public class SoapNameSpaceContext implements NamespaceContext {
    public static final String SOAP = "_soap_";
    public static final String SOAPENV = "_soapenv_";
    public static final String XSD = "_xsd_";
    public static final String WSDL = "_wsdl_";
    private Map<String, String> nameSpaceURIS;

    public SoapNameSpaceContext() {
        this.nameSpaceURIS = new HashMap<>();
        this.nameSpaceURIS.put("_soap_", "http://schemas.xmlsoap.org/wsdl/soap/");
        this.nameSpaceURIS.put("_wsdl_", "http://schemas.xmlsoap.org/wsdl/");
        this.nameSpaceURIS.put("_xsd_", "http://www.w3.org/2001/XMLSchema");
        this.nameSpaceURIS.put("_soapenv_", "http://schemas.xmlsoap.org/soap/envelope/");
    }

    public SoapNameSpaceContext(String text) {
        this.nameSpaceURIS = new HashMap<>();
        addNameSpaces(text);
    }

    public SoapNameSpaceContext(SoapNameSpaceContext paramSoapDustNameSpaceContext) {
        this.nameSpaceURIS =
                new HashMap<>(paramSoapDustNameSpaceContext.nameSpaceURIS);
    }

    public String getNamespaceURI(String paramString) {
        String uri = this.nameSpaceURIS.get(paramString);
        return uri;
    }

    public String getPrefix(String paramString) {
        throw new UnsupportedOperationException();
    }

    public Iterator<?> getPrefixes(String paramString) {
        throw new UnsupportedOperationException();
    }

    public void addNamespace(String paramString1, String paramString2) {
        this.nameSpaceURIS.put(paramString1, paramString2);
    }

    private void addNameSpaces(String text) {

        String nameSpaceExpression = "<\\w+:";
        String nameSpaceUriExpression = "=\".+?\"";

        Pattern pattern = Pattern.compile(nameSpaceExpression);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // Get all groups for this match
            String groupStr = matcher.group();

            String namespace = groupStr.replace("<", "").replace(":", "");

            if (!this.nameSpaceURIS.containsKey(namespace)) {

                Pattern nameSpaceUriPattern =
                        Pattern.compile("xmlns:" + namespace + nameSpaceUriExpression);
                Matcher nameSpaceUriMatcher = nameSpaceUriPattern.matcher(text);

                if (nameSpaceUriMatcher.find()) {
                    String nameSpaceUri = nameSpaceUriMatcher.group();
                    nameSpaceUri =
                            nameSpaceUri.replace("xmlns:" + namespace + "=", "").replace("\"", "");

                    this.nameSpaceURIS.put(namespace, nameSpaceUri);
                }

            }

        }
    }
}
