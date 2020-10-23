// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.source32;

import org.apache.commons.rng.core.BaseProvider;
import org.apache.commons.rng.core.util.NumberFactory;

public abstract class IntProvider extends BaseProvider implements RandomIntSource
{
    @Override
    public int nextInt() {
        return this.next();
    }
    
    @Override
    public boolean nextBoolean() {
        return NumberFactory.makeBoolean(this.nextInt());
    }
    
    @Override
    public double nextDouble() {
        return NumberFactory.makeDouble(this.nextInt(), this.nextInt());
    }
    
    @Override
    public float nextFloat() {
        return NumberFactory.makeFloat(this.nextInt());
    }
    
    @Override
    public long nextLong() {
        return NumberFactory.makeLong(this.nextInt(), this.nextInt());
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        nextBytesFill(this, bytes, 0, bytes.length);
    }
    
    @Override
    public void nextBytes(final byte[] bytes, final int start, final int len) {
        this.checkIndex(0, bytes.length - 1, start);
        this.checkIndex(0, bytes.length - start, len);
        nextBytesFill(this, bytes, start, len);
    }
    
    static void nextBytesFill(final RandomIntSource source, final byte[] bytes, final int start, final int len) {
        int index = start;
        int random;
        for (int indexLoopLimit = index + (len & 0x7FFFFFFC); index < indexLoopLimit; bytes[index++] = (byte)random, bytes[index++] = (byte)(random >>> 8), bytes[index++] = (byte)(random >>> 16), bytes[index++] = (byte)(random >>> 24)) {
            random = source.next();
        }
        final int indexLimit = start + len;
        if (index < indexLimit) {
            int random2 = source.next();
            while (true) {
                bytes[index++] = (byte)random2;
                if (index >= indexLimit) {
                    break;
                }
                random2 >>>= 8;
            }
        }
    }
}
