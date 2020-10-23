// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.Element;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;
import org.w3c.dom.css.DocumentCSS;

public abstract class AbstractStylableDocument extends AbstractDocument implements DocumentCSS, DocumentView
{
    protected transient AbstractView defaultView;
    protected transient CSSEngine cssEngine;
    
    protected AbstractStylableDocument() {
    }
    
    protected AbstractStylableDocument(final DocumentType dt, final DOMImplementation impl) {
        super(dt, impl);
    }
    
    public void setCSSEngine(final CSSEngine ctx) {
        this.cssEngine = ctx;
    }
    
    public CSSEngine getCSSEngine() {
        return this.cssEngine;
    }
    
    @Override
    public StyleSheetList getStyleSheets() {
        throw new RuntimeException(" !!! Not implemented");
    }
    
    @Override
    public AbstractView getDefaultView() {
        if (this.defaultView == null) {
            final ExtensibleDOMImplementation impl = (ExtensibleDOMImplementation)this.implementation;
            this.defaultView = impl.createViewCSS(this);
        }
        return this.defaultView;
    }
    
    public void clearViewCSS() {
        this.defaultView = null;
        if (this.cssEngine != null) {
            this.cssEngine.dispose();
        }
        this.cssEngine = null;
    }
    
    @Override
    public CSSStyleDeclaration getOverrideStyle(final Element elt, final String pseudoElt) {
        throw new RuntimeException(" !!! Not implemented");
    }
}
