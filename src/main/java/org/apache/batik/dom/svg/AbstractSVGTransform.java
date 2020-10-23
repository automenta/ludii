// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGMatrix;
import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGTransform;

public abstract class AbstractSVGTransform implements SVGTransform
{
    protected short type;
    protected AffineTransform affineTransform;
    protected float angle;
    protected float x;
    protected float y;
    
    public AbstractSVGTransform() {
        this.type = 0;
    }
    
    protected abstract SVGMatrix createMatrix();
    
    public void setType(final short type) {
        this.type = type;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void assign(final AbstractSVGTransform t) {
        this.type = t.type;
        this.affineTransform = t.affineTransform;
        this.angle = t.angle;
        this.x = t.x;
        this.y = t.y;
    }
    
    @Override
    public short getType() {
        return this.type;
    }
    
    @Override
    public SVGMatrix getMatrix() {
        return this.createMatrix();
    }
    
    @Override
    public float getAngle() {
        return this.angle;
    }
    
    @Override
    public void setMatrix(final SVGMatrix matrix) {
        this.type = 1;
        this.affineTransform = new AffineTransform(matrix.getA(), matrix.getB(), matrix.getC(), matrix.getD(), matrix.getE(), matrix.getF());
    }
    
    @Override
    public void setTranslate(final float tx, final float ty) {
        this.type = 2;
        this.affineTransform = AffineTransform.getTranslateInstance(tx, ty);
    }
    
    @Override
    public void setScale(final float sx, final float sy) {
        this.type = 3;
        this.affineTransform = AffineTransform.getScaleInstance(sx, sy);
    }
    
    @Override
    public void setRotate(final float angle, final float cx, final float cy) {
        this.type = 4;
        this.affineTransform = AffineTransform.getRotateInstance(Math.toRadians(angle), cx, cy);
        this.angle = angle;
        this.x = cx;
        this.y = cy;
    }
    
    @Override
    public void setSkewX(final float angle) {
        this.type = 5;
        this.affineTransform = AffineTransform.getShearInstance(Math.tan(Math.toRadians(angle)), 0.0);
        this.angle = angle;
    }
    
    @Override
    public void setSkewY(final float angle) {
        this.type = 6;
        this.affineTransform = AffineTransform.getShearInstance(0.0, Math.tan(Math.toRadians(angle)));
        this.angle = angle;
    }
}
