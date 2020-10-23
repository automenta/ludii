// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultPseudoElementSelector extends AbstractElementSelector
{
    public DefaultPseudoElementSelector(final String uri, final String name) {
        super(uri, name);
    }
    
    @Override
    public short getSelectorType() {
        return 9;
    }
    
    @Override
    public String toString() {
        return ":" + this.getLocalName();
    }
}
