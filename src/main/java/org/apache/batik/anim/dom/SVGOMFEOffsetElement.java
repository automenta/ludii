// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEOffsetElement;

public class SVGOMFEOffsetElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEOffsetElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedNumber dx;
    protected SVGOMAnimatedNumber dy;
    
    protected SVGOMFEOffsetElement() {
    }
    
    public SVGOMFEOffsetElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.in = this.createLiveAnimatedString(null, "in");
        this.dx = this.createLiveAnimatedNumber(null, "dx", 0.0f);
        this.dy = this.createLiveAnimatedNumber(null, "dy", 0.0f);
    }
    
    @Override
    public String getLocalName() {
        return "feOffset";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedNumber getDx() {
        return this.dx;
    }
    
    @Override
    public SVGAnimatedNumber getDy() {
        return this.dy;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEOffsetElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEOffsetElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "dx", new TraitInformation(true, 2));
        t.put(null, "dy", new TraitInformation(true, 2));
        SVGOMFEOffsetElement.xmlTraitInformation = t;
    }
}
