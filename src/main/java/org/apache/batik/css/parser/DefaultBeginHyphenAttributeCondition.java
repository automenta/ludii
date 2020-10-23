// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultBeginHyphenAttributeCondition extends DefaultAttributeCondition
{
    public DefaultBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) {
        super(localName, namespaceURI, specified, value);
    }
    
    @Override
    public short getConditionType() {
        return 8;
    }
    
    @Override
    public String toString() {
        return "[" + this.getLocalName() + "|=\"" + this.getValue() + "\"]";
    }
}
