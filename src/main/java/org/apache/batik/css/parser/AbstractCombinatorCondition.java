// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.Condition;
import org.w3c.css.sac.CombinatorCondition;

public abstract class AbstractCombinatorCondition implements CombinatorCondition
{
    protected Condition firstCondition;
    protected Condition secondCondition;
    
    protected AbstractCombinatorCondition(final Condition c1, final Condition c2) {
        this.firstCondition = c1;
        this.secondCondition = c2;
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
