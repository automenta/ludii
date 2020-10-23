// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultClassCondition extends DefaultAttributeCondition
{
    public DefaultClassCondition(final String namespaceURI, final String value) {
        super("class", namespaceURI, true, value);
    }
    
    @Override
    public short getConditionType() {
        return 9;
    }
    
    @Override
    public String toString() {
        return "." + this.getValue();
    }
}
