// 
// Decompiled by Procyon v0.5.36
// 

package function_approx;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import collections.FVector;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LinearFunction
{
    protected FVector theta;
    protected String featureSetFile;
    
    public LinearFunction(final FVector theta) {
        this.featureSetFile = null;
        this.theta = theta;
    }
    
    public float predict(final FVector denseFeatures) {
        return this.effectiveParams().dot(denseFeatures);
    }
    
    public float predict(final TIntArrayList sparseFeatures) {
        return this.effectiveParams().dotSparse(sparseFeatures);
    }
    
    public FVector effectiveParams() {
        return this.theta;
    }
    
    public FVector trainableParams() {
        return this.theta;
    }
    
    public void gradientDescent(final FVector gradients, final float stepSize) {
        this.trainableParams().addScaled(gradients, -stepSize);
    }
    
    public void gradientDescent(final FVector gradients, final float stepSize, final float weightDecayParam) {
        this.trainableParams().addScaled(this.trainableParams(), -stepSize * weightDecayParam);
        this.gradientDescent(gradients, stepSize);
    }
    
    public void setTheta(final FVector newTheta) {
        this.theta = newTheta;
    }
    
    public String featureSetFile() {
        return this.featureSetFile;
    }
    
    public void setFeatureSetFile(final String featureSetFile) {
        this.featureSetFile = featureSetFile;
    }
    
    public void writeToFile(final String filepath, final String[] featureSetFiles) {
        try (final PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8)) {
            for (int i = 0; i < this.theta.dim(); ++i) {
                writer.println(this.theta.get(i));
            }
            for (final String fsf : featureSetFiles) {
                writer.println("FeatureSet=" + new File(fsf).getName());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static LinearFunction fromFile(final String filepath) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8))) {
            final TFloatArrayList readFloats = new TFloatArrayList();
            String featureSetFile = null;
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("FeatureSet=")) {
                    featureSetFile = line.substring("FeatureSet=".length());
                }
                else {
                    readFloats.add(Float.parseFloat(line));
                }
            }
            final float[] floats = new float[readFloats.size()];
            for (int i = 0; i < floats.length; ++i) {
                floats[i] = readFloats.getQuick(i);
            }
            final LinearFunction func = new LinearFunction(FVector.wrap(floats));
            func.setFeatureSetFile(featureSetFile);
            return func;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
