// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.image.Raster;
import org.apache.batik.ext.awt.image.renderable.TileRable;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Shape;
import java.awt.image.renderable.RenderContext;
import java.awt.color.ColorSpace;
import org.apache.batik.ext.awt.image.renderable.TileRable8Bit;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.PaintContext;

public class PatternPaintContext implements PaintContext
{
    private ColorModel rasterCM;
    private WritableRaster raster;
    private RenderedImage tiled;
    protected AffineTransform usr2dev;
    private static Rectangle EVERYTHING;
    
    public AffineTransform getUsr2Dev() {
        return this.usr2dev;
    }
    
    public PatternPaintContext(final ColorModel destCM, final AffineTransform usr2dev, RenderingHints hints, final Filter tile, final Rectangle2D patternRegion, final boolean overflow) {
        if (usr2dev == null) {
            throw new IllegalArgumentException();
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        if (tile == null) {
            throw new IllegalArgumentException();
        }
        this.usr2dev = usr2dev;
        final TileRable tileRable = new TileRable8Bit(tile, PatternPaintContext.EVERYTHING, patternRegion, overflow);
        final ColorSpace destCS = destCM.getColorSpace();
        if (destCS == ColorSpace.getInstance(1000)) {
            tileRable.setColorSpaceLinear(false);
        }
        else if (destCS == ColorSpace.getInstance(1004)) {
            tileRable.setColorSpaceLinear(true);
        }
        final RenderContext rc = new RenderContext(usr2dev, PatternPaintContext.EVERYTHING, hints);
        this.tiled = tileRable.createRendering(rc);
        if (this.tiled != null) {
            final Rectangle2D devRgn = usr2dev.createTransformedShape(patternRegion).getBounds();
            if (devRgn.getWidth() > 128.0 || devRgn.getHeight() > 128.0) {
                this.tiled = new TileCacheRed(GraphicsUtil.wrap(this.tiled), 256, 64);
            }
            this.rasterCM = this.tiled.getColorModel();
            if (this.rasterCM.hasAlpha()) {
                if (destCM.hasAlpha()) {
                    this.rasterCM = GraphicsUtil.coerceColorModel(this.rasterCM, destCM.isAlphaPremultiplied());
                }
                else {
                    this.rasterCM = GraphicsUtil.coerceColorModel(this.rasterCM, false);
                }
            }
            return;
        }
        this.rasterCM = ColorModel.getRGBdefault();
        final WritableRaster wr = this.rasterCM.createCompatibleWritableRaster(32, 32);
        this.tiled = GraphicsUtil.wrap(new BufferedImage(this.rasterCM, wr, false, null));
    }
    
    @Override
    public void dispose() {
        this.raster = null;
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.rasterCM;
    }
    
    @Override
    public Raster getRaster(final int x, final int y, final int width, final int height) {
        if (this.raster == null || this.raster.getWidth() < width || this.raster.getHeight() < height) {
            this.raster = this.rasterCM.createCompatibleWritableRaster(width, height);
        }
        final WritableRaster wr = this.raster.createWritableChild(0, 0, width, height, x, y, null);
        this.tiled.copyData(wr);
        GraphicsUtil.coerceData(wr, this.tiled.getColorModel(), this.rasterCM.isAlphaPremultiplied());
        if (this.raster.getWidth() == width && this.raster.getHeight() == height) {
            return this.raster;
        }
        return wr.createTranslatedChild(0, 0);
    }
    
    static {
        PatternPaintContext.EVERYTHING = new Rectangle(-536870912, -536870912, 1073741823, 1073741823);
    }
}
