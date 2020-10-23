// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.dom.AbstractDocument;

public abstract class SVGDescriptiveElement extends SVGStylableElement
{
    protected SVGDescriptiveElement() {
    }
    
    protected SVGDescriptiveElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }
    
    public void setXMLlang(final String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }
    
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }
    
    public void setXMLspace(final String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }
}
