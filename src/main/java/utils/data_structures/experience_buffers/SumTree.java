// 
// Decompiled by Procyon v0.5.36
// 

package utils.data_structures.experience_buffers;

import collections.FVector;
import math.BitTwiddling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SumTree implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final List<FVector> nodes;
    protected float maxRecordedPriority;
    
    public SumTree(final int capacity) {
        assert capacity > 0;
        this.nodes = new ArrayList<>();
        final int treeDepth = BitTwiddling.log2RoundUp(capacity);
        int levelSize = 1;
        for (int i = 0; i < treeDepth + 1; ++i) {
            final FVector nodesAtThisDepth = new FVector(levelSize);
            this.nodes.add(nodesAtThisDepth);
            levelSize *= 2;
        }
        assert this.nodes.get(this.nodes.size() - 1).dim() == BitTwiddling.nextPowerOf2(capacity);
        this.maxRecordedPriority = 1.0f;
    }
    
    public int sample() {
        return this.sample(ThreadLocalRandom.current().nextDouble());
    }
    
    public int sample(final double inQueryValue) {
        assert this.totalPriority() != 0.0f;
        assert inQueryValue >= 0.0;
        assert inQueryValue <= 1.0;
        double queryValue = inQueryValue * this.totalPriority();
        int nodeIdx = 0;
        for (int i = 1; i < this.nodes.size(); ++i) {
            final FVector nodesAtThisDepth = this.nodes.get(i);
            final int leftChild = nodeIdx * 2;
            final float leftSum = nodesAtThisDepth.get(leftChild);
            if (queryValue < leftSum) {
                nodeIdx = leftChild;
            }
            else {
                nodeIdx = leftChild + 1;
                queryValue -= leftSum;
            }
        }
        return nodeIdx;
    }
    
    public int[] stratifiedSample(final int batchSize) {
        assert this.totalPriority() != 0.0;
        final FVector bounds = FVector.linspace(0.0f, 1.0f, batchSize + 1, true);
        assert bounds.dim() == batchSize + 1;
        final int[] result = new int[batchSize];
        for (int i = 0; i < batchSize; ++i) {
            final float segmentStart = bounds.get(i);
            final float segmentEnd = bounds.get(i + 1);
            final double queryVal = ThreadLocalRandom.current().nextDouble(segmentStart, segmentEnd);
            result[i] = this.sample(queryVal);
        }
        return result;
    }
    
    public float get(final int nodeIdx) {
        return this.nodes.get(this.nodes.size() - 1).get(nodeIdx);
    }
    
    public void set(final int inNodeIdx, final float value) {
        assert value >= 0.0f;
        int nodeIdx = inNodeIdx;
        this.maxRecordedPriority = Math.max(this.maxRecordedPriority, value);
        final float deltaValue = value - this.get(nodeIdx);
        for (int i = this.nodes.size() - 1; i >= 0; --i) {
            final FVector nodesAtThisDepth = this.nodes.get(i);
            nodesAtThisDepth.addToEntry(nodeIdx, deltaValue);
            nodeIdx /= 2;
        }
        assert nodeIdx == 0;
    }
    
    public float maxRecordedPriority() {
        return this.maxRecordedPriority;
    }
    
    public float totalPriority() {
        return this.nodes.get(0).get(0);
    }
}
