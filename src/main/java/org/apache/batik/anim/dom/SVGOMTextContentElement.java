// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGTestsSupport;
import org.w3c.dom.svg.SVGStringList;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;

public abstract class SVGOMTextContentElement extends SVGStylableElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] LENGTH_ADJUST_VALUES;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected AbstractSVGAnimatedLength textLength;
    protected SVGOMAnimatedEnumeration lengthAdjust;
    
    protected SVGOMTextContentElement() {
    }
    
    protected SVGOMTextContentElement(final String prefix, final AbstractDocument owner) {
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
        this.lengthAdjust = this.createLiveAnimatedEnumeration(null, "lengthAdjust", SVGOMTextContentElement.LENGTH_ADJUST_VALUES, (short)1);
        this.textLength = new AbstractSVGAnimatedLength(null, "textLength", 2, true) {
            boolean usedDefault;
            
            @Override
            protected String getDefaultValue() {
                this.usedDefault = true;
                return String.valueOf(SVGOMTextContentElement.this.getComputedTextLength());
            }
            
            @Override
            public SVGLength getBaseVal() {
                if (this.baseVal == null) {
                    this.baseVal = new SVGTextLength(this.direction);
                }
                return this.baseVal;
            }
            
            class SVGTextLength extends BaseSVGLength
            {
                public SVGTextLength() {
                    super(direction);
                }
                
                @Override
                protected void revalidate() {
                    AbstractSVGAnimatedLength.this.usedDefault = false;
                    super.revalidate();
                    if (AbstractSVGAnimatedLength.this.usedDefault) {
                        this.valid = false;
                    }
                }
            }
        };
        this.liveAttributeValues.put(null, "textLength", this.textLength);
        this.textLength.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
    }
    
    public SVGAnimatedLength getTextLength() {
        return this.textLength;
    }
    
    public SVGAnimatedEnumeration getLengthAdjust() {
        return this.lengthAdjust;
    }
    
    public int getNumberOfChars() {
        return SVGTextContentSupport.getNumberOfChars(this);
    }
    
    public float getComputedTextLength() {
        return SVGTextContentSupport.getComputedTextLength(this);
    }
    
    public float getSubStringLength(final int charnum, final int nchars) throws DOMException {
        return SVGTextContentSupport.getSubStringLength(this, charnum, nchars);
    }
    
    public SVGPoint getStartPositionOfChar(final int charnum) throws DOMException {
        return SVGTextContentSupport.getStartPositionOfChar(this, charnum);
    }
    
    public SVGPoint getEndPositionOfChar(final int charnum) throws DOMException {
        return SVGTextContentSupport.getEndPositionOfChar(this, charnum);
    }
    
    public SVGRect getExtentOfChar(final int charnum) throws DOMException {
        return SVGTextContentSupport.getExtentOfChar(this, charnum);
    }
    
    public float getRotationOfChar(final int charnum) throws DOMException {
        return SVGTextContentSupport.getRotationOfChar(this, charnum);
    }
    
    public int getCharNumAtPosition(final SVGPoint point) {
        return SVGTextContentSupport.getCharNumAtPosition(this, point.getX(), point.getY());
    }
    
    public void selectSubString(final int charnum, final int nchars) throws DOMException {
        SVGTextContentSupport.selectSubString(this, charnum, nchars);
    }
    
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }
    
    public void setXMLlang(final String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }
    
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }
    
    public void setXMLspace(final String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }
    
    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures(this);
    }
    
    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions(this);
    }
    
    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage(this);
    }
    
    public boolean hasExtension(final String extension) {
        return SVGTestsSupport.hasExtension(this, extension);
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMTextContentElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "textLength", new TraitInformation(true, 3, (short)3));
        t.put(null, "lengthAdjust", new TraitInformation(true, 15));
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMTextContentElement.xmlTraitInformation = t;
        LENGTH_ADJUST_VALUES = new String[] { "", "spacing", "spacingAndGlyphs" };
    }
}
