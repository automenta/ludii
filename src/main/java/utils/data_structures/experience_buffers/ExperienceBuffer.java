// 
// Decompiled by Procyon v0.5.36
// 

package utils.data_structures.experience_buffers;

import expert_iteration.ExItExperience;

public interface ExperienceBuffer
{
    void add(final ExItExperience p0);
    
    ExItExperience[] sampleExperienceBatch(final int p0);
    
    ExItExperience[] sampleExperienceBatchUniformly(final int p0);
    
    void writeToFile(final String p0);
}
