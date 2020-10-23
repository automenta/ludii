// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.Iterator;
import org.w3c.dom.Element;
import org.apache.batik.anim.values.AnimatableValue;
import java.util.LinkedList;

public abstract class AbstractSVGAnimatedValue implements AnimatedLiveAttributeValue
{
    protected AbstractElement element;
    protected String namespaceURI;
    protected String localName;
    protected boolean hasAnimVal;
    protected LinkedList listeners;
    
    public AbstractSVGAnimatedValue(final AbstractElement elt, final String ns, final String ln) {
        this.listeners = new LinkedList();
        this.element = elt;
        this.namespaceURI = ns;
        this.localName = ln;
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    public boolean isSpecified() {
        return this.hasAnimVal || this.element.hasAttributeNS(this.namespaceURI, this.localName);
    }
    
    protected abstract void updateAnimatedValue(final AnimatableValue p0);
    
    @Override
    public void addAnimatedAttributeListener(final AnimatedAttributeListener aal) {
        if (!this.listeners.contains(aal)) {
            this.listeners.add(aal);
        }
    }
    
    @Override
    public void removeAnimatedAttributeListener(final AnimatedAttributeListener aal) {
        this.listeners.remove(aal);
    }
    
    protected void fireBaseAttributeListeners() {
        if (this.element instanceof SVGOMElement) {
            ((SVGOMElement)this.element).fireBaseAttributeListeners(this.namespaceURI, this.localName);
        }
    }
    
    protected void fireAnimatedAttributeListeners() {
        for (final Object listener1 : this.listeners) {
            final AnimatedAttributeListener listener2 = (AnimatedAttributeListener)listener1;
            listener2.animatedAttributeChanged(this.element, this);
        }
    }
}
