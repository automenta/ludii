// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFESpecularLightingElement;

public class SVGOMFESpecularLightingElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFESpecularLightingElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedNumber surfaceScale;
    protected SVGOMAnimatedNumber specularConstant;
    protected SVGOMAnimatedNumber specularExponent;
    
    protected SVGOMFESpecularLightingElement() {
    }
    
    public SVGOMFESpecularLightingElement(final String prefix, final AbstractDocument owner) {
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
        this.specularConstant = this.createLiveAnimatedNumber(null, "specularConstant", 1.0f);
        this.specularExponent = this.createLiveAnimatedNumber(null, "specularExponent", 1.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feSpecularLighting";
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
    public SVGAnimatedNumber getSpecularConstant() {
        return this.specularConstant;
    }
    
    @Override
    public SVGAnimatedNumber getSpecularExponent() {
        return this.specularExponent;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFESpecularLightingElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFESpecularLightingElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "surfaceScale", new TraitInformation(true, 2));
        t.put(null, "specularConstant", new TraitInformation(true, 2));
        t.put(null, "specularExponent", new TraitInformation(true, 2));
        SVGOMFESpecularLightingElement.xmlTraitInformation = t;
    }
}
