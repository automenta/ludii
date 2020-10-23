// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.source64;

import org.apache.commons.rng.core.BaseProvider;
import org.apache.commons.rng.core.util.NumberFactory;

public abstract class LongProvider extends BaseProvider implements RandomLongSource
{
    @Override
    public long nextLong() {
        return this.next();
    }
    
    @Override
    public int nextInt() {
        return NumberFactory.makeInt(this.nextLong());
    }
    
    @Override
    public double nextDouble() {
        return NumberFactory.makeDouble(this.nextLong());
    }
    
    @Override
    public boolean nextBoolean() {
        return NumberFactory.makeBoolean(this.nextLong());
    }
    
    @Override
    public float nextFloat() {
        return NumberFactory.makeFloat(this.nextInt());
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
    
    static void nextBytesFill(final RandomLongSource source, final byte[] bytes, final int start, final int len) {
        int index = start;
        long random;
        for (int indexLoopLimit = index + (len & 0x7FFFFFF8); index < indexLoopLimit; bytes[index++] = (byte)random, bytes[index++] = (byte)(random >>> 8), bytes[index++] = (byte)(random >>> 16), bytes[index++] = (byte)(random >>> 24), bytes[index++] = (byte)(random >>> 32), bytes[index++] = (byte)(random >>> 40), bytes[index++] = (byte)(random >>> 48), bytes[index++] = (byte)(random >>> 56)) {
            random = source.next();
        }
        final int indexLimit = start + len;
        if (index < indexLimit) {
            long random2 = source.next();
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
