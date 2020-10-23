// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEDisplacementMapElement;

public class SVGOMFEDisplacementMapElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEDisplacementMapElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] CHANNEL_SELECTOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedNumber scale;
    protected SVGOMAnimatedEnumeration xChannelSelector;
    protected SVGOMAnimatedEnumeration yChannelSelector;
    
    protected SVGOMFEDisplacementMapElement() {
    }
    
    public SVGOMFEDisplacementMapElement(final String prefix, final AbstractDocument owner) {
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
        this.in2 = this.createLiveAnimatedString(null, "in2");
        this.scale = this.createLiveAnimatedNumber(null, "scale", 0.0f);
        this.xChannelSelector = this.createLiveAnimatedEnumeration(null, "xChannelSelector", SVGOMFEDisplacementMapElement.CHANNEL_SELECTOR_VALUES, (short)4);
        this.yChannelSelector = this.createLiveAnimatedEnumeration(null, "yChannelSelector", SVGOMFEDisplacementMapElement.CHANNEL_SELECTOR_VALUES, (short)4);
    }
    
    @Override
    public String getLocalName() {
        return "feDisplacementMap";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    public SVGAnimatedString getIn2() {
        return this.in2;
    }
    
    @Override
    public SVGAnimatedNumber getScale() {
        return this.scale;
    }
    
    @Override
    public SVGAnimatedEnumeration getXChannelSelector() {
        return this.xChannelSelector;
    }
    
    @Override
    public SVGAnimatedEnumeration getYChannelSelector() {
        return this.yChannelSelector;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEDisplacementMapElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEDisplacementMapElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        t.put(null, "in2", new TraitInformation(true, 16));
        t.put(null, "scale", new TraitInformation(true, 2));
        t.put(null, "xChannelSelector", new TraitInformation(true, 15));
        t.put(null, "yChannelSelector", new TraitInformation(true, 15));
        SVGOMFEDisplacementMapElement.xmlTraitInformation = t;
        CHANNEL_SELECTOR_VALUES = new String[] { "", "R", "G", "B", "A" };
    }
}
