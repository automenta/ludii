// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

public class Combo
{
    private final int[] array;
    private final int count;
    
    public Combo(final int n, final int seed) {
        this.array = new int[n];
        int on = 0;
        for (int b = 0; b < n; ++b) {
            if ((seed & 1 << b) != 0x0) {
                this.array[b] = ++on;
            }
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
