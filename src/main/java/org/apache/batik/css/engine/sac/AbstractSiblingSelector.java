// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;

public abstract class AbstractSiblingSelector implements SiblingSelector, ExtendedSelector
{
    protected short nodeType;
    protected Selector selector;
    protected SimpleSelector simpleSelector;
    
    protected AbstractSiblingSelector(final short type, final Selector sel, final SimpleSelector simple) {
        this.nodeType = type;
        this.selector = sel;
        this.simpleSelector = simple;
    }
    
    @Override
    public short getNodeType() {
        return this.nodeType;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final AbstractSiblingSelector s = (AbstractSiblingSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector);
    }
    
    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.selector).getSpecificity() + ((ExtendedSelector)this.simpleSelector).getSpecificity();
    }
    
    @Override
    public Selector getSelector() {
        return this.selector;
    }
    
    @Override
    public SimpleSelector getSiblingSelector() {
        return this.simpleSelector;
    }
}
