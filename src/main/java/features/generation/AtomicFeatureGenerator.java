// 
// Decompiled by Procyon v0.5.36
// 

package features.generation;

import features.Walk;
import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.features.AbsoluteFeature;
import features.features.Feature;
import features.features.RelativeFeature;
import features.patterns.Pattern;
import game.Game;
import game.equipment.component.Component;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class AtomicFeatureGenerator
{
    protected final Game game;
    protected final List<Feature> features;
    
    public AtomicFeatureGenerator(final Game game, final int maxWalkSize, final int maxStraightWalkSize) {
        this.game = game;
        (this.features = this.simplifyFeatureSet(this.generateFeatures(maxWalkSize, maxStraightWalkSize))).sort((o1, o2) -> {
            final List<FeatureElement> els1 = o1.pattern().featureElements();
            final List<FeatureElement> els2 = o2.pattern().featureElements();
            if (els1.size() < els2.size()) {
                return -1;
            }
            if (els1.size() > els2.size()) {
                return 1;
            }
            int sumWalkLengths1 = 0;
            int sumWalkLengths2 = 0;
            for (final FeatureElement el : els1) {
                if (el instanceof RelativeFeatureElement) {
                    sumWalkLengths1 += ((RelativeFeatureElement) el).walk().steps().size();
                }
            }
            for (final FeatureElement el : els2) {
                if (el instanceof RelativeFeatureElement) {
                    sumWalkLengths2 += ((RelativeFeatureElement) el).walk().steps().size();
                }
            }
            return sumWalkLengths1 - sumWalkLengths2;
        });
    }
    
    public List<Feature> getFeatures() {
        return this.features;
    }
    
    private List<Feature> generateFeatures(final int maxSize, final int maxStraightWalkSize) {
        final List<Feature> emptyFeatures = new ArrayList<>();
        emptyFeatures.add(new RelativeFeature(new Pattern(), new Walk(), null));
        emptyFeatures.add(new RelativeFeature(new Pattern(), null, new Walk()));
        final Set<Feature> generatedFeatures = new HashSet<>(16384);
        generatedFeatures.addAll(emptyFeatures);
        final TIntArrayList connectivities = this.game.board().topology().trueOrthoConnectivities(this.game);
        final TFloatArrayList allGameRotations = Walk.allGameRotations(this.game);
        final List<FeatureElement.ElementType> elementTypes = FeatureGenerationUtils.usefulElementTypes(this.game);
        for (int walkSize = 0; walkSize <= maxStraightWalkSize; ++walkSize) {
            final List<Walk> allWalks = generateAllWalks(walkSize, maxSize, allGameRotations);
            for (final Feature baseFeature : emptyFeatures) {
                final Pattern basePattern = baseFeature.pattern();
                for (final Walk walk : allWalks) {
                    for (final FeatureElement.ElementType elementType : elementTypes) {
                        final TIntArrayList itemIndices = new TIntArrayList();
                        if (elementType == FeatureElement.ElementType.Item) {
                            final Component[] components = this.game.equipment().components();
                            for (int i = 1; i < components.length; ++i) {
                                if (components[i] != null) {
                                    itemIndices.add(i);
                                }
                            }
                        }
                        else if (elementType == FeatureElement.ElementType.IsPos) {
                            System.err.println("WARNING: not yet including position indices in AtomicFeatureGenerator.generateFeatures()");
                        }
                        else if (elementType == FeatureElement.ElementType.Connectivity) {
                            itemIndices.addAll(connectivities);
                        }
                        else {
                            itemIndices.add(-1);
                        }
                        for (final boolean not : new boolean[] { false, true }) {
                            for (int idx = 0; idx < itemIndices.size(); ++idx) {
                                final Pattern newPattern = new Pattern(baseFeature.pattern());
                                if (elementType != FeatureElement.ElementType.LastFrom && elementType != FeatureElement.ElementType.LastTo) {
                                    newPattern.addElement(new RelativeFeatureElement(elementType, not, new Walk(walk), itemIndices.getQuick(idx)));
                                }
                                if (newPattern.isConsistent()) {
                                    newPattern.removeRedundancies();
                                    if (!newPattern.equals(basePattern) || elementType != FeatureElement.ElementType.LastFrom || elementType != FeatureElement.ElementType.LastTo) {
                                        Feature newFeature;
                                        if (baseFeature instanceof AbsoluteFeature) {
                                            final AbsoluteFeature absBase = (AbsoluteFeature)baseFeature;
                                            newFeature = new AbsoluteFeature(newPattern, absBase.toPosition(), absBase.fromPosition());
                                        }
                                        else {
                                            final Walk lastTo = (elementType == FeatureElement.ElementType.LastTo) ? new Walk(walk) : null;
                                            final Walk lastFrom = (elementType == FeatureElement.ElementType.LastFrom) ? new Walk(walk) : null;
                                            final RelativeFeature relBase = (RelativeFeature)baseFeature;
                                            newFeature = new RelativeFeature(newPattern, (relBase.toPosition() != null) ? new Walk(relBase.toPosition()) : null, (relBase.fromPosition() != null) ? new Walk(relBase.fromPosition()) : null, lastTo, lastFrom);
                                        }
                                        newFeature.normalise(this.game);
                                        newFeature.pattern().removeRedundancies();
                                        generatedFeatures.add(newFeature);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(generatedFeatures);
    }
    
    private static List<Walk> generateAllWalks(final int walkSize, final int maxWalkSize, final TFloatArrayList allGameRotations) {
        if (walkSize < 0) {
            final List<Walk> walks = new ArrayList<>(1);
            walks.add(null);
            return walks;
        }
        List<Walk> allWalks = Collections.singletonList(new Walk());
        List<Walk> allWalksReplacement;
        for (int currWalkLengths = 0; currWalkLengths < walkSize; ++currWalkLengths, allWalks = allWalksReplacement) {
            allWalksReplacement = new ArrayList<>(allWalks.size() * 4);
            for (final Walk walk : allWalks) {
                for (int i = 0; i < allGameRotations.size(); ++i) {
                    final float rot = allGameRotations.getQuick(i);
                    if ((rot == 0.0f || currWalkLengths == 0 || walkSize <= maxWalkSize) && (rot != 0.5f || currWalkLengths == 0)) {
                        final Walk newWalk = new Walk(walk);
                        newWalk.steps().add(rot);
                        allWalksReplacement.add(newWalk);
                    }
                }
            }
        }
        return allWalks;
    }
    
    private List<Feature> simplifyFeatureSet(final List<Feature> featuresIn) {
        final List<Feature> simplified = new ArrayList<>(featuresIn.size());
        final Map<Object, RotRefInvariantFeature> featuresToKeep = new HashMap<>();
        final TFloatArrayList rotations = Walk.allGameRotations(this.game);
        final boolean[] reflections = { true, false };
        for (final Feature feature : featuresIn) {
            boolean shouldAddFeature = true;
            for (int i = 0; i < rotations.size(); ++i) {
                final float rotation = rotations.get(i);
                for (final boolean reflect : reflections) {
                    Feature rotatedFeature = feature.rotatedCopy(rotation);
                    if (reflect) {
                        rotatedFeature = rotatedFeature.reflectedCopy();
                    }
                    rotatedFeature.normalise(this.game);
                    final RotRefInvariantFeature wrapped = new RotRefInvariantFeature(rotatedFeature);
                    if (featuresToKeep.containsKey(wrapped)) {
                        shouldAddFeature = false;
                        final Feature keepFeature = featuresToKeep.remove(wrapped).feature;
                        final float requiredRot = (rotation == 0.0f) ? 0.0f : (1.0f - rotation);
                        if (keepFeature.pattern().allowedRotations() != null && !keepFeature.pattern().allowedRotations().contains(requiredRot)) {
                            final TFloatArrayList allowedRotations = new TFloatArrayList();
                            allowedRotations.addAll(keepFeature.pattern().allowedRotations());
                            allowedRotations.add(requiredRot);
                            keepFeature.pattern().setAllowedRotations(allowedRotations);
                            keepFeature.pattern().allowedRotations().sort();
                            keepFeature.normalise(this.game);
                        }
                        final RotRefInvariantFeature wrappedKeep = new RotRefInvariantFeature(keepFeature);
                        featuresToKeep.put(wrappedKeep, wrappedKeep);
                        break;
                    }
                }
                if (!shouldAddFeature) {
                    break;
                }
            }
            if (shouldAddFeature) {
                final RotRefInvariantFeature wrapped2 = new RotRefInvariantFeature(feature);
                featuresToKeep.put(wrapped2, wrapped2);
            }
        }
        for (final RotRefInvariantFeature feature2 : featuresToKeep.values()) {
            simplified.add(feature2.feature);
        }
        return simplified;
    }
    
    private static class RotRefInvariantFeature
    {
        protected Feature feature;
        
        public RotRefInvariantFeature(final Feature feature) {
            this.feature = feature;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof RotRefInvariantFeature && this.feature.equalsIgnoreRotRef(((RotRefInvariantFeature)other).feature);
        }
        
        @Override
        public int hashCode() {
            return this.feature.hashCodeIgnoreRotRef();
        }
    }
}
