/*
 * Decompiled with CFR 0.150.
 */
package collections;

import java.util.Locale;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static boolean contains(double[] arr, double val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(int[] arr, int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(Object[] arr, Object val) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == null && val == null) {
                return true;
            }
            if (arr[i] == null || !arr[i].equals(val)) continue;
            return true;
        }
        return false;
    }

    public static int indexOf(int val, int[] arr) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] != val) continue;
            return i;
        }
        return -1;
    }

    public static int max(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int val : arr) {
            if (val <= max) continue;
            max = val;
        }
        return max;
    }

    public static float max(float[] arr) {
        float max = Float.NEGATIVE_INFINITY;
        for (float val : arr) {
            if (!(val > max)) continue;
            max = val;
        }
        return max;
    }

    public static int numOccurrences(double[] arr, double val) {
        int num = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] != val) continue;
            ++num;
        }
        return num;
    }

    public static String matrixToString(float[][] matrix, int numDecimals) {
        int i;
        int maxStrLength = 0;
        float[][] arrf = matrix;
        int n = arrf.length;
        for (int j = 0; j < n; ++j) {
            float[] arr;
            for (float element : arr = arrf[j]) {
                int length = String.valueOf((int)element).length();
                if (length <= maxStrLength) continue;
                maxStrLength = length;
            }
        }
        StringBuilder sb = new StringBuilder();
        int digitsFormat = 1;
        for (i = 1; i < maxStrLength; ++i) {
            digitsFormat *= 10;
        }
        for (i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                sb.append(String.format(Locale.ROOT, "%" + digitsFormat + "." + numDecimals + "f", Float.valueOf(matrix[i][j])));
                if (j >= matrix[i].length - 1) continue;
                sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

