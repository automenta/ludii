// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.Shape;
import java.util.Set;
import java.util.Iterator;
import java.awt.image.RenderedImage;
import java.util.Collection;
import java.util.List;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;
import java.util.HashMap;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Map;
import java.util.Vector;
import java.awt.Rectangle;

public abstract class AbstractRed implements CachableRed
{
    protected Rectangle bounds;
    protected Vector srcs;
    protected Map props;
    protected SampleModel sm;
    protected ColorModel cm;
    protected int tileGridXOff;
    protected int tileGridYOff;
    protected int tileWidth;
    protected int tileHeight;
    protected int minTileX;
    protected int minTileY;
    protected int numXTiles;
    protected int numYTiles;
    
    protected AbstractRed() {
    }
    
    protected AbstractRed(final Rectangle bounds, final Map props) {
        this.init((CachableRed)null, bounds, null, null, bounds.x, bounds.y, props);
    }
    
    protected AbstractRed(final CachableRed src, final Map props) {
        this.init(src, src.getBounds(), src.getColorModel(), src.getSampleModel(), src.getTileGridXOffset(), src.getTileGridYOffset(), props);
    }
    
    protected AbstractRed(final CachableRed src, final Rectangle bounds, final Map props) {
        this.init(src, bounds, src.getColorModel(), src.getSampleModel(), src.getTileGridXOffset(), src.getTileGridYOffset(), props);
    }
    
    protected AbstractRed(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final Map props) {
        this.init(src, bounds, cm, sm, (src == null) ? 0 : src.getTileGridXOffset(), (src == null) ? 0 : src.getTileGridYOffset(), props);
    }
    
    protected AbstractRed(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        this.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }
    
    protected void init(final CachableRed src, Rectangle bounds, ColorModel cm, SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        this.srcs = new Vector(1);
        if (src != null) {
            this.srcs.add(src);
            if (bounds == null) {
                bounds = src.getBounds();
            }
            if (cm == null) {
                cm = src.getColorModel();
            }
            if (sm == null) {
                sm = src.getSampleModel();
            }
        }
        this.bounds = bounds;
        this.tileGridXOff = tileGridXOff;
        this.tileGridYOff = tileGridYOff;
        this.props = new HashMap();
        if (props != null) {
            this.props.putAll(props);
        }
        if (cm == null) {
            cm = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0);
        }
        this.cm = cm;
        if (sm == null) {
            sm = cm.createCompatibleSampleModel(bounds.width, bounds.height);
        }
        this.sm = sm;
        this.updateTileGridInfo();
    }
    
    protected AbstractRed(final List srcs, final Rectangle bounds, final Map props) {
        this.init(srcs, bounds, null, null, bounds.x, bounds.y, props);
    }
    
    protected AbstractRed(final List srcs, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final Map props) {
        this.init(srcs, bounds, cm, sm, bounds.x, bounds.y, props);
    }
    
    protected AbstractRed(final List srcs, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        this.init(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }
    
    protected void init(final List srcs, Rectangle bounds, ColorModel cm, SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        this.srcs = new Vector();
        if (srcs != null) {
            this.srcs.addAll(srcs);
        }
        if (srcs.size() != 0) {
            final CachableRed src = srcs.get(0);
            if (bounds == null) {
                bounds = src.getBounds();
            }
            if (cm == null) {
                cm = src.getColorModel();
            }
            if (sm == null) {
                sm = src.getSampleModel();
            }
        }
        this.bounds = bounds;
        this.tileGridXOff = tileGridXOff;
        this.tileGridYOff = tileGridYOff;
        this.props = new HashMap();
        if (props != null) {
            this.props.putAll(props);
        }
        if (cm == null) {
            cm = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0);
        }
        this.cm = cm;
        if (sm == null) {
            sm = cm.createCompatibleSampleModel(bounds.width, bounds.height);
        }
        this.sm = sm;
        this.updateTileGridInfo();
    }
    
    protected void updateTileGridInfo() {
        this.tileWidth = this.sm.getWidth();
        this.tileHeight = this.sm.getHeight();
        this.minTileX = this.getXTile(this.bounds.x);
        this.minTileY = this.getYTile(this.bounds.y);
        final int x1 = this.bounds.x + this.bounds.width - 1;
        final int maxTileX = this.getXTile(x1);
        this.numXTiles = maxTileX - this.minTileX + 1;
        final int y1 = this.bounds.y + this.bounds.height - 1;
        final int maxTileY = this.getYTile(y1);
        this.numYTiles = maxTileY - this.minTileY + 1;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
    }
    
    @Override
    public Vector getSources() {
        return this.srcs;
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.cm;
    }
    
    @Override
    public SampleModel getSampleModel() {
        return this.sm;
    }
    
    @Override
    public int getMinX() {
        return this.bounds.x;
    }
    
    @Override
    public int getMinY() {
        return this.bounds.y;
    }
    
    @Override
    public int getWidth() {
        return this.bounds.width;
    }
    
    @Override
    public int getHeight() {
        return this.bounds.height;
    }
    
    @Override
    public int getTileWidth() {
        return this.tileWidth;
    }
    
    @Override
    public int getTileHeight() {
        return this.tileHeight;
    }
    
    @Override
    public int getTileGridXOffset() {
        return this.tileGridXOff;
    }
    
    @Override
    public int getTileGridYOffset() {
        return this.tileGridYOff;
    }
    
    @Override
    public int getMinTileX() {
        return this.minTileX;
    }
    
    @Override
    public int getMinTileY() {
        return this.minTileY;
    }
    
    @Override
    public int getNumXTiles() {
        return this.numXTiles;
    }
    
    @Override
    public int getNumYTiles() {
        return this.numYTiles;
    }
    
    @Override
    public Object getProperty(final String name) {
        Object ret = this.props.get(name);
        if (ret != null) {
            return ret;
        }
        for (final Object src : this.srcs) {
            final RenderedImage ri = (RenderedImage)src;
            ret = ri.getProperty(name);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
    
    @Override
    public String[] getPropertyNames() {
        final Set keys = this.props.keySet();
        String[] ret = new String[keys.size()];
        keys.toArray(ret);
        for (final Object src : this.srcs) {
            final RenderedImage ri = (RenderedImage)src;
            final String[] srcProps = ri.getPropertyNames();
            if (srcProps.length != 0) {
                final String[] tmp = new String[ret.length + srcProps.length];
                System.arraycopy(ret, 0, tmp, 0, ret.length);
                System.arraycopy(srcProps, 0, tmp, ret.length, srcProps.length);
                ret = tmp;
            }
        }
        return ret;
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle outputRgn) {
        if (srcIndex < 0 || srcIndex > this.srcs.size()) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        if (!outputRgn.intersects(this.bounds)) {
            return new Rectangle();
        }
        return outputRgn.intersection(this.bounds);
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle inputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        if (!inputRgn.intersects(this.bounds)) {
            return new Rectangle();
        }
        return inputRgn.intersection(this.bounds);
    }
    
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        final WritableRaster wr = this.makeTile(tileX, tileY);
        return this.copyData(wr);
    }
    
    @Override
    public Raster getData() {
        return this.getData(this.bounds);
    }
    
    @Override
    public Raster getData(final Rectangle rect) {
        final SampleModel smRet = this.sm.createCompatibleSampleModel(rect.width, rect.height);
        final Point pt = new Point(rect.x, rect.y);
        final WritableRaster wr = Raster.createWritableRaster(smRet, pt);
        return this.copyData(wr);
    }
    
    public final int getXTile(final int xloc) {
        final int tgx = xloc - this.tileGridXOff;
        if (tgx >= 0) {
            return tgx / this.tileWidth;
        }
        return (tgx - this.tileWidth + 1) / this.tileWidth;
    }
    
    public final int getYTile(final int yloc) {
        final int tgy = yloc - this.tileGridYOff;
        if (tgy >= 0) {
            return tgy / this.tileHeight;
        }
        return (tgy - this.tileHeight + 1) / this.tileHeight;
    }
    
    public void copyToRaster(final WritableRaster wr) {
        int tx0 = this.getXTile(wr.getMinX());
        int ty0 = this.getYTile(wr.getMinY());
        int tx2 = this.getXTile(wr.getMinX() + wr.getWidth() - 1);
        int ty2 = this.getYTile(wr.getMinY() + wr.getHeight() - 1);
        if (tx0 < this.minTileX) {
            tx0 = this.minTileX;
        }
        if (ty0 < this.minTileY) {
            ty0 = this.minTileY;
        }
        if (tx2 >= this.minTileX + this.numXTiles) {
            tx2 = this.minTileX + this.numXTiles - 1;
        }
        if (ty2 >= this.minTileY + this.numYTiles) {
            ty2 = this.minTileY + this.numYTiles - 1;
        }
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
    }
    
    public WritableRaster makeTile(final int tileX, final int tileY) {
        if (tileX < this.minTileX || tileX >= this.minTileX + this.numXTiles || tileY < this.minTileY || tileY >= this.minTileY + this.numYTiles) {
            throw new IndexOutOfBoundsException("Requested Tile (" + tileX + ',' + tileY + ") lies outside the bounds of image");
        }
        final Point pt = new Point(this.tileGridXOff + tileX * this.tileWidth, this.tileGridYOff + tileY * this.tileHeight);
        WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        int x0 = wr.getMinX();
        int y0 = wr.getMinY();
        int x2 = x0 + wr.getWidth() - 1;
        int y2 = y0 + wr.getHeight() - 1;
        if (x0 < this.bounds.x || x2 >= this.bounds.x + this.bounds.width || y0 < this.bounds.y || y2 >= this.bounds.y + this.bounds.height) {
            if (x0 < this.bounds.x) {
                x0 = this.bounds.x;
            }
            if (y0 < this.bounds.y) {
                y0 = this.bounds.y;
            }
            if (x2 >= this.bounds.x + this.bounds.width) {
                x2 = this.bounds.x + this.bounds.width - 1;
            }
            if (y2 >= this.bounds.y + this.bounds.height) {
                y2 = this.bounds.y + this.bounds.height - 1;
            }
            wr = wr.createWritableChild(x0, y0, x2 - x0 + 1, y2 - y0 + 1, x0, y0, null);
        }
        return wr;
    }
    
    public static void copyBand(final Raster src, final int srcBand, final WritableRaster dst, final int dstBand) {
        final Rectangle srcR = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
        final Rectangle dstR = new Rectangle(dst.getMinX(), dst.getMinY(), dst.getWidth(), dst.getHeight());
        final Rectangle cpR = srcR.intersection(dstR);
        int[] samples = null;
        for (int y = cpR.y; y < cpR.y + cpR.height; ++y) {
            samples = src.getSamples(cpR.x, y, cpR.width, 1, srcBand, samples);
            dst.setSamples(cpR.x, y, cpR.width, 1, dstBand, samples);
        }
    }
}
