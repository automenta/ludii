// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultPseudoClassCondition extends AbstractAttributeCondition
{
    protected String namespaceURI;
    
    public DefaultPseudoClassCondition(final String namespaceURI, final String value) {
        super(value);
        this.namespaceURI = namespaceURI;
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
    public String toString() {
        return ":" + this.getValue();
    }
}
