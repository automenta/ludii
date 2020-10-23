// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import org.apache.batik.util.Platform;
import java.util.Collection;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWMFReader
{
    public static final float PIXEL_PER_INCH;
    public static final float MM_PER_PIXEL;
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;
    protected int width;
    protected int height;
    protected int inch;
    protected float scaleX;
    protected float scaleY;
    protected float scaleXY;
    protected int vpW;
    protected int vpH;
    protected int vpX;
    protected int vpY;
    protected int xSign;
    protected int ySign;
    protected volatile boolean bReading;
    protected boolean isAldus;
    protected boolean isotropic;
    protected int mtType;
    protected int mtHeaderSize;
    protected int mtVersion;
    protected int mtSize;
    protected int mtNoObjects;
    protected int mtMaxRecord;
    protected int mtNoParameters;
    protected int windowWidth;
    protected int windowHeight;
    protected int numObjects;
    protected List objectVector;
    public int lastObjectIdx;
    
    public AbstractWMFReader() {
        this.xSign = 1;
        this.ySign = 1;
        this.bReading = false;
        this.isAldus = false;
        this.isotropic = true;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleXY = 1.0f;
        this.left = -1;
        this.top = -1;
        this.width = -1;
        this.height = -1;
        this.right = this.left + this.width;
        this.bottom = this.top + this.height;
        this.numObjects = 0;
        this.objectVector = new ArrayList();
    }
    
    public AbstractWMFReader(final int width, final int height) {
        this();
        this.width = width;
        this.height = height;
    }
    
    protected short readShort(final DataInputStream is) throws IOException {
        final byte[] js = new byte[2];
        is.readFully(js);
        final int iTemp = (0xFF & js[1]) << 8;
        short i = (short)(0xFFFF & iTemp);
        i |= (short)(0xFF & js[0]);
        return i;
    }
    
    protected int readInt(final DataInputStream is) throws IOException {
        final byte[] js = new byte[4];
        is.readFully(js);
        int i = (0xFF & js[3]) << 24;
        i |= (0xFF & js[2]) << 16;
        i |= (0xFF & js[1]) << 8;
        i |= (0xFF & js[0]);
        return i;
    }
    
    public float getViewportWidthUnits() {
        return (float)this.vpW;
    }
    
    public float getViewportHeightUnits() {
        return (float)this.vpH;
    }
    
    public float getViewportWidthInch() {
        return this.vpW / (float)this.inch;
    }
    
    public float getViewportHeightInch() {
        return this.vpH / (float)this.inch;
    }
    
    public float getPixelsPerUnit() {
        return AbstractWMFReader.PIXEL_PER_INCH / this.inch;
    }
    
    public int getVpW() {
        return (int)(AbstractWMFReader.PIXEL_PER_INCH * this.vpW / this.inch);
    }
    
    public int getVpH() {
        return (int)(AbstractWMFReader.PIXEL_PER_INCH * this.vpH / this.inch);
    }
    
    public int getLeftUnits() {
        return this.left;
    }
    
    public int getRightUnits() {
        return this.right;
    }
    
    public int getTopUnits() {
        return this.top;
    }
    
    public int getWidthUnits() {
        return this.width;
    }
    
    public int getHeightUnits() {
        return this.height;
    }
    
    public int getBottomUnits() {
        return this.bottom;
    }
    
    public int getMetaFileUnitsPerInch() {
        return this.inch;
    }
    
    public Rectangle getRectangleUnits() {
        final Rectangle rec = new Rectangle(this.left, this.top, this.width, this.height);
        return rec;
    }
    
    public Rectangle2D getRectanglePixel() {
        final float _left = AbstractWMFReader.PIXEL_PER_INCH * this.left / this.inch;
        final float _right = AbstractWMFReader.PIXEL_PER_INCH * this.right / this.inch;
        final float _top = AbstractWMFReader.PIXEL_PER_INCH * this.top / this.inch;
        final float _bottom = AbstractWMFReader.PIXEL_PER_INCH * this.bottom / this.inch;
        final Rectangle2D.Float rec = new Rectangle2D.Float(_left, _top, _right - _left, _bottom - _top);
        return rec;
    }
    
    public Rectangle2D getRectangleInch() {
        final float _left = this.left / (float)this.inch;
        final float _right = this.right / (float)this.inch;
        final float _top = this.top / (float)this.inch;
        final float _bottom = this.bottom / (float)this.inch;
        final Rectangle2D.Float rec = new Rectangle2D.Float(_left, _top, _right - _left, _bottom - _top);
        return rec;
    }
    
    public int getWidthPixels() {
        return (int)(AbstractWMFReader.PIXEL_PER_INCH * this.width / this.inch);
    }
    
    public float getUnitsToPixels() {
        return AbstractWMFReader.PIXEL_PER_INCH / this.inch;
    }
    
    public float getVpWFactor() {
        return AbstractWMFReader.PIXEL_PER_INCH * this.width / this.inch / this.vpW;
    }
    
    public float getVpHFactor() {
        return AbstractWMFReader.PIXEL_PER_INCH * this.height / this.inch / this.vpH;
    }
    
    public int getHeightPixels() {
        return (int)(AbstractWMFReader.PIXEL_PER_INCH * this.height / this.inch);
    }
    
    public int getXSign() {
        return this.xSign;
    }
    
    public int getYSign() {
        return this.ySign;
    }
    
    protected synchronized void setReading(final boolean state) {
        this.bReading = state;
    }
    
    public synchronized boolean isReading() {
        return this.bReading;
    }
    
    public abstract void reset();
    
    protected abstract boolean readRecords(final DataInputStream p0) throws IOException;
    
    public void read(final DataInputStream is) throws IOException {
        this.reset();
        this.setReading(true);
        final int dwIsAldus = this.readInt(is);
        if (dwIsAldus == -1698247209) {
            final int key = dwIsAldus;
            this.isAldus = true;
            this.readShort(is);
            this.left = this.readShort(is);
            this.top = this.readShort(is);
            this.right = this.readShort(is);
            this.bottom = this.readShort(is);
            this.inch = this.readShort(is);
            final int reserved = this.readInt(is);
            final short checksum = this.readShort(is);
            if (this.left > this.right) {
                final int _i = this.right;
                this.right = this.left;
                this.left = _i;
                this.xSign = -1;
            }
            if (this.top > this.bottom) {
                final int _i = this.bottom;
                this.bottom = this.top;
                this.top = _i;
                this.ySign = -1;
            }
            this.width = this.right - this.left;
            this.height = this.bottom - this.top;
            this.mtType = this.readShort(is);
            this.mtHeaderSize = this.readShort(is);
        }
        else {
            this.mtType = dwIsAldus << 16 >> 16;
            this.mtHeaderSize = dwIsAldus >> 16;
        }
        this.mtVersion = this.readShort(is);
        this.mtSize = this.readInt(is);
        this.mtNoObjects = this.readShort(is);
        this.mtMaxRecord = this.readInt(is);
        this.mtNoParameters = this.readShort(is);
        this.numObjects = this.mtNoObjects;
        final List tempList = new ArrayList(this.numObjects);
        for (int i = 0; i < this.numObjects; ++i) {
            tempList.add(new GdiObject(i, false));
        }
        this.objectVector.addAll(tempList);
        final boolean ret = this.readRecords(is);
        is.close();
        if (!ret) {
            throw new IOException("Unhandled exception while reading records");
        }
    }
    
    public int addObject(final int type, final Object obj) {
        int i;
        for (int startIdx = i = 0; i < this.numObjects; ++i) {
            final GdiObject gdi = this.objectVector.get(i);
            if (!gdi.used) {
                gdi.Setup(type, obj);
                this.lastObjectIdx = i;
                break;
            }
        }
        return this.lastObjectIdx;
    }
    
    public int addObjectAt(final int type, final Object obj, final int idx) {
        if (idx == 0 || idx > this.numObjects) {
            this.addObject(type, obj);
            return this.lastObjectIdx;
        }
        this.lastObjectIdx = idx;
        for (int i = 0; i < this.numObjects; ++i) {
            final GdiObject gdi = this.objectVector.get(i);
            if (i == idx) {
                gdi.Setup(type, obj);
                break;
            }
        }
        return idx;
    }
    
    public GdiObject getObject(final int idx) {
        return this.objectVector.get(idx);
    }
    
    public int getNumObjects() {
        return this.numObjects;
    }
    
    static {
        PIXEL_PER_INCH = (float)Platform.getScreenResolution();
        MM_PER_PIXEL = 25.4f / Platform.getScreenResolution();
    }
}
