// 
// Decompiled by Procyon v0.5.36
// 

package optimisers;

import collections.FVector;

import java.io.Serializable;

public abstract class Optimiser implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final float baseStepSize;
    
    public Optimiser(final float baseStepSize) {
        this.baseStepSize = baseStepSize;
    }
    
    public abstract void maximiseObjective(final FVector p0, final FVector p1);
    
    public final void minimiseObjective(final FVector params, final FVector gradients) {
        final FVector negatedGrads = gradients.copy();
        negatedGrads.mult(-1.0f);
        this.maximiseObjective(params, negatedGrads);
    }
    
    public abstract void writeToFile(final String p0);
}
