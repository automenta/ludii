// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.Condition;
import org.w3c.css.sac.CombinatorCondition;

public abstract class AbstractCombinatorCondition implements CombinatorCondition, ExtendedCondition
{
    protected Condition firstCondition;
    protected Condition secondCondition;
    
    protected AbstractCombinatorCondition(final Condition c1, final Condition c2) {
        this.firstCondition = c1;
        this.secondCondition = c2;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final AbstractCombinatorCondition c = (AbstractCombinatorCondition)obj;
        return c.firstCondition.equals(this.firstCondition) && c.secondCondition.equals(this.secondCondition);
    }
    
    @Override
    public int getSpecificity() {
        return ((ExtendedCondition)this.getFirstCondition()).getSpecificity() + ((ExtendedCondition)this.getSecondCondition()).getSpecificity();
    }
    
    @Override
    public Condition getFirstCondition() {
        return this.firstCondition;
    }
    
    @Override
    public Condition getSecondCondition() {
        return this.secondCondition;
    }
}
