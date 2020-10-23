// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGLinearGradientElement;

public class SVGOMLinearGradientElement extends SVGOMGradientElement implements SVGLinearGradientElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength x1;
    protected SVGOMAnimatedLength y1;
    protected SVGOMAnimatedLength x2;
    protected SVGOMAnimatedLength y2;
    
    protected SVGOMLinearGradientElement() {
    }
    
    public SVGOMLinearGradientElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x1 = this.createLiveAnimatedLength(null, "x1", "0%", (short)2, false);
        this.y1 = this.createLiveAnimatedLength(null, "y1", "0%", (short)1, false);
        this.x2 = this.createLiveAnimatedLength(null, "x2", "100%", (short)2, false);
        this.y2 = this.createLiveAnimatedLength(null, "y2", "0%", (short)1, false);
    }
    
    @Override
    public String getLocalName() {
        return "linearGradient";
    }
    
    @Override
    public SVGAnimatedLength getX1() {
        return this.x1;
    }
    
    @Override
    public SVGAnimatedLength getY1() {
        return this.y1;
    }
    
    @Override
    public SVGAnimatedLength getX2() {
        return this.x2;
    }
    
    @Override
    public SVGAnimatedLength getY2() {
        return this.y2;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMLinearGradientElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMLinearGradientElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMGradientElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        SVGOMLinearGradientElement.xmlTraitInformation = t;
    }
}
