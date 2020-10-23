// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.css.sac.Condition;

public class CSSAndCondition extends AbstractCombinatorCondition
{
    public CSSAndCondition(final Condition c1, final Condition c2) {
        super(c1, c2);
    }
    
    @Override
    public short getConditionType() {
        return 0;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        return ((ExtendedCondition)this.getFirstCondition()).match(e, pseudoE) && ((ExtendedCondition)this.getSecondCondition()).match(e, pseudoE);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        ((ExtendedCondition)this.getFirstCondition()).fillAttributeSet(attrSet);
        ((ExtendedCondition)this.getSecondCondition()).fillAttributeSet(attrSet);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getFirstCondition()) + this.getSecondCondition();
    }
}
