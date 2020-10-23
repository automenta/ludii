// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.util.Map;
import java.awt.Rectangle;

public class TranslateRed extends AbstractRed
{
    protected int deltaX;
    protected int deltaY;
    
    public TranslateRed(final CachableRed cr, final int xloc, final int yloc) {
        super(cr, new Rectangle(xloc, yloc, cr.getWidth(), cr.getHeight()), cr.getColorModel(), cr.getSampleModel(), cr.getTileGridXOffset() + xloc - cr.getMinX(), cr.getTileGridYOffset() + yloc - cr.getMinY(), null);
        this.deltaX = xloc - cr.getMinX();
        this.deltaY = yloc - cr.getMinY();
    }
    
    public int getDeltaX() {
        return this.deltaX;
    }
    
    public int getDeltaY() {
        return this.deltaY;
    }
    
    public CachableRed getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.getSource().getProperty(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.getSource().getPropertyNames();
    }
    
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        final Raster r = this.getSource().getTile(tileX, tileY);
        return r.createTranslatedChild(r.getMinX() + this.deltaX, r.getMinY() + this.deltaY);
    }
    
    @Override
    public Raster getData() {
        final Raster r = this.getSource().getData();
        return r.createTranslatedChild(r.getMinX() + this.deltaX, r.getMinY() + this.deltaY);
    }
    
    @Override
    public Raster getData(final Rectangle rect) {
        final Rectangle r = (Rectangle)rect.clone();
        r.translate(-this.deltaX, -this.deltaY);
        final Raster ret = this.getSource().getData(r);
        return ret.createTranslatedChild(ret.getMinX() + this.deltaX, ret.getMinY() + this.deltaY);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final WritableRaster wr2 = wr.createWritableTranslatedChild(wr.getMinX() - this.deltaX, wr.getMinY() - this.deltaY);
        this.getSource().copyData(wr2);
        return wr;
    }
}
