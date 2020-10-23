// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Program
{
    private short[] instructions;
    
    public short[] getInstructions() {
        return this.instructions;
    }
    
    protected void readInstructions(final RandomAccessFile raf, final int count) throws IOException {
        this.instructions = new short[count];
        for (int i = 0; i < count; ++i) {
            this.instructions[i] = (short)raf.readUnsignedByte();
        }
    }
    
    protected void readInstructions(final ByteArrayInputStream bais, final int count) {
        this.instructions = new short[count];
        for (int i = 0; i < count; ++i) {
            this.instructions[i] = (short)bais.read();
        }
    }
}
