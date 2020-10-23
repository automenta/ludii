// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGPatternElement;

public class SVGOMPatternElement extends SVGStylableElement implements SVGPatternElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedEnumeration patternUnits;
    protected SVGOMAnimatedEnumeration patternContentUnits;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    
    protected SVGOMPatternElement() {
    }
    
    public SVGOMPatternElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "0", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "0", (short)1, true);
        this.patternUnits = this.createLiveAnimatedEnumeration(null, "patternUnits", SVGOMPatternElement.UNITS_VALUES, (short)2);
        this.patternContentUnits = this.createLiveAnimatedEnumeration(null, "patternContentUnits", SVGOMPatternElement.UNITS_VALUES, (short)1);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }
    
    @Override
    public String getLocalName() {
        return "pattern";
    }
    
    @Override
    public SVGAnimatedTransformList getPatternTransform() {
        throw new UnsupportedOperationException("SVGPatternElement.getPatternTransform is not implemented");
    }
    
    @Override
    public SVGAnimatedEnumeration getPatternUnits() {
        return this.patternUnits;
    }
    
    @Override
    public SVGAnimatedEnumeration getPatternContentUnits() {
        return this.patternContentUnits;
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
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMPatternElement.xmlTraitInformation;
    }
    
    @Override
    public SVGAnimatedString getHref() {
        return this.href;
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
    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures(this);
    }
    
    @Override
    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions(this);
    }
    
    @Override
    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage(this);
    }
    
    @Override
    public boolean hasExtension(final String extension) {
        return SVGTestsSupport.hasExtension(this, extension);
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMPatternElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMPatternElement();
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        t.put(null, "patternUnits", new TraitInformation(true, 15));
        t.put(null, "patternContentUnits", new TraitInformation(true, 15));
        t.put(null, "patternTransform", new TraitInformation(true, 9));
        t.put(null, "viewBox", new TraitInformation(true, 13));
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMPatternElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(5)).addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        SVGOMPatternElement.attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMPatternElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMPatternElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        SVGOMPatternElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        UNITS_VALUES = new String[] { "", "userSpaceOnUse", "objectBoundingBox" };
    }
}
