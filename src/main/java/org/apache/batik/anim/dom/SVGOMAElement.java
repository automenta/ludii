// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAElement;

public class SVGOMAElement extends SVGURIReferenceGraphicsElement implements SVGAElement
{
    protected static final AttributeInitializer attributeInitializer;
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString target;
    
    protected SVGOMAElement() {
    }
    
    public SVGOMAElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.target = this.createLiveAnimatedString(null, "target");
    }
    
    @Override
    public String getLocalName() {
        return "a";
    }
    
    @Override
    public SVGAnimatedString getTarget() {
        return this.target;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMAElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMAElement.xmlTraitInformation;
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMAElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMAElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "replace");
        SVGOMAElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onRequest");
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGURIReferenceGraphicsElement.xmlTraitInformation);
        t.put(null, "target", new TraitInformation(true, 16));
        SVGOMAElement.xmlTraitInformation = t;
    }
}
