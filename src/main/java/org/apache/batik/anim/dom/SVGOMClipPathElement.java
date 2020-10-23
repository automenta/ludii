// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGClipPathElement;

public class SVGOMClipPathElement extends SVGGraphicsElement implements SVGClipPathElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] CLIP_PATH_UNITS_VALUES;
    protected SVGOMAnimatedEnumeration clipPathUnits;
    
    protected SVGOMClipPathElement() {
    }
    
    public SVGOMClipPathElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.clipPathUnits = this.createLiveAnimatedEnumeration(null, "clipPathUnits", SVGOMClipPathElement.CLIP_PATH_UNITS_VALUES, (short)1);
    }
    
    @Override
    public String getLocalName() {
        return "clipPath";
    }
    
    @Override
    public SVGAnimatedEnumeration getClipPathUnits() {
        return this.clipPathUnits;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMClipPathElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMClipPathElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "clipPathUnits", new TraitInformation(true, 15));
        SVGOMClipPathElement.xmlTraitInformation = t;
        CLIP_PATH_UNITS_VALUES = new String[] { "", "userSpaceOnUse", "objectBoundingBox" };
    }
}
