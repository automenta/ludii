// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.Element;

public class CSSClassCondition extends CSSAttributeCondition
{
    public CSSClassCondition(final String localName, final String namespaceURI, final String value) {
        super(localName, namespaceURI, true, value);
    }
    
    @Override
    public short getConditionType() {
        return 9;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        if (!(e instanceof CSSStylableElement)) {
            return false;
        }
        final String attr = ((CSSStylableElement)e).getCSSClass();
        final String val = this.getValue();
        final int attrLen = attr.length();
        for (int valLen = val.length(), i = attr.indexOf(val); i != -1; i = attr.indexOf(val, i + valLen)) {
            if ((i == 0 || Character.isSpaceChar(attr.charAt(i - 1))) && (i + valLen == attrLen || Character.isSpaceChar(attr.charAt(i + valLen)))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return '.' + this.getValue();
    }
}
