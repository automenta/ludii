// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEDiffuseLightingElement;

public class SVGOMFEDiffuseLightingElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEDiffuseLightingElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedNumber surfaceScale;
    protected SVGOMAnimatedNumber diffuseConstant;
    
    protected SVGOMFEDiffuseLightingElement() {
    }
    
    public SVGOMFEDiffuseLightingElement(final String prefix, final AbstractDocument owner) {
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
        this.surfaceScale = this.createLiveAnimatedNumber(null, "surfaceScale", 1.0f);
        this.diffuseConstant = this.createLiveAnimatedNumber(null, "diffuseConstant", 1.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feDiffuseLighting";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedNumber getSurfaceScale() {
        return this.surfaceScale;
    }
    
    @Override
    public SVGAnimatedNumber getDiffuseConstant() {
        return this.diffuseConstant;
    }
    
    @Override
    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new UnsupportedOperationException("SVGFEDiffuseLightingElement.getKernelUnitLengthX is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getKernelUnitLengthY() {
        throw new UnsupportedOperationException("SVGFEDiffuseLightingElement.getKernelUnitLengthY is not implemented");
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEDiffuseLightingElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEDiffuseLightingElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "surfaceScale", new TraitInformation(true, 2));
        t.put(null, "diffuseConstant", new TraitInformation(true, 2));
        t.put(null, "kernelUnitLength", new TraitInformation(true, 4));
        SVGOMFEDiffuseLightingElement.xmlTraitInformation = t;
    }
}
