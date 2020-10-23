// 
// Decompiled by Procyon v0.5.36
// 

package features;

import collections.ChunkSet;
import features.instances.Footprint;
import util.state.State;
import util.state.containerState.ContainerState;

import java.util.HashMap;
import java.util.Map;

public class ActiveFeaturesCache
{
    protected final ThreadLocal<Map<FeatureSet.ProactiveFeaturesKey, CachedDataFootprint>> threadLocalCache;
    
    public ActiveFeaturesCache() {
        this.threadLocalCache = ThreadLocal.withInitial(HashMap::new);
    }
    
    public void cache(final State state, final int from, final int to, final int[] activeFeaturesToCache, final int player) {
        final ContainerState container = state.containerStates()[0];
        final FeatureSet.ProactiveFeaturesKey key = new FeatureSet.ProactiveFeaturesKey(player, from, to);
        final Map<FeatureSet.ProactiveFeaturesKey, CachedDataFootprint> map = this.threadLocalCache.get();
        final CachedDataFootprint pair = map.get(key);
        final Footprint footprint = pair.footprint;
        ChunkSet maskedEmptyCells;
        if (container.emptyChunkSetCell() != null && footprint.emptyCell() != null) {
            maskedEmptyCells = container.emptyChunkSetCell().clone();
            maskedEmptyCells.and(footprint.emptyCell());
        }
        else {
            maskedEmptyCells = null;
        }
        ChunkSet maskedEmptyVertices;
        if (container.emptyChunkSetVertex() != null && footprint.emptyVertex() != null) {
            maskedEmptyVertices = container.emptyChunkSetVertex().clone();
            maskedEmptyVertices.and(footprint.emptyVertex());
        }
        else {
            maskedEmptyVertices = null;
        }
        ChunkSet maskedEmptyEdges;
        if (container.emptyChunkSetEdge() != null && footprint.emptyEdge() != null) {
            maskedEmptyEdges = container.emptyChunkSetEdge().clone();
            maskedEmptyEdges.and(footprint.emptyEdge());
        }
        else {
            maskedEmptyEdges = null;
        }
        final ChunkSet maskedWhoCells = container.cloneWhoCell();
        if (maskedWhoCells != null && footprint.whoCell() != null) {
            maskedWhoCells.and(footprint.whoCell());
        }
        final ChunkSet maskedWhoVertices = container.cloneWhoVertex();
        if (maskedWhoVertices != null && footprint.whoVertex() != null) {
            maskedWhoVertices.and(footprint.whoVertex());
        }
        final ChunkSet maskedWhoEdges = container.cloneWhoEdge();
        if (maskedWhoEdges != null && footprint.whoEdge() != null) {
            maskedWhoEdges.and(footprint.whoEdge());
        }
        final ChunkSet maskedWhatCells = container.cloneWhatCell();
        if (maskedWhatCells != null && footprint.whatCell() != null) {
            maskedWhatCells.and(footprint.whatCell());
        }
        final ChunkSet maskedWhatVertices = container.cloneWhatVertex();
        if (maskedWhatVertices != null && footprint.whatVertex() != null) {
            maskedWhatVertices.and(footprint.whatVertex());
        }
        final ChunkSet maskedWhatEdges = container.cloneWhatEdge();
        if (maskedWhatEdges != null && footprint.whatEdge() != null) {
            maskedWhatEdges.and(footprint.whatEdge());
        }
        final CachedData data = new CachedData(activeFeaturesToCache, maskedEmptyCells, maskedEmptyVertices, maskedEmptyEdges, maskedWhoCells, maskedWhoVertices, maskedWhoEdges, maskedWhatCells, maskedWhatVertices, maskedWhatEdges);
        map.put(key, new CachedDataFootprint(data, footprint));
    }
    
    public int[] getCachedActiveFeatures(final FeatureSet featureSet, final State state, final int from, final int to, final int player) {
        final FeatureSet.ProactiveFeaturesKey key = new FeatureSet.ProactiveFeaturesKey(player, from, to);
        final Map<FeatureSet.ProactiveFeaturesKey, CachedDataFootprint> map = this.threadLocalCache.get();
        final CachedDataFootprint pair = map.get(key);
        if (pair == null) {
            final Footprint footprint = featureSet.generateFootprint(state, from, to, player);
            map.put(key, new CachedDataFootprint(null, footprint));
        }
        else {
            final CachedData cachedData = pair.data;
            if (cachedData != null) {
                final ContainerState container = state.containerStates()[0];
                final Footprint footprint2 = pair.footprint;
                if (container.emptyChunkSetCell() != null && !container.emptyChunkSetCell().matches(footprint2.emptyCell(), cachedData.emptyStateCells)) {
                    return null;
                }
                if (container.emptyChunkSetVertex() != null && !container.emptyChunkSetVertex().matches(footprint2.emptyVertex(), cachedData.emptyStateVertices)) {
                    return null;
                }
                if (container.emptyChunkSetEdge() != null && !container.emptyChunkSetEdge().matches(footprint2.emptyEdge(), cachedData.emptyStateEdges)) {
                    return null;
                }
                if (footprint2.whoCell() != null && !container.matchesWhoCell(footprint2.whoCell(), cachedData.whoStateCells)) {
                    return null;
                }
                if (footprint2.whoVertex() != null && !container.matchesWhoVertex(footprint2.whoVertex(), cachedData.whoStateVertices)) {
                    return null;
                }
                if (footprint2.whoEdge() != null && !container.matchesWhoEdge(footprint2.whoEdge(), cachedData.whoStateEdges)) {
                    return null;
                }
                if (footprint2.whatCell() != null && !container.matchesWhatCell(footprint2.whatCell(), cachedData.whatStateCells)) {
                    return null;
                }
                if (footprint2.whatVertex() != null && !container.matchesWhatVertex(footprint2.whatVertex(), cachedData.whatStateVertices)) {
                    return null;
                }
                if (footprint2.whatEdge() != null && !container.matchesWhatEdge(footprint2.whatEdge(), cachedData.whatStateEdges)) {
                    return null;
                }
                return cachedData.activeFeatureIndices;
            }
        }
        return null;
    }
    
    private static class CachedData
    {
        public final int[] activeFeatureIndices;
        public final ChunkSet emptyStateCells;
        public final ChunkSet emptyStateVertices;
        public final ChunkSet emptyStateEdges;
        public final ChunkSet whoStateCells;
        public final ChunkSet whoStateVertices;
        public final ChunkSet whoStateEdges;
        public final ChunkSet whatStateCells;
        public final ChunkSet whatStateVertices;
        public final ChunkSet whatStateEdges;
        
        public CachedData(final int[] activeFeatureIndices, final ChunkSet emptyStateCells, final ChunkSet emptyStateVertices, final ChunkSet emptyStateEdges, final ChunkSet whoStateCells, final ChunkSet whoStateVertices, final ChunkSet whoStateEdges, final ChunkSet whatStateCells, final ChunkSet whatStateVertices, final ChunkSet whatStateEdges) {
            this.activeFeatureIndices = activeFeatureIndices;
            this.emptyStateCells = emptyStateCells;
            this.emptyStateVertices = emptyStateEdges;
            this.emptyStateEdges = emptyStateEdges;
            this.whoStateCells = whoStateCells;
            this.whoStateVertices = whoStateVertices;
            this.whoStateEdges = whoStateEdges;
            this.whatStateCells = whatStateCells;
            this.whatStateVertices = whatStateVertices;
            this.whatStateEdges = whatStateEdges;
        }
    }
    
    private static class CachedDataFootprint
    {
        public final CachedData data;
        public final Footprint footprint;
        
        public CachedDataFootprint(final CachedData data, final Footprint footprint) {
            this.data = data;
            this.footprint = footprint;
        }
    }
}
