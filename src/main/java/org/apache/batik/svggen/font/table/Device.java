// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Device
{
    private int startSize;
    private int endSize;
    private int deltaFormat;
    private int[] deltaValues;
    
    public Device(final RandomAccessFile raf) throws IOException {
        this.startSize = raf.readUnsignedShort();
        this.endSize = raf.readUnsignedShort();
        this.deltaFormat = raf.readUnsignedShort();
        int size = this.startSize - this.endSize;
        switch (this.deltaFormat) {
            case 1: {
                size = ((size % 8 == 0) ? (size / 8) : (size / 8 + 1));
                break;
            }
            case 2: {
                size = ((size % 4 == 0) ? (size / 4) : (size / 4 + 1));
                break;
            }
            case 3: {
                size = ((size % 2 == 0) ? (size / 2) : (size / 2 + 1));
                break;
            }
        }
        this.deltaValues = new int[size];
        for (int i = 0; i < size; ++i) {
            this.deltaValues[i] = raf.readUnsignedShort();
        }
    }
}
