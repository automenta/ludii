// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

public class SVGOMAnimatedLength extends AbstractSVGAnimatedLength
{
    protected String defaultValue;
    
    public SVGOMAnimatedLength(final AbstractElement elt, final String ns, final String ln, final String def, final short dir, final boolean nonneg) {
        super(elt, ns, ln, dir, nonneg);
        this.defaultValue = def;
    }
    
    @Override
    protected String getDefaultValue() {
        return this.defaultValue;
    }
}
