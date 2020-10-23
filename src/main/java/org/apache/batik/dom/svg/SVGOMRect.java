// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGRect;

public class SVGOMRect implements SVGRect
{
    protected float x;
    protected float y;
    protected float w;
    protected float h;
    
    public SVGOMRect() {
    }
    
    public SVGOMRect(final float x, final float y, final float w, final float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public void setX(final float x) throws DOMException {
        this.x = x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public void setY(final float y) throws DOMException {
        this.y = y;
    }
    
    @Override
    public float getWidth() {
        return this.w;
    }
    
    @Override
    public void setWidth(final float width) throws DOMException {
        this.w = width;
    }
    
    @Override
    public float getHeight() {
        return this.h;
    }
    
    @Override
    public void setHeight(final float height) throws DOMException {
        this.h = height;
    }
}
