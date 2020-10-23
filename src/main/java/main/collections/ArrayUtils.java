// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import java.util.Locale;

public class ArrayUtils
{
    private ArrayUtils() {
    }
    
    public static boolean contains(final double[] arr, final double val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == val) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean contains(final int[] arr, final int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == val) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean contains(final Object[] arr, final Object val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == null && val == null) {
                return true;
            }
            if (arr[i] != null && arr[i].equals(val)) {
                return true;
            }
        }
        return false;
    }
    
    public static int indexOf(final int val, final int[] arr) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }
    
    public static int max(final int[] arr) {
        int max = Integer.MIN_VALUE;
        for (final int val : arr) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }
    
    public static float max(final float[] arr) {
        float max = Float.NEGATIVE_INFINITY;
        for (final float val : arr) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }
    
    public static int numOccurrences(final double[] arr, final double val) {
        int num = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == val) {
                ++num;
            }
        }
        return num;
    }
    
    public static String matrixToString(final float[][] matrix, final int numDecimals) {
        int maxStrLength = 0;
        for (final float[] array : matrix) {
            final float[] arr = array;
            for (final float element : array) {
                final int length = String.valueOf((int)element).length();
                if (length > maxStrLength) {
                    maxStrLength = length;
                }
            }
        }
        final StringBuilder sb = new StringBuilder();
        int digitsFormat = 1;
        for (int i = 1; i < maxStrLength; ++i) {
            digitsFormat *= 10;
        }
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                sb.append(String.format(Locale.ROOT, "%" + digitsFormat + "." + numDecimals + "f", matrix[i][j]));
                if (j < matrix[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
