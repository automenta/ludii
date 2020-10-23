// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class SVGOMToBeImplementedElement extends SVGGraphicsElement
{
    protected String localName;
    
    protected SVGOMToBeImplementedElement() {
    }
    
    public SVGOMToBeImplementedElement(final String prefix, final AbstractDocument owner, final String localName) {
        super(prefix, owner);
        this.localName = localName;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMToBeImplementedElement();
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final SVGOMToBeImplementedElement ae = (SVGOMToBeImplementedElement)n;
        ae.localName = this.localName;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final SVGOMToBeImplementedElement ae = (SVGOMToBeImplementedElement)n;
        ae.localName = this.localName;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final SVGOMToBeImplementedElement ae = (SVGOMToBeImplementedElement)n;
        ae.localName = this.localName;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final SVGOMToBeImplementedElement ae = (SVGOMToBeImplementedElement)n;
        ae.localName = this.localName;
        return n;
    }
}
