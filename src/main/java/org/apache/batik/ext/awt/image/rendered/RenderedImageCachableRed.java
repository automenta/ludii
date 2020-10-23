// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Shape;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.awt.image.RenderedImage;

public class RenderedImageCachableRed implements CachableRed
{
    private RenderedImage src;
    private Vector srcs;
    
    public static CachableRed wrap(final RenderedImage ri) {
        if (ri instanceof CachableRed) {
            return (CachableRed)ri;
        }
        if (ri instanceof BufferedImage) {
            return new BufferedImageCachableRed((BufferedImage)ri);
        }
        return new RenderedImageCachableRed(ri);
    }
    
    public RenderedImageCachableRed(final RenderedImage src) {
        this.srcs = new Vector(0);
        if (src == null) {
            throw new IllegalArgumentException();
        }
        this.src = src;
    }
    
    @Override
    public Vector getSources() {
        return this.srcs;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
    }
    
    @Override
    public int getMinX() {
        return this.src.getMinX();
    }
    
    @Override
    public int getMinY() {
        return this.src.getMinY();
    }
    
    @Override
    public int getWidth() {
        return this.src.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.src.getHeight();
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.src.getColorModel();
    }
    
    @Override
    public SampleModel getSampleModel() {
        return this.src.getSampleModel();
    }
    
    @Override
    public int getMinTileX() {
        return this.src.getMinTileX();
    }
    
    @Override
    public int getMinTileY() {
        return this.src.getMinTileY();
    }
    
    @Override
    public int getNumXTiles() {
        return this.src.getNumXTiles();
    }
    
    @Override
    public int getNumYTiles() {
        return this.src.getNumYTiles();
    }
    
    @Override
    public int getTileGridXOffset() {
        return this.src.getTileGridXOffset();
    }
    
    @Override
    public int getTileGridYOffset() {
        return this.src.getTileGridYOffset();
    }
    
    @Override
    public int getTileWidth() {
        return this.src.getTileWidth();
    }
    
    @Override
    public int getTileHeight() {
        return this.src.getTileHeight();
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.src.getProperty(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.src.getPropertyNames();
    }
    
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        return this.src.getTile(tileX, tileY);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster raster) {
        return this.src.copyData(raster);
    }
    
    @Override
    public Raster getData() {
        return this.src.getData();
    }
    
    @Override
    public Raster getData(final Rectangle rect) {
        return this.src.getData(rect);
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle outputRgn) {
        throw new IndexOutOfBoundsException("Nonexistant source requested.");
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle inputRgn) {
        throw new IndexOutOfBoundsException("Nonexistant source requested.");
    }
}
