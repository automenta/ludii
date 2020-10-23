// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFEMergeElement;

public class SVGOMFEMergeElement extends SVGOMFilterPrimitiveStandardAttributes implements SVGFEMergeElement
{
    protected SVGOMFEMergeElement() {
    }
    
    public SVGOMFEMergeElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "feMerge";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEMergeElement();
    }
}
