// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public class RectValue extends AbstractValue
{
    protected Value top;
    protected Value right;
    protected Value bottom;
    protected Value left;
    
    public RectValue(final Value t, final Value r, final Value b, final Value l) {
        this.top = t;
        this.right = r;
        this.bottom = b;
        this.left = l;
    }
    
    @Override
    public short getPrimitiveType() {
        return 24;
    }
    
    @Override
    public String getCssText() {
        return "rect(" + this.top.getCssText() + ", " + this.right.getCssText() + ", " + this.bottom.getCssText() + ", " + this.left.getCssText() + ')';
    }
    
    @Override
    public Value getTop() throws DOMException {
        return this.top;
    }
    
    @Override
    public Value getRight() throws DOMException {
        return this.right;
    }
    
    @Override
    public Value getBottom() throws DOMException {
        return this.bottom;
    }
    
    @Override
    public Value getLeft() throws DOMException {
        return this.left;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
