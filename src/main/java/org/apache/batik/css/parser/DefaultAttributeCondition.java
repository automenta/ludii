// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultAttributeCondition extends AbstractAttributeCondition
{
    protected String localName;
    protected String namespaceURI;
    protected boolean specified;
    
    public DefaultAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) {
        super(value);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.specified = specified;
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
    public String toString() {
        if (this.value == null) {
            return "[" + this.localName + "]";
        }
        return "[" + this.localName + "=\"" + this.value + "\"]";
    }
}
