// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class CSSDescendantSelector extends AbstractDescendantSelector
{
    public CSSDescendantSelector(final Selector ancestor, final SimpleSelector simple) {
        super(ancestor, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 10;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        final ExtendedSelector p = (ExtendedSelector)this.getAncestorSelector();
        if (!((ExtendedSelector)this.getSimpleSelector()).match(e, pseudoE)) {
            return false;
        }
        for (Node n = e.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() == 1 && p.match((Element)n, null)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        ((ExtendedSelector)this.getSimpleSelector()).fillAttributeSet(attrSet);
    }
    
    @Override
    public String toString() {
        return this.getAncestorSelector() + " " + this.getSimpleSelector();
    }
}
