// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.Reader;
import java.awt.geom.AffineTransform;

public class AWTTransformProducer implements TransformListHandler
{
    protected AffineTransform affineTransform;
    
    public static AffineTransform createAffineTransform(final Reader r) throws ParseException {
        final TransformListParser p = new TransformListParser();
        final AWTTransformProducer th = new AWTTransformProducer();
        p.setTransformListHandler(th);
        p.parse(r);
        return th.getAffineTransform();
    }
    
    public static AffineTransform createAffineTransform(final String s) throws ParseException {
        final TransformListParser p = new TransformListParser();
        final AWTTransformProducer th = new AWTTransformProducer();
        p.setTransformListHandler(th);
        p.parse(s);
        return th.getAffineTransform();
    }
    
    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }
    
    @Override
    public void startTransformList() throws ParseException {
        this.affineTransform = new AffineTransform();
    }
    
    @Override
    public void matrix(final float a, final float b, final float c, final float d, final float e, final float f) throws ParseException {
        this.affineTransform.concatenate(new AffineTransform(a, b, c, d, e, f));
    }
    
    @Override
    public void rotate(final float theta) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getRotateInstance(Math.toRadians(theta)));
    }
    
    @Override
    public void rotate(final float theta, final float cx, final float cy) throws ParseException {
        final AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(theta), cx, cy);
        this.affineTransform.concatenate(at);
    }
    
    @Override
    public void translate(final float tx) throws ParseException {
        final AffineTransform at = AffineTransform.getTranslateInstance(tx, 0.0);
        this.affineTransform.concatenate(at);
    }
    
    @Override
    public void translate(final float tx, final float ty) throws ParseException {
        final AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
        this.affineTransform.concatenate(at);
    }
    
    @Override
    public void scale(final float sx) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sx));
    }
    
    @Override
    public void scale(final float sx, final float sy) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sy));
    }
    
    @Override
    public void skewX(final float skx) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getShearInstance(Math.tan(Math.toRadians(skx)), 0.0));
    }
    
    @Override
    public void skewY(final float sky) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getShearInstance(0.0, Math.tan(Math.toRadians(sky))));
    }
    
    @Override
    public void endTransformList() throws ParseException {
    }
}
