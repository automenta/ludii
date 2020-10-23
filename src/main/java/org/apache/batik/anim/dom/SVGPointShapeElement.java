// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGPointList;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimatedPoints;

public abstract class SVGPointShapeElement extends SVGGraphicsElement implements SVGAnimatedPoints
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedPoints points;
    
    protected SVGPointShapeElement() {
    }
    
    public SVGPointShapeElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.points = this.createLiveAnimatedPoints(null, "points", "");
    }
    
    public SVGOMAnimatedPoints getSVGOMAnimatedPoints() {
        return this.points;
    }
    
    @Override
    public SVGPointList getPoints() {
        return this.points.getPoints();
    }
    
    @Override
    public SVGPointList getAnimatedPoints() {
        return this.points.getAnimatedPoints();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGPointShapeElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "points", new TraitInformation(true, 31));
        SVGPointShapeElement.xmlTraitInformation = t;
    }
}
