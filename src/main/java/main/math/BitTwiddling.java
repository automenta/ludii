// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

public final class BitTwiddling
{
    private BitTwiddling() {
    }
    
    public static final boolean oppositeSigns(final int a, final int b) {
        return (a ^ b) < 0;
    }
    
    public static final boolean oppositeSigns(final long a, final long b) {
        return (a ^ b) < 0L;
    }
    
    public static final boolean exactlyOneBitSet(final int a) {
        return a != 0 && (a & a - 1) == 0x0;
    }
    
    public static final boolean exactlyOneBitSet(final long a) {
        return a != 0L && (a & a - 1L) == 0x0L;
    }
    
    public static final boolean leOneBitSet(final int a) {
        return (a & a - 1) == 0x0;
    }
    
    public static final boolean leOneBitSet(final long a) {
        return (a & a - 1L) == 0x0L;
    }
    
    public static final int topBitPos(final int a) {
        return 31 - Integer.numberOfLeadingZeros(a);
    }
    
    public static final int topBitPos(final long a) {
        return 63 - Long.numberOfLeadingZeros(a);
    }
    
    public static final int lowBitPos(final int v) {
        return Integer.numberOfTrailingZeros(v);
    }
    
    public static final int lowBitPos(final long v) {
        return Long.numberOfTrailingZeros(v);
    }
    
    public static final int bottomBit(final int a) {
        return Integer.lowestOneBit(a);
    }
    
    public static final long bottomBit(final long a) {
        return Long.lowestOneBit(a);
    }
    
    public static final int countBits(final int v) {
        return Integer.bitCount(v);
    }
    
    public static final int countBits(final long v) {
        return Long.bitCount(v);
    }
    
    public static final int nextPermutation(final int v) {
        final int t = (v | v - 1) + 1;
        return t | ((t & -t) / (v & -v) >>> 1) - 1;
    }
    
    public static final long nextPermutation(final long v) {
        final long t = (v | v - 1L) + 1L;
        return t | ((t & -t) / (v & -v) >>> 1) - 1L;
    }
    
    public static final int oneIfNonZero(final int n) {
        return (n | ~n + 1) >>> 31;
    }
    
    public static final int oneIfZero(final int n) {
        return oneIfNonZero(n) ^ 0x1;
    }
    
    public static final long oneIfNonZero(final long n) {
        return (n | ~n + 1L) >>> 63;
    }
    
    public static final long oneIfZero(final long n) {
        return oneIfNonZero(n) ^ 0x1L;
    }
    
    public static int reverseByte(final int value) {
        return (int)((value * 2149582850L & 0x884422110L) * 4311810305L >>> 32) & 0xFF;
    }
    
    public static int bitsRequired(final int value) {
        return (int)Math.ceil(Math.log(value + 1) / Math.log(2.0));
    }
    
    public static int maskI(final int numBits) {
        return (1 << numBits) - 1;
    }
    
    public static long maskL(final int numBits) {
        return (1L << numBits) - 1L;
    }
    
    public static int nextPowerOf2(final int n) {
        if (n <= 1) {
            return 1;
        }
        return (int)Math.pow(2.0, Math.ceil(Math.log(n) / Math.log(2.0)));
    }
    
    public static boolean isPowerOf2(final int n) {
        return n > 0 && (n & n - 1) == 0x0;
    }
    
    public static int log2RoundDown(final int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }
    
    public static int log2RoundUp(final int x) {
        return 32 - Integer.numberOfLeadingZeros(x - 1);
    }
}
