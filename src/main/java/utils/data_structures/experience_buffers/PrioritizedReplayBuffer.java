// 
// Decompiled by Procyon v0.5.36
// 

package utils.data_structures.experience_buffers;

import expert_iteration.ExItExperience;
import game.Game;
import game.equipment.container.Container;
import util.state.containerState.ContainerState;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class PrioritizedReplayBuffer implements Serializable, ExperienceBuffer
{
    private static final long serialVersionUID = 1L;
    protected final int replayCapacity;
    protected final SumTree sumTree;
    protected final ExItExperience[] buffer;
    protected long addCount;
    protected final double alpha;
    protected final double beta;
    
    public PrioritizedReplayBuffer(final int replayCapacity) {
        this(replayCapacity, 0.5, 0.5);
    }
    
    public PrioritizedReplayBuffer(final int replayCapacity, final double alpha, final double beta) {
        this.replayCapacity = replayCapacity;
        this.sumTree = new SumTree(replayCapacity);
        this.buffer = new ExItExperience[replayCapacity];
        this.addCount = 0L;
        this.alpha = alpha;
        this.beta = beta;
    }
    
    @Override
    public void add(final ExItExperience experience) {
        this.sumTree.set(this.cursor(), this.sumTree.maxRecordedPriority());
        this.buffer[this.cursor()] = experience;
        ++this.addCount;
    }
    
    public void add(final ExItExperience experience, final float priority) {
        this.sumTree.set(this.cursor(), (float)Math.pow(priority, this.alpha));
        this.buffer[this.cursor()] = experience;
        ++this.addCount;
    }
    
    public float[] getPriorities(final int[] indices) {
        final float[] priorities = new float[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            priorities[i] = this.sumTree.get(indices[i]);
        }
        return priorities;
    }
    
    public boolean isEmpty() {
        return this.addCount == 0L;
    }
    
    public boolean isFull() {
        return this.addCount >= this.replayCapacity;
    }
    
    public int size() {
        if (this.isFull()) {
            return this.replayCapacity;
        }
        return (int)this.addCount;
    }
    
    public int[] sampleIndexBatch(final int batchSize) {
        return this.sumTree.stratifiedSample(batchSize);
    }
    
    @Override
    public ExItExperience[] sampleExperienceBatch(final int batchSize) {
        final int numSamples = (int)Math.min(batchSize, this.addCount);
        final ExItExperience[] batch = new ExItExperience[numSamples];
        final int[] indices = this.sampleIndexBatch(numSamples);
        final double[] weights = new double[batchSize];
        double maxWeight = Double.NEGATIVE_INFINITY;
        final int maxIdx = Math.min(this.replayCapacity, (int)this.addCount) - 1;
        for (int i = 0; i < numSamples; ++i) {
            if (indices[i] > maxIdx) {
                indices[i] = maxIdx;
            }
        }
        final float[] priorities = this.getPriorities(indices);
        for (int j = 0; j < numSamples; ++j) {
            batch[j] = this.buffer[indices[j]];
            final double prob = priorities[j] / this.sumTree.totalPriority();
            weights[j] = Math.pow(1.0 / this.size() * (1.0 / prob), this.beta);
            maxWeight = Math.max(maxWeight, weights[j]);
        }
        for (int j = 0; j < numSamples; ++j) {
            batch[j].setWeightPER((float)(weights[j] / maxWeight));
            batch[j].setBufferIdx(indices[j]);
        }
        return batch;
    }
    
    @Override
    public ExItExperience[] sampleExperienceBatchUniformly(final int batchSize) {
        final int numSamples = (int)Math.min(batchSize, this.addCount);
        final ExItExperience[] batch = new ExItExperience[numSamples];
        final int bufferSize = this.size();
        for (int i = 0; i < numSamples; ++i) {
            batch[i] = this.buffer[ThreadLocalRandom.current().nextInt(bufferSize)];
        }
        return batch;
    }
    
    public void setPriorities(final int[] indices, final float[] priorities) {
        assert indices.length == priorities.length;
        for (int i = 0; i < indices.length; ++i) {
            this.sumTree.set(indices[i], (float)Math.pow(priorities[i], this.alpha));
        }
    }
    
    public SumTree sumTree() {
        return this.sumTree;
    }
    
    public double alpha() {
        return this.alpha;
    }
    
    public double beta() {
        return this.beta;
    }
    
    public long addCount() {
        return this.addCount;
    }
    
    public int cursor() {
        return (int)(this.addCount % this.replayCapacity);
    }
    
    public static PrioritizedReplayBuffer fromFile(final Game game, final String filepath) {
        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            final PrioritizedReplayBuffer buffer = (PrioritizedReplayBuffer)reader.readObject();
            for (final ExItExperience exp : buffer.buffer) {
                if (exp != null) {
                    final ExItExperience.ExItExperienceState state = exp.state();
                    final ContainerState[] containerStates2;
                    final ContainerState[] containerStates = containerStates2 = state.state().containerStates();
                    for (final ContainerState containerState : containerStates2) {
                        if (containerState != null) {
                            final String containerName = containerState.nameFromFile();
                            for (final Container container : game.equipment().containers()) {
                                if (container != null && container.name().equals(containerName)) {
                                    containerState.setContainer(container);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return buffer;
        }
        catch (IOException | ClassNotFoundException ex2) {
            ex2.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void writeToFile(final String filepath) {
        try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            out.writeObject(this);
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
