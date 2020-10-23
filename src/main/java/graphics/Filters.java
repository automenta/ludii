// 
// Decompiled by Procyon v0.5.36
// 

package graphics;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Filters
{
    public static ConvolveOp gaussianBlurFilter(final int radius, final boolean horizontal) {
        if (radius < 1) {
            System.out.printf("radius=%d.\n", radius);
            return null;
        }
        final int size = radius * 2 + 1;
        final float[] data = new float[size];
        final float sigma = radius / 3.0f;
        final float twoSigmaSquare = 2.0f * sigma * sigma;
        final float sigmaRoot = (float)Math.sqrt(twoSigmaSquare * 3.141592653589793);
        float total = 0.0f;
        for (int i = -radius; i <= radius; ++i) {
            final float distance = (float)(i * i);
            final int index = i + radius;
            data[index] = (float)Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; ++i) {
            final float[] array = data;
            final int n = i;
            array[n] /= total;
        }
        final Kernel kernel = horizontal ? new Kernel(size, 1, data) : new Kernel(1, size, data);
        return new ConvolveOp(kernel, 1, null);
    }
}
