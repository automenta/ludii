// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEColorMatrixElement;

public class SVGOMFEColorMatrixElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEColorMatrixElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] TYPE_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedEnumeration type;
    
    protected SVGOMFEColorMatrixElement() {
    }
    
    public SVGOMFEColorMatrixElement(final String prefix, final AbstractDocument owner) {
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
        this.type = this.createLiveAnimatedEnumeration(null, "type", SVGOMFEColorMatrixElement.TYPE_VALUES, (short)1);
    }
    
    @Override
    public String getLocalName() {
        return "feColorMatrix";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedEnumeration getType() {
        return this.type;
    }
    
    @Override
    public SVGAnimatedNumberList getValues() {
        throw new UnsupportedOperationException("SVGFEColorMatrixElement.getValues is not implemented");
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEColorMatrixElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEColorMatrixElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "type", new TraitInformation(true, 15));
        t.put(null, "values", new TraitInformation(true, 13));
        SVGOMFEColorMatrixElement.xmlTraitInformation = t;
        TYPE_VALUES = new String[] { "", "matrix", "saturate", "hueRotate", "luminanceToAlpha" };
    }
}
