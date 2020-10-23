// 
// Decompiled by Procyon v0.5.36
// 

package metrics;

public class Range<E1, E2>
{
    private final E1 min;
    private final E2 max;
    
    public Range(final E1 min, final E2 max) {
        this.min = min;
        this.max = max;
    }
    
    public E1 min() {
        return this.min;
    }
    
    public E2 max() {
        return this.max;
    }
}
