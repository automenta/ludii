// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import java.io.*;

public class ExponentialMovingAverage implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final double alpha;
    protected double runningMean;
    protected double denominator;
    
    public ExponentialMovingAverage() {
        this(0.05);
    }
    
    public ExponentialMovingAverage(final double alpha) {
        this.runningMean = 0.0;
        this.denominator = 0.0;
        this.alpha = alpha;
    }
    
    public double movingAvg() {
        return this.runningMean;
    }
    
    public void observe(final double data) {
        this.denominator = (1.0 - this.alpha) * this.denominator + 1.0;
        this.runningMean += 1.0 / this.denominator * (data - this.runningMean);
    }
    
    public void writeToFile(final String filepath) {
        try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            out.writeObject(this);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
