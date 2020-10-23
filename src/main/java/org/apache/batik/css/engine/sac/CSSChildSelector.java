// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;

public class CSSChildSelector extends AbstractDescendantSelector
{
    public CSSChildSelector(final Selector ancestor, final SimpleSelector simple) {
        super(ancestor, simple);
    }
    
    @Override
    public short getSelectorType() {
        return 11;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        final Node n = e.getParentNode();
        return n != null && n.getNodeType() == 1 && ((ExtendedSelector)this.getAncestorSelector()).match((Element)n, null) && ((ExtendedSelector)this.getSimpleSelector()).match(e, pseudoE);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        ((ExtendedSelector)this.getAncestorSelector()).fillAttributeSet(attrSet);
        ((ExtendedSelector)this.getSimpleSelector()).fillAttributeSet(attrSet);
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
