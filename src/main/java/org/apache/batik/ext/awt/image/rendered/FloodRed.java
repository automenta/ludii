// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Graphics2D;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.WritableRaster;

public class FloodRed extends AbstractRed
{
    private WritableRaster raster;
    
    public FloodRed(final Rectangle bounds) {
        this(bounds, new Color(0, 0, 0, 0));
    }
    
    public FloodRed(final Rectangle bounds, final Paint paint) {
        final ColorModel cm = GraphicsUtil.sRGB_Unpre;
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        int tw = bounds.width;
        if (tw > defSz) {
            tw = defSz;
        }
        int th = bounds.height;
        if (th > defSz) {
            th = defSz;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(tw, th);
        this.init((CachableRed)null, bounds, cm, sm, 0, 0, null);
        this.raster = Raster.createWritableRaster(sm, new Point(0, 0));
        final BufferedImage offScreen = new BufferedImage(cm, this.raster, cm.isAlphaPremultiplied(), null);
        final Graphics2D g = GraphicsUtil.createGraphics(offScreen);
        g.setPaint(paint);
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.dispose();
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        final int tx = this.tileGridXOff + x * this.tileWidth;
        final int ty = this.tileGridYOff + y * this.tileHeight;
        return this.raster.createTranslatedChild(tx, ty);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final int tx0 = this.getXTile(wr.getMinX());
        final int ty0 = this.getYTile(wr.getMinY());
        final int tx2 = this.getXTile(wr.getMinX() + wr.getWidth() - 1);
        final int ty2 = this.getYTile(wr.getMinY() + wr.getHeight() - 1);
        final boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        for (int y = ty0; y <= ty2; ++y) {
            for (int x = tx0; x <= tx2; ++x) {
                final Raster r = this.getTile(x, y);
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                }
                else {
                    GraphicsUtil.copyData_FALLBACK(r, wr);
                }
            }
        }
        return wr;
    }
}
