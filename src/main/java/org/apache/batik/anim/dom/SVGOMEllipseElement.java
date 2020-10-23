// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGEllipseElement;

public class SVGOMEllipseElement extends SVGGraphicsElement implements SVGEllipseElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength cx;
    protected SVGOMAnimatedLength cy;
    protected SVGOMAnimatedLength rx;
    protected SVGOMAnimatedLength ry;
    
    protected SVGOMEllipseElement() {
    }
    
    public SVGOMEllipseElement(final String prefix, final AbstractDocument owner) {
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
        this.rx = this.createLiveAnimatedLength(null, "rx", null, (short)2, true);
        this.ry = this.createLiveAnimatedLength(null, "ry", null, (short)1, true);
    }
    
    @Override
    public String getLocalName() {
        return "ellipse";
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
    public SVGAnimatedLength getRx() {
        return this.rx;
    }
    
    @Override
    public SVGAnimatedLength getRy() {
        return this.ry;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMEllipseElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMEllipseElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "cx", new TraitInformation(true, 3, (short)1));
        t.put(null, "cy", new TraitInformation(true, 3, (short)2));
        t.put(null, "rx", new TraitInformation(true, 3, (short)1));
        t.put(null, "ry", new TraitInformation(true, 3, (short)2));
        SVGOMEllipseElement.xmlTraitInformation = t;
    }
}
