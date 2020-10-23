// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.dom.Element;

public class CSSElementSelector extends AbstractElementSelector
{
    public CSSElementSelector(final String uri, final String name) {
        super(uri, name);
    }
    
    @Override
    public short getSelectorType() {
        return 4;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        final String name = this.getLocalName();
        if (name == null) {
            return true;
        }
        String eName;
        if (e.getPrefix() == null) {
            eName = e.getNodeName();
        }
        else {
            eName = e.getLocalName();
        }
        return eName.equals(name);
    }
    
    @Override
    public int getSpecificity() {
        return (this.getLocalName() != null) ? 1 : 0;
    }
    
    @Override
    public String toString() {
        final String name = this.getLocalName();
        if (name == null) {
            return "*";
        }
        return name;
    }
}
