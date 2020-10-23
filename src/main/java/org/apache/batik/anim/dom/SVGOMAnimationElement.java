// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Element;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.SVGAnimationContext;
import org.w3c.dom.svg.SVGElement;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimationElement;

public abstract class SVGOMAnimationElement extends SVGOMElement implements SVGAnimationElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    
    protected SVGOMAnimationElement() {
    }
    
    protected SVGOMAnimationElement(final String prefix, final AbstractDocument owner) {
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
    public SVGElement getTargetElement() {
        return ((SVGAnimationContext)this.getSVGContext()).getTargetElement();
    }
    
    @Override
    public float getStartTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getStartTime();
    }
    
    @Override
    public float getCurrentTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getCurrentTime();
    }
    
    @Override
    public float getSimpleDuration() throws DOMException {
        final float dur = ((SVGAnimationContext)this.getSVGContext()).getSimpleDuration();
        if (dur == Float.POSITIVE_INFINITY) {
            throw this.createDOMException((short)9, "animation.dur.indefinite", null);
        }
        return dur;
    }
    
    public float getHyperlinkBeginTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getHyperlinkBeginTime();
    }
    
    @Override
    public boolean beginElement() throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).beginElement();
    }
    
    @Override
    public boolean beginElementAt(final float offset) throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).beginElementAt(offset);
    }
    
    @Override
    public boolean endElement() throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).endElement();
    }
    
    @Override
    public boolean endElementAt(final float offset) throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).endElementAt(offset);
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
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
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMAnimationElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMAnimationElement.xmlTraitInformation = t;
    }
}
