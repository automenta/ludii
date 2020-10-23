// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGSymbolElement;

public class SVGOMSymbolElement extends SVGStylableElement implements SVGSymbolElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMSymbolElement() {
    }
    
    public SVGOMSymbolElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }
    
    @Override
    public String getLocalName() {
        return "symbol";
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
    
    public short getZoomAndPan() {
        return SVGZoomAndPanSupport.getZoomAndPan(this);
    }
    
    public void setZoomAndPan(final short val) {
        SVGZoomAndPanSupport.setZoomAndPan(this, val);
    }
    
    @Override
    public SVGAnimatedRect getViewBox() {
        throw new UnsupportedOperationException("SVGFitToViewBox.getViewBox is not implemented");
    }
    
    @Override
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMSymbolElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMSymbolElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMSymbolElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put(null, "viewBox", new TraitInformation(true, 13));
        SVGOMSymbolElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(1)).addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
    }
}
