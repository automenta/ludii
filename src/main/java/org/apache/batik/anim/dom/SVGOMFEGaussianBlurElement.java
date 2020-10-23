// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEGaussianBlurElement;

public class SVGOMFEGaussianBlurElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEGaussianBlurElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    
    protected SVGOMFEGaussianBlurElement() {
    }
    
    public SVGOMFEGaussianBlurElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.in = this.createLiveAnimatedString(null, "in");
    }
    
    @Override
    public String getLocalName() {
        return "feGaussianBlur";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedNumber getStdDeviationX() {
        throw new UnsupportedOperationException("SVGFEGaussianBlurElement.getStdDeviationX is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getStdDeviationY() {
        throw new UnsupportedOperationException("SVGFEGaussianBlurElement.getStdDeviationY is not implemented");
    }
    
    @Override
    public void setStdDeviation(final float devX, final float devY) {
        this.setAttributeNS(null, "stdDeviation", Float.toString(devX) + " " + Float.toString(devY));
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEGaussianBlurElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEGaussianBlurElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "stdDeviation", new TraitInformation(true, 4));
        SVGOMFEGaussianBlurElement.xmlTraitInformation = t;
    }
}
