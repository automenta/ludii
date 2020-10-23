// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGException;
import java.awt.geom.NoninvertibleTransformException;
import org.w3c.dom.DOMException;
import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGMatrix;

public abstract class AbstractSVGMatrix implements SVGMatrix
{
    protected static final AffineTransform FLIP_X_TRANSFORM;
    protected static final AffineTransform FLIP_Y_TRANSFORM;
    
    protected abstract AffineTransform getAffineTransform();
    
    @Override
    public float getA() {
        return (float)this.getAffineTransform().getScaleX();
    }
    
    @Override
    public void setA(final float a) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(a, at.getShearY(), at.getShearX(), at.getScaleY(), at.getTranslateX(), at.getTranslateY());
    }
    
    @Override
    public float getB() {
        return (float)this.getAffineTransform().getShearY();
    }
    
    @Override
    public void setB(final float b) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(at.getScaleX(), b, at.getShearX(), at.getScaleY(), at.getTranslateX(), at.getTranslateY());
    }
    
    @Override
    public float getC() {
        return (float)this.getAffineTransform().getShearX();
    }
    
    @Override
    public void setC(final float c) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(at.getScaleX(), at.getShearY(), c, at.getScaleY(), at.getTranslateX(), at.getTranslateY());
    }
    
    @Override
    public float getD() {
        return (float)this.getAffineTransform().getScaleY();
    }
    
    @Override
    public void setD(final float d) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), d, at.getTranslateX(), at.getTranslateY());
    }
    
    @Override
    public float getE() {
        return (float)this.getAffineTransform().getTranslateX();
    }
    
    @Override
    public void setE(final float e) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), e, at.getTranslateY());
    }
    
    @Override
    public float getF() {
        return (float)this.getAffineTransform().getTranslateY();
    }
    
    @Override
    public void setF(final float f) throws DOMException {
        final AffineTransform at = this.getAffineTransform();
        at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), at.getTranslateX(), f);
    }
    
    @Override
    public SVGMatrix multiply(final SVGMatrix secondMatrix) {
        final AffineTransform at = new AffineTransform(secondMatrix.getA(), secondMatrix.getB(), secondMatrix.getC(), secondMatrix.getD(), secondMatrix.getE(), secondMatrix.getF());
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.concatenate(at);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix inverse() throws SVGException {
        try {
            return new SVGOMMatrix(this.getAffineTransform().createInverse());
        }
        catch (NoninvertibleTransformException e) {
            throw new SVGOMException((short)2, e.getMessage());
        }
    }
    
    @Override
    public SVGMatrix translate(final float x, final float y) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.translate(x, y);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix scale(final float scaleFactor) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.scale(scaleFactor, scaleFactor);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix scaleNonUniform(final float scaleFactorX, final float scaleFactorY) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.scale(scaleFactorX, scaleFactorY);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix rotate(final float angle) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.rotate(Math.toRadians(angle));
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix rotateFromVector(final float x, final float y) throws SVGException {
        if (x == 0.0f || y == 0.0f) {
            throw new SVGOMException((short)1, "");
        }
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.rotate(Math.atan2(y, x));
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix flipX() {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.concatenate(AbstractSVGMatrix.FLIP_X_TRANSFORM);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix flipY() {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.concatenate(AbstractSVGMatrix.FLIP_Y_TRANSFORM);
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix skewX(final float angleDeg) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.concatenate(AffineTransform.getShearInstance(Math.tan(Math.toRadians(angleDeg)), 0.0));
        return new SVGOMMatrix(tr);
    }
    
    @Override
    public SVGMatrix skewY(final float angleDeg) {
        final AffineTransform tr = (AffineTransform)this.getAffineTransform().clone();
        tr.concatenate(AffineTransform.getShearInstance(0.0, Math.tan(Math.toRadians(angleDeg))));
        return new SVGOMMatrix(tr);
    }
    
    static {
        FLIP_X_TRANSFORM = new AffineTransform(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        FLIP_Y_TRANSFORM = new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f);
    }
}
