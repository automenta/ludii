// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEPointLightElement;

public class SVGOMFEPointLightElement extends SVGOMElement implements SVGFEPointLightElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber x;
    protected SVGOMAnimatedNumber y;
    protected SVGOMAnimatedNumber z;
    
    protected SVGOMFEPointLightElement() {
    }
    
    public SVGOMFEPointLightElement(final String prefix, final AbstractDocument owner) {
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
    }
    
    @Override
    public String getLocalName() {
        return "fePointLight";
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
    protected Node newNode() {
        return new SVGOMFEPointLightElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEPointLightElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 2));
        t.put(null, "y", new TraitInformation(true, 2));
        t.put(null, "z", new TraitInformation(true, 2));
        SVGOMFEPointLightElement.xmlTraitInformation = t;
    }
}
