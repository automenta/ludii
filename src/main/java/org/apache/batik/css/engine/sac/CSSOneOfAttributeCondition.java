// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.dom.Element;

public class CSSOneOfAttributeCondition extends CSSAttributeCondition
{
    public CSSOneOfAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) {
        super(localName, namespaceURI, specified, value);
    }
    
    @Override
    public short getConditionType() {
        return 7;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        final String attr = e.getAttribute(this.getLocalName());
        final String val = this.getValue();
        final int i = attr.indexOf(val);
        if (i == -1) {
            return false;
        }
        if (i != 0 && !Character.isSpaceChar(attr.charAt(i - 1))) {
            return false;
        }
        final int j = i + val.length();
        return j == attr.length() || (j < attr.length() && Character.isSpaceChar(attr.charAt(j)));
    }
    
    @Override
    public String toString() {
        return "[" + this.getLocalName() + "~=\"" + this.getValue() + "\"]";
    }
}
