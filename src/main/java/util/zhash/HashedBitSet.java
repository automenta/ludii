// 
// Decompiled by Procyon v0.5.36
// 

package util.zhash;

import util.state.State;

import java.io.Serializable;
import java.util.BitSet;

public class HashedBitSet implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final BitSet internalState;
    private final long[] hashes;
    
    public HashedBitSet(final ZobristHashGenerator generator, final int numSites) {
        this.internalState = new BitSet(numSites);
        this.hashes = ZobristHashUtilities.getSequence(generator, numSites);
    }
    
    private HashedBitSet(final HashedBitSet that) {
        this.internalState = (BitSet)that.internalState.clone();
        this.hashes = that.hashes;
    }
    
    public void clear(final State trialState) {
        long hashDelta = 0L;
        for (int site = 0; site < this.hashes.length; ++site) {
            if (this.internalState.get(site)) {
                hashDelta ^= this.hashes[site];
            }
        }
        this.internalState.clear();
        trialState.updateStateHash(hashDelta);
    }
    
    public long calculateHashAfterRemap(final int[] siteRemap, final boolean invert) {
        long hashDelta = 0L;
        if (siteRemap == null) {
            for (int site = 0; site < this.hashes.length; ++site) {
                final boolean siteValue = this.internalState.get(site);
                final boolean newValue = invert == (!siteValue);
                if (newValue) {
                    hashDelta ^= this.hashes[site];
                }
            }
            return hashDelta;
        }
        for (int site = 0; site < this.hashes.length; ++site) {
            final int newSite = siteRemap[site];
            final boolean siteValue2 = this.internalState.get(site);
            final boolean newValue2 = invert == (!siteValue2);
            if (newValue2) {
                hashDelta ^= this.hashes[newSite];
            }
        }
        return hashDelta;
    }
    
    public void setTo(final State trialState, final HashedBitSet src) {
        long hashDelta = 0L;
        for (int site = 0; site < this.hashes.length; ++site) {
            if (this.internalState.get(site) != src.internalState.get(site)) {
                hashDelta ^= this.hashes[site];
            }
        }
        this.internalState.clear();
        this.internalState.or(src.internalState);
        trialState.updateStateHash(hashDelta);
    }
    
    public void set(final State trialState, final int bitIndex, final boolean on) {
        if (on != this.internalState.get(bitIndex)) {
            trialState.updateStateHash(this.hashes[bitIndex]);
        }
        this.internalState.set(bitIndex, on);
    }
    
    public HashedBitSet clone() {
        return new HashedBitSet(this);
    }
    
    public BitSet internalStateCopy() {
        return (BitSet)this.internalState.clone();
    }
    
    public boolean get(final int bitIndex) {
        return this.internalState.get(bitIndex);
    }
    
    public int nextSetBit(final int fromIndex) {
        return this.internalState.nextSetBit(fromIndex);
    }
}
