// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.Condition;

public class DefaultAndCondition extends AbstractCombinatorCondition
{
    public DefaultAndCondition(final Condition c1, final Condition c2) {
        super(c1, c2);
    }
    
    @Override
    public short getConditionType() {
        return 0;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getFirstCondition()) + this.getSecondCondition();
    }
}
