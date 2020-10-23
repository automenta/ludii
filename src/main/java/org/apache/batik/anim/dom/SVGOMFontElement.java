// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFontElement;

public class SVGOMFontElement extends SVGStylableElement implements SVGFontElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMFontElement() {
    }
    
    public SVGOMFontElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }
    
    @Override
    public String getLocalName() {
        return "font";
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFontElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFontElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMFontElement.xmlTraitInformation = t;
    }
}
