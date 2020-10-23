// 
// Decompiled by Procyon v0.5.36
// 

package features;

import features.features.Feature;
import features.instances.*;
import game.Game;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import main.collections.ChunkSet;
import main.collections.FVector;
import main.collections.FastArrayList;
import util.Context;
import util.FeatureSetInterface;
import util.Move;
import util.Trial;
import util.state.State;
import util.state.containerState.ContainerState;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FeatureSet extends FeatureSetInterface
{
    protected final Feature[] features;
    protected HashMap<ReactiveFeaturesKey, FastFeatureInstanceNode[]> reactiveInstances;
    protected HashMap<ProactiveFeaturesKey, FastFeatureInstanceNode[]> proactiveInstances;
    protected HashMap<ReactiveFeaturesKey, FastFeaturesNode[]> reactiveFeatures;
    protected HashMap<ProactiveFeaturesKey, FastFeaturesNode[]> proactiveFeatures;
    public static final float FEATURE_WEIGHT_THRESHOLD = 0.001f;
    protected HashMap<ReactiveFeaturesKey, FastFeaturesNode[]> reactiveFeaturesThresholded;
    protected HashMap<ProactiveFeaturesKey, FastFeaturesNode[]> proactiveFeaturesThresholded;
    protected Game game;
    protected FVector featureInitWeights;
    protected ActiveFeaturesCache activeProactiveFeaturesCache;
    
    public FeatureSet(final List<Feature> features) {
        this.game = null;
        this.featureInitWeights = null;
        this.features = new Feature[features.size()];
        for (int i = 0; i < this.features.length; ++i) {
            (this.features[i] = features.get(i)).setFeatureSetIndex(i);
        }
        this.reactiveInstances = null;
        this.proactiveInstances = null;
        this.reactiveFeatures = null;
        this.proactiveFeatures = null;
    }
    
    public FeatureSet(final String filename) {
        this.game = null;
        this.featureInitWeights = null;
        Feature[] tempFeatures;
        try (final Stream<String> stream = Files.lines(Paths.get(filename))) {
            tempFeatures = stream.map(s -> Feature.fromString(s)).toArray(Feature[]::new);
        }
        catch (IOException exception) {
            tempFeatures = null;
        }
        this.features = tempFeatures;
        for (int i = 0; i < this.features.length; ++i) {
            this.features[i].setFeatureSetIndex(i);
        }
    }
    
    public Feature[] features() {
        return this.features;
    }
    
    public int getNumFeatures() {
        return this.features.length;
    }
    
    public void instantiateFeatures(final Game newGame, final int[] supportedPlayers, final FVector weights) {
        this.game = newGame;
        if (weights == null) {
            this.featureInitWeights = null;
        }
        else {
            this.featureInitWeights = new FVector(weights);
        }
        this.activeProactiveFeaturesCache = new ActiveFeaturesCache();
        final Map<ReactiveFeaturesKey, List<FeatureInstanceNode>> reactiveInstancesWIP = new HashMap<>();
        final Map<ProactiveFeaturesKey, List<FeatureInstanceNode>> proactiveInstancesWIP = new HashMap<>();
        final Context featureGenContext = new Context(this.game, new Trial(this.game));
        for (int i = 0; i < supportedPlayers.length; ++i) {
            final int player = supportedPlayers[i];
            for (final Feature feature : this.features) {
                final List<FeatureInstance> newInstances = feature.instantiateFeature(this.game, featureGenContext.state().containerStates()[0], player, -1, -1);
                for (final FeatureInstance instance : newInstances) {
                    final int lastFrom = instance.lastFrom();
                    final int lastTo = instance.lastTo();
                    final int from = instance.from();
                    final int to = instance.to();
                    if (lastFrom >= 0 || lastTo >= 0) {
                        final ReactiveFeaturesKey key = new ReactiveFeaturesKey(player, lastFrom, lastTo, from, to);
                        List<FeatureInstanceNode> instanceNodes = reactiveInstancesWIP.get(key);
                        if (instanceNodes == null) {
                            instanceNodes = new ArrayList<>(1);
                            reactiveInstancesWIP.put(key, instanceNodes);
                        }
                        insertInstanceInForest(instance, instanceNodes);
                    }
                    else {
                        final ProactiveFeaturesKey key2 = new ProactiveFeaturesKey(player, from, to);
                        List<FeatureInstanceNode> instanceNodes = proactiveInstancesWIP.get(key2);
                        if (instanceNodes == null) {
                            instanceNodes = new ArrayList<>(1);
                            proactiveInstancesWIP.put(key2, instanceNodes);
                        }
                        insertInstanceInForest(instance, instanceNodes);
                    }
                }
            }
        }
        simplifyInstanceForests(reactiveInstancesWIP, proactiveInstancesWIP);
        this.reactiveInstances = new HashMap<>((int) Math.ceil(reactiveInstancesWIP.size() / 0.75f), 0.75f);
        for (final Map.Entry<ReactiveFeaturesKey, List<FeatureInstanceNode>> entry : reactiveInstancesWIP.entrySet()) {
            final FastFeatureInstanceNode[] roots = new FastFeatureInstanceNode[entry.getValue().size()];
            for (int j = 0; j < roots.length; ++j) {
                roots[j] = new FastFeatureInstanceNode(entry.getValue().get(j));
            }
            this.reactiveInstances.put(entry.getKey(), roots);
        }
        this.proactiveInstances = new HashMap<>((int) Math.ceil(proactiveInstancesWIP.size() / 0.75f), 0.75f);
        for (final Map.Entry<ProactiveFeaturesKey, List<FeatureInstanceNode>> entry2 : proactiveInstancesWIP.entrySet()) {
            final FastFeatureInstanceNode[] roots = new FastFeatureInstanceNode[entry2.getValue().size()];
            for (int j = 0; j < roots.length; ++j) {
                roots[j] = new FastFeatureInstanceNode(entry2.getValue().get(j));
            }
            this.proactiveInstances.put(entry2.getKey(), roots);
        }
        this.reactiveFeatures = new HashMap<>((int) Math.ceil(this.reactiveInstances.size() / 0.75f), 0.75f);
        for (final Map.Entry<ReactiveFeaturesKey, FastFeatureInstanceNode[]> entry3 : this.reactiveInstances.entrySet()) {
            final FastFeaturesNode[] roots2 = new FastFeaturesNode[entry3.getValue().length];
            for (int j = 0; j < roots2.length; ++j) {
                roots2[j] = new FastFeaturesNode(entry3.getValue()[j]);
            }
            this.reactiveFeatures.put(entry3.getKey(), roots2);
        }
        this.proactiveFeatures = new HashMap<>((int) Math.ceil(this.proactiveInstances.size() / 0.75f), 0.75f);
        for (final Map.Entry<ProactiveFeaturesKey, FastFeatureInstanceNode[]> entry4 : this.proactiveInstances.entrySet()) {
            final FastFeaturesNode[] roots2 = new FastFeaturesNode[entry4.getValue().length];
            for (int j = 0; j < roots2.length; ++j) {
                roots2[j] = new FastFeaturesNode(entry4.getValue()[j]);
            }
            this.proactiveFeatures.put(entry4.getKey(), roots2);
        }
        this.reactiveFeaturesThresholded = new HashMap<>((int) Math.ceil(this.reactiveFeatures.size() / 0.75f), 0.75f);
        for (final Map.Entry<ReactiveFeaturesKey, FastFeaturesNode[]> entry5 : this.reactiveFeatures.entrySet()) {
            final List<FastFeaturesNode> roots3 = new ArrayList<>(entry5.getValue().length);
            for (final FastFeaturesNode node : entry5.getValue()) {
                final FastFeaturesNode optimisedNode = FastFeaturesNode.thresholdedNode(node, weights);
                if (optimisedNode != null) {
                    roots3.add(optimisedNode);
                }
            }
            this.reactiveFeaturesThresholded.put(entry5.getKey(), roots3.toArray(new FastFeaturesNode[0]));
        }
        this.proactiveFeaturesThresholded = new HashMap<>((int) Math.ceil(this.proactiveFeatures.size() / 0.75f), 0.75f);
        for (final Map.Entry<ProactiveFeaturesKey, FastFeaturesNode[]> entry6 : this.proactiveFeatures.entrySet()) {
            final List<FastFeaturesNode> roots3 = new ArrayList<>(entry6.getValue().length);
            for (final FastFeaturesNode node : entry6.getValue()) {
                final FastFeaturesNode optimisedNode = FastFeaturesNode.thresholdedNode(node, weights);
                if (optimisedNode != null) {
                    roots3.add(optimisedNode);
                }
            }
            this.proactiveFeaturesThresholded.put(entry6.getKey(), roots3.toArray(new FastFeaturesNode[0]));
        }
    }
    
    @Override
    public TIntArrayList getActiveFeatureIndices(final State state, final int lastFrom, final int lastTo, final int from, final int to, final int player, final boolean thresholded) {
        final boolean[] featuresActive = new boolean[this.features.length];
        TIntArrayList activeFeatureIndices;
        if (this.proactiveFeatures.size() > 0) {
            int[] cachedActiveFeatureIndices;
            if (thresholded) {
                cachedActiveFeatureIndices = this.activeProactiveFeaturesCache.getCachedActiveFeatures(this, state, from, to, player);
            }
            else {
                cachedActiveFeatureIndices = null;
            }
            if (cachedActiveFeatureIndices != null) {
                activeFeatureIndices = new TIntArrayList(cachedActiveFeatureIndices);
            }
            else {
                activeFeatureIndices = new TIntArrayList();
                final List<FastFeaturesNode[]> featuresNodesToCheck = this.getFeaturesNodesToCheckProactive(state, from, to, thresholded);
                for (int i = 0; i < featuresNodesToCheck.size(); ++i) {
                    final FastFeaturesNode[] nodesArray = featuresNodesToCheck.get(i);
                    for (int j = 0; j < nodesArray.length; ++j) {
                        final FastFeaturesNode node = nodesArray[j];
                        final BitwiseTest test = node.test;
                        if (test.matches(state)) {
                            final int[] featureIndices = node.activeFeatureIndices;
                            for (int idx = 0; idx < featureIndices.length; ++idx) {
                                featuresActive[featureIndices[idx]] = true;
                            }
                            featuresNodesToCheck.add(node.children);
                        }
                    }
                }
                for (int i = 0; i < featuresActive.length; ++i) {
                    if (featuresActive[i]) {
                        activeFeatureIndices.add(i);
                    }
                }
                if (thresholded) {
                    this.activeProactiveFeaturesCache.cache(state, from, to, activeFeatureIndices.toArray(), player);
                }
                Arrays.fill(featuresActive, false);
            }
        }
        else {
            activeFeatureIndices = new TIntArrayList();
        }
        final List<FastFeaturesNode[]> featuresNodesToCheck2 = this.getFeaturesNodesToCheckReactive(state, lastFrom, lastTo, from, to, thresholded);
        for (int k = 0; k < featuresNodesToCheck2.size(); ++k) {
            final FastFeaturesNode[] nodesArray2 = featuresNodesToCheck2.get(k);
            for (int l = 0; l < nodesArray2.length; ++l) {
                final FastFeaturesNode node2 = nodesArray2[l];
                final BitwiseTest test2 = node2.test;
                if (test2.matches(state)) {
                    final int[] featureIndices2 = node2.activeFeatureIndices;
                    for (int idx2 = 0; idx2 < featureIndices2.length; ++idx2) {
                        featuresActive[featureIndices2[idx2]] = true;
                    }
                    featuresNodesToCheck2.add(node2.children);
                }
            }
        }
        for (int k = 0; k < featuresActive.length; ++k) {
            if (featuresActive[k]) {
                activeFeatureIndices.add(k);
            }
        }
        return activeFeatureIndices;
    }
    
    public List<FeatureInstance> getActiveFeatureInstances(final State state, final int lastFrom, final int lastTo, final int from, final int to, final int player) {
        final List<FeatureInstance> activeInstances = new ArrayList<>();
        final List<FastFeatureInstanceNode[]> instanceNodesToCheck = this.getInstanceNodesToCheck(state, lastFrom, lastTo, from, to, player);
        for (int i = 0; i < instanceNodesToCheck.size(); ++i) {
            final FastFeatureInstanceNode[] nodesArray = instanceNodesToCheck.get(i);
            for (int j = 0; j < nodesArray.length; ++j) {
                final FeatureInstance instance = nodesArray[j].featureInstance;
                if (instance.matches(state)) {
                    activeInstances.add(instance);
                    instanceNodesToCheck.add(nodesArray[j].children);
                }
            }
        }
        return activeInstances;
    }
    
    public List<Feature> getActiveFeatures(final Context context, final int lastFrom, final int lastTo, final int from, final int to, final int player, final boolean thresholded) {
        final TIntArrayList activeFeatureIndices = this.getActiveFeatureIndices(context.state(), lastFrom, lastTo, from, to, player, thresholded);
        final List<Feature> activeFeatures = new ArrayList<>(activeFeatureIndices.size());
        final TIntIterator it = activeFeatureIndices.iterator();
        while (it.hasNext()) {
            activeFeatures.add(this.features[it.next()]);
        }
        return activeFeatures;
    }
    
    @Override
    public float computeLogitFastReturn(final State state, final int lastFrom, final int lastTo, final int from, final int to, final float autoPlayThreshold, final FVector weightVector, final int player, final boolean thresholded) {
        float logit = 0.0f;
        if (this.proactiveFeatures.size() > 0) {
            int[] cachedActiveFeatureIndices;
            if (thresholded) {
                cachedActiveFeatureIndices = this.activeProactiveFeaturesCache.getCachedActiveFeatures(this, state, from, to, player);
            }
            else {
                cachedActiveFeatureIndices = null;
            }
            if (cachedActiveFeatureIndices != null) {
                final TIntArrayList activeFeatureIndices = new TIntArrayList(cachedActiveFeatureIndices);
                for (int i = 0; i < activeFeatureIndices.size(); ++i) {
                    final int activeFeature = activeFeatureIndices.getQuick(i);
                    logit += weightVector.get(activeFeature);
                }
                if (logit >= autoPlayThreshold) {
                    return logit;
                }
            }
            else {
                final TIntArrayList activeFeatureIndices = new TIntArrayList();
                final boolean[] featuresActive = new boolean[this.features.length];
                final List<FastFeaturesNode[]> featuresNodesToCheck = this.getFeaturesNodesToCheckProactive(state, from, to, thresholded);
                for (int j = 0; j < featuresNodesToCheck.size(); ++j) {
                    final FastFeaturesNode[] nodesArray = featuresNodesToCheck.get(j);
                    for (int k = 0; k < nodesArray.length; ++k) {
                        final FastFeaturesNode node = nodesArray[k];
                        final BitwiseTest test = node.test;
                        if (test.matches(state)) {
                            final int[] featureIndices = node.activeFeatureIndices;
                            for (int idx = 0; idx < featureIndices.length; ++idx) {
                                logit += weightVector.get(featureIndices[idx]);
                                featuresActive[featureIndices[idx]] = true;
                            }
                            if (logit >= autoPlayThreshold) {
                                return logit;
                            }
                            featuresNodesToCheck.add(node.children);
                        }
                    }
                }
                if (thresholded) {
                    for (int j = 0; j < featuresActive.length; ++j) {
                        if (featuresActive[j]) {
                            activeFeatureIndices.add(j);
                        }
                    }
                    this.activeProactiveFeaturesCache.cache(state, from, to, activeFeatureIndices.toArray(), player);
                }
            }
        }
        final List<FastFeaturesNode[]> featuresNodesToCheck2 = this.getFeaturesNodesToCheckReactive(state, lastFrom, lastTo, from, to, thresholded);
        for (int l = 0; l < featuresNodesToCheck2.size(); ++l) {
            final FastFeaturesNode[] nodesArray2 = featuresNodesToCheck2.get(l);
            for (int m = 0; m < nodesArray2.length; ++m) {
                final FastFeaturesNode node2 = nodesArray2[m];
                final BitwiseTest test2 = node2.test;
                if (test2.matches(state)) {
                    final int[] featureIndices2 = node2.activeFeatureIndices;
                    for (int idx2 = 0; idx2 < featureIndices2.length; ++idx2) {
                        logit += weightVector.get(featureIndices2[idx2]);
                    }
                    if (logit >= autoPlayThreshold) {
                        return logit;
                    }
                    featuresNodesToCheck2.add(node2.children);
                }
            }
        }
        return logit;
    }
    
    private List<FastFeatureInstanceNode[]> getInstanceNodesToCheck(final State state, final int lastFrom, final int lastTo, final int from, final int to, final int player) {
        final List<FastFeatureInstanceNode[]> instanceNodesToCheck = new ArrayList<>();
        final int[] froms = (from >= 0) ? new int[] { -1, from } : new int[] { -1 };
        final int[] tos = (to >= 0) ? new int[] { -1, to } : new int[] { -1 };
        final int[] lastFroms = (lastFrom >= 0) ? new int[] { -1, lastFrom } : new int[] { -1 };
        final int[] lastTos = (lastTo >= 0) ? new int[] { -1, lastTo } : new int[] { -1 };
        if (lastFrom >= 0 || lastTo >= 0) {
            for (int i = 0; i < lastFroms.length; ++i) {
                final int lastFromPos = lastFroms[i];
                for (int j = 0; j < lastTos.length; ++j) {
                    final int lastToPos = lastTos[j];
                    for (int k = 0; k < froms.length; ++k) {
                        final int fromPos = froms[k];
                        for (int l = 0; l < tos.length; ++l) {
                            final int toPos = tos[l];
                            if (lastToPos >= 0 || lastFromPos >= 0) {
                                final FastFeatureInstanceNode[] nodes = this.reactiveInstances.get(new ReactiveFeaturesKey(player, lastFromPos, lastToPos, fromPos, toPos));
                                if (nodes != null) {
                                    instanceNodesToCheck.add(nodes);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int m = 0; m < froms.length; ++m) {
            final int fromPos2 = froms[m];
            for (int l2 = 0; l2 < tos.length; ++l2) {
                final int toPos2 = tos[l2];
                if (toPos2 >= 0 || fromPos2 >= 0) {
                    final FastFeatureInstanceNode[] nodes2 = this.proactiveInstances.get(new ProactiveFeaturesKey(player, fromPos2, toPos2));
                    if (nodes2 != null) {
                        instanceNodesToCheck.add(nodes2);
                    }
                }
            }
        }
        return instanceNodesToCheck;
    }
    
    private List<FastFeaturesNode[]> getFeaturesNodesToCheckProactive(final State state, final int from, final int to, final boolean thresholded) {
        final List<FastFeaturesNode[]> featuresNodesToCheck = new ArrayList<>();
        final int mover = state.mover();
        final int[] froms = (from >= 0) ? new int[] { -1, from } : new int[] { -1 };
        final int[] tos = (to >= 0) ? new int[] { -1, to } : new int[] { -1 };
        HashMap<ProactiveFeaturesKey, FastFeaturesNode[]> featuresMap;
        if (thresholded) {
            featuresMap = this.proactiveFeaturesThresholded;
        }
        else {
            featuresMap = this.proactiveFeatures;
        }
        for (int k = 0; k < froms.length; ++k) {
            final int fromPos = froms[k];
            for (int l = 0; l < tos.length; ++l) {
                final int toPos = tos[l];
                if (toPos >= 0 || fromPos >= 0) {
                    final FastFeaturesNode[] nodes = featuresMap.get(new ProactiveFeaturesKey(mover, fromPos, toPos));
                    if (nodes != null) {
                        featuresNodesToCheck.add(nodes);
                    }
                }
            }
        }
        return featuresNodesToCheck;
    }
    
    private List<FastFeaturesNode[]> getFeaturesNodesToCheckReactive(final State state, final int lastFrom, final int lastTo, final int from, final int to, final boolean thresholded) {
        final List<FastFeaturesNode[]> featuresNodesToCheck = new ArrayList<>();
        if (this.reactiveFeatures.isEmpty()) {
            return featuresNodesToCheck;
        }
        HashMap<ReactiveFeaturesKey, FastFeaturesNode[]> featuresMap;
        if (thresholded) {
            featuresMap = this.reactiveFeaturesThresholded;
        }
        else {
            featuresMap = this.reactiveFeatures;
        }
        final int mover = state.mover();
        if (from >= 0) {
            if (to >= 0) {
                if (lastFrom >= 0) {
                    if (lastTo >= 0) {
                        addFeaturesNodes(mover, lastFrom, lastTo, from, to, featuresMap, featuresNodesToCheck);
                        addFeaturesNodes(mover, lastFrom, lastTo, -1, to, featuresMap, featuresNodesToCheck);
                        addFeaturesNodes(mover, lastFrom, lastTo, from, -1, featuresMap, featuresNodesToCheck);
                        addFeaturesNodes(mover, -1, lastTo, from, to, featuresMap, featuresNodesToCheck);
                        addFeaturesNodes(mover, -1, lastTo, -1, to, featuresMap, featuresNodesToCheck);
                        addFeaturesNodes(mover, -1, lastTo, from, -1, featuresMap, featuresNodesToCheck);
                    }
                    addFeaturesNodes(mover, lastFrom, -1, from, to, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, lastFrom, -1, -1, to, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, lastFrom, -1, from, -1, featuresMap, featuresNodesToCheck);
                }
                else if (lastTo >= 0) {
                    addFeaturesNodes(mover, -1, lastTo, from, to, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, -1, lastTo, -1, to, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, -1, lastTo, from, -1, featuresMap, featuresNodesToCheck);
                }
            }
            else if (lastFrom >= 0) {
                if (lastTo >= 0) {
                    addFeaturesNodes(mover, lastFrom, lastTo, from, -1, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, -1, lastTo, from, -1, featuresMap, featuresNodesToCheck);
                }
                addFeaturesNodes(mover, lastFrom, -1, from, -1, featuresMap, featuresNodesToCheck);
            }
            else if (lastTo >= 0) {
                addFeaturesNodes(mover, -1, lastTo, from, -1, featuresMap, featuresNodesToCheck);
            }
        }
        else if (to >= 0) {
            if (lastFrom >= 0) {
                if (lastTo >= 0) {
                    addFeaturesNodes(mover, lastFrom, lastTo, -1, to, featuresMap, featuresNodesToCheck);
                    addFeaturesNodes(mover, -1, lastTo, -1, to, featuresMap, featuresNodesToCheck);
                }
                addFeaturesNodes(mover, lastFrom, -1, -1, to, featuresMap, featuresNodesToCheck);
            }
            else if (lastTo >= 0) {
                addFeaturesNodes(mover, -1, lastTo, -1, to, featuresMap, featuresNodesToCheck);
            }
        }
        return featuresNodesToCheck;
    }
    
    private static void addFeaturesNodes(final int mover, final int lastFrom, final int lastTo, final int from, final int to, final HashMap<ReactiveFeaturesKey, FastFeaturesNode[]> featuresMap, final List<FastFeaturesNode[]> outFeaturesNodes) {
        final FastFeaturesNode[] nodes = featuresMap.get(new ReactiveFeaturesKey(mover, lastFrom, lastTo, from, to));
        if (nodes != null) {
            outFeaturesNodes.add(nodes);
        }
    }
    
    public Footprint generateFootprint(final State state, final int from, final int to, final int player) {
        final ContainerState container = state.containerStates()[0];
        final ChunkSet footprintEmptyCells = (container.emptyChunkSetCell() != null) ? new ChunkSet(container.emptyChunkSetCell().chunkSize(), 1) : null;
        final ChunkSet footprintEmptyVertices = (container.emptyChunkSetVertex() != null) ? new ChunkSet(container.emptyChunkSetVertex().chunkSize(), 1) : null;
        final ChunkSet footprintEmptyEdges = (container.emptyChunkSetEdge() != null) ? new ChunkSet(container.emptyChunkSetEdge().chunkSize(), 1) : null;
        final ChunkSet footprintWhoCells = (container.chunkSizeWhoCell() > 0) ? new ChunkSet(container.chunkSizeWhoCell(), 1) : null;
        final ChunkSet footprintWhoVertices = (container.chunkSizeWhoVertex() > 0) ? new ChunkSet(container.chunkSizeWhoVertex(), 1) : null;
        final ChunkSet footprintWhoEdges = (container.chunkSizeWhoEdge() > 0) ? new ChunkSet(container.chunkSizeWhoEdge(), 1) : null;
        final ChunkSet footprintWhatCells = (container.chunkSizeWhatCell() > 0) ? new ChunkSet(container.chunkSizeWhatCell(), 1) : null;
        final ChunkSet footprintWhatVertices = (container.chunkSizeWhatVertex() > 0) ? new ChunkSet(container.chunkSizeWhatVertex(), 1) : null;
        final ChunkSet footprintWhatEdges = (container.chunkSizeWhatEdge() > 0) ? new ChunkSet(container.chunkSizeWhatEdge(), 1) : null;
        final List<FastFeatureInstanceNode[]> instanceNodes = this.getInstanceNodesToCheck(state, -1, -1, from, to, player);
        for (int i = 0; i < instanceNodes.size(); ++i) {
            final FastFeatureInstanceNode[] nodesArray = instanceNodes.get(i);
            for (int j = 0; j < nodesArray.length; ++j) {
                final FeatureInstance instance = nodesArray[j].featureInstance;
                if (instance.mustEmpty() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintEmptyCells.or(instance.mustEmpty());
                            break;
                        }
                        case Vertex: {
                            footprintEmptyVertices.or(instance.mustEmpty());
                            break;
                        }
                        case Edge: {
                            footprintEmptyEdges.or(instance.mustEmpty());
                            break;
                        }
                    }
                }
                if (instance.mustNotEmpty() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintEmptyCells.or(instance.mustNotEmpty());
                            break;
                        }
                        case Vertex: {
                            footprintEmptyVertices.or(instance.mustNotEmpty());
                            break;
                        }
                        case Edge: {
                            footprintEmptyEdges.or(instance.mustNotEmpty());
                            break;
                        }
                    }
                }
                if (instance.mustWhoMask() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintWhoCells.or(instance.mustWhoMask());
                            break;
                        }
                        case Vertex: {
                            footprintWhoVertices.or(instance.mustWhoMask());
                            break;
                        }
                        case Edge: {
                            footprintWhoEdges.or(instance.mustWhoMask());
                            break;
                        }
                    }
                }
                if (instance.mustNotWhoMask() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintWhoCells.or(instance.mustNotWhoMask());
                            break;
                        }
                        case Vertex: {
                            footprintWhoVertices.or(instance.mustNotWhoMask());
                            break;
                        }
                        case Edge: {
                            footprintWhoEdges.or(instance.mustNotWhoMask());
                            break;
                        }
                    }
                }
                if (instance.mustWhatMask() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintWhatCells.or(instance.mustWhatMask());
                            break;
                        }
                        case Vertex: {
                            footprintWhatVertices.or(instance.mustWhatMask());
                            break;
                        }
                        case Edge: {
                            footprintWhatEdges.or(instance.mustWhatMask());
                            break;
                        }
                    }
                }
                if (instance.mustNotWhatMask() != null) {
                    switch (instance.graphElementType()) {
                        case Cell: {
                            footprintWhatCells.or(instance.mustNotWhatMask());
                            break;
                        }
                        case Vertex: {
                            footprintWhatVertices.or(instance.mustNotWhatMask());
                            break;
                        }
                        case Edge: {
                            footprintWhatEdges.or(instance.mustNotWhatMask());
                            break;
                        }
                    }
                }
                instanceNodes.add(nodesArray[j].children);
            }
        }
        return new Footprint(footprintEmptyCells, footprintEmptyVertices, footprintEmptyEdges, footprintWhoCells, footprintWhoVertices, footprintWhoEdges, footprintWhatCells, footprintWhatVertices, footprintWhatEdges);
    }
    
    @Override
    public List<TIntArrayList> computeSparseFeatureVectors(final State state, final Move lastDecisionMove, final FastArrayList<Move> actions, final boolean thresholded) {
        final List<TIntArrayList> sparseFeatureVectors = new ArrayList<>(actions.size());
        for (final Move move : actions) {
            final int lastFrom = FeatureUtils.fromPos(lastDecisionMove);
            final int lastTo = FeatureUtils.toPos(lastDecisionMove);
            final int from = FeatureUtils.fromPos(move);
            final int to = FeatureUtils.toPos(move);
            final TIntArrayList sparseFeatureVector = this.getActiveFeatureIndices(state, lastFrom, lastTo, from, to, move.mover(), thresholded);
            sparseFeatureVectors.add(sparseFeatureVector);
        }
        return sparseFeatureVectors;
    }
    
    public FeatureSet createExpandedFeatureSet(final List<FeatureInstance> activeFeatureInstances, final boolean combineMaxWeightedFeatures, final FVector featureWeights) {
        final int numActiveInstances = activeFeatureInstances.size();
        final List<FeatureInstancePair> allPairs = new ArrayList<>();
        for (int i = 0; i < numActiveInstances; ++i) {
            final FeatureInstance firstInstance = activeFeatureInstances.get(i);
            for (int j = i + 1; j < numActiveInstances; ++j) {
                final FeatureInstance secondInstance = activeFeatureInstances.get(j);
                if (firstInstance.anchorSite() == secondInstance.anchorSite()) {
                    allPairs.add(new FeatureInstancePair(firstInstance, secondInstance));
                }
            }
        }
        if (combineMaxWeightedFeatures) {
            final FVector absWeights = featureWeights.copy();
            absWeights.abs();
            allPairs.sort((o1, o2) -> {
                final float score1 = Math.max(absWeights.get(o1.a.feature().featureSetIndex()), absWeights.get(o1.b.feature().featureSetIndex()));
                final float score2 = Math.max(absWeights.get(o2.a.feature().featureSetIndex()), absWeights.get(o2.b.feature().featureSetIndex()));
                if (score1 == score2) {
                    return 0;
                }
                if (score1 < score2) {
                    return -1;
                }
                return 1;
            });
        }
        else {
            Collections.shuffle(allPairs);
        }
        while (!allPairs.isEmpty()) {
            final FeatureInstancePair pair = allPairs.remove(allPairs.size() - 1);
            final FeatureSet newFeatureSet = this.createExpandedFeatureSet(this.game, pair.a, pair.b);
            if (newFeatureSet != null) {
                return newFeatureSet;
            }
        }
        return null;
    }
    
    public FeatureSet createExpandedFeatureSet(final Game targetGame, final FeatureInstance firstFeatureInstance, final FeatureInstance secondFeatureInstance) {
        final Feature combinedFeature = Feature.combineFeatures(targetGame, firstFeatureInstance, secondFeatureInstance);
        boolean featureAlreadyExists = false;
        for (final Feature oldFeature : this.features) {
            if (combinedFeature.equals(oldFeature)) {
                featureAlreadyExists = true;
                break;
            }
            TFloatArrayList allowedRotations = combinedFeature.pattern().allowedRotations();
            if (allowedRotations == null) {
                allowedRotations = Walk.allGameRotations(this.game);
            }
            for (int i = 0; i < allowedRotations.size(); ++i) {
                final Feature rotatedCopy = combinedFeature.rotatedCopy(allowedRotations.getQuick(i));
                if (rotatedCopy.equals(oldFeature)) {
                    featureAlreadyExists = true;
                    break;
                }
            }
        }
        if (!featureAlreadyExists) {
            final List<Feature> newFeatureList = new ArrayList<>(this.features.length + 1);
            for (final Feature feature : this.features) {
                newFeatureList.add(feature);
            }
            newFeatureList.add(combinedFeature);
            return new FeatureSet(newFeatureList);
        }
        return null;
    }
    
    public boolean hasInstantiatedFeatures(final Game targetGame, final FVector weights) {
        if (this.featureInitWeights == null) {
            return this.game == targetGame && weights == null;
        }
        return this.game == targetGame && this.featureInitWeights.equals(weights);
    }
    
    public void toFile(final String filepath) {
        try (final PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8)) {
            for (final Feature feature : this.features) {
                writer.println(feature);
            }
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private static void simplifyInstanceForests(final Map<ReactiveFeaturesKey, List<FeatureInstanceNode>> reactiveInstancesWIP, final Map<ProactiveFeaturesKey, List<FeatureInstanceNode>> proactiveInstancesWIP) {
        final List<List<FeatureInstanceNode>> allForests = new ArrayList<>(2);
        allForests.addAll(proactiveInstancesWIP.values());
        allForests.addAll(reactiveInstancesWIP.values());
        for (final List<FeatureInstanceNode> forest : allForests) {
            for (final FeatureInstanceNode root : forest) {
                final List<FeatureInstanceNode> rootsToProcess = new ArrayList<>();
                rootsToProcess.add(root);
                while (!rootsToProcess.isEmpty()) {
                    final FeatureInstanceNode rootToProcess = rootsToProcess.remove(0);
                    if (!rootToProcess.featureInstance.hasNoTests()) {
                        final List<FeatureInstanceNode> descendants = rootToProcess.collectDescendants();
                        for (final FeatureInstanceNode descendant : descendants) {
                            descendant.featureInstance.removeTests(rootToProcess.featureInstance);
                        }
                    }
                    rootsToProcess.addAll(rootToProcess.children);
                }
                final List<FeatureInstanceNode> allNodes = root.collectDescendants();
                for (final FeatureInstanceNode node : allNodes) {
                    FeatureInstanceNode ancestor;
                    for (ancestor = node.parent; ancestor.featureInstance.hasNoTests() && ancestor != root; ancestor = ancestor.parent) {}
                    if (ancestor != node.parent) {
                        node.parent.children.remove(node);
                        ancestor.children.add(node);
                        node.parent = ancestor;
                    }
                }
            }
        }
    }
    
    private static void insertInstanceInForest(final FeatureInstance instance, final List<FeatureInstanceNode> instanceNodes) {
        final FeatureInstanceNode parentNode = findDeepestParent(instance, instanceNodes);
        if (parentNode == null) {
            instanceNodes.add(new FeatureInstanceNode(instance, null));
        }
        else {
            final FeatureInstanceNode newNode = new FeatureInstanceNode(instance, parentNode);
            int i = 0;
            while (i < parentNode.children.size()) {
                final FeatureInstanceNode child = parentNode.children.get(i);
                if (instance.generalises(child.featureInstance)) {
                    parentNode.children.remove(i);
                    newNode.children.add(child);
                    child.parent = newNode;
                }
                else {
                    ++i;
                }
            }
            parentNode.children.add(newNode);
            parentNode.children.trimToSize();
        }
    }
    
    private static FeatureInstanceNode findDeepestParent(final FeatureInstance instance, final List<FeatureInstanceNode> instanceNodes) {
        FeatureInstanceNode deepestParent = null;
        int deepestParentDepthLevel = -1;
        int currDepthLevel = 0;
        for (List<FeatureInstanceNode> currDepthNodes = instanceNodes, nextDepthNodes = new ArrayList<>(); !currDepthNodes.isEmpty(); currDepthNodes = nextDepthNodes, nextDepthNodes = new ArrayList<>(), ++currDepthLevel) {
            for (final FeatureInstanceNode node : currDepthNodes) {
                if (node.featureInstance.generalises(instance)) {
                    if (currDepthLevel > deepestParentDepthLevel) {
                        deepestParent = node;
                        deepestParentDepthLevel = currDepthLevel;
                    }
                    nextDepthNodes.addAll(node.children);
                }
            }
        }
        return deepestParent;
    }
    
    public void printProactiveFeaturesTree(final int player, final int from, final int to) {
        System.out.println("---");
        this.proactiveFeatures.get(new ProactiveFeaturesKey(player, from, to))[0].print(0);
        System.out.println("---");
    }
    
    public static class ProactiveFeaturesKey
    {
        private final int playerIdx;
        private final int from;
        private final int to;
        private final transient int cachedHashCode;
        
        public ProactiveFeaturesKey(final int playerIdx, final int from, final int to) {
            this.playerIdx = playerIdx;
            this.from = from;
            this.to = to;
            final int prime = 31;
            int result = 17;
            result = 31 * result + from;
            result = 31 * result + playerIdx;
            result = 31 * result + to;
            this.cachedHashCode = result;
        }
        
        @Override
        public int hashCode() {
            return this.cachedHashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ProactiveFeaturesKey)) {
                return false;
            }
            final ProactiveFeaturesKey other = (ProactiveFeaturesKey)obj;
            return this.playerIdx == other.playerIdx && this.from == other.from && this.to == other.to;
        }
        
        @Override
        public String toString() {
            return "[ProactiveFeaturesKey: " + this.playerIdx + ", " + this.from + ", " + this.to + "]";
        }
    }
    
    public static class ReactiveFeaturesKey
    {
        private final int playerIdx;
        private final int lastFrom;
        private final int lastTo;
        private final int from;
        private final int to;
        private final transient int cachedHashCode;
        
        public ReactiveFeaturesKey(final int playerIdx, final int lastFrom, final int lastTo, final int from, final int to) {
            this.playerIdx = playerIdx;
            this.lastFrom = lastFrom;
            this.lastTo = lastTo;
            this.from = from;
            this.to = to;
            final int prime = 31;
            int result = 17;
            result = 31 * result + from;
            result = 31 * result + lastFrom;
            result = 31 * result + lastTo;
            result = 31 * result + playerIdx;
            result = 31 * result + to;
            this.cachedHashCode = result;
        }
        
        @Override
        public int hashCode() {
            return this.cachedHashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ReactiveFeaturesKey)) {
                return false;
            }
            final ReactiveFeaturesKey other = (ReactiveFeaturesKey)obj;
            return this.playerIdx == other.playerIdx && this.lastFrom == other.lastFrom && this.lastTo == other.lastTo && this.from == other.from && this.to == other.to;
        }
    }
    
    private static class FeatureInstancePair
    {
        protected final FeatureInstance a;
        protected final FeatureInstance b;
        
        protected FeatureInstancePair(final FeatureInstance a, final FeatureInstance b) {
            this.a = a;
            this.b = b;
        }
    }
    
    private static class FeatureInstanceNode
    {
        protected final FeatureInstance featureInstance;
        protected final ArrayList<FeatureInstanceNode> children;
        protected FeatureInstanceNode parent;
        
        public FeatureInstanceNode(final FeatureInstance featureInstance, final FeatureInstanceNode parent) {
            this.children = new ArrayList<>(2);
            this.featureInstance = featureInstance;
            this.parent = parent;
        }
        
        public List<FeatureInstanceNode> collectDescendants() {
            final List<FeatureInstanceNode> result = new ArrayList<>();
            final List<FeatureInstanceNode> nodesToCheck = new ArrayList<>();
            nodesToCheck.addAll(this.children);
            while (!nodesToCheck.isEmpty()) {
                final FeatureInstanceNode node = nodesToCheck.remove(nodesToCheck.size() - 1);
                result.add(node);
                nodesToCheck.addAll(node.children);
            }
            return result;
        }
        
        public void print(final int depthLevel) {
            for (int i = 0; i < depthLevel; ++i) {
                System.out.print("\t");
            }
            System.out.println(this.featureInstance);
            for (final FeatureInstanceNode child : this.children) {
                child.print(depthLevel + 1);
            }
        }
    }
    
    private static class FastFeatureInstanceNode
    {
        protected final FeatureInstance featureInstance;
        protected final FastFeatureInstanceNode[] children;
        
        public FastFeatureInstanceNode(final FeatureInstanceNode slowNode) {
            this.featureInstance = slowNode.featureInstance;
            this.children = new FastFeatureInstanceNode[slowNode.children.size()];
            for (int i = 0; i < this.children.length; ++i) {
                this.children[i] = new FastFeatureInstanceNode(slowNode.children.get(i));
            }
        }
    }
    
    private static class FastFeaturesNode
    {
        protected BitwiseTest test;
        protected final FastFeaturesNode[] children;
        protected final int[] activeFeatureIndices;
        
        public FastFeaturesNode(final FastFeatureInstanceNode instanceNode) {
            this.test = instanceNode.featureInstance;
            final FastFeatureInstanceNode[] instanceChildren = instanceNode.children;
            final List<FastFeaturesNode> childrenList = new ArrayList<>(instanceChildren.length);
            final TIntArrayList featureIndicesList = new TIntArrayList();
            featureIndicesList.add(instanceNode.featureInstance.feature().featureSetIndex());
            for (final FastFeatureInstanceNode instanceChild : instanceChildren) {
                final FeatureInstance instance = instanceChild.featureInstance;
                if (instance.hasNoTests()) {
                    final int featureIdx = instance.feature().featureSetIndex();
                    if (!featureIndicesList.contains(featureIdx)) {
                        featureIndicesList.add(featureIdx);
                    }
                }
                else {
                    childrenList.add(new FastFeaturesNode(instanceChild));
                }
            }
            featureIndicesList.sort();
            final int numChildren = childrenList.size();
            final boolean[] skipIndices = new boolean[numChildren];
            for (int i = 0; i < numChildren; ++i) {
                if (!skipIndices[i]) {
                    final FastFeaturesNode child = childrenList.get(i);
                    if (child.children.length == 0) {
                        for (int j = i + 1; j < numChildren; ++j) {
                            if (!skipIndices[j]) {
                                final FastFeaturesNode otherChild = childrenList.get(j);
                                if (otherChild.children.length == 0 && Arrays.equals(child.activeFeatureIndices, otherChild.activeFeatureIndices)) {
                                    final BitwiseTest testA = child.test;
                                    final BitwiseTest testB = otherChild.test;
                                    assert testA.graphElementType() == testB.graphElementType();
                                    if (testA.onlyRequiresSingleMustEmpty() && testB.onlyRequiresSingleMustEmpty()) {
                                        if (testA instanceof FeatureInstance) {
                                            final FeatureInstance instanceA = (FeatureInstance)testA;
                                            final FeatureInstance instanceB = (FeatureInstance)testB;
                                            final ChunkSet combinedTest = instanceA.mustEmpty().clone();
                                            combinedTest.or(instanceB.mustEmpty());
                                            child.test = new OneOfMustEmpty(combinedTest, testA.graphElementType());
                                        }
                                        else {
                                            final OneOfMustEmpty A = (OneOfMustEmpty)testA;
                                            if (testB instanceof FeatureInstance) {
                                                final FeatureInstance instanceB = (FeatureInstance)testB;
                                                A.mustEmpties().or(instanceB.mustEmpty());
                                                child.test = new OneOfMustEmpty(A.mustEmpties(), testA.graphElementType());
                                            }
                                            else {
                                                final OneOfMustEmpty B = (OneOfMustEmpty)testB;
                                                A.mustEmpties().or(B.mustEmpties());
                                                child.test = new OneOfMustEmpty(A.mustEmpties(), testA.graphElementType());
                                            }
                                        }
                                        skipIndices[j] = true;
                                    }
                                    else if (testA.onlyRequiresSingleMustWho() && testB.onlyRequiresSingleMustWho()) {
                                        if (testA instanceof FeatureInstance) {
                                            final FeatureInstance instanceA = (FeatureInstance)testA;
                                            final FeatureInstance instanceB = (FeatureInstance)testB;
                                            final ChunkSet whoA = instanceA.mustWho();
                                            final ChunkSet whoMaskA = instanceA.mustWhoMask();
                                            final ChunkSet whoB = instanceB.mustWho();
                                            final ChunkSet whoMaskB = instanceB.mustWhoMask();
                                            final ChunkSet combinedMask = whoMaskA.clone();
                                            combinedMask.or(whoMaskB);
                                            if (whoMaskA.intersects(whoMaskB)) {
                                                final ChunkSet cloneB = whoB.clone();
                                                cloneB.and(whoMaskB);
                                                if (!whoA.matches(combinedMask, cloneB)) {
                                                    continue;
                                                }
                                            }
                                            final ChunkSet combinedWhos = whoA.clone();
                                            combinedWhos.or(whoB);
                                            child.test = new OneOfMustWho(combinedWhos, combinedMask, testA.graphElementType());
                                        }
                                        else {
                                            final OneOfMustWho A2 = (OneOfMustWho)testA;
                                            final ChunkSet whosA = A2.mustWhos();
                                            final ChunkSet whosMaskA = A2.mustWhosMask();
                                            if (testB instanceof FeatureInstance) {
                                                final FeatureInstance instanceB2 = (FeatureInstance)testB;
                                                final ChunkSet whoB = instanceB2.mustWho();
                                                final ChunkSet whoMaskB = instanceB2.mustWhoMask();
                                                if (whosMaskA.intersects(whoMaskB) && !whosA.matches(whoMaskB, whoB)) {
                                                    continue;
                                                }
                                                whosA.or(whoB);
                                                whosMaskA.or(whoMaskB);
                                                child.test = new OneOfMustWho(whosA, whosMaskA, testA.graphElementType());
                                            }
                                            else {
                                                final OneOfMustWho B2 = (OneOfMustWho)testB;
                                                final ChunkSet whosB = B2.mustWhos();
                                                final ChunkSet whosMaskB = B2.mustWhosMask();
                                                if (whosMaskA.intersects(whosMaskB)) {
                                                    if (!whosA.matches(whosMaskB, whosB)) {
                                                        continue;
                                                    }
                                                    if (!whosB.matches(whosMaskA, whosA)) {
                                                        continue;
                                                    }
                                                }
                                                whosA.or(whosB);
                                                whosMaskA.or(whosMaskB);
                                                child.test = new OneOfMustWho(whosA, whosMaskA, testA.graphElementType());
                                            }
                                        }
                                        skipIndices[j] = true;
                                    }
                                    else if (testA.onlyRequiresSingleMustWhat() && testB.onlyRequiresSingleMustWhat()) {
                                        if (testA instanceof FeatureInstance) {
                                            final FeatureInstance instanceA = (FeatureInstance)testA;
                                            final FeatureInstance instanceB = (FeatureInstance)testB;
                                            final ChunkSet whatA = instanceA.mustWhat();
                                            final ChunkSet whatMaskA = instanceA.mustWhatMask();
                                            final ChunkSet whatB = instanceB.mustWhat();
                                            final ChunkSet whatMaskB = instanceB.mustWhatMask();
                                            final ChunkSet combinedMask = whatMaskA.clone();
                                            combinedMask.or(whatMaskB);
                                            if (whatMaskA.intersects(whatMaskB)) {
                                                final ChunkSet cloneB = whatB.clone();
                                                cloneB.and(whatMaskB);
                                                if (!whatA.matches(combinedMask, cloneB)) {
                                                    continue;
                                                }
                                            }
                                            final ChunkSet combinedWhats = whatA.clone();
                                            combinedWhats.or(whatB);
                                            child.test = new OneOfMustWhat(combinedWhats, combinedMask, testA.graphElementType());
                                        }
                                        else {
                                            final OneOfMustWhat A3 = (OneOfMustWhat)testA;
                                            final ChunkSet whatsA = A3.mustWhats();
                                            final ChunkSet whatsMaskA = A3.mustWhatsMask();
                                            if (testB instanceof FeatureInstance) {
                                                final FeatureInstance instanceB2 = (FeatureInstance)testB;
                                                final ChunkSet whatB = instanceB2.mustWhat();
                                                final ChunkSet whatMaskB = instanceB2.mustWhatMask();
                                                if (whatsMaskA.intersects(whatMaskB) && !whatsA.matches(whatMaskB, whatB)) {
                                                    continue;
                                                }
                                                whatsA.or(whatB);
                                                whatsMaskA.or(whatMaskB);
                                                child.test = new OneOfMustWhat(whatsA, whatsMaskA, testA.graphElementType());
                                            }
                                            else {
                                                final OneOfMustWhat B3 = (OneOfMustWhat)testB;
                                                final ChunkSet whatsB = B3.mustWhats();
                                                final ChunkSet whatsMaskB = B3.mustWhatsMask();
                                                if (whatsMaskA.intersects(whatsMaskB)) {
                                                    if (!whatsA.matches(whatsMaskB, whatsB)) {
                                                        continue;
                                                    }
                                                    if (!whatsB.matches(whatsMaskA, whatsA)) {
                                                        continue;
                                                    }
                                                }
                                                whatsA.or(whatsB);
                                                whatsMaskA.or(whatsMaskB);
                                                child.test = new OneOfMustWhat(whatsA, whatsMaskA, testA.graphElementType());
                                            }
                                        }
                                        skipIndices[j] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final List<FastFeaturesNode> remainingChildren = new ArrayList<>();
            for (int k = 0; k < numChildren; ++k) {
                if (!skipIndices[k]) {
                    remainingChildren.add(childrenList.get(k));
                }
            }
            remainingChildren.toArray(this.children = new FastFeaturesNode[remainingChildren.size()]);
            this.activeFeatureIndices = featureIndicesList.toArray();
        }
        
        private FastFeaturesNode(final BitwiseTest test, final FastFeaturesNode[] children, final int[] activeFeatureIndices) {
            this.test = test;
            this.children = children;
            this.activeFeatureIndices = activeFeatureIndices;
        }
        
        public static FastFeaturesNode thresholdedNode(final FastFeaturesNode other, final FVector weights) {
            final List<FastFeaturesNode> thresholdedChildren = new ArrayList<>(other.children.length);
            for (final FastFeaturesNode child : other.children) {
                final FastFeaturesNode thresholdedChild = thresholdedNode(child, weights);
                if (thresholdedChild != null) {
                    thresholdedChildren.add(thresholdedChild);
                }
            }
            final TIntArrayList thresholdedFeatures = new TIntArrayList(other.activeFeatureIndices.length);
            for (final int activeFeature : other.activeFeatureIndices) {
                if (weights == null || Math.abs(weights.get(activeFeature)) >= 0.001f) {
                    thresholdedFeatures.add(activeFeature);
                }
            }
            if (thresholdedChildren.isEmpty() && thresholdedFeatures.isEmpty()) {
                return null;
            }
            return new FastFeaturesNode(other.test, thresholdedChildren.toArray(new FastFeaturesNode[0]), thresholdedFeatures.toArray());
        }
        
        public void print(final int depthLevel) {
            for (int i = 0; i < depthLevel; ++i) {
                System.out.print("\t");
            }
            System.out.println(this);
            for (final FastFeaturesNode child : this.children) {
                child.print(depthLevel + 1);
            }
        }
        
        @Override
        public String toString() {
            return String.format("%s %s", this.test, Arrays.toString(this.activeFeatureIndices));
        }
    }
}
