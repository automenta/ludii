// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.Element;
import org.w3c.dom.views.DocumentView;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.css.ViewCSS;

public class CSSOMViewCSS implements ViewCSS
{
    protected CSSEngine cssEngine;
    
    public CSSOMViewCSS(final CSSEngine engine) {
        this.cssEngine = engine;
    }
    
    @Override
    public DocumentView getDocument() {
        return (DocumentView)this.cssEngine.getDocument();
    }
    
    @Override
    public CSSStyleDeclaration getComputedStyle(final Element elt, final String pseudoElt) {
        if (elt instanceof CSSStylableElement) {
            return new CSSOMComputedStyle(this.cssEngine, (CSSStylableElement)elt, pseudoElt);
        }
        return null;
    }
}
