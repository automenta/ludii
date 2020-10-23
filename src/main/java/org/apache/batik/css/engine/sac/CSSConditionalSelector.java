// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.ConditionalSelector;

public class CSSConditionalSelector implements ConditionalSelector, ExtendedSelector
{
    protected SimpleSelector simpleSelector;
    protected Condition condition;
    
    public CSSConditionalSelector(final SimpleSelector s, final Condition c) {
        this.simpleSelector = s;
        this.condition = c;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final CSSConditionalSelector s = (CSSConditionalSelector)obj;
        return s.simpleSelector.equals(this.simpleSelector) && s.condition.equals(this.condition);
    }
    
    @Override
    public short getSelectorType() {
        return 0;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        return ((ExtendedSelector)this.getSimpleSelector()).match(e, pseudoE) && ((ExtendedCondition)this.getCondition()).match(e, pseudoE);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        ((ExtendedSelector)this.getSimpleSelector()).fillAttributeSet(attrSet);
        ((ExtendedCondition)this.getCondition()).fillAttributeSet(attrSet);
    }
    
    @Override
    public int getSpecificity() {
        return ((ExtendedSelector)this.getSimpleSelector()).getSpecificity() + ((ExtendedCondition)this.getCondition()).getSpecificity();
    }
    
    @Override
    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }
    
    @Override
    public Condition getCondition() {
        return this.condition;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.simpleSelector) + this.condition;
    }
}
