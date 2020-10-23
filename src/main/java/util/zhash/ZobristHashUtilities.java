// 
// Decompiled by Procyon v0.5.36
// 

package util.zhash;

public final class ZobristHashUtilities
{
    public static final long INITIAL_VALUE = 0L;
    public static final long UNKNOWN = -1L;
    
    public static final ZobristHashGenerator getHashGenerator() {
        return new ZobristHashGenerator();
    }
    
    public static final long getNext(final ZobristHashGenerator generator) {
        return generator.next();
    }
    
    public static final long[] getSequence(final ZobristHashGenerator generator, final int dim) {
        final long[] results = new long[dim];
        for (int i = 0; i < dim; ++i) {
            results[i] = generator.next();
        }
        return results;
    }
    
    public static final long[][] getSequence(final ZobristHashGenerator generator, final int dim1, final int dim2) {
        final long[][] results = new long[dim1][];
        for (int i = 0; i < dim1; ++i) {
            results[i] = getSequence(generator, dim2);
        }
        return results;
    }
    
    public static final long[][][] getSequence(final ZobristHashGenerator generator, final int dim1, final int dim2, final int dim3) {
        final long[][][] results = new long[dim1][][];
        for (int i = 0; i < dim1; ++i) {
            results[i] = getSequence(generator, dim2, dim3);
        }
        return results;
    }
}
