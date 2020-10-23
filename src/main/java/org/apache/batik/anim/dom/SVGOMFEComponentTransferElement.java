// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFEComponentTransferElement;

public class SVGOMFEComponentTransferElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEComponentTransferElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    
    protected SVGOMFEComponentTransferElement() {
    }
    
    public SVGOMFEComponentTransferElement(final String prefix, final AbstractDocument owner) {
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
    }
    
    @Override
    public String getLocalName() {
        return "feComponentTransfer";
    }
    
    @Override
    public SVGAnimatedString getIn1() {
        return this.in;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEComponentTransferElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFEComponentTransferElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "in", new TraitInformation(true, 16));
        SVGOMFEComponentTransferElement.xmlTraitInformation = t;
    }
}
