// 
// Decompiled by Procyon v0.5.36
// 

package util.zhash;

import org.apache.commons.rng.core.source64.SplitMix64;

public class ZobristHashGenerator extends SplitMix64
{
    private static final long RNG_SEED = 3544273448235996400L;
    private int counter;
    
    public ZobristHashGenerator() {
        super(3544273448235996400L);
    }
    
    public ZobristHashGenerator(final int pos) {
        super(3544273448235996400L);
        while (this.counter < pos) {
            this.next();
        }
    }
    
    @Override
    public long next() {
        ++this.counter;
        return super.next();
    }
    
    public int getSequencePosition() {
        return this.counter;
    }
}
