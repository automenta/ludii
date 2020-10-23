// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEConvolveMatrixElement;

public class SVGOMFEConvolveMatrixElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEConvolveMatrixElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] EDGE_MODE_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedEnumeration edgeMode;
    protected SVGOMAnimatedNumber bias;
    protected SVGOMAnimatedBoolean preserveAlpha;
    
    protected SVGOMFEConvolveMatrixElement() {
    }
    
    public SVGOMFEConvolveMatrixElement(final String prefix, final AbstractDocument owner) {
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
        this.edgeMode = this.createLiveAnimatedEnumeration(null, "edgeMode", SVGOMFEConvolveMatrixElement.EDGE_MODE_VALUES, (short)1);
        this.bias = this.createLiveAnimatedNumber(null, "bias", 0.0f);
        this.preserveAlpha = this.createLiveAnimatedBoolean(null, "preserveAlpha", false);
    }
    
    @Override
    public String getLocalName() {
        return "feConvolveMatrix";
    }
    
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedEnumeration getEdgeMode() {
        return this.edgeMode;
    }
    
    @Override
    public SVGAnimatedNumberList getKernelMatrix() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelMatrix is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getOrderX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getOrderX is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getOrderY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getOrderY is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getTargetX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getTargetX is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getTargetY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getTargetY is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getDivisor() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getDivisor is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getBias() {
        return this.bias;
    }
    
    @Override
    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelUnitLengthX is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getKernelUnitLengthY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelUnitLengthY is not implemented");
    }
    
    @Override
    public SVGAnimatedBoolean getPreserveAlpha() {
        return this.preserveAlpha;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEConvolveMatrixElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEConvolveMatrixElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "order", new TraitInformation(true, 4));
        t.put(null, "kernelUnitLength", new TraitInformation(true, 4));
        t.put(null, "kernelMatrix", new TraitInformation(true, 13));
        t.put(null, "divisor", new TraitInformation(true, 2));
        t.put(null, "bias", new TraitInformation(true, 2));
        t.put(null, "targetX", new TraitInformation(true, 1));
        t.put(null, "targetY", new TraitInformation(true, 1));
        t.put(null, "edgeMode", new TraitInformation(true, 15));
        t.put(null, "preserveAlpha", new TraitInformation(true, 49));
        SVGOMFEConvolveMatrixElement.xmlTraitInformation = t;
        EDGE_MODE_VALUES = new String[] { "", "duplicate", "wrap", "none" };
    }
}
