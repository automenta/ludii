// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class PadMode implements Serializable
{
    public static final int MODE_ZERO_PAD = 1;
    public static final int MODE_REPLICATE = 2;
    public static final int MODE_WRAP = 3;
    public static final PadMode ZERO_PAD;
    public static final PadMode REPLICATE;
    public static final PadMode WRAP;
    private int mode;
    
    public int getMode() {
        return this.mode;
    }
    
    private PadMode(final int mode) {
        this.mode = mode;
    }
    
    private Object readResolve() throws ObjectStreamException {
        switch (this.mode) {
            case 1: {
                return PadMode.ZERO_PAD;
            }
            case 2: {
                return PadMode.REPLICATE;
            }
            case 3: {
                return PadMode.WRAP;
            }
            default: {
                throw new RuntimeException("Unknown Pad Mode type");
            }
        }
    }
    
    static {
        ZERO_PAD = new PadMode(1);
        REPLICATE = new PadMode(2);
        WRAP = new PadMode(3);
    }
}
