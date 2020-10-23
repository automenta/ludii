// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFESpotLightElement;

public class SVGOMFESpotLightElement extends SVGOMElement implements SVGFESpotLightElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber x;
    protected SVGOMAnimatedNumber y;
    protected SVGOMAnimatedNumber z;
    protected SVGOMAnimatedNumber pointsAtX;
    protected SVGOMAnimatedNumber pointsAtY;
    protected SVGOMAnimatedNumber pointsAtZ;
    protected SVGOMAnimatedNumber specularExponent;
    protected SVGOMAnimatedNumber limitingConeAngle;
    
    protected SVGOMFESpotLightElement() {
    }
    
    public SVGOMFESpotLightElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedNumber(null, "x", 0.0f);
        this.y = this.createLiveAnimatedNumber(null, "y", 0.0f);
        this.z = this.createLiveAnimatedNumber(null, "z", 0.0f);
        this.pointsAtX = this.createLiveAnimatedNumber(null, "pointsAtX", 0.0f);
        this.pointsAtY = this.createLiveAnimatedNumber(null, "pointsAtY", 0.0f);
        this.pointsAtZ = this.createLiveAnimatedNumber(null, "pointsAtZ", 0.0f);
        this.specularExponent = this.createLiveAnimatedNumber(null, "specularExponent", 1.0f);
        this.limitingConeAngle = this.createLiveAnimatedNumber(null, "limitingConeAngle", 0.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feSpotLight";
    }
    
    @Override
    public SVGAnimatedNumber getX() {
        return this.x;
    }
    
    @Override
    public SVGAnimatedNumber getY() {
        return this.y;
    }
    
    @Override
    public SVGAnimatedNumber getZ() {
        return this.z;
    }
    
    @Override
    public SVGAnimatedNumber getPointsAtX() {
        return this.pointsAtX;
    }
    
    @Override
    public SVGAnimatedNumber getPointsAtY() {
        return this.pointsAtY;
    }
    
    @Override
    public SVGAnimatedNumber getPointsAtZ() {
        return this.pointsAtZ;
    }
    
    @Override
    public SVGAnimatedNumber getSpecularExponent() {
        return this.specularExponent;
    }
    
    @Override
    public SVGAnimatedNumber getLimitingConeAngle() {
        return this.limitingConeAngle;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFESpotLightElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 2));
        t.put(null, "y", new TraitInformation(true, 2));
        t.put(null, "z", new TraitInformation(true, 2));
        t.put(null, "pointsAtX", new TraitInformation(true, 2));
        t.put(null, "pointsAtY", new TraitInformation(true, 2));
        t.put(null, "pointsAtZ", new TraitInformation(true, 2));
        t.put(null, "specularExponent", new TraitInformation(true, 2));
        t.put(null, "limitingConeAngle", new TraitInformation(true, 2));
        SVGOMFESpotLightElement.xmlTraitInformation = t;
    }
}
