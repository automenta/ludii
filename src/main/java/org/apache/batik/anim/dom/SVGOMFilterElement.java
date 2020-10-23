// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFilterElement;

public class SVGOMFilterElement extends SVGStylableElement implements SVGFilterElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedEnumeration filterUnits;
    protected SVGOMAnimatedEnumeration primitiveUnits;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMFilterElement() {
    }
    
    public SVGOMFilterElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.filterUnits = this.createLiveAnimatedEnumeration(null, "filterUnits", SVGOMFilterElement.UNITS_VALUES, (short)2);
        this.primitiveUnits = this.createLiveAnimatedEnumeration(null, "primitiveUnits", SVGOMFilterElement.UNITS_VALUES, (short)1);
        this.x = this.createLiveAnimatedLength(null, "x", "-10%", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "-10%", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "120%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "120%", (short)1, true);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }
    
    @Override
    public String getLocalName() {
        return "filter";
    }
    
    @Override
    public SVGAnimatedEnumeration getFilterUnits() {
        return this.filterUnits;
    }
    
    @Override
    public SVGAnimatedEnumeration getPrimitiveUnits() {
        return this.primitiveUnits;
    }
    
    @Override
    public SVGAnimatedLength getX() {
        return this.x;
    }
    
    @Override
    public SVGAnimatedLength getY() {
        return this.y;
    }
    
    @Override
    public SVGAnimatedLength getWidth() {
        return this.width;
    }
    
    @Override
    public SVGAnimatedLength getHeight() {
        return this.height;
    }
    
    @Override
    public SVGAnimatedInteger getFilterResX() {
        throw new UnsupportedOperationException("SVGFilterElement.getFilterResX is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getFilterResY() {
        throw new UnsupportedOperationException("SVGFilterElement.getFilterResY is not implemented");
    }
    
    @Override
    public void setFilterRes(final int filterResX, final int filterResY) {
        throw new UnsupportedOperationException("SVGFilterElement.setFilterRes is not implemented");
    }
    
    @Override
    public SVGAnimatedString getHref() {
        return this.href;
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFilterElement.xmlTraitInformation;
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
        return SVGOMFilterElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFilterElement();
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "filterUnits", new TraitInformation(true, 15));
        t.put(null, "primitiveUnits", new TraitInformation(true, 15));
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        t.put(null, "filterRes", new TraitInformation(true, 4));
        SVGOMFilterElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMFilterElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMFilterElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        SVGOMFilterElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        UNITS_VALUES = new String[] { "", "userSpaceOnUse", "objectBoundingBox" };
    }
}
