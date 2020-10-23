// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGDefinitionSrcElement;

public class SVGOMDefinitionSrcElement extends SVGOMElement implements SVGDefinitionSrcElement
{
    protected SVGOMDefinitionSrcElement() {
    }
    
    public SVGOMDefinitionSrcElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "definition-src";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMDefinitionSrcElement();
    }
}
