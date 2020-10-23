// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.source64;

import org.apache.commons.rng.core.util.NumberFactory;
import org.apache.commons.rng.simple.internal.SeedFactory;

public class SplitMix64 extends LongProvider
{
    private long state;
    
    public SplitMix64() {
        this(SeedFactory.createLong());
    }
    
    public SplitMix64(final Long seed) {
        this.setSeedInternal(seed);
    }
    
    private void setSeedInternal(final Long seed) {
        this.state = seed;
    }
    
    @Override
    public long next() {
        final long state = this.state - 7046029254386353131L;
        this.state = state;
        long z = state;
        z = (z ^ z >>> 30) * -4658895280553007687L;
        z = (z ^ z >>> 27) * -7723592293110705685L;
        return z ^ z >>> 31;
    }
    
    @Override
    protected byte[] getStateInternal() {
        return NumberFactory.makeByteArray(this.state);
    }
    
    @Override
    protected void setStateInternal(final byte[] s) {
        this.checkStateSize(s, 8);
        this.state = NumberFactory.makeLong(s);
    }
}
