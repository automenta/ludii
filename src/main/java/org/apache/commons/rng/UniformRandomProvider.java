// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng;

public interface UniformRandomProvider
{
    void nextBytes(final byte[] p0);
    
    void nextBytes(final byte[] p0, final int p1, final int p2);
    
    int nextInt();
    
    int nextInt(final int p0);
    
    long nextLong();
    
    long nextLong(final long p0);
    
    boolean nextBoolean();
    
    float nextFloat();
    
    double nextDouble();
}
