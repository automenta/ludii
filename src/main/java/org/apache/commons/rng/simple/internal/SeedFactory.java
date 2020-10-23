// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.simple.internal;

import org.apache.commons.rng.core.source32.RandomIntSource;
import org.apache.commons.rng.core.source32.Well44497b;
import org.apache.commons.rng.core.source64.RandomLongSource;
import org.apache.commons.rng.core.source64.SplitMix64;
import org.apache.commons.rng.core.util.NumberFactory;

public final class SeedFactory
{
    private static final RandomIntSource SEED_GENERATOR;
    
    static {
        final long t = System.currentTimeMillis();
        final int h = System.identityHashCode(Runtime.getRuntime());
        final SplitMix64 rng = new SplitMix64(t ^ NumberFactory.makeLong(h, ~h));
        SEED_GENERATOR = new Well44497b(createIntArray(1391, rng));
    }
    
    private SeedFactory() {
    }
    
    public static long createLong() {
        final int number = System.identityHashCode(new Object());
        synchronized (SeedFactory.SEED_GENERATOR) {
            return NumberFactory.makeLong(SeedFactory.SEED_GENERATOR.next() ^ number, SeedFactory.SEED_GENERATOR.next() ^ number);
        }
    }
    
    static int[] createIntArray(final int n, final RandomLongSource source) {
        return createIntArray(n, source, null);
    }
    
    private static int[] createIntArray(final int n, final RandomLongSource source, final Object h) {
        final int[] array = new int[n];
        final int hash = System.identityHashCode(h);
        for (int i = 0; i < n; i += 2) {
            long result;
            synchronized (source) {
                result = source.next() ^ NumberFactory.makeLong(hash, hash);
            }
            final long v = result;
            array[i] = NumberFactory.extractHi(v);
            if (i + 1 < n) {
                array[i + 1] = NumberFactory.extractLo(v);
            }
        }
        return array;
    }

}
