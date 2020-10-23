// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;

public class XMLResourceDescriptor
{
    public static final String XML_PARSER_CLASS_NAME_KEY = "org.xml.sax.driver";
    public static final String CSS_PARSER_CLASS_NAME_KEY = "org.w3c.css.sac.driver";
    public static final String RESOURCES = "resources/XMLResourceDescriptor.properties";
    protected static Properties parserProps;
    protected static String xmlParserClassName;
    protected static String cssParserClassName;
    
    protected static synchronized Properties getParserProps() {
        if (XMLResourceDescriptor.parserProps != null) {
            return XMLResourceDescriptor.parserProps;
        }
        XMLResourceDescriptor.parserProps = new Properties();
        try {
            final Class cls = XMLResourceDescriptor.class;
            final InputStream is = cls.getResourceAsStream("resources/XMLResourceDescriptor.properties");
            XMLResourceDescriptor.parserProps.load(is);
        }
        catch (IOException ioe) {
            throw new MissingResourceException(ioe.getMessage(), "resources/XMLResourceDescriptor.properties", null);
        }
        return XMLResourceDescriptor.parserProps;
    }
    
    public static String getXMLParserClassName() {
        if (XMLResourceDescriptor.xmlParserClassName == null) {
            XMLResourceDescriptor.xmlParserClassName = getParserProps().getProperty("org.xml.sax.driver");
        }
        return XMLResourceDescriptor.xmlParserClassName;
    }
    
    public static void setXMLParserClassName(final String xmlParserClassName) {
        XMLResourceDescriptor.xmlParserClassName = xmlParserClassName;
    }
    
    public static String getCSSParserClassName() {
        if (XMLResourceDescriptor.cssParserClassName == null) {
            XMLResourceDescriptor.cssParserClassName = getParserProps().getProperty("org.w3c.css.sac.driver");
        }
        return XMLResourceDescriptor.cssParserClassName;
    }
    
    public static void setCSSParserClassName(final String cssParserClassName) {
        XMLResourceDescriptor.cssParserClassName = cssParserClassName;
    }
    
    static {
        XMLResourceDescriptor.parserProps = null;
    }
}
