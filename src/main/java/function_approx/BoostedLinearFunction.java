// 
// Decompiled by Procyon v0.5.36
// 

package function_approx;

import gnu.trove.list.array.TFloatArrayList;
import collections.FVector;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BoostedLinearFunction extends LinearFunction
{
    protected final LinearFunction booster;
    
    public BoostedLinearFunction(final FVector theta, final LinearFunction booster) {
        super(theta);
        this.booster = booster;
    }
    
    @Override
    public FVector effectiveParams() {
        final FVector params = this.booster.effectiveParams().copy();
        params.add(this.trainableParams());
        return params;
    }
    
    @Override
    public void writeToFile(final String filepath, final String[] featureSetFiles) {
        try (final PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8)) {
            for (int i = 0; i < this.theta.dim(); ++i) {
                writer.println(this.theta.get(i));
            }
            for (final String fsf : featureSetFiles) {
                writer.println("FeatureSet=" + new File(fsf).getName());
            }
            writer.println("Effective Params:");
            final FVector effectiveParams = this.effectiveParams();
            for (int j = 0; j < effectiveParams.dim(); ++j) {
                writer.println(effectiveParams.get(j));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static BoostedLinearFunction boostedFromFile(final String filepath, final LinearFunction booster) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8))) {
            final TFloatArrayList readFloats = new TFloatArrayList();
            String featureSetFile = null;
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("FeatureSet=")) {
                    featureSetFile = line.substring("FeatureSet=".length());
                }
                else {
                    if (line.equals("Effective Params:")) {
                        break;
                    }
                    readFloats.add(Float.parseFloat(line));
                }
            }
            final float[] floats = new float[readFloats.size()];
            for (int i = 0; i < floats.length; ++i) {
                floats[i] = readFloats.getQuick(i);
            }
            LinearFunction boosterFunc = booster;
            if (boosterFunc == null) {
                final TFloatArrayList effectiveParams = new TFloatArrayList();
                if (!line.equals("Effective Params:")) {
                    System.err.println("Error in BoostedLinearFunction::boostedFromFile file! Expected line: \"Effective Params:\"");
                }
                for (line = reader.readLine(); line != null; line = reader.readLine()) {
                    effectiveParams.add(Float.parseFloat(line));
                }
                final float[] boosterFloats = new float[effectiveParams.size()];
                for (int j = 0; j < boosterFloats.length; ++j) {
                    boosterFloats[j] = effectiveParams.getQuick(j) - floats[j];
                }
                boosterFunc = new LinearFunction(FVector.wrap(boosterFloats));
            }
            final BoostedLinearFunction func = new BoostedLinearFunction(FVector.wrap(floats), boosterFunc);
            func.setFeatureSetFile(featureSetFile);
            return func;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
