// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class DefaultDirectAdjacentSelector extends AbstractSiblingSelector
{
    public DefaultDirectAdjacentSelector(final short type, final Selector parent, final SimpleSelector simple) {
        super(type, parent, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 12;
    }
    
    @Override
    public String toString() {
        return this.getSelector() + " + " + this.getSiblingSelector();
    }
}
