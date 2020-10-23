// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGMPathElement;

public class SVGOMMPathElement extends SVGOMURIReferenceElement implements SVGMPathElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMMPathElement() {
    }
    
    public SVGOMMPathElement(final String prefix, final AbstractDocument owner) {
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
        return "mpath";
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMMPathElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMMPathElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMMPathElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMURIReferenceElement.xmlTraitInformation);
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMMPathElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMMPathElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMMPathElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        SVGOMMPathElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}
