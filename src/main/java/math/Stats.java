/*
 * Decompiled with CFR 0.150.
 */
package math;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Locale;

public class Stats {
    protected String label = "?";
    protected TDoubleArrayList samples = new TDoubleArrayList();
    protected final double ci = 1.95996;
    protected double sum;
    protected double mean;
    protected double varn;
    protected double devn;
    protected double conf;
    protected double min;
    protected double max;

    public Stats() {
        this.label = "Unnamed";
    }

    public Stats(String str) {
        this.label = str;
    }

    public String label() {
        return this.label;
    }

    public void setLabel(String lbl) {
        this.label = lbl;
    }

    public void addSample(double val) {
        this.samples.add(val);
    }

    public double get(int index) {
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

    public void set(TDoubleArrayList list) {
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
        double val;
        int i;
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
        for (i = 0; i < this.samples.size(); ++i) {
            val = this.samples.getQuick(i);
            this.sum += val;
            if (val < this.min) {
                this.min = val;
                continue;
            }
            if (!(val > this.max)) continue;
            this.max = val;
        }
        this.mean = this.sum / (double)this.samples.size();
        if (this.samples.size() > 1) {
            for (i = 0; i < this.samples.size(); ++i) {
                val = this.samples.getQuick(i);
                double diff = val - this.mean;
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
        String str = String.format(Locale.ROOT, "%s: N=%d, mean=%.6f (+/-%.6f), sd=%.6f, min=%.6f, max=%.6f.", this.label, this.samples.size(), this.mean, this.conf, this.devn, this.min, this.max);
        System.out.println(str);
    }

    public String toString() {
        String str = String.format(Locale.ROOT, "%s: N=%d, mean=%.6f (+/-%.6f).", this.label, this.samples.size(), this.mean, this.conf);
        return str;
    }
}

