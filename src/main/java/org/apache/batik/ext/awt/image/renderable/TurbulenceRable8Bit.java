// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.TurbulencePatternRed;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;

public class TurbulenceRable8Bit extends AbstractColorInterpolationRable implements TurbulenceRable
{
    int seed;
    int numOctaves;
    double baseFreqX;
    double baseFreqY;
    boolean stitched;
    boolean fractalNoise;
    Rectangle2D region;
    
    public TurbulenceRable8Bit(final Rectangle2D region) {
        this.seed = 0;
        this.numOctaves = 1;
        this.baseFreqX = 0.0;
        this.baseFreqY = 0.0;
        this.stitched = false;
        this.fractalNoise = false;
        this.region = region;
    }
    
    public TurbulenceRable8Bit(final Rectangle2D region, final int seed, final int numOctaves, final double baseFreqX, final double baseFreqY, final boolean stitched, final boolean fractalNoise) {
        this.seed = 0;
        this.numOctaves = 1;
        this.baseFreqX = 0.0;
        this.baseFreqY = 0.0;
        this.stitched = false;
        this.fractalNoise = false;
        this.seed = seed;
        this.numOctaves = numOctaves;
        this.baseFreqX = baseFreqX;
        this.baseFreqY = baseFreqY;
        this.stitched = stitched;
        this.fractalNoise = fractalNoise;
        this.region = region;
    }
    
    @Override
    public Rectangle2D getTurbulenceRegion() {
        return (Rectangle2D)this.region.clone();
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.region.clone();
    }
    
    @Override
    public int getSeed() {
        return this.seed;
    }
    
    @Override
    public int getNumOctaves() {
        return this.numOctaves;
    }
    
    @Override
    public double getBaseFrequencyX() {
        return this.baseFreqX;
    }
    
    @Override
    public double getBaseFrequencyY() {
        return this.baseFreqY;
    }
    
    @Override
    public boolean isStitched() {
        return this.stitched;
    }
    
    @Override
    public boolean isFractalNoise() {
        return this.fractalNoise;
    }
    
    @Override
    public void setTurbulenceRegion(final Rectangle2D turbulenceRegion) {
        this.touch();
        this.region = turbulenceRegion;
    }
    
    @Override
    public void setSeed(final int seed) {
        this.touch();
        this.seed = seed;
    }
    
    @Override
    public void setNumOctaves(final int numOctaves) {
        this.touch();
        this.numOctaves = numOctaves;
    }
    
    @Override
    public void setBaseFrequencyX(final double baseFreqX) {
        this.touch();
        this.baseFreqX = baseFreqX;
    }
    
    @Override
    public void setBaseFrequencyY(final double baseFreqY) {
        this.touch();
        this.baseFreqY = baseFreqY;
    }
    
    @Override
    public void setStitched(final boolean stitched) {
        this.touch();
        this.stitched = stitched;
    }
    
    @Override
    public void setFractalNoise(final boolean fractalNoise) {
        this.touch();
        this.fractalNoise = fractalNoise;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final Shape aoi = rc.getAreaOfInterest();
        Rectangle2D aoiRect;
        if (aoi == null) {
            aoiRect = this.getBounds2D();
        }
        else {
            final Rectangle2D rect = this.getBounds2D();
            aoiRect = aoi.getBounds2D();
            if (!aoiRect.intersects(rect)) {
                return null;
            }
            Rectangle2D.intersect(aoiRect, rect, aoiRect);
        }
        final AffineTransform usr2dev = rc.getTransform();
        final Rectangle devRect = usr2dev.createTransformedShape(aoiRect).getBounds();
        if (devRect.width <= 0 || devRect.height <= 0) {
            return null;
        }
        final ColorSpace cs = this.getOperationColorSpace();
        Rectangle2D tile = null;
        if (this.stitched) {
            tile = (Rectangle2D)this.region.clone();
        }
        AffineTransform patternTxf = new AffineTransform();
        try {
            patternTxf = usr2dev.createInverse();
        }
        catch (NoninvertibleTransformException ex) {}
        return new TurbulencePatternRed(this.baseFreqX, this.baseFreqY, this.numOctaves, this.seed, this.fractalNoise, tile, patternTxf, devRect, cs, true);
    }
}
