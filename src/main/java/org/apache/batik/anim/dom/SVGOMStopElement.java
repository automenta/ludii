// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGStopElement;

public class SVGOMStopElement extends SVGStylableElement implements SVGStopElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber offset;
    
    protected SVGOMStopElement() {
    }
    
    public SVGOMStopElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.offset = this.createLiveAnimatedNumber(null, "offset", 0.0f, true);
    }
    
    @Override
    public String getLocalName() {
        return "stop";
    }
    
    @Override
    public SVGAnimatedNumber getOffset() {
        return this.offset;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMStopElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMStopElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "offset", new TraitInformation(true, 47));
        SVGOMStopElement.xmlTraitInformation = t;
    }
}
