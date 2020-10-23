// 
// Decompiled by Procyon v0.5.36
// 

package util.zhash;

import collections.ChunkSet;
import math.BitTwiddling;
import util.state.State;

import java.io.Serializable;

public class HashedChunkSet implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final ChunkSet internalState;
    private final long[][] hashes;
    
    public HashedChunkSet(final ZobristHashGenerator generator, final int maxChunkVal, final int numChunks) {
        this.internalState = new ChunkSet(BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(maxChunkVal)), numChunks);
        this.hashes = ZobristHashUtilities.getSequence(generator, numChunks, maxChunkVal + 1);
    }
    
    public HashedChunkSet(final long[][] hashes, final int maxChunkVal, final int numSites) {
        this.internalState = new ChunkSet(BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(maxChunkVal)), numSites);
        this.hashes = hashes;
    }
    
    private HashedChunkSet(final HashedChunkSet that) {
        this.internalState = that.internalState.clone();
        this.hashes = that.hashes;
    }
    
    public void clear(final State trialState) {
        long hashDelta = 0L;
        for (int site = 0; site < this.hashes.length; ++site) {
            hashDelta ^= this.hashes[site][this.internalState.getChunk(site)];
        }
        this.internalState.clear();
        trialState.updateStateHash(hashDelta);
    }
    
    public long calculateHashAfterRemap(final int[] siteRemap, final int[] valueRemap) {
        long hashDelta = 0L;
        if (valueRemap == null) {
            for (int site = 0; site < this.hashes.length && site < siteRemap.length; ++site) {
                final int newSite = siteRemap[site];
                final int siteValue = this.internalState.getChunk(site);
                hashDelta ^= (this.hashes[newSite][siteValue] ^ this.hashes[newSite][0]);
            }
            return hashDelta;
        }
        if (siteRemap == null) {
            for (int site = 0; site < this.hashes.length; ++site) {
                final int siteValue2 = this.internalState.getChunk(site);
                final int newValue = valueRemap[siteValue2];
                hashDelta ^= (this.hashes[site][newValue] ^ this.hashes[site][0]);
            }
            return hashDelta;
        }
        for (int site = 0; site < this.hashes.length && site < siteRemap.length; ++site) {
            final int siteValue2 = this.internalState.getChunk(site);
            final int newValue = valueRemap[siteValue2];
            final int newSite2 = siteRemap[site];
            hashDelta ^= (this.hashes[newSite2][newValue] ^ this.hashes[newSite2][0]);
        }
        return hashDelta;
    }
    
    public void setTo(final State trialState, final HashedChunkSet src) {
        long hashDelta = 0L;
        for (int site = 0; site < this.hashes.length; ++site) {
            hashDelta ^= (this.hashes[site][this.internalState.getChunk(site)] ^ this.hashes[site][src.internalState.getChunk(site)]);
        }
        this.internalState.clear();
        this.internalState.or(src.internalState);
        trialState.updateStateHash(hashDelta);
    }
    
    public void setBit(final State trialState, final int chunk, final int bit, final boolean value) {
        long hashDelta = this.hashes[chunk][this.internalState.getChunk(chunk)];
        this.internalState.setBit(chunk, bit, value);
        hashDelta ^= this.hashes[chunk][this.internalState.getChunk(chunk)];
        trialState.updateStateHash(hashDelta);
    }
    
    public void setChunk(final State trialState, final int site, final int val) {
        long hashDelta = this.hashes[site][this.internalState.getChunk(site)];
        this.internalState.setChunk(site, val);
        hashDelta ^= this.hashes[site][val];
        trialState.updateStateHash(hashDelta);
    }
    
    public HashedChunkSet clone() {
        return new HashedChunkSet(this);
    }
    
    public ChunkSet internalStateCopy() {
        return this.internalState.clone();
    }
    
    public int getBit(final int site, final int location) {
        return this.internalState.getBit(site, location);
    }
    
    public int getChunk(final int site) {
        return this.internalState.getChunk(site);
    }
    
    public int numChunks() {
        return this.internalState.numChunks();
    }
    
    public boolean matches(final ChunkSet mask, final ChunkSet pattern) {
        return this.internalState.matches(mask, pattern);
    }
    
    public int chunkSize() {
        return this.internalState.chunkSize();
    }
    
    public boolean violatesNot(final ChunkSet mask, final ChunkSet pattern) {
        return this.internalState.violatesNot(mask, pattern);
    }
    
    public boolean violatesNot(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return this.internalState.violatesNot(mask, pattern, startWord);
    }
}
