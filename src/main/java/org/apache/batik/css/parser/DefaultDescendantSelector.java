// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class DefaultDescendantSelector extends AbstractDescendantSelector
{
    public DefaultDescendantSelector(final Selector ancestor, final SimpleSelector simple) {
        super(ancestor, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 10;
    }
    
    @Override
    public String toString() {
        return this.getAncestorSelector() + " " + this.getSimpleSelector();
    }
}
