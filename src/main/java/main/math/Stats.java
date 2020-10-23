// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Locale;

public class Stats
{
    protected String label;
    protected TDoubleArrayList samples;
    protected final double ci = 1.95996;
    protected double sum;
    protected double mean;
    protected double varn;
    protected double devn;
    protected double conf;
    protected double min;
    protected double max;
    
    public Stats() {
        this.label = "?";
        this.samples = new TDoubleArrayList();
        this.label = "Unnamed";
    }
    
    public Stats(final String str) {
        this.label = "?";
        this.samples = new TDoubleArrayList();
        this.label = str;
    }
    
    public String label() {
        return this.label;
    }
    
    public void setLabel(final String lbl) {
        this.label = lbl;
    }
    
    public void addSample(final double val) {
        this.samples.add(val);
    }
    
    public double get(final int index) {
        return this.samples.get(index);
    }
    
    public int numSamples() {
        return this.samples.size();
    }
    
    public double sum() {
        return this.sum;
    }
    
    public double mean() {
        return this.mean;
    }
    
    public double varn() {
        return this.varn;
    }
    
    public double devn() {
        return this.devn;
    }
    
    public double conf() {
        return this.conf;
    }
    
    public double min() {
        return this.min;
    }
    
    public double max() {
        return this.max;
    }
    
    public double range() {
        return this.max() - this.min();
    }
    
    public void set(final TDoubleArrayList list) {
        this.samples = list;
    }
    
    public void clear() {
        this.samples.clear();
        this.sum = 0.0;
        this.mean = 0.0;
        this.varn = 0.0;
        this.devn = 0.0;
        this.conf = 0.0;
        this.min = 0.0;
        this.max = 0.0;
    }
    
    public void measure() {
        this.sum = 0.0;
        this.mean = 0.0;
        this.varn = 0.0;
        this.devn = 0.0;
        this.conf = 0.0;
        this.min = 0.0;
        this.max = 0.0;
        if (this.samples.size() == 0) {
            return;
        }
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < this.samples.size(); ++i) {
            final double val = this.samples.getQuick(i);
            this.sum += val;
            if (val < this.min) {
                this.min = val;
            }
            else if (val > this.max) {
                this.max = val;
            }
        }
        this.mean = this.sum / this.samples.size();
        if (this.samples.size() > 1) {
            for (int i = 0; i < this.samples.size(); ++i) {
                final double val = this.samples.getQuick(i);
                final double diff = val - this.mean;
                this.varn += diff * diff;
            }
            this.varn /= this.samples.size() - 1;
            this.devn = Math.sqrt(this.varn);
            this.conf = 1.95996 * this.devn / Math.sqrt(this.samples.size());
        }
    }
    
    public void show() {
        System.out.printf(this.toString());
    }
    
    public void showFull() {
        final String str = String.format(Locale.ROOT, "%s: N=%d, mean=%.6f (+/-%.6f), sd=%.6f, min=%.6f, max=%.6f.", this.label, this.samples.size(), this.mean, this.conf, this.devn, this.min, this.max);
        System.out.println(str);
    }
    
    @Override
    public String toString() {
        final String str = String.format(Locale.ROOT, "%s: N=%d, mean=%.6f (+/-%.6f).", this.label, this.samples.size(), this.mean, this.conf);
        return str;
    }
}
