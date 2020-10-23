// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGMetadataElement;

public class SVGOMMetadataElement extends SVGOMElement implements SVGMetadataElement
{
    protected SVGOMMetadataElement() {
    }
    
    public SVGOMMetadataElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "metadata";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMMetadataElement();
    }
}
