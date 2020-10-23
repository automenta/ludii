// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.DiffuseLightingRed;
import org.apache.batik.ext.awt.image.rendered.BumpMap;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.Light;

public class DiffuseLightingRable8Bit extends AbstractColorInterpolationRable implements DiffuseLightingRable
{
    private double surfaceScale;
    private double kd;
    private Light light;
    private Rectangle2D litRegion;
    private float[] kernelUnitLength;
    
    public DiffuseLightingRable8Bit(final Filter src, final Rectangle2D litRegion, final Light light, final double kd, final double surfaceScale, final double[] kernelUnitLength) {
        super(src, null);
        this.kernelUnitLength = null;
        this.setLight(light);
        this.setKd(kd);
        this.setSurfaceScale(surfaceScale);
        this.setLitRegion(litRegion);
        this.setKernelUnitLength(kernelUnitLength);
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.litRegion.clone();
    }
    
    @Override
    public Rectangle2D getLitRegion() {
        return this.getBounds2D();
    }
    
    @Override
    public void setLitRegion(final Rectangle2D litRegion) {
        this.touch();
        this.litRegion = litRegion;
    }
    
    @Override
    public Light getLight() {
        return this.light;
    }
    
    @Override
    public void setLight(final Light light) {
        this.touch();
        this.light = light;
    }
    
    @Override
    public double getSurfaceScale() {
        return this.surfaceScale;
    }
    
    @Override
    public void setSurfaceScale(final double surfaceScale) {
        this.touch();
        this.surfaceScale = surfaceScale;
    }
    
    @Override
    public double getKd() {
        return this.kd;
    }
    
    @Override
    public void setKd(final double kd) {
        this.touch();
        this.kd = kd;
    }
    
    @Override
    public double[] getKernelUnitLength() {
        if (this.kernelUnitLength == null) {
            return null;
        }
        final double[] ret = { this.kernelUnitLength[0], this.kernelUnitLength[1] };
        return ret;
    }
    
    @Override
    public void setKernelUnitLength(final double[] kernelUnitLength) {
        this.touch();
        if (kernelUnitLength == null) {
            this.kernelUnitLength = null;
            return;
        }
        if (this.kernelUnitLength == null) {
            this.kernelUnitLength = new float[2];
        }
        this.kernelUnitLength[0] = (float)kernelUnitLength[0];
        this.kernelUnitLength[1] = (float)kernelUnitLength[1];
    }
    
    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        final Rectangle2D aoiR = aoi.getBounds2D();
        Rectangle2D.intersect(aoiR, this.getBounds2D(), aoiR);
        final AffineTransform at = rc.getTransform();
        Rectangle devRect = at.createTransformedShape(aoiR).getBounds();
        if (devRect.width == 0 || devRect.height == 0) {
            return null;
        }
        final double sx = at.getScaleX();
        final double sy = at.getScaleY();
        final double shx = at.getShearX();
        final double shy = at.getShearY();
        final double tx = at.getTranslateX();
        final double ty = at.getTranslateY();
        double scaleX = Math.sqrt(sx * sx + shy * shy);
        double scaleY = Math.sqrt(sy * sy + shx * shx);
        if (scaleX == 0.0 || scaleY == 0.0) {
            return null;
        }
        if (this.kernelUnitLength != null) {
            if (this.kernelUnitLength[0] > 0.0f && scaleX > 1.0f / this.kernelUnitLength[0]) {
                scaleX = 1.0f / this.kernelUnitLength[0];
            }
            if (this.kernelUnitLength[1] > 0.0f && scaleY > 1.0f / this.kernelUnitLength[1]) {
                scaleY = 1.0f / this.kernelUnitLength[1];
            }
        }
        final AffineTransform scale = AffineTransform.getScaleInstance(scaleX, scaleY);
        devRect = scale.createTransformedShape(aoiR).getBounds();
        aoiR.setRect(aoiR.getX() - 2.0 / scaleX, aoiR.getY() - 2.0 / scaleY, aoiR.getWidth() + 4.0 / scaleX, aoiR.getHeight() + 4.0 / scaleY);
        rc = (RenderContext)rc.clone();
        rc.setAreaOfInterest(aoiR);
        rc.setTransform(scale);
        CachableRed cr = GraphicsUtil.wrap(this.getSource().createRendering(rc));
        final BumpMap bumpMap = new BumpMap(cr, this.surfaceScale, scaleX, scaleY);
        cr = new DiffuseLightingRed(this.kd, this.light, bumpMap, devRect, 1.0 / scaleX, 1.0 / scaleY, this.isColorSpaceLinear());
        final AffineTransform shearAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        if (!shearAt.isIdentity()) {
            final RenderingHints rh = rc.getRenderingHints();
            final Rectangle padRect = new Rectangle(devRect.x - 1, devRect.y - 1, devRect.width + 2, devRect.height + 2);
            cr = new PadRed(cr, padRect, PadMode.REPLICATE, rh);
            cr = new AffineRed(cr, shearAt, rh);
        }
        return cr;
    }
}
