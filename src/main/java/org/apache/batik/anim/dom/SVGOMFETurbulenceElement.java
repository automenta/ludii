// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFETurbulenceElement;

public class SVGOMFETurbulenceElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFETurbulenceElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] STITCH_TILES_VALUES;
    protected static final String[] TYPE_VALUES;
    protected SVGOMAnimatedInteger numOctaves;
    protected SVGOMAnimatedNumber seed;
    protected SVGOMAnimatedEnumeration stitchTiles;
    protected SVGOMAnimatedEnumeration type;
    
    protected SVGOMFETurbulenceElement() {
    }
    
    public SVGOMFETurbulenceElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.numOctaves = this.createLiveAnimatedInteger(null, "numOctaves", 1);
        this.seed = this.createLiveAnimatedNumber(null, "seed", 0.0f);
        this.stitchTiles = this.createLiveAnimatedEnumeration(null, "stitchTiles", SVGOMFETurbulenceElement.STITCH_TILES_VALUES, (short)2);
        this.type = this.createLiveAnimatedEnumeration(null, "type", SVGOMFETurbulenceElement.TYPE_VALUES, (short)2);
    }
    
    @Override
    public String getLocalName() {
        return "feTurbulence";
    }
    
    @Override
    public SVGAnimatedNumber getBaseFrequencyX() {
        throw new UnsupportedOperationException("SVGFETurbulenceElement.getBaseFrequencyX is not implemented");
    }
    
    @Override
    public SVGAnimatedNumber getBaseFrequencyY() {
        throw new UnsupportedOperationException("SVGFETurbulenceElement.getBaseFrequencyY is not implemented");
    }
    
    @Override
    public SVGAnimatedInteger getNumOctaves() {
        return this.numOctaves;
    }
    
    @Override
    public SVGAnimatedNumber getSeed() {
        return this.seed;
    }
    
    @Override
    public SVGAnimatedEnumeration getStitchTiles() {
        return this.stitchTiles;
    }
    
    @Override
    public SVGAnimatedEnumeration getType() {
        return this.type;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFETurbulenceElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFETurbulenceElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, "baseFrequency", new TraitInformation(true, 4));
        t.put(null, "numOctaves", new TraitInformation(true, 1));
        t.put(null, "seed", new TraitInformation(true, 2));
        t.put(null, "stitchTiles", new TraitInformation(true, 15));
        t.put(null, "type", new TraitInformation(true, 15));
        SVGOMFETurbulenceElement.xmlTraitInformation = t;
        STITCH_TILES_VALUES = new String[] { "", "stitch", "noStitch" };
        TYPE_VALUES = new String[] { "", "fractalNoise", "turbulence" };
    }
}
