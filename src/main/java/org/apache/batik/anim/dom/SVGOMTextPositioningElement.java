// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGTextPositioningElement;

public abstract class SVGOMTextPositioningElement extends SVGOMTextContentElement implements SVGTextPositioningElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLengthList x;
    protected SVGOMAnimatedLengthList y;
    protected SVGOMAnimatedLengthList dx;
    protected SVGOMAnimatedLengthList dy;
    protected SVGOMAnimatedNumberList rotate;
    
    protected SVGOMTextPositioningElement() {
    }
    
    protected SVGOMTextPositioningElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLengthList(null, "x", this.getDefaultXValue(), true, (short)2);
        this.y = this.createLiveAnimatedLengthList(null, "y", this.getDefaultYValue(), true, (short)1);
        this.dx = this.createLiveAnimatedLengthList(null, "dx", "", true, (short)2);
        this.dy = this.createLiveAnimatedLengthList(null, "dy", "", true, (short)1);
        this.rotate = this.createLiveAnimatedNumberList(null, "rotate", "", true);
    }
    
    @Override
    public SVGAnimatedLengthList getX() {
        return this.x;
    }
    
    @Override
    public SVGAnimatedLengthList getY() {
        return this.y;
    }
    
    @Override
    public SVGAnimatedLengthList getDx() {
        return this.dx;
    }
    
    @Override
    public SVGAnimatedLengthList getDy() {
        return this.dy;
    }
    
    @Override
    public SVGAnimatedNumberList getRotate() {
        return this.rotate;
    }
    
    protected String getDefaultXValue() {
        return "";
    }
    
    protected String getDefaultYValue() {
        return "";
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMTextPositioningElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextContentElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 14, (short)1));
        t.put(null, "y", new TraitInformation(true, 14, (short)2));
        t.put(null, "dx", new TraitInformation(true, 14, (short)1));
        t.put(null, "dy", new TraitInformation(true, 14, (short)2));
        t.put(null, "rotate", new TraitInformation(true, 13));
        SVGOMTextPositioningElement.xmlTraitInformation = t;
    }
}
