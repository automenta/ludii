// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEBlendElement;

public class SVGOMFEBlendElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEBlendElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] MODE_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedEnumeration mode;
    
    protected SVGOMFEBlendElement() {
    }
    
    public SVGOMFEBlendElement(final String prefix, final AbstractDocument owner) {
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
        this.mode = this.createLiveAnimatedEnumeration(null, "mode", SVGOMFEBlendElement.MODE_VALUES, (short)1);
    }
    
    @Override
    public String getLocalName() {
        return "feBlend";
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
    public SVGAnimatedEnumeration getMode() {
        return this.mode;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEBlendElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEBlendElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "surfaceScale", new TraitInformation(true, 2));
        t.put(null, "diffuseConstant", new TraitInformation(true, 2));
        t.put(null, "kernelUnitLength", new TraitInformation(true, 4));
        SVGOMFEBlendElement.xmlTraitInformation = t;
        MODE_VALUES = new String[] { "", "normal", "multiply", "screen", "darken", "lighten" };
    }
}
