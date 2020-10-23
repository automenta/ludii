// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.apache.batik.constants.XMLConstants;

public class XLinkSupport implements XMLConstants
{
    public static String getXLinkType(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "type");
    }
    
    public static void setXLinkType(final Element elt, final String str) {
        if (!"simple".equals(str) && !"extended".equals(str) && !"locator".equals(str) && !"arc".equals(str)) {
            throw new DOMException((short)12, "xlink:type='" + str + "'");
        }
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "type", str);
    }
    
    public static String getXLinkRole(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "role");
    }
    
    public static void setXLinkRole(final Element elt, final String str) {
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "role", str);
    }
    
    public static String getXLinkArcRole(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "arcrole");
    }
    
    public static void setXLinkArcRole(final Element elt, final String str) {
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "arcrole", str);
    }
    
    public static String getXLinkTitle(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "title");
    }
    
    public static void setXLinkTitle(final Element elt, final String str) {
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "title", str);
    }
    
    public static String getXLinkShow(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "show");
    }
    
    public static void setXLinkShow(final Element elt, final String str) {
        if (!"new".equals(str) && !"replace".equals(str) && !"embed".equals(str)) {
            throw new DOMException((short)12, "xlink:show='" + str + "'");
        }
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "show", str);
    }
    
    public static String getXLinkActuate(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "actuate");
    }
    
    public static void setXLinkActuate(final Element elt, final String str) {
        if (!"onReplace".equals(str) && !"onLoad".equals(str)) {
            throw new DOMException((short)12, "xlink:actuate='" + str + "'");
        }
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "actuate", str);
    }
    
    public static String getXLinkHref(final Element elt) {
        return elt.getAttributeNS("http://www.w3.org/1999/xlink", "href");
    }
    
    public static void setXLinkHref(final Element elt, final String str) {
        elt.setAttributeNS("http://www.w3.org/1999/xlink", "href", str);
    }
}
