/*
 * Decompiled with CFR 0.150.
 */
package collections;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FVector
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final float[] floats;

    public FVector(int d) {
        this.floats = new float[d];
    }

    public FVector(int d, float fillValue) {
        this.floats = new float[d];
        Arrays.fill(this.floats, fillValue);
    }

    public FVector(float[] floats) {
        this.floats = new float[floats.length];
        System.arraycopy(floats, 0, this.floats, 0, floats.length);
    }

    public FVector(FVector other) {
        this(other.floats);
    }

    public FVector(float[] floats, boolean steal) {
        if (!steal) {
            throw new IllegalArgumentException("steal must be true when instantiating a vector that steals data");
        }
        this.floats = floats;
    }

    public FVector copy() {
        return new FVector(this);
    }

    public static FVector ones(int d) {
        FVector ones = new FVector(d);
        ones.fill(0, d, 1.0f);
        return ones;
    }

    public static FVector zeros(int d) {
        return new FVector(d);
    }

    public static FVector wrap(float[] floats) {
        return new FVector(floats, true);
    }

    public int argMax() {
        float max = Float.NEGATIVE_INFINITY;
        int d = this.floats.length;
        int maxIdx = -1;
        for (int i = 0; i < d; ++i) {
            if (!(this.floats[i] > max)) continue;
            max = this.floats[i];
            maxIdx = i;
        }
        return maxIdx;
    }

    public int argMaxRand() {
        float max = Float.NEGATIVE_INFINITY;
        int d = this.floats.length;
        int maxIdx = -1;
        int numMaxFound = 0;
        for (int i = 0; i < d; ++i) {
            float val = this.floats[i];
            if (val > max) {
                max = val;
                maxIdx = i;
                numMaxFound = 1;
                continue;
            }
            if (val != max || ThreadLocalRandom.current().nextInt() % ++numMaxFound != 0) continue;
            maxIdx = i;
        }
        return maxIdx;
    }

    public int argMin() {
        float min = Float.POSITIVE_INFINITY;
        int d = this.floats.length;
        int minIdx = -1;
        for (int i = 0; i < d; ++i) {
            if (!(this.floats[i] < min)) continue;
            min = this.floats[i];
            minIdx = i;
        }
        return minIdx;
    }

    public int argMinRand() {
        float min = Float.POSITIVE_INFINITY;
        int d = this.floats.length;
        int minIdx = -1;
        int numMinFound = 0;
        for (int i = 0; i < d; ++i) {
            float val = this.floats[i];
            if (val < min) {
                min = val;
                minIdx = i;
                numMinFound = 1;
                continue;
            }
            if (val != min || ThreadLocalRandom.current().nextInt() % ++numMinFound != 0) continue;
            minIdx = i;
        }
        return minIdx;
    }

    public int dim() {
        return this.floats.length;
    }

    public float get(int entry) {
        return this.floats[entry];
    }

    public float max() {
        float max = Float.NEGATIVE_INFINITY;
        int d = this.floats.length;
        for (float aFloat : this.floats) {
            if (!(aFloat > max)) continue;
            max = aFloat;
        }
        return max;
    }

    public float min() {
        float min = Float.POSITIVE_INFINITY;
        int d = this.floats.length;
        for (float aFloat : this.floats) {
            if (!(aFloat < min)) continue;
            min = aFloat;
        }
        return min;
    }

    public float mean() {
        return this.sum() / this.floats.length;
    }

    public double norm() {
        float sumSquares = 0.0f;
        int d = this.floats.length;
        for (float aFloat : this.floats) {
            sumSquares += aFloat * aFloat;
        }
        return Math.sqrt(sumSquares);
    }

    public void set(int entry, float value) {
        this.floats[entry] = value;
    }

    public float sum() {
        float sum = 0.0f;
        int d = this.floats.length;
        for (float aFloat : this.floats) {
            sum += aFloat;
        }
        return sum;
    }

    public void abs() {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            this.floats[i] = Math.abs(this.floats[i]);
        }
    }

    public void add(float value) {
        int d = this.floats.length;
        int i = 0;
        while (i < d) {
            int n = i++;
            this.floats[n] = this.floats[n] + value;
        }
    }

    public void add(float[] toAdd) {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            int n = i;
            this.floats[n] = this.floats[n] + toAdd[i];
        }
    }

    public void add(FVector other) {
        this.add(other.floats);
    }

    public void addToEntry(int entry, float value) {
        int n = entry;
        this.floats[n] = this.floats[n] + value;
    }

    public void addScaled(FVector other, float scalar) {
        int d = this.floats.length;
        float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            int n = i;
            this.floats[n] = this.floats[n] + otherFloats[i] * scalar;
        }
    }

    public void div(float scalar) {
        int d = this.floats.length;
        float mult = 1.0f / scalar;
        int i = 0;
        while (i < d) {
            int n = i++;
            this.floats[n] = this.floats[n] * mult;
        }
    }

    public void elementwiseDivision(FVector other) {
        int d = this.floats.length;
        float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            int n = i;
            this.floats[n] = this.floats[n] / otherFloats[i];
        }
    }

    public void hadamardProduct(FVector other) {
        int d = this.floats.length;
        float[] otherFloats = other.floats;
        for (int i = 0; i < d; ++i) {
            int n = i;
            this.floats[n] = this.floats[n] * otherFloats[i];
        }
    }

    public void log() {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.log(this.floats[i]);
        }
    }

    public void mult(float scalar) {
        int d = this.floats.length;
        int i = 0;
        while (i < d) {
            int n = i++;
            this.floats[n] = this.floats[n] * scalar;
        }
    }

    public void raiseToPower(double power) {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.pow(this.floats[i], power);
        }
    }

    public void softmax() {
        int d = this.floats.length;
        float max = this.max();
        double sumExponents = 0.0;
        for (int i = 0; i < d; ++i) {
            double exp = Math.exp(this.floats[i] - max);
            sumExponents += exp;
            this.floats[i] = (float)exp;
        }
        this.div((float)sumExponents);
    }

    public void sqrt() {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            this.floats[i] = (float)Math.sqrt(this.floats[i]);
        }
    }

    public void subtract(float value) {
        int d = this.floats.length;
        int i = 0;
        while (i < d) {
            int n = i++;
            this.floats[n] = this.floats[n] - value;
        }
    }

    public void subtract(float[] toSubtract) {
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            int n = i;
            this.floats[n] = this.floats[n] - toSubtract[i];
        }
    }

    public void subtract(FVector other) {
        this.subtract(other.floats);
    }

    public int sampleFromDistribution() {
        float rand = ThreadLocalRandom.current().nextFloat();
        int d = this.floats.length;
        float accum = 0.0f;
        for (int i = 0; i < d; ++i) {
            if (!(rand < (accum += this.floats[i]))) continue;
            return i;
        }
        return d - 1;
    }

    public int sampleProportionally() {
        float sum = this.sum();
        float rand = ThreadLocalRandom.current().nextFloat();
        int d = this.floats.length;
        float accum = 0.0f;
        for (int i = 0; i < d; ++i) {
            if (!(rand < (accum += this.floats[i] / sum))) continue;
            return i;
        }
        return d - 1;
    }

    public float dot(FVector other) {
        float sum = 0.0f;
        float[] otherFloats = other.floats;
        int d = this.floats.length;
        for (int i = 0; i < d; ++i) {
            sum += this.floats[i] * otherFloats[i];
        }
        return sum;
    }

    public float dotSparse(TIntArrayList sparseBinary) {
        float sum = 0.0f;
        int numOnes = sparseBinary.size();
        for (int i = 0; i < numOnes; ++i) {
            sum += this.floats[sparseBinary.getQuick(i)];
        }
        return sum;
    }

    public double normalisedEntropy() {
        int dim = this.dim();
        if (dim <= 1) {
            return 0.0;
        }
        double entropy = 0.0;
        for (int i = 0; i < dim; ++i) {
            float prob = this.floats[i];
            if (!(prob > 0.0f)) continue;
            entropy -= prob * Math.log(prob);
        }
        return entropy / Math.log(dim);
    }

    public boolean containsNaN() {
        for (float aFloat : this.floats) {
            if (!Float.isNaN(aFloat)) continue;
            return true;
        }
        return false;
    }

    public void fill(int startInclusive, int endExclusive, float val) {
        Arrays.fill(this.floats, startInclusive, endExclusive, val);
    }

    public void copyFrom(FVector src, int srcPos, int destPos, int length) {
        System.arraycopy(src.floats, srcPos, this.floats, destPos, length);
    }

    public FVector append(float newValue) {
        FVector newVector = new FVector(this.floats.length + 1);
        System.arraycopy(this.floats, 0, newVector.floats, 0, this.floats.length);
        newVector.floats[this.floats.length] = newValue;
        return newVector;
    }

    public FVector cut(int entry) {
        return this.cut(entry, entry + 1);
    }

    public FVector cut(int startEntryInclusive, int endEntryExclusive) {
        int newD = this.floats.length - (endEntryExclusive - startEntryInclusive);
        FVector newVector = new FVector(newD);
        System.arraycopy(this.floats, 0, newVector.floats, 0, startEntryInclusive);
        System.arraycopy(this.floats, endEntryExclusive, newVector.floats, startEntryInclusive, this.floats.length - endEntryExclusive);
        return newVector;
    }

    public FVector insert(int index, float value) {
        FVector newVector = new FVector(this.floats.length + 1);
        System.arraycopy(this.floats, 0, newVector.floats, 0, index);
        newVector.floats[index] = value;
        System.arraycopy(this.floats, index, newVector.floats, index + 1, this.floats.length - index);
        return newVector;
    }

    public FVector insert(int index, float[] values) {
        FVector newVector = new FVector(this.floats.length + values.length);
        System.arraycopy(this.floats, 0, newVector.floats, 0, index);
        System.arraycopy(values, 0, newVector.floats, index, values.length);
        System.arraycopy(this.floats, index, newVector.floats, index + values.length, this.floats.length - index);
        return newVector;
    }

    public static FVector concat(FVector a, FVector b) {
        FVector concat = new FVector(a.dim() + b.dim());
        System.arraycopy(a.floats, 0, concat.floats, 0, a.dim());
        System.arraycopy(b.floats, 0, concat.floats, a.dim(), b.dim());
        return concat;
    }

    public static float crossEntropy(FVector trueDist, FVector estDist) {
        int d = trueDist.dim();
        float[] trueFloats = trueDist.floats;
        float[] estFloats = estDist.floats;
        float result = 0.0f;
        for (int i = 0; i < d; ++i) {
            result = (float)(result - trueFloats[i] * Math.log(estFloats[i]));
        }
        return result;
    }

    public static FVector elementwiseMax(FVector a, FVector b) {
        int d = a.dim();
        float[] aFloats = a.floats;
        float[] bFloats = b.floats;
        float[] result = new float[d];
        for (int i = 0; i < d; ++i) {
            result[i] = Math.max(aFloats[i], bFloats[i]);
        }
        return FVector.wrap(result);
    }

    public static float klDivergence(FVector trueDist, FVector estDist) {
        int d = trueDist.dim();
        float[] trueFloats = trueDist.floats;
        float[] estFloats = estDist.floats;
        float result = 0.0f;
        for (int i = 0; i < d; ++i) {
            if (trueFloats[i] == 0.0f) continue;
            result = (float)(result - trueFloats[i] * Math.log(estFloats[i] / trueFloats[i]));
        }
        return result;
    }

    public static FVector mean(FVector[] vectors) {
        int d = vectors[0].dim();
        float[] means = new float[d];
        for (FVector vector : vectors) {
            float[] vals = vector.floats;
            for (int i = 0; i < d; ++i) {
                int n = i;
                means[n] = means[n] + vals[i];
            }
        }
        FVector meanVector = FVector.wrap(means);
        meanVector.mult(1.0f / vectors.length);
        return meanVector;
    }

    public static FVector mean(List<FVector> vectors) {
        int d = vectors.get(0).dim();
        float[] means = new float[d];
        for (FVector vector : vectors) {
            float[] vals = vector.floats;
            for (int i = 0; i < d; ++i) {
                int n = i;
                means[n] = means[n] + vals[i];
            }
        }
        FVector meanVector = FVector.wrap(means);
        meanVector.mult(1.0f / vectors.size());
        return meanVector;
    }

    public static FVector linspace(float start, float stop, int num, boolean endInclusive) {
        FVector result = new FVector(num);
        float step = endInclusive ? (stop - start) / (num - 1) : (stop - start) / num;
        for (int i = 0; i < num; ++i) {
            result.set(i, start + i * step);
        }
        return result;
    }

    public String toLine() {
        String result = "";
        for (int i = 0; i < this.floats.length; ++i) {
            result = result + this.floats[i];
            if (i >= this.floats.length - 1) continue;
            result = result + ",";
        }
        return result;
    }

    public String toString() {
        return String.format("[%s]", this.toLine());
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.floats);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FVector)) {
            return false;
        }
        FVector other = (FVector)obj;
        return Arrays.equals(this.floats, other.floats);
    }
}

