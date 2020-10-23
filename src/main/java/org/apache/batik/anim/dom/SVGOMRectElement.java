// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.Attr;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGRectElement;

public class SVGOMRectElement extends SVGGraphicsElement implements SVGRectElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected AbstractSVGAnimatedLength rx;
    protected AbstractSVGAnimatedLength ry;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    
    protected SVGOMRectElement() {
    }
    
    public SVGOMRectElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", null, (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", null, (short)1, true);
        this.rx = new AbstractSVGAnimatedLength(this, null, "rx", 2, true) {
            @Override
            protected String getDefaultValue() {
                final Attr attr = SVGOMRectElement.this.getAttributeNodeNS(null, "ry");
                if (attr == null) {
                    return "0";
                }
                return attr.getValue();
            }
            
            @Override
            protected void attrChanged() {
                super.attrChanged();
                final AbstractSVGAnimatedLength ry = (AbstractSVGAnimatedLength)SVGOMRectElement.this.getRy();
                if (this.isSpecified() && !ry.isSpecified()) {
                    ry.attrChanged();
                }
            }
        };
        this.ry = new AbstractSVGAnimatedLength(this, null, "ry", 1, true) {
            @Override
            protected String getDefaultValue() {
                final Attr attr = SVGOMRectElement.this.getAttributeNodeNS(null, "rx");
                if (attr == null) {
                    return "0";
                }
                return attr.getValue();
            }
            
            @Override
            protected void attrChanged() {
                super.attrChanged();
                final AbstractSVGAnimatedLength rx = (AbstractSVGAnimatedLength)SVGOMRectElement.this.getRx();
                if (this.isSpecified() && !rx.isSpecified()) {
                    rx.attrChanged();
                }
            }
        };
        this.liveAttributeValues.put(null, "rx", this.rx);
        this.liveAttributeValues.put(null, "ry", this.ry);
        final AnimatedAttributeListener l = ((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener();
        this.rx.addAnimatedAttributeListener(l);
        this.ry.addAnimatedAttributeListener(l);
    }
    
    @Override
    public String getLocalName() {
        return "rect";
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
    public SVGAnimatedLength getRx() {
        return this.rx;
    }
    
    @Override
    public SVGAnimatedLength getRy() {
        return this.ry;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMRectElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMRectElement.xmlTraitInformation;
    }
    
    @Override
    public void updateAttributeValue(final String ns, final String ln, final AnimatableValue val) {
        if (ns == null) {
            if (ln.equals("rx")) {
                super.updateAttributeValue(ns, ln, val);
                final AbstractSVGAnimatedLength ry = (AbstractSVGAnimatedLength)this.getRy();
                if (!ry.isSpecified()) {
                    super.updateAttributeValue(ns, "ry", val);
                }
                return;
            }
            if (ln.equals("ry")) {
                super.updateAttributeValue(ns, ln, val);
                final AbstractSVGAnimatedLength rx = (AbstractSVGAnimatedLength)this.getRx();
                if (!rx.isSpecified()) {
                    super.updateAttributeValue(ns, "rx", val);
                }
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "rx", new TraitInformation(true, 3, (short)1));
        t.put(null, "ry", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        SVGOMRectElement.xmlTraitInformation = t;
    }
}
