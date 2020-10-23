// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import org.w3c.dom.Element;
import java.util.Map;
import org.apache.batik.util.SVGConstants;

public class DefaultStyleHandler implements StyleHandler, SVGConstants
{
    static Map ignoreAttributes;
    
    @Override
    public void setStyle(final Element element, final Map styleMap, final SVGGeneratorContext generatorContext) {
        final String tagName = element.getTagName();
        for (final Object o : styleMap.keySet()) {
            final String styleName = (String)o;
            if (element.getAttributeNS(null, styleName).length() == 0 && this.appliesTo(styleName, tagName)) {
                element.setAttributeNS(null, styleName, styleMap.get(styleName));
            }
        }
    }
    
    protected boolean appliesTo(final String styleName, final String tagName) {
        final Set s = DefaultStyleHandler.ignoreAttributes.get(tagName);
        return s == null || !s.contains(styleName);
    }
    
    static {
        DefaultStyleHandler.ignoreAttributes = new HashMap();
        final Set textAttributes = new HashSet();
        textAttributes.add("font-size");
        textAttributes.add("font-family");
        textAttributes.add("font-style");
        textAttributes.add("font-weight");
        DefaultStyleHandler.ignoreAttributes.put("rect", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("circle", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("ellipse", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("polygon", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("polygon", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("line", textAttributes);
        DefaultStyleHandler.ignoreAttributes.put("path", textAttributes);
    }
}
