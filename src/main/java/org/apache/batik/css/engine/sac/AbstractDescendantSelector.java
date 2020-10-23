// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.DescendantSelector;

public abstract class AbstractDescendantSelector implements DescendantSelector, ExtendedSelector
{
    protected Selector ancestorSelector;
    protected SimpleSelector simpleSelector;
    
    protected AbstractDescendantSelector(final Selector ancestor, final SimpleSelector simple) {
        this.ancestorSelector = ancestor;
        this.simpleSelector = simple;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final AbstractDescendantSelector s = (AbstractDescendantSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector);
    }
    
    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.ancestorSelector).getSpecificity() + ((ExtendedSelector)this.simpleSelector).getSpecificity();
    }
    
    @Override
    public Selector getAncestorSelector() {
        return this.ancestorSelector;
    }
    
    @Override
    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }
}
