// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.Attr;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGRadialGradientElement;

public class SVGOMRadialGradientElement extends SVGOMGradientElement implements SVGRadialGradientElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength cx;
    protected SVGOMAnimatedLength cy;
    protected AbstractSVGAnimatedLength fx;
    protected AbstractSVGAnimatedLength fy;
    protected SVGOMAnimatedLength r;
    
    protected SVGOMRadialGradientElement() {
    }
    
    public SVGOMRadialGradientElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.cx = this.createLiveAnimatedLength(null, "cx", "50%", (short)2, false);
        this.cy = this.createLiveAnimatedLength(null, "cy", "50%", (short)1, false);
        this.r = this.createLiveAnimatedLength(null, "r", "50%", (short)0, false);
        this.fx = new AbstractSVGAnimatedLength(this, null, "fx", 2, false) {
            @Override
            protected String getDefaultValue() {
                final Attr attr = SVGOMRadialGradientElement.this.getAttributeNodeNS(null, "cx");
                if (attr == null) {
                    return "50%";
                }
                return attr.getValue();
            }
        };
        this.fy = new AbstractSVGAnimatedLength(this, null, "fy", 1, false) {
            @Override
            protected String getDefaultValue() {
                final Attr attr = SVGOMRadialGradientElement.this.getAttributeNodeNS(null, "cy");
                if (attr == null) {
                    return "50%";
                }
                return attr.getValue();
            }
        };
        this.liveAttributeValues.put(null, "fx", this.fx);
        this.liveAttributeValues.put(null, "fy", this.fy);
        final AnimatedAttributeListener l = ((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener();
        this.fx.addAnimatedAttributeListener(l);
        this.fy.addAnimatedAttributeListener(l);
    }
    
    @Override
    public String getLocalName() {
        return "radialGradient";
    }
    
    @Override
    public SVGAnimatedLength getCx() {
        return this.cx;
    }
    
    @Override
    public SVGAnimatedLength getCy() {
        return this.cy;
    }
    
    @Override
    public SVGAnimatedLength getR() {
        return this.r;
    }
    
    @Override
    public SVGAnimatedLength getFx() {
        return this.fx;
    }
    
    @Override
    public SVGAnimatedLength getFy() {
        return this.fy;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMRadialGradientElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMRadialGradientElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMGradientElement.xmlTraitInformation);
        t.put(null, "cx", new TraitInformation(true, 3, (short)1));
        t.put(null, "cy", new TraitInformation(true, 3, (short)2));
        t.put(null, "fx", new TraitInformation(true, 3, (short)1));
        t.put(null, "fy", new TraitInformation(true, 3, (short)2));
        t.put(null, "r", new TraitInformation(true, 3, (short)3));
        SVGOMRadialGradientElement.xmlTraitInformation = t;
    }
}
