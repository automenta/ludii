// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class CSSDirectAdjacentSelector extends AbstractSiblingSelector
{
    public CSSDirectAdjacentSelector(final short type, final Selector parent, final SimpleSelector simple) {
        super(type, parent, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 12;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        Node n = e;
        if (!((ExtendedSelector)this.getSiblingSelector()).match(e, pseudoE)) {
            return false;
        }
        while ((n = n.getPreviousSibling()) != null && n.getNodeType() != 1) {}
        return n != null && ((ExtendedSelector)this.getSelector()).match((Element)n, null);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        ((ExtendedSelector)this.getSelector()).fillAttributeSet(attrSet);
        ((ExtendedSelector)this.getSiblingSelector()).fillAttributeSet(attrSet);
    }
    
    @Override
    public String toString() {
        return this.getSelector() + " + " + this.getSiblingSelector();
    }
}
