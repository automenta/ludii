// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

public class SVGOMLength extends AbstractSVGLength
{
    protected AbstractElement element;
    
    public SVGOMLength(final AbstractElement elt) {
        super((short)0);
        this.element = elt;
    }
    
    @Override
    protected SVGOMElement getAssociatedElement() {
        return (SVGOMElement)this.element;
    }
}
