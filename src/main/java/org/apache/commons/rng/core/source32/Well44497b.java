// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.source32;

public class Well44497b extends Well44497a
{
    public Well44497b(final int[] seed) {
        super(seed);
    }
    
    @Override
    public int next() {
        int z4 = super.next();
        z4 ^= (z4 << 7 & 0x93DD1400);
        z4 ^= (z4 << 15 & 0xFA118000);
        return z4;
    }
}
