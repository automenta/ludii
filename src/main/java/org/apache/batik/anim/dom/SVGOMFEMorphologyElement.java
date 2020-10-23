// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEMorphologyElement;

public class SVGOMFEMorphologyElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEMorphologyElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] OPERATOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedEnumeration operator;
    
    protected SVGOMFEMorphologyElement() {
    }
    
    public SVGOMFEMorphologyElement(final String prefix, final AbstractDocument owner) {
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
        this.operator = this.createLiveAnimatedEnumeration(null, "operator", SVGOMFEMorphologyElement.OPERATOR_VALUES, (short)1);
    }
    
    @Override
    public String getLocalName() {
        return "feMorphology";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedEnumeration getOperator() {
        return this.operator;
    }
    
    @Override
    public SVGAnimatedNumber getRadiusX() {
        throw new UnsupportedOperationException("SVGFEMorphologyElement.getRadiusX is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getRadiusY() {
        throw new UnsupportedOperationException("SVGFEMorphologyElement.getRadiusY is not implemented");
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEMorphologyElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEMorphologyElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "operator", new TraitInformation(true, 15));
        t.put(null, "radius", new TraitInformation(true, 4));
        SVGOMFEMorphologyElement.xmlTraitInformation = t;
        OPERATOR_VALUES = new String[] { "", "erode", "dilate" };
    }
}
