// 
// Decompiled by Procyon v0.5.36
// 

package optimisers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OptimiserFactory
{
    private OptimiserFactory() {
    }
    
    public static Optimiser createOptimiser(final String string) {
        if (string.equalsIgnoreCase("SGD")) {
            return new SGD(0.05f);
        }
        if (string.equalsIgnoreCase("RMSProp")) {
            return new DeepmindRMSProp();
        }
        if (string.equalsIgnoreCase("AMSGrad")) {
            return new AMSGrad(3.0E-4f);
        }
        final URL optimiserURL = OptimiserFactory.class.getResource(string);
        File optimiserFile = null;
        if (optimiserURL != null) {
            optimiserFile = new File(optimiserURL.getFile());
        }
        else {
            optimiserFile = new File(string);
        }
        String[] lines = new String[0];
        if (optimiserFile.exists()) {
            try (final BufferedReader reader = new BufferedReader(new FileReader(optimiserFile))) {
                final List<String> linesList = new ArrayList<>();
                final String line = reader.readLine();
                while (line != null) {
                    linesList.add(line);
                }
                lines = linesList.toArray(lines);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            lines = string.split(";");
        }
        final String firstLine = lines[0];
        if (firstLine.startsWith("optimiser=")) {
            final String optimiserName = firstLine.substring("optimiser=".length());
            if (optimiserName.equalsIgnoreCase("SGD")) {
                return SGD.fromLines(lines);
            }
            if (optimiserName.equalsIgnoreCase("RMSProp")) {
                return DeepmindRMSProp.fromLines(lines);
            }
            if (optimiserName.equalsIgnoreCase("AMSGrad")) {
                return AMSGrad.fromLines(lines);
            }
            System.err.println("Unknown optimizer name: " + optimiserName);
        }
        else {
            System.err.println("Expecting Optimizer file to start with \"optimiser=\", but it starts with " + firstLine);
        }
        System.err.printf("Warning: cannot convert string \"%s\" to Optimiser; defaulting to vanilla SGD.%n", string);
        return new SGD(0.05f);
    }
}
