// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.source32;

import org.apache.commons.rng.core.util.NumberFactory;

import java.util.Arrays;

public abstract class AbstractWell extends IntProvider
{
    private static final int BLOCK_SIZE = 32;
    protected int index;
    protected final int[] v;
    
    protected AbstractWell(final int k, final int[] seed) {
        final int r = calculateBlockCount(k);
        this.v = new int[r];
        this.index = 0;
        this.setSeedInternal(seed);
    }
    
    @Override
    protected byte[] getStateInternal() {
        final int[] s = Arrays.copyOf(this.v, this.v.length + 1);
        s[this.v.length] = this.index;
        return NumberFactory.makeByteArray(s);
    }
    
    @Override
    protected void setStateInternal(final byte[] s) {
        this.checkStateSize(s, (this.v.length + 1) * 4);
        final int[] tmp = NumberFactory.makeIntArray(s);
        System.arraycopy(tmp, 0, this.v, 0, this.v.length);
        this.index = tmp[this.v.length];
    }
    
    private void setSeedInternal(final int[] seed) {
        System.arraycopy(seed, 0, this.v, 0, Math.min(seed.length, this.v.length));
        if (seed.length < this.v.length) {
            for (int i = seed.length; i < this.v.length; ++i) {
                final long current = this.v[i - seed.length];
                this.v[i] = (int)(1812433253L * (current ^ current >> 30) + i & 0xFFFFFFFFL);
            }
        }
        this.index = 0;
    }
    
    private static int calculateBlockCount(final int k) {
        return (k + 32 - 1) / 32;
    }
    
    protected static final class IndexTable
    {
        private final int[] iRm1;
        private final int[] iRm2;
        private final int[] i1;
        private final int[] i2;
        private final int[] i3;
        
        public IndexTable(final int k, final int m1, final int m2, final int m3) {
            final int r = calculateBlockCount(k);
            this.iRm1 = new int[r];
            this.iRm2 = new int[r];
            this.i1 = new int[r];
            this.i2 = new int[r];
            this.i3 = new int[r];
            for (int j = 0; j < r; ++j) {
                this.iRm1[j] = (j + r - 1) % r;
                this.iRm2[j] = (j + r - 2) % r;
                this.i1[j] = (j + m1) % r;
                this.i2[j] = (j + m2) % r;
                this.i3[j] = (j + m3) % r;
            }
        }
        
        public int getIndexPred(final int index) {
            return this.iRm1[index];
        }
        
        public int getIndexPred2(final int index) {
            return this.iRm2[index];
        }
        
        public int getIndexM1(final int index) {
            return this.i1[index];
        }
        
        public int getIndexM2(final int index) {
            return this.i2[index];
        }
        
        public int getIndexM3(final int index) {
            return this.i3[index];
        }
    }
}
