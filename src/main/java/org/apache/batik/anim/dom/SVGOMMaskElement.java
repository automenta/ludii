// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGMaskElement;

public class SVGOMMaskElement extends SVGGraphicsElement implements SVGMaskElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedEnumeration maskUnits;
    protected SVGOMAnimatedEnumeration maskContentUnits;
    
    protected SVGOMMaskElement() {
    }
    
    public SVGOMMaskElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "-10%", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "-10%", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "120%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "120%", (short)1, true);
        this.maskUnits = this.createLiveAnimatedEnumeration(null, "maskUnits", SVGOMMaskElement.UNITS_VALUES, (short)2);
        this.maskContentUnits = this.createLiveAnimatedEnumeration(null, "maskContentUnits", SVGOMMaskElement.UNITS_VALUES, (short)1);
    }
    
    @Override
    public String getLocalName() {
        return "mask";
    }
    
    @Override
    public SVGAnimatedEnumeration getMaskUnits() {
        return this.maskUnits;
    }
    
    @Override
    public SVGAnimatedEnumeration getMaskContentUnits() {
        return this.maskContentUnits;
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
    protected Node newNode() {
        return new SVGOMMaskElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMMaskElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        t.put(null, "maskUnits", new TraitInformation(true, 15));
        t.put(null, "maskContentUnits", new TraitInformation(true, 15));
        SVGOMMaskElement.xmlTraitInformation = t;
        UNITS_VALUES = new String[] { "", "userSpaceOnUse", "objectBoundingBox" };
    }
}
