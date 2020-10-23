// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

public interface CSSStylableElement extends Element
{
    StyleMap getComputedStyleMap(final String p0);
    
    void setComputedStyleMap(final String p0, final StyleMap p1);
    
    String getXMLId();
    
    String getCSSClass();
    
    ParsedURL getCSSBase();
    
    boolean isPseudoInstanceOf(final String p0);
    
    StyleDeclarationProvider getOverrideStyleDeclarationProvider();
}
