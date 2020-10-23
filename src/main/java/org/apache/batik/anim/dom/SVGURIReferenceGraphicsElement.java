// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;

public abstract class SVGURIReferenceGraphicsElement extends SVGGraphicsElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString href;
    
    protected SVGURIReferenceGraphicsElement() {
    }
    
    protected SVGURIReferenceGraphicsElement(final String prefix, final AbstractDocument owner) {
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
    }
    
    public SVGAnimatedString getHref() {
        return this.href;
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGURIReferenceGraphicsElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put("http://www.w3.org/1999/xlink", "href", new TraitInformation(true, 10));
        SVGURIReferenceGraphicsElement.xmlTraitInformation = t;
    }
}
