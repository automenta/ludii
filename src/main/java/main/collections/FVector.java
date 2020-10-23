// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FVector implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final float[] floats;
    
    public FVector(final int d) {
        this.floats = new float[d];
    }
    
    public FVector(final int d, final float fillValue) {
        Arrays.fill(this.floats = new float[d], fillValue);
    }
    
    public FVector(final float[] floats) {
        System.arraycopy(floats, 0, this.floats = new float[floats.length], 0, floats.length);
    }
    
    public FVector(final FVector other) {
        this(other.floats);
    }
    
    public FVector(final float[] floats, final boolean steal) {
        if (!steal) {
            throw new IllegalArgumentException("steal must be true when instantiating a vector that steals data");
        }
        this.floats = floats;
    }
    
    public FVector copy() {
        return new FVector(this);
    }
    
    public static FVector ones(final int d) {
        final FVector ones = new FVector(d);
        ones.fill(0, d, 1.0f);
        return ones;
    }
    
    public static FVector zeros(final int d) {
        return new FVector(d);
    }
    
    public static FVector wrap(final float[] floats) {
        return new FVector(floats, true);
    }
    
    public int argMax() {
        float max = Float.NEGATIVE_INFINITY;
        final int d = this.floats.length;
        int maxIdx = -1;
        for (int i = 0; i < d; ++i) {
            if (this.floats[i] > max) {
                max = this.floats[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }
    
    public int argMaxRand() {
        float max = Float.NEGATIVE_INFINITY;
        final int d = this.floats.length;
        int maxIdx = -1;
        int numMaxFound = 0;
        for (int i = 0; i < d; ++i) {
            final float val = this.floats[i];
            if (val > max) {
                max = val;
                maxIdx = i;
                numMaxFound = 1;
            }
            else if (val == max && ThreadLocalRandom.current().nextInt() % ++numMaxFound == 0) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }
    
    public int argMin() {
        float min = Float.POSITIVE_INFINITY;
        final int d = this.floats.length;
        int minIdx = -1;
        for (int i = 0; i < d; ++i) {
            if (this.floats[i] < min) {
                min = this.floats[i];
                minIdx = i;
            }
        }
        return minIdx;
    }
    
    public int argMinRand() {
        float min = Float.POSITIVE_INFINITY;
        final int d = this.floats.length;
        int minIdx = -1;
        int numMinFound = 0;
        for (int i = 0; i < d; ++i) {
            final float val = this.floats[i];
            if (val < min) {
                min = val;
                minIdx = i;
                numMinFound = 1;
            }
            else if (val == min && ThreadLocalRandom.current().nextInt() % ++numMinFound == 0) {
                minIdx = i;
            }
        }
        return minIdx;
    }
    
    public int dim() {
        return this.floats.length;
    }
    
    public float get(final int entry) {
        return this.floats[entry];
    }
    
    public float max() {
        float max = Float.NEGATIVE_INFINITY;
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            if (this.floats[i] > max) {
                max = this.floats[i];
            }
        }
        return max;
    }
    
    public float min() {
        float min = Float.POSITIVE_INFINITY;
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            if (this.floats[i] < min) {
                min = this.floats[i];
            }
        }
        return min;
    }
    
    public float mean() {
        return this.sum() / this.floats.length;
    }
    
    public double norm() {
        float sumSquares = 0.0f;
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            sumSquares += this.floats[i] * this.floats[i];
        }
        return Math.sqrt(sumSquares);
    }
    
    public void set(final int entry, final float value) {
        this.floats[entry] = value;
    }
    
    public float sum() {
        float sum = 0.0f;
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            sum += this.floats[i];
        }
        return sum;
    }
    
    public void abs() {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            this.floats[i] = Math.abs(this.floats[i]);
        }
    }
    
    public void add(final float value) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] += value;
        }
    }
    
    public void add(final float[] toAdd) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] += toAdd[i];
        }
    }
    
    public void add(final FVector other) {
        this.add(other.floats);
    }
    
    public void addToEntry(final int entry, final float value) {
        final float[] floats = this.floats;
        floats[entry] += value;
    }
    
    public void addScaled(final FVector other, final float scalar) {
        final int d = this.floats.length;
        final float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] += otherFloats[i] * scalar;
        }
    }
    
    public void div(final float scalar) {
        final int d = this.floats.length;
        final float mult = 1.0f / scalar;
        for (int i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] *= mult;
        }
    }
    
    public void elementwiseDivision(final FVector other) {
        final int d = this.floats.length;
        final float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] /= otherFloats[i];
        }
    }
    
    public void hadamardProduct(final FVector other) {
        final int d = this.floats.length;
        final float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] *= otherFloats[i];
        }
    }
    
    public void log() {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.log(this.floats[i]);
        }
    }
    
    public void mult(final float scalar) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] *= scalar;
        }
    }
    
    public void raiseToPower(final double power) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.pow(this.floats[i], power);
        }
    }
    
    public void softmax() {
        final int d = this.floats.length;
        final float max = this.max();
        double sumExponents = 0.0;
        for (int i = 0; i < d; ++i) {
            final double exp = Math.exp(this.floats[i] - max);
            sumExponents += exp;
            this.floats[i] = (float)exp;
        }
        this.div((float)sumExponents);
    }
    
    public void sqrt() {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.sqrt(this.floats[i]);
        }
    }
    
    public void subtract(final float value) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] -= value;
        }
    }
    
    public void subtract(final float[] toSubtract) {
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            final float[] floats = this.floats;
            final int n = i;
            floats[n] -= toSubtract[i];
        }
    }
    
    public void subtract(final FVector other) {
        this.subtract(other.floats);
    }
    
    public int sampleFromDistribution() {
        final float rand = ThreadLocalRandom.current().nextFloat();
        final int d = this.floats.length;
        float accum = 0.0f;
        for (int i = 0; i < d; ++i) {
            accum += this.floats[i];
            if (rand < accum) {
                return i;
            }
        }
        return d - 1;
    }
    
    public int sampleProportionally() {
        final float sum = this.sum();
        final float rand = ThreadLocalRandom.current().nextFloat();
        final int d = this.floats.length;
        float accum = 0.0f;
        for (int i = 0; i < d; ++i) {
            accum += this.floats[i] / sum;
            if (rand < accum) {
                return i;
            }
        }
        return d - 1;
    }
    
    public float dot(final FVector other) {
        float sum = 0.0f;
        final float[] otherFloats = other.floats;
        for (int d = this.floats.length, i = 0; i < d; ++i) {
            sum += this.floats[i] * otherFloats[i];
        }
        return sum;
    }
    
    public float dotSparse(final TIntArrayList sparseBinary) {
        float sum = 0.0f;
        for (int numOnes = sparseBinary.size(), i = 0; i < numOnes; ++i) {
            sum += this.floats[sparseBinary.getQuick(i)];
        }
        return sum;
    }
    
    public double normalisedEntropy() {
        final int dim = this.dim();
        if (dim <= 1) {
            return 0.0;
        }
        double entropy = 0.0;
        for (int i = 0; i < dim; ++i) {
            final float prob = this.floats[i];
            if (prob > 0.0f) {
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy / Math.log(dim);
    }
    
    public boolean containsNaN() {
        for (int i = 0; i < this.floats.length; ++i) {
            if (Float.isNaN(this.floats[i])) {
                return true;
            }
        }
        return false;
    }
    
    public void fill(final int startInclusive, final int endExclusive, final float val) {
        Arrays.fill(this.floats, startInclusive, endExclusive, val);
    }
    
    public void copyFrom(final FVector src, final int srcPos, final int destPos, final int length) {
        System.arraycopy(src.floats, srcPos, this.floats, destPos, length);
    }
    
    public FVector append(final float newValue) {
        final FVector newVector = new FVector(this.floats.length + 1);
        System.arraycopy(this.floats, 0, newVector.floats, 0, this.floats.length);
        newVector.floats[this.floats.length] = newValue;
        return newVector;
    }
    
    public FVector cut(final int entry) {
        return this.cut(entry, entry + 1);
    }
    
    public FVector cut(final int startEntryInclusive, final int endEntryExclusive) {
        final int newD = this.floats.length - (endEntryExclusive - startEntryInclusive);
        final FVector newVector = new FVector(newD);
        System.arraycopy(this.floats, 0, newVector.floats, 0, startEntryInclusive);
        System.arraycopy(this.floats, endEntryExclusive, newVector.floats, startEntryInclusive, this.floats.length - endEntryExclusive);
        return newVector;
    }
    
    public FVector insert(final int index, final float value) {
        final FVector newVector = new FVector(this.floats.length + 1);
        System.arraycopy(this.floats, 0, newVector.floats, 0, index);
        newVector.floats[index] = value;
        System.arraycopy(this.floats, index, newVector.floats, index + 1, this.floats.length - index);
        return newVector;
    }
    
    public FVector insert(final int index, final float[] values) {
        final FVector newVector = new FVector(this.floats.length + values.length);
        System.arraycopy(this.floats, 0, newVector.floats, 0, index);
        System.arraycopy(values, 0, newVector.floats, index, values.length);
        System.arraycopy(this.floats, index, newVector.floats, index + values.length, this.floats.length - index);
        return newVector;
    }
    
    public static FVector concat(final FVector a, final FVector b) {
        final FVector concat = new FVector(a.dim() + b.dim());
        System.arraycopy(a.floats, 0, concat.floats, 0, a.dim());
        System.arraycopy(b.floats, 0, concat.floats, a.dim(), b.dim());
        return concat;
    }
    
    public static float crossEntropy(final FVector trueDist, final FVector estDist) {
        final int d = trueDist.dim();
        final float[] trueFloats = trueDist.floats;
        final float[] estFloats = estDist.floats;
        float result = 0.0f;
        for (int i = 0; i < d; ++i) {
            result -= (float)(trueFloats[i] * Math.log(estFloats[i]));
        }
        return result;
    }
    
    public static FVector elementwiseMax(final FVector a, final FVector b) {
        final int d = a.dim();
        final float[] aFloats = a.floats;
        final float[] bFloats = b.floats;
        final float[] result = new float[d];
        for (int i = 0; i < d; ++i) {
            result[i] = Math.max(aFloats[i], bFloats[i]);
        }
        return wrap(result);
    }
    
    public static float klDivergence(final FVector trueDist, final FVector estDist) {
        final int d = trueDist.dim();
        final float[] trueFloats = trueDist.floats;
        final float[] estFloats = estDist.floats;
        float result = 0.0f;
        for (int i = 0; i < d; ++i) {
            if (trueFloats[i] != 0.0f) {
                result -= (float)(trueFloats[i] * Math.log(estFloats[i] / trueFloats[i]));
            }
        }
        return result;
    }
    
    public static FVector mean(final FVector[] vectors) {
        final int d = vectors[0].dim();
        final float[] means = new float[d];
        for (final FVector vector : vectors) {
            final float[] vals = vector.floats;
            for (int i = 0; i < d; ++i) {
                final float[] array = means;
                final int n = i;
                array[n] += vals[i];
            }
        }
        final FVector meanVector = wrap(means);
        meanVector.mult(1.0f / vectors.length);
        return meanVector;
    }
    
    public static FVector mean(final List<FVector> vectors) {
        final int d = vectors.get(0).dim();
        final float[] means = new float[d];
        for (final FVector vector : vectors) {
            final float[] vals = vector.floats;
            for (int i = 0; i < d; ++i) {
                final float[] array = means;
                final int n = i;
                array[n] += vals[i];
            }
        }
        final FVector meanVector = wrap(means);
        meanVector.mult(1.0f / vectors.size());
        return meanVector;
    }
    
    public static FVector linspace(final float start, final float stop, final int num, final boolean endInclusive) {
        final FVector result = new FVector(num);
        final float step = endInclusive ? ((stop - start) / (num - 1)) : ((stop - start) / num);
        for (int i = 0; i < num; ++i) {
            result.set(i, start + i * step);
        }
        return result;
    }
    
    public String toLine() {
        String result = "";
        for (int i = 0; i < this.floats.length; ++i) {
            result += this.floats[i];
            if (i < this.floats.length - 1) {
                result += ",";
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.toLine());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.floats);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FVector)) {
            return false;
        }
        final FVector other = (FVector)obj;
        return Arrays.equals(this.floats, other.floats);
    }
}
