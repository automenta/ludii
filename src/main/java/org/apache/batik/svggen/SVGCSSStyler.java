// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.NodeList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGCSSStyler implements SVGSyntax
{
    private static final char CSS_PROPERTY_VALUE_SEPARATOR = ':';
    private static final char CSS_RULE_SEPARATOR = ';';
    private static final char SPACE = ' ';
    
    public static void style(final Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Element element = (Element)node;
            final StringBuffer styleAttrBuffer = new StringBuffer();
            final int nAttr = attributes.getLength();
            final List toBeRemoved = new ArrayList();
            for (int i = 0; i < nAttr; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String attrName = attr.getName();
                if (SVGStylingAttributes.set.contains(attrName)) {
                    styleAttrBuffer.append(attrName);
                    styleAttrBuffer.append(':');
                    styleAttrBuffer.append(attr.getValue());
                    styleAttrBuffer.append(';');
                    styleAttrBuffer.append(' ');
                    toBeRemoved.add(attrName);
                }
            }
            if (styleAttrBuffer.length() > 0) {
                element.setAttributeNS(null, "style", styleAttrBuffer.toString().trim());
                final int n = toBeRemoved.size();
                for (final Object aToBeRemoved : toBeRemoved) {
                    element.removeAttribute((String)aToBeRemoved);
                }
            }
        }
        final NodeList children = node.getChildNodes();
        for (int nChildren = children.getLength(), j = 0; j < nChildren; ++j) {
            final Node child = children.item(j);
            style(child);
        }
    }
}
