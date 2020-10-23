// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public abstract class SVGOMFilterPrimitiveStandardAttributes extends SVGStylableElement implements SVGFilterPrimitiveStandardAttributes
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedString result;
    
    protected SVGOMFilterPrimitiveStandardAttributes() {
    }
    
    protected SVGOMFilterPrimitiveStandardAttributes(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0%", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0%", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "100%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "100%", (short)1, true);
        this.result = this.createLiveAnimatedString(null, "result");
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
    public SVGAnimatedString getResult() {
        return this.result;
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        t.put(null, "result", new TraitInformation(true, 16));
        SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation = t;
    }
}
