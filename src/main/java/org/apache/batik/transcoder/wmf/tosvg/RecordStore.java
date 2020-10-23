// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.Vector;
import java.net.URL;

public class RecordStore
{
    private transient URL url;
    protected transient int numRecords;
    protected transient int numObjects;
    public transient int lastObjectIdx;
    protected transient int vpX;
    protected transient int vpY;
    protected transient int vpW;
    protected transient int vpH;
    protected transient Vector records;
    protected transient Vector objectVector;
    protected transient boolean bReading;
    
    public RecordStore() {
        this.bReading = false;
        this.reset();
    }
    
    public void reset() {
        this.numRecords = 0;
        this.vpX = 0;
        this.vpY = 0;
        this.vpW = 1000;
        this.vpH = 1000;
        this.numObjects = 0;
        this.records = new Vector(20, 20);
        this.objectVector = new Vector();
    }
    
    synchronized void setReading(final boolean state) {
        this.bReading = state;
    }
    
    synchronized boolean isReading() {
        return this.bReading;
    }
    
    public boolean read(final DataInputStream is) throws IOException {
        this.setReading(true);
        this.reset();
        int functionId = 0;
        this.numRecords = 0;
        this.numObjects = is.readShort();
        this.objectVector.ensureCapacity(this.numObjects);
        for (int i = 0; i < this.numObjects; ++i) {
            this.objectVector.add(new GdiObject(i, false));
        }
        while (functionId != -1) {
            functionId = is.readShort();
            if (functionId == -1) {
                break;
            }
            MetaRecord mr = null;
            switch (functionId) {
                case 763:
                case 1313:
                case 1583:
                case 2610: {
                    final short len = is.readShort();
                    final byte[] b = new byte[len];
                    for (int j = 0; j < len; ++j) {
                        b[j] = is.readByte();
                    }
                    final String str = new String(b);
                    mr = new MetaRecord.StringRecord(str);
                    break;
                }
                default: {
                    mr = new MetaRecord();
                    break;
                }
            }
            final int numPts = is.readShort();
            mr.numPoints = numPts;
            mr.functionId = functionId;
            for (int k = 0; k < numPts; ++k) {
                mr.AddElement((int)is.readShort());
            }
            this.records.add(mr);
            ++this.numRecords;
        }
        this.setReading(false);
        return true;
    }
    
    public void addObject(final int type, final Object obj) {
        for (int i = 0; i < this.numObjects; ++i) {
            final GdiObject gdi = this.objectVector.get(i);
            if (!gdi.used) {
                gdi.Setup(type, obj);
                this.lastObjectIdx = i;
                break;
            }
        }
    }
    
    public void addObjectAt(final int type, final Object obj, final int idx) {
        if (idx == 0 || idx > this.numObjects) {
            this.addObject(type, obj);
            return;
        }
        this.lastObjectIdx = idx;
        for (int i = 0; i < this.numObjects; ++i) {
            final GdiObject gdi = this.objectVector.get(i);
            if (i == idx) {
                gdi.Setup(type, obj);
                break;
            }
        }
    }
    
    public URL getUrl() {
        return this.url;
    }
    
    public void setUrl(final URL newUrl) {
        this.url = newUrl;
    }
    
    public GdiObject getObject(final int idx) {
        return this.objectVector.get(idx);
    }
    
    public MetaRecord getRecord(final int idx) {
        return this.records.get(idx);
    }
    
    public int getNumRecords() {
        return this.numRecords;
    }
    
    public int getNumObjects() {
        return this.numObjects;
    }
    
    public int getVpX() {
        return this.vpX;
    }
    
    public int getVpY() {
        return this.vpY;
    }
    
    public int getVpW() {
        return this.vpW;
    }
    
    public int getVpH() {
        return this.vpH;
    }
    
    public void setVpX(final int newValue) {
        this.vpX = newValue;
    }
    
    public void setVpY(final int newValue) {
        this.vpY = newValue;
    }
    
    public void setVpW(final int newValue) {
        this.vpW = newValue;
    }
    
    public void setVpH(final int newValue) {
        this.vpH = newValue;
    }
}
