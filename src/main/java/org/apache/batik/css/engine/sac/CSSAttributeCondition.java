// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Element;

public class CSSAttributeCondition extends AbstractAttributeCondition
{
    protected String localName;
    protected String namespaceURI;
    protected boolean specified;
    
    public CSSAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) {
        super(value);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.specified = specified;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final CSSAttributeCondition c = (CSSAttributeCondition)obj;
        return c.namespaceURI.equals(this.namespaceURI) && c.localName.equals(this.localName) && c.specified == this.specified;
    }
    
    @Override
    public int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localName.hashCode() ^ (this.specified ? -1 : 0);
    }
    
    @Override
    public short getConditionType() {
        return 4;
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    public boolean getSpecified() {
        return this.specified;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        final String val = this.getValue();
        if (val == null) {
            return !e.getAttribute(this.getLocalName()).equals("");
        }
        return e.getAttribute(this.getLocalName()).equals(val);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        attrSet.add(this.localName);
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            return '[' + this.localName + ']';
        }
        return '[' + this.localName + "=\"" + this.value + "\"]";
    }
}
