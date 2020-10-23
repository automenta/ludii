// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGCircleElement;

public class SVGOMCircleElement extends SVGGraphicsElement implements SVGCircleElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength cx;
    protected SVGOMAnimatedLength cy;
    protected SVGOMAnimatedLength r;
    
    protected SVGOMCircleElement() {
    }
    
    public SVGOMCircleElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.cx = this.createLiveAnimatedLength(null, "cx", "0", (short)2, false);
        this.cy = this.createLiveAnimatedLength(null, "cy", "0", (short)1, false);
        this.r = this.createLiveAnimatedLength(null, "r", null, (short)0, true);
    }
    
    @Override
    public String getLocalName() {
        return "circle";
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
    protected Node newNode() {
        return new SVGOMCircleElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMCircleElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "cx", new TraitInformation(true, 3, (short)1));
        t.put(null, "cy", new TraitInformation(true, 3, (short)2));
        t.put(null, "r", new TraitInformation(true, 3, (short)3));
        SVGOMCircleElement.xmlTraitInformation = t;
    }
}
