// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.CSSEngine;

public class CSSOMSVGViewCSS extends CSSOMViewCSS
{
    public CSSOMSVGViewCSS(final CSSEngine engine) {
        super(engine);
    }
    
    @Override
    public CSSStyleDeclaration getComputedStyle(final Element elt, final String pseudoElt) {
        if (elt instanceof CSSStylableElement) {
            return new CSSOMSVGComputedStyle(this.cssEngine, (CSSStylableElement)elt, pseudoElt);
        }
        return null;
    }
}
