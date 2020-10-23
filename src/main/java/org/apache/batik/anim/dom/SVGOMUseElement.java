// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGElementInstance;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGOMUseShadowRoot;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGUseElement;

public class SVGOMUseElement extends SVGURIReferenceGraphicsElement implements SVGUseElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMUseShadowRoot shadowTree;
    
    protected SVGOMUseElement() {
    }
    
    public SVGOMUseElement(final String prefix, final AbstractDocument owner) {
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
    }
    
    @Override
    public String getLocalName() {
        return "use";
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
    public SVGElementInstance getInstanceRoot() {
        throw new UnsupportedOperationException("SVGUseElement.getInstanceRoot is not implemented");
    }
    
    @Override
    public SVGElementInstance getAnimatedInstanceRoot() {
        throw new UnsupportedOperationException("SVGUseElement.getAnimatedInstanceRoot is not implemented");
    }
    
    @Override
    public Node getCSSFirstChild() {
        if (this.shadowTree != null) {
            return this.shadowTree.getFirstChild();
        }
        return null;
    }
    
    @Override
    public Node getCSSLastChild() {
        return this.getCSSFirstChild();
    }
    
    @Override
    public boolean isHiddenFromSelectors() {
        return true;
    }
    
    public void setUseShadowTree(final SVGOMUseShadowRoot r) {
        this.shadowTree = r;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMUseElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMUseElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMUseElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGURIReferenceGraphicsElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        SVGOMUseElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMUseElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMUseElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "embed");
        SVGOMUseElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}
