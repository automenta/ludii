// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core;

import org.apache.commons.rng.RandomProviderState;
import org.apache.commons.rng.RestorableUniformRandomProvider;

public abstract class BaseProvider implements RestorableUniformRandomProvider
{
    @Override
    public int nextInt(final int n) {
        this.checkStrictlyPositive(n);
        if ((n & -n) == n) {
            return (int)(n * (long)(this.nextInt() >>> 1) >> 31);
        }
        int bits;
        int val;
        do {
            bits = this.nextInt() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
    
    @Override
    public long nextLong(final long n) {
        this.checkStrictlyPositive(n);
        long bits;
        long val;
        do {
            bits = this.nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }
    
    @Override
    public RandomProviderState saveState() {
        return new RandomProviderDefaultState(this.getStateInternal());
    }
    
    @Override
    public void restoreState(final RandomProviderState state) {
        if (state instanceof RandomProviderDefaultState) {
            this.setStateInternal(((RandomProviderDefaultState)state).getState());
            return;
        }
        throw new IllegalArgumentException("Foreign instance");
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    protected byte[] getStateInternal() {
        throw new UnsupportedOperationException();
    }
    
    protected void setStateInternal(final byte[] state) {
        throw new UnsupportedOperationException();
    }
    
    protected void fillState(final int[] state, final int[] seed) {
        final int stateSize = state.length;
        final int seedSize = seed.length;
        System.arraycopy(seed, 0, state, 0, Math.min(seedSize, stateSize));
        if (seedSize < stateSize) {
            for (int i = seedSize; i < stateSize; ++i) {
                state[i] = (int)(scrambleWell(state[i - seed.length], i) & 0xFFFFFFFFL);
            }
        }
    }
    
    protected void fillState(final long[] state, final long[] seed) {
        final int stateSize = state.length;
        final int seedSize = seed.length;
        System.arraycopy(seed, 0, state, 0, Math.min(seedSize, stateSize));
        if (seedSize < stateSize) {
            for (int i = seedSize; i < stateSize; ++i) {
                state[i] = scrambleWell(state[i - seed.length], i);
            }
        }
    }
    
    protected void checkStateSize(final byte[] state, final int expected) {
        if (state.length != expected) {
            throw new IllegalArgumentException("State size must be " + expected + " but was " + state.length);
        }
    }
    
    protected void checkIndex(final int min, final int max, final int index) {
        if (index < min || index > max) {
            throw new IndexOutOfBoundsException(index + " is out of interval [" + min + ", " + max + "]");
        }
    }
    
    private void checkStrictlyPositive(final long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Must be strictly positive: " + n);
        }
    }
    
    private static long scramble(final long n, final long mult, final int shift, final int add) {
        return mult * (n ^ n >> shift) + add;
    }
    
    private static long scrambleWell(final long n, final int add) {
        return scramble(n, 1812433253L, 30, add);
    }
}
