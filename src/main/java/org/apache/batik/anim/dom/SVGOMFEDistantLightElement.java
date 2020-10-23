// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEDistantLightElement;

public class SVGOMFEDistantLightElement extends SVGOMElement implements SVGFEDistantLightElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber azimuth;
    protected SVGOMAnimatedNumber elevation;
    
    protected SVGOMFEDistantLightElement() {
    }
    
    public SVGOMFEDistantLightElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.azimuth = this.createLiveAnimatedNumber(null, "azimuth", 0.0f);
        this.elevation = this.createLiveAnimatedNumber(null, "elevation", 0.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feDistantLight";
    }
    
    @Override
    public SVGAnimatedNumber getAzimuth() {
        return this.azimuth;
    }
    
    @Override
    public SVGAnimatedNumber getElevation() {
        return this.elevation;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEDistantLightElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEDistantLightElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "azimuth", new TraitInformation(true, 2));
        t.put(null, "elevation", new TraitInformation(true, 2));
        SVGOMFEDistantLightElement.xmlTraitInformation = t;
    }
}
