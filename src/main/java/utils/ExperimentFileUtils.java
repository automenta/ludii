// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import java.io.File;

public class ExperimentFileUtils
{
    private static final String fileSequenceFormat = "%s_%05d.%s";
    
    private ExperimentFileUtils() {
    }
    
    public static String getNextFilepath(final String baseFilepath, final String extension) {
        int index;
        String result;
        for (index = 0, result = String.format("%s_%05d.%s", baseFilepath, index, extension); new File(result).exists(); result = String.format("%s_%05d.%s", baseFilepath, index, extension)) {
            ++index;
        }
        return result;
    }
    
    public static String getLastFilepath(final String baseFilepath, final String extension) {
        int index = 0;
        String result = null;
        while (new File(String.format("%s_%05d.%s", baseFilepath, index, extension)).exists()) {
            result = String.format("%s_%05d.%s", baseFilepath, index, extension);
            ++index;
        }
        return result;
    }
}
