// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.util.Arrays;

public class Sites
{
    private int count;
    private final int[] sites;
    
    public Sites(final int count) {
        this.count = 0;
        this.count = count;
        this.sites = new int[count];
        for (int n = 0; n < count; ++n) {
            this.sites[n] = n;
        }
    }
    
    public Sites(final int[] sites) {
        this.count = 0;
        this.count = sites.length;
        System.arraycopy(sites, 0, this.sites = new int[this.count], 0, this.count);
    }
    
    public Sites(final Sites other) {
        this.count = 0;
        this.count = other.count;
        this.sites = new int[other.sites.length];
        System.arraycopy(other.sites, 0, this.sites, 0, this.sites.length);
    }
    
    public int count() {
        return this.count;
    }
    
    public int[] sites() {
        return this.sites;
    }
    
    public void set(final int newCount) {
        if (newCount > this.sites.length) {
            System.out.println("** Sites.set() A: Bad count " + newCount + " for " + this.sites.length + " entries.");
            try {
                throw new Exception("Exception.");
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        this.count = newCount;
        for (int n = 0; n < this.count; ++n) {
            this.sites[n] = n;
        }
        for (int n = this.count; n < this.sites.length; ++n) {
            this.sites[n] = -1;
        }
    }
    
    public void set(final Sites other) {
        if (other.count > this.sites.length) {
            System.out.println("** Sites.set() B: Bad count " + other.count + " for " + this.sites.length + " entries.");
            return;
        }
        this.count = other.count;
        System.arraycopy(other.sites, 0, this.sites, 0, this.count);
        for (int n = this.count; n < this.sites.length; ++n) {
            this.sites[n] = -1;
        }
    }
    
    public int nthValue(final int n) {
        return this.sites[n];
    }
    
    public void add(final int val) {
        if (this.count >= this.sites.length) {
            System.out.println("** Sites.add(): Trying to add " + val + " to full array.");
            return;
        }
        this.sites[this.count] = val;
        ++this.count;
    }
    
    public void remove(final int val) {
        if (this.count < 1) {
            System.out.println("** Sites.remove(): Trying to remove " + val + " from " + this.count + " entries.");
            return;
        }
        for (int n = Math.min(val, this.count - 1); n >= 0; --n) {
            if (this.sites[n] == val) {
                this.sites[n] = this.sites[this.count - 1];
                this.sites[this.count - 1] = -1;
                --this.count;
                return;
            }
        }
        for (int n = this.count - 1; n > Math.min(val, this.count - 1); --n) {
            if (this.sites[n] == val) {
                this.sites[n] = this.sites[this.count - 1];
                this.sites[this.count - 1] = -1;
                --this.count;
                return;
            }
        }
        System.out.println("** Sites.remove(): Failed to find value " + val + ".");
    }
    
    public int removeNth(final int n) {
        if (this.count < 1) {
            System.out.println("** Sites.remove(): Trying to remove " + n + "th entry from " + this.count + " entries.");
            return -1;
        }
        final int val = this.sites[n];
        this.sites[n] = this.sites[this.count - 1];
        this.sites[this.count - 1] = -1;
        --this.count;
        return val;
    }
    
    @Override
    public String toString() {
        return "Sites{" + Arrays.toString(this.sites) + " (count at:= " + this.count + ")}";
    }
}
