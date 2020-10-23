// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGMarkerElement;

public class SVGOMMarkerElement extends SVGStylableElement implements SVGMarkerElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected static final String[] ORIENT_TYPE_VALUES;
    protected SVGOMAnimatedLength refX;
    protected SVGOMAnimatedLength refY;
    protected SVGOMAnimatedLength markerWidth;
    protected SVGOMAnimatedLength markerHeight;
    protected SVGOMAnimatedMarkerOrientValue orient;
    protected SVGOMAnimatedEnumeration markerUnits;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected SVGOMAnimatedRect viewBox;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMMarkerElement() {
    }
    
    public SVGOMMarkerElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.refX = this.createLiveAnimatedLength(null, "refX", "0", (short)2, false);
        this.refY = this.createLiveAnimatedLength(null, "refY", "0", (short)1, false);
        this.markerWidth = this.createLiveAnimatedLength(null, "markerWidth", "3", (short)2, true);
        this.markerHeight = this.createLiveAnimatedLength(null, "markerHeight", "3", (short)1, true);
        this.orient = this.createLiveAnimatedMarkerOrientValue(null, "orient");
        this.markerUnits = this.createLiveAnimatedEnumeration(null, "markerUnits", SVGOMMarkerElement.UNITS_VALUES, (short)2);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
        this.viewBox = this.createLiveAnimatedRect(null, "viewBox", null);
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }
    
    @Override
    public String getLocalName() {
        return "marker";
    }
    
    @Override
    public SVGAnimatedLength getRefX() {
        return this.refX;
    }
    
    @Override
    public SVGAnimatedLength getRefY() {
        return this.refY;
    }
    
    @Override
    public SVGAnimatedEnumeration getMarkerUnits() {
        return this.markerUnits;
    }
    
    @Override
    public SVGAnimatedLength getMarkerWidth() {
        return this.markerWidth;
    }
    
    @Override
    public SVGAnimatedLength getMarkerHeight() {
        return this.markerHeight;
    }
    
    @Override
    public SVGAnimatedEnumeration getOrientType() {
        return this.orient.getAnimatedEnumeration();
    }
    
    @Override
    public SVGAnimatedAngle getOrientAngle() {
        return this.orient.getAnimatedAngle();
    }
    
    @Override
    public void setOrientToAuto() {
        this.setAttributeNS(null, "orient", "auto");
    }
    
    @Override
    public void setOrientToAngle(final SVGAngle angle) {
        this.setAttributeNS(null, "orient", angle.getValueAsString());
    }
    
    @Override
    public SVGAnimatedRect getViewBox() {
        return this.viewBox;
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
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMMarkerElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMMarkerElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMMarkerElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "refX", new TraitInformation(true, 3, (short)1));
        t.put(null, "refY", new TraitInformation(true, 3, (short)2));
        t.put(null, "markerWidth", new TraitInformation(true, 3, (short)1));
        t.put(null, "markerHeight", new TraitInformation(true, 3, (short)2));
        t.put(null, "markerUnits", new TraitInformation(true, 15));
        t.put(null, "orient", new TraitInformation(true, 15));
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMMarkerElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(1)).addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        UNITS_VALUES = new String[] { "", "userSpaceOnUse", "stroke-width" };
        ORIENT_TYPE_VALUES = new String[] { "", "auto", "" };
    }
}
