// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.dom.Element;

public class CSSBeginHyphenAttributeCondition extends CSSAttributeCondition
{
    public CSSBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) {
        super(localName, namespaceURI, specified, value);
    }
    
    @Override
    public short getConditionType() {
        return 8;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        return e.getAttribute(this.getLocalName()).startsWith(this.getValue());
    }
    
    @Override
    public String toString() {
        return '[' + this.getLocalName() + "|=\"" + this.getValue() + "\"]";
    }
}
