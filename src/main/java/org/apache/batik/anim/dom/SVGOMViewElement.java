// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.w3c.dom.svg.SVGStringList;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGViewElement;

public class SVGOMViewElement extends SVGOMElement implements SVGViewElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    
    protected SVGOMViewElement() {
    }
    
    public SVGOMViewElement(final String prefix, final AbstractDocument owner) {
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
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }
    
    @Override
    public String getLocalName() {
        return "view";
    }
    
    @Override
    public SVGStringList getViewTarget() {
        throw new UnsupportedOperationException("SVGViewElement.getViewTarget is not implemented");
    }
    
    @Override
    public short getZoomAndPan() {
        return SVGZoomAndPanSupport.getZoomAndPan(this);
    }
    
    @Override
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
        return SVGOMViewElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMViewElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMViewElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put(null, "viewBox", new TraitInformation(true, 13));
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMViewElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(2)).addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        SVGOMViewElement.attributeInitializer.addAttribute(null, null, "zoomAndPan", "magnify");
    }
}
