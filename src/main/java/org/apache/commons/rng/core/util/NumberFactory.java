// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core.util;

import java.util.Arrays;

public final class NumberFactory
{
    private static final long DOUBLE_HIGH_BITS = 4607182418800017408L;
    private static final float FLOAT_MULTIPLIER = 1.1920929E-7f;
    private static final double DOUBLE_MULTIPLIER = 2.220446049250313E-16;
    private static final long LONG_LOWEST_BYTE_MASK = 255L;
    private static final int LONG_SIZE = 8;
    private static final int INT_LOWEST_BYTE_MASK = 255;
    private static final int INT_SIZE = 4;
    
    private NumberFactory() {
    }
    
    public static boolean makeBoolean(final int v) {
        return v >>> 31 != 0;
    }
    
    public static boolean makeBoolean(final long v) {
        return v >>> 63 != 0L;
    }
    
    public static double makeDouble(final long v) {
        return Double.longBitsToDouble(0x3FF0000000000000L | v >>> 12) - 1.0;
    }
    
    public static double makeDouble(final int v, final int w) {
        final long high = (long)(v >>> 6) << 26;
        final int low = w >>> 6;
        return (high | (long)low) * 2.220446049250313E-16;
    }
    
    public static float makeFloat(final int v) {
        return (v >>> 9) * 1.1920929E-7f;
    }
    
    public static long makeLong(final int v, final int w) {
        return (long)v << 32 | ((long)w & 0xFFFFFFFFL);
    }
    
    public static int makeInt(final long v) {
        return extractHi(v) ^ extractLo(v);
    }
    
    public static int extractHi(final long v) {
        return (int)(v >>> 32);
    }
    
    public static int extractLo(final long v) {
        return (int)v;
    }
    
    public static byte[] makeByteArray(final long v) {
        final byte[] b = new byte[8];
        for (int i = 0; i < 8; ++i) {
            final int shift = i * 8;
            b[i] = (byte)(v >>> shift & 0xFFL);
        }
        return b;
    }
    
    public static long makeLong(final byte[] input) {
        checkSize(8, input.length);
        long v = 0L;
        for (int i = 0; i < 8; ++i) {
            final int shift = i * 8;
            v |= ((long)input[i] & 0xFFL) << shift;
        }
        return v;
    }
    
    public static byte[] makeByteArray(final long[] input) {
        final int size = input.length * 8;
        final byte[] b = new byte[size];
        for (int i = 0; i < input.length; ++i) {
            final byte[] current = makeByteArray(input[i]);
            System.arraycopy(current, 0, b, i * 8, 8);
        }
        return b;
    }
    
    public static long[] makeLongArray(final byte[] input) {
        final int size = input.length;
        final int num = size / 8;
        checkSize(num * 8, size);
        final long[] output = new long[num];
        for (int i = 0; i < num; ++i) {
            final int from = i * 8;
            final byte[] current = Arrays.copyOfRange(input, from, from + 8);
            output[i] = makeLong(current);
        }
        return output;
    }
    
    public static byte[] makeByteArray(final int v) {
        final byte[] b = new byte[4];
        for (int i = 0; i < 4; ++i) {
            final int shift = i * 8;
            b[i] = (byte)(v >>> shift & 0xFF);
        }
        return b;
    }
    
    public static int makeInt(final byte[] input) {
        checkSize(4, input.length);
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            final int shift = i * 8;
            v |= (input[i] & 0xFF) << shift;
        }
        return v;
    }
    
    public static byte[] makeByteArray(final int[] input) {
        final int size = input.length * 4;
        final byte[] b = new byte[size];
        for (int i = 0; i < input.length; ++i) {
            final byte[] current = makeByteArray(input[i]);
            System.arraycopy(current, 0, b, i * 4, 4);
        }
        return b;
    }
    
    public static int[] makeIntArray(final byte[] input) {
        final int size = input.length;
        final int num = size / 4;
        checkSize(num * 4, size);
        final int[] output = new int[num];
        for (int i = 0; i < num; ++i) {
            final int from = i * 4;
            final byte[] current = Arrays.copyOfRange(input, from, from + 4);
            output[i] = makeInt(current);
        }
        return output;
    }
    
    private static void checkSize(final int expected, final int actual) {
        if (expected != actual) {
            throw new IllegalArgumentException("Array size: Expected " + expected + " but was " + actual);
        }
    }
}
