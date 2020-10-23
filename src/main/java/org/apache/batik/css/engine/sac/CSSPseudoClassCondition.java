// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.Element;

public class CSSPseudoClassCondition extends AbstractAttributeCondition
{
    protected String namespaceURI;
    
    public CSSPseudoClassCondition(final String namespaceURI, final String value) {
        super(value);
        this.namespaceURI = namespaceURI;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final CSSPseudoClassCondition c = (CSSPseudoClassCondition)obj;
        return c.namespaceURI.equals(this.namespaceURI);
    }
    
    @Override
    public int hashCode() {
        return this.namespaceURI.hashCode();
    }
    
    @Override
    public short getConditionType() {
        return 10;
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public String getLocalName() {
        return null;
    }
    
    @Override
    public boolean getSpecified() {
        return false;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        return e instanceof CSSStylableElement && ((CSSStylableElement)e).isPseudoInstanceOf(this.getValue());
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
    }
    
    @Override
    public String toString() {
        return ":" + this.getValue();
    }
}
