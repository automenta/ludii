// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class DefaultChildSelector extends AbstractDescendantSelector
{
    public DefaultChildSelector(final Selector ancestor, final SimpleSelector simple) {
        super(ancestor, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 11;
    }
    
    @Override
    public String toString() {
        final SimpleSelector s = this.getSimpleSelector();
        if (s.getSelectorType() == 9) {
            return String.valueOf(this.getAncestorSelector()) + s;
        }
        return this.getAncestorSelector() + " > " + s;
    }
}
