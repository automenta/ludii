/*
 * Decompiled with CFR 0.150.
 */
package math;

public final class BitTwiddling {
    private BitTwiddling() {
    }

    public static boolean oppositeSigns(int a, int b) {
        return (a ^ b) < 0;
    }

    public static boolean oppositeSigns(long a, long b) {
        return (a ^ b) < 0L;
    }

    public static boolean exactlyOneBitSet(int a) {
        if (a == 0) {
            return false;
        }
        return (a & a - 1) == 0;
    }

    public static boolean exactlyOneBitSet(long a) {
        if (a == 0L) {
            return false;
        }
        return (a & a - 1L) == 0L;
    }

    public static boolean leOneBitSet(int a) {
        return (a & a - 1) == 0;
    }

    public static boolean leOneBitSet(long a) {
        return (a & a - 1L) == 0L;
    }

    public static int topBitPos(int a) {
        return 31 - Integer.numberOfLeadingZeros(a);
    }

    public static int topBitPos(long a) {
        return 63 - Long.numberOfLeadingZeros(a);
    }

    public static int lowBitPos(int v) {
        return Integer.numberOfTrailingZeros(v);
    }

    public static int lowBitPos(long v) {
        return Long.numberOfTrailingZeros(v);
    }

    public static int bottomBit(int a) {
        return Integer.lowestOneBit(a);
    }

    public static long bottomBit(long a) {
        return Long.lowestOneBit(a);
    }

    public static int countBits(int v) {
        return Integer.bitCount(v);
    }

    public static int countBits(long v) {
        return Long.bitCount(v);
    }

    public static int nextPermutation(int v) {
        int t = (v | v - 1) + 1;
        return t | ((t & -t) / (v & -v) >>> 1) - 1;
    }

    public static long nextPermutation(long v) {
        long t = (v | v - 1L) + 1L;
        return t | ((t & -t) / (v & -v) >>> 1) - 1L;
    }

    public static int oneIfNonZero(int n) {
        return (n | ~n + 1) >>> 31;
    }

    public static int oneIfZero(int n) {
        return BitTwiddling.oneIfNonZero(n) ^ 1;
    }

    public static long oneIfNonZero(long n) {
        return (n | (n ^ 0xFFFFFFFFFFFFFFFFL) + 1L) >>> 63;
    }

    public static long oneIfZero(long n) {
        return BitTwiddling.oneIfNonZero(n) ^ 1L;
    }

    public static int reverseByte(int value) {
        return (int)((value * 0x80200802L & 0x884422110L) * 0x101010101L >>> 32) & 0xFF;
    }

    public static int bitsRequired(int value) {
        return (int)Math.ceil(Math.log(value + 1) / Math.log(2.0));
    }

    public static int maskI(int numBits) {
        return (1 << numBits) - 1;
    }

    public static long maskL(int numBits) {
        return (1L << numBits) - 1L;
    }

    public static int nextPowerOf2(int n) {
        if (n <= 1) {
            return 1;
        }
        return (int)Math.pow(2.0, Math.ceil(Math.log(n) / Math.log(2.0)));
    }

    public static boolean isPowerOf2(int n) {
        return n > 0 && (n & n - 1) == 0;
    }

    public static int log2RoundDown(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    public static int log2RoundUp(int x) {
        return 32 - Integer.numberOfLeadingZeros(x - 1);
    }
}

