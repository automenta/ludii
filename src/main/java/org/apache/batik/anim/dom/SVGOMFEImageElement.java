// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEImageElement;

public class SVGOMFEImageElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEImageElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMFEImageElement() {
    }
    
    public SVGOMFEImageElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }
    
    @Override
    public String getLocalName() {
        return "feImage";
    }
    
    @Override
    public SVGAnimatedString getHref() {
        return this.href;
    }
    
    @Override
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }
    
    @Override
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }
    
    @Override
    public void setXMLlang(final String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }
    
    @Override
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }
    
    @Override
    public void setXMLspace(final String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMFEImageElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEImageElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEImageElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put("http://www.w3.org/1999/xlink", "href", new TraitInformation(true, 10));
        SVGOMFEImageElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMFEImageElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMFEImageElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "embed");
        SVGOMFEImageElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}
