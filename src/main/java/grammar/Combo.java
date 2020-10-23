/*
 * Decompiled with CFR 0.150.
 */
package grammar;

public class Combo {
    private final int[] array;
    private final int count;

    public Combo(int n, int seed) {
        this.array = new int[n];
        int on = 0;
        for (int b = 0; b < n; ++b) {
            if ((seed & 1 << b) == 0) continue;
            this.array[b] = ++on;
        }
        this.count = on;
    }

    public int[] array() {
        return this.array;
    }

    public int length() {
        return this.array.length;
    }

    public int count() {
        return this.count;
    }
}

