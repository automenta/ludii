// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.apache.batik.constants.XMLConstants;

public final class XMLSupport implements XMLConstants
{
    private XMLSupport() {
    }
    
    public static String getXMLLang(final Element elt) {
        Attr attr = elt.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang");
        if (attr != null) {
            return attr.getNodeValue();
        }
        for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() == 1) {
                attr = ((Element)n).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang");
                if (attr != null) {
                    return attr.getNodeValue();
                }
            }
        }
        return "en";
    }
    
    public static String getXMLSpace(final Element elt) {
        Attr attr = elt.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "space");
        if (attr != null) {
            return attr.getNodeValue();
        }
        for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() == 1) {
                attr = ((Element)n).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "space");
                if (attr != null) {
                    return attr.getNodeValue();
                }
            }
        }
        return "default";
    }
    
    public static String defaultXMLSpace(final String data) {
        final int nChars = data.length();
        final StringBuffer result = new StringBuffer(nChars);
        boolean space = false;
        for (int i = 0; i < nChars; ++i) {
            final char c = data.charAt(i);
            switch (c) {
                case '\n':
                case '\r': {
                    space = false;
                    break;
                }
                case '\t':
                case ' ': {
                    if (!space) {
                        result.append(' ');
                        space = true;
                        break;
                    }
                    break;
                }
                default: {
                    result.append(c);
                    space = false;
                    break;
                }
            }
        }
        return result.toString().trim();
    }
    
    public static String preserveXMLSpace(final String data) {
        final int nChars = data.length();
        final StringBuffer result = new StringBuffer(nChars);
        for (int i = 0; i < data.length(); ++i) {
            final char c = data.charAt(i);
            switch (c) {
                case '\t':
                case '\n':
                case '\r': {
                    result.append(' ');
                    break;
                }
                default: {
                    result.append(c);
                    break;
                }
            }
        }
        return result.toString();
    }
}
