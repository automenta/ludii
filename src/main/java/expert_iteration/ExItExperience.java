// 
// Decompiled by Procyon v0.5.36
// 

package expert_iteration;

import main.collections.FVector;
import main.collections.FastArrayList;
import util.Context;
import util.Move;
import util.state.State;

import java.io.Serializable;

public class ExItExperience implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final ExItExperienceState state;
    protected final FastArrayList<Move> moves;
    protected final FVector expertDistribution;
    protected final FVector expertValueEstimates;
    protected FVector stateFeatureVector;
    protected int episodeDuration;
    protected double[] playerOutcomes;
    protected float weightPER;
    protected float weightCEExplore;
    protected int bufferIdx;
    
    public ExItExperience(final ExItExperienceState state, final FastArrayList<Move> moves, final FVector expertDistribution, final FVector expertValueEstimates) {
        this.episodeDuration = -1;
        this.playerOutcomes = null;
        this.weightPER = -1.0f;
        this.weightCEExplore = -1.0f;
        this.bufferIdx = -1;
        this.state = state;
        this.moves = moves;
        this.expertDistribution = expertDistribution;
        this.expertValueEstimates = expertValueEstimates;
    }
    
    public ExItExperienceState state() {
        return this.state;
    }
    
    public FastArrayList<Move> moves() {
        return this.moves;
    }
    
    public int bufferIdx() {
        return this.bufferIdx;
    }
    
    public FVector expertDistribution() {
        return this.expertDistribution;
    }
    
    public FVector expertValueEstimates() {
        return this.expertValueEstimates;
    }
    
    public int episodeDuration() {
        return this.episodeDuration;
    }
    
    public double[] playerOutcomes() {
        return this.playerOutcomes;
    }
    
    public void setBufferIdx(final int bufferIdx) {
        this.bufferIdx = bufferIdx;
    }
    
    public void setEpisodeDuration(final int episodeDuration) {
        this.episodeDuration = episodeDuration;
    }
    
    public void setPlayerOutcomes(final double[] playerOutcomes) {
        this.playerOutcomes = playerOutcomes;
    }
    
    public void setStateFeatureVector(final FVector vector) {
        this.stateFeatureVector = vector;
    }
    
    public void setWeightCEExplore(final float weightCEExplore) {
        this.weightCEExplore = weightCEExplore;
    }
    
    public void setWeightPER(final float weightPER) {
        this.weightPER = weightPER;
    }
    
    public FVector stateFeatureVector() {
        return this.stateFeatureVector;
    }
    
    public float weightCEExplore() {
        return this.weightCEExplore;
    }
    
    public float weightPER() {
        return this.weightPER;
    }
    
    public static final class ExItExperienceState implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final State state;
        private final Move lastDecisionMove;
        
        public ExItExperienceState(final Context context) {
            this.state = context.state();
            this.lastDecisionMove = context.trial().lastMove();
        }
        
        public State state() {
            return this.state;
        }
        
        public Move lastDecisionMove() {
            return this.lastDecisionMove;
        }
    }
}
