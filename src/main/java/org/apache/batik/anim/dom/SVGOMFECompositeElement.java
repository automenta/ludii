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
import org.w3c.dom.svg.SVGFECompositeElement;

public class SVGOMFECompositeElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFECompositeElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] OPERATOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedEnumeration operator;
    protected SVGOMAnimatedNumber k1;
    protected SVGOMAnimatedNumber k2;
    protected SVGOMAnimatedNumber k3;
    protected SVGOMAnimatedNumber k4;
    
    protected SVGOMFECompositeElement() {
    }
    
    public SVGOMFECompositeElement(final String prefix, final AbstractDocument owner) {
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
        this.in2 = this.createLiveAnimatedString(null, "in2");
        this.operator = this.createLiveAnimatedEnumeration(null, "operator", SVGOMFECompositeElement.OPERATOR_VALUES, (short)1);
        this.k1 = this.createLiveAnimatedNumber(null, "k1", 0.0f);
        this.k2 = this.createLiveAnimatedNumber(null, "k2", 0.0f);
        this.k3 = this.createLiveAnimatedNumber(null, "k3", 0.0f);
        this.k4 = this.createLiveAnimatedNumber(null, "k4", 0.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feComposite";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedString getIn2() {
        return this.in2;
    }
    
    @Override
    public SVGAnimatedEnumeration getOperator() {
        return this.operator;
    }
    
    @Override
    public SVGAnimatedNumber getK1() {
        return this.k1;
    }
    
    @Override
    public SVGAnimatedNumber getK2() {
        return this.k2;
    }
    
    @Override
    public SVGAnimatedNumber getK3() {
        return this.k3;
    }
    
    @Override
    public SVGAnimatedNumber getK4() {
        return this.k4;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFECompositeElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFECompositeElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "in2", new TraitInformation(true, 16));
        t.put(null, "operator", new TraitInformation(true, 15));
        t.put(null, "k1", new TraitInformation(true, 2));
        t.put(null, "k2", new TraitInformation(true, 2));
        t.put(null, "k3", new TraitInformation(true, 2));
        t.put(null, "k4", new TraitInformation(true, 2));
        SVGOMFECompositeElement.xmlTraitInformation = t;
        OPERATOR_VALUES = new String[] { "", "over", "in", "out", "atop", "xor", "arithmetic" };
    }
}
