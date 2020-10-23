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

public class UniformExperienceBuffer implements Serializable, ExperienceBuffer
{
    private static final long serialVersionUID = 1L;
    protected final int replayCapacity;
    protected final ExItExperience[] buffer;
    protected long addCount;
    
    public UniformExperienceBuffer(final int replayCapacity) {
        this.replayCapacity = replayCapacity;
        this.buffer = new ExItExperience[replayCapacity];
    }
    
    @Override
    public void add(final ExItExperience experience) {
        this.buffer[this.cursor()] = experience;
        ++this.addCount;
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
    
    @Override
    public ExItExperience[] sampleExperienceBatch(final int batchSize) {
        return this.sampleExperienceBatchUniformly(batchSize);
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
    
    private int cursor() {
        return (int)(this.addCount % this.replayCapacity);
    }
    
    public static UniformExperienceBuffer fromFile(final Game game, final String filepath) {
        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            final UniformExperienceBuffer buffer = (UniformExperienceBuffer)reader.readObject();
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
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
