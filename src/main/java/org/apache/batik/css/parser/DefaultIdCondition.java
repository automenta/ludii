// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultIdCondition extends AbstractAttributeCondition
{
    public DefaultIdCondition(final String value) {
        super(value);
    }
    
    @Override
    public short getConditionType() {
        return 5;
    }
    
    @Override
    public String getNamespaceURI() {
        return null;
    }
    
    @Override
    public String getLocalName() {
        return "id";
    }
    
    @Override
    public boolean getSpecified() {
        return true;
    }
    
    @Override
    public String toString() {
        return "#" + this.getValue();
    }
}
