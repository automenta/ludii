// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class DefaultElementSelector extends AbstractElementSelector
{
    public DefaultElementSelector(final String uri, final String name) {
        super(uri, name);
    }
    
    @Override
    public short getSelectorType() {
        return 4;
    }
    
    @Override
    public String toString() {
        final String name = this.getLocalName();
        if (name == null) {
            return "*";
        }
        return name;
    }
}
