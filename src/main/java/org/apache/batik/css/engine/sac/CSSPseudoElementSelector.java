// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.dom.Element;

public class CSSPseudoElementSelector extends AbstractElementSelector
{
    public CSSPseudoElementSelector(final String uri, final String name) {
        super(uri, name);
    }
    
    @Override
    public short getSelectorType() {
        return 9;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        return this.getLocalName().equalsIgnoreCase(pseudoE);
    }
    
    @Override
    public int getSpecificity() {
        return 0;
    }
    
    @Override
    public String toString() {
        return ":" + this.getLocalName();
    }
}
