// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.util.Map;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class BufferedImageCachableRed extends AbstractRed
{
    BufferedImage bi;
    
    public BufferedImageCachableRed(final BufferedImage bi) {
        super((CachableRed)null, new Rectangle(bi.getMinX(), bi.getMinY(), bi.getWidth(), bi.getHeight()), bi.getColorModel(), bi.getSampleModel(), bi.getMinX(), bi.getMinY(), null);
        this.bi = bi;
    }
    
    public BufferedImageCachableRed(final BufferedImage bi, final int xloc, final int yloc) {
        super((CachableRed)null, new Rectangle(xloc, yloc, bi.getWidth(), bi.getHeight()), bi.getColorModel(), bi.getSampleModel(), xloc, yloc, null);
        this.bi = bi;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
    }
    
    public BufferedImage getBufferedImage() {
        return this.bi;
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.bi.getProperty(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.bi.getPropertyNames();
    }
    
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        return this.bi.getTile(tileX, tileY);
    }
    
    @Override
    public Raster getData() {
        final Raster r = this.bi.getData();
        return r.createTranslatedChild(this.getMinX(), this.getMinY());
    }
    
    @Override
    public Raster getData(final Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();
        if (!r.intersects(this.getBounds())) {
            return null;
        }
        r = r.intersection(this.getBounds());
        r.translate(-this.getMinX(), -this.getMinY());
        final Raster ret = this.bi.getData(r);
        return ret.createTranslatedChild(ret.getMinX() + this.getMinX(), ret.getMinY() + this.getMinY());
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final WritableRaster wr2 = wr.createWritableTranslatedChild(wr.getMinX() - this.getMinX(), wr.getMinY() - this.getMinY());
        GraphicsUtil.copyData(this.bi.getRaster(), wr2);
        return wr;
    }
}
