// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGComponentTransferFunctionElement;

public abstract class SVGOMComponentTransferFunctionElement extends SVGOMElement implements SVGComponentTransferFunctionElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] TYPE_VALUES;
    protected SVGOMAnimatedEnumeration type;
    protected SVGOMAnimatedNumberList tableValues;
    protected SVGOMAnimatedNumber slope;
    protected SVGOMAnimatedNumber intercept;
    protected SVGOMAnimatedNumber amplitude;
    protected SVGOMAnimatedNumber exponent;
    protected SVGOMAnimatedNumber offset;
    
    protected SVGOMComponentTransferFunctionElement() {
    }
    
    protected SVGOMComponentTransferFunctionElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.type = this.createLiveAnimatedEnumeration(null, "type", SVGOMComponentTransferFunctionElement.TYPE_VALUES, (short)1);
        this.tableValues = this.createLiveAnimatedNumberList(null, "tableValues", "", false);
        this.slope = this.createLiveAnimatedNumber(null, "slope", 1.0f);
        this.intercept = this.createLiveAnimatedNumber(null, "intercept", 0.0f);
        this.amplitude = this.createLiveAnimatedNumber(null, "amplitude", 1.0f);
        this.exponent = this.createLiveAnimatedNumber(null, "exponent", 1.0f);
        this.offset = this.createLiveAnimatedNumber(null, "exponent", 0.0f);
    }
    
    @Override
    public SVGAnimatedEnumeration getType() {
        return this.type;
    }
    
    @Override
    public SVGAnimatedNumberList getTableValues() {
        throw new UnsupportedOperationException("SVGComponentTransferFunctionElement.getTableValues is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getSlope() {
        return this.slope;
    }
    
    @Override
    public SVGAnimatedNumber getIntercept() {
        return this.intercept;
    }
    
    @Override
    public SVGAnimatedNumber getAmplitude() {
        return this.amplitude;
    }
    
    @Override
    public SVGAnimatedNumber getExponent() {
        return this.exponent;
    }
    
    @Override
    public SVGAnimatedNumber getOffset() {
        return this.offset;
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMComponentTransferFunctionElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "type", new TraitInformation(true, 15));
        t.put(null, "tableValues", new TraitInformation(true, 13));
        t.put(null, "slope", new TraitInformation(true, 2));
        t.put(null, "intercept", new TraitInformation(true, 2));
        t.put(null, "amplitude", new TraitInformation(true, 2));
        t.put(null, "exponent", new TraitInformation(true, 2));
        t.put(null, "offset", new TraitInformation(true, 2));
        SVGOMComponentTransferFunctionElement.xmlTraitInformation = t;
        TYPE_VALUES = new String[] { "", "identity", "table", "discrete", "linear", "gamma" };
    }
}
