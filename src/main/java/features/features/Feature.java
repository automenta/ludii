// 
// Decompiled by Procyon v0.5.36
// 

package features.features;

import features.Walk;
import features.elements.AbsoluteFeatureElement;
import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.graph_search.GraphSearch;
import features.graph_search.Path;
import features.instances.FeatureInstance;
import features.patterns.Pattern;
import game.Game;
import game.types.board.SiteType;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import topology.TopologyElement;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Feature
{
    protected Pattern pattern;
    protected SiteType graphElementType;
    protected int featureSetIndex;
    protected String comment;
    
    public Feature() {
        this.graphElementType = null;
        this.featureSetIndex = -1;
        this.comment = "";
    }
    
    public Pattern pattern() {
        return this.pattern;
    }
    
    public int featureSetIndex() {
        return this.featureSetIndex;
    }
    
    public void setFeatureSetIndex(final int newIdx) {
        this.featureSetIndex = newIdx;
    }
    
    public abstract Feature rotatedCopy(final float p0);
    
    public abstract Feature reflectedCopy();
    
    public abstract boolean generalises(final Feature p0);
    
    public final List<FeatureInstance> instantiateFeature(final Game game, final ContainerState container, final int player, final int fromPosConstraint, final int toPosConstraint) {
        SiteType instanceType;
        if (this.graphElementType != null) {
            instanceType = this.graphElementType;
        }
        else if (game.board().defaultSite() == SiteType.Vertex) {
            instanceType = SiteType.Vertex;
        }
        else {
            instanceType = SiteType.Cell;
        }
        final List<FeatureInstance> instances = new ArrayList<>();
        final int[] reflections = this.pattern.allowsReflection() ? new int[] { 1, -1 } : new int[] { 1 };
        final List<? extends TopologyElement> sites = game.graphPlayElements();
        boolean moreSitesRelevant = true;
        for (int siteIdx = 0; siteIdx < sites.size(); ++siteIdx) {
            final TopologyElement anchorSite = sites.get(siteIdx);
            if (anchorSite.sortedOrthos().length != 0) {
                TFloatArrayList rots = this.pattern.allowedRotations();
                if (rots == null) {
                    rots = Walk.rotationsForNumOrthos(anchorSite.sortedOrthos().length);
                }
                if (rots.isEmpty()) {
                    System.err.println("Warning: rots.size() == 0 in Feature.instantiateFeature()");
                }
                for (final int reflectionMult : reflections) {
                    boolean moreReflectionsRelevant = false;
                    for (int rotIdx = 0; rotIdx < rots.size(); ++rotIdx) {
                        boolean moreRotationsRelevant = false;
                        final float rot = rots.get(rotIdx);
                        boolean allElementsAbsolute = true;
                        final List<FeatureInstance> instancesWithActions = new ArrayList<>(1);
                        final FeatureInstance baseInstance = new FeatureInstance(this, siteIdx, reflectionMult, rot, instanceType);
                        if (this instanceof AbsoluteFeature) {
                            final AbsoluteFeature absThis = (AbsoluteFeature)this;
                            if ((toPosConstraint < 0 || toPosConstraint == absThis.toPosition) && (fromPosConstraint < 0 || fromPosConstraint == absThis.fromPosition)) {
                                baseInstance.setAction(absThis.toPosition, absThis.fromPosition);
                                baseInstance.setLastAction(absThis.lastToPosition, absThis.lastFromPosition);
                                instancesWithActions.add(baseInstance);
                            }
                        }
                        else {
                            final RelativeFeature relThis = (RelativeFeature)this;
                            final Walk toWalk = relThis.toPosition;
                            final Walk fromWalk = relThis.fromPosition;
                            final Walk lastToWalk = relThis.lastToPosition;
                            final Walk lastFromWalk = relThis.lastFromPosition;
                            TIntArrayList possibleToPositions;
                            if (toWalk == null) {
                                possibleToPositions = TIntArrayList.wrap(new int[] { -1 });
                            }
                            else {
                                possibleToPositions = toWalk.resolveWalk(game, anchorSite, rot, reflectionMult);
                                final TFloatArrayList steps = toWalk.steps();
                                if (!steps.isEmpty()) {
                                    moreRotationsRelevant = true;
                                    if (!moreReflectionsRelevant) {
                                        for (int step = 0; step < steps.size(); ++step) {
                                            final float turn = steps.getQuick(step);
                                            if (turn != 0.0f && turn != 0.5f && turn != -0.5f) {
                                                moreReflectionsRelevant = true;
                                            }
                                        }
                                    }
                                }
                            }
                            for (int toPosIdx = 0; toPosIdx < possibleToPositions.size(); ++toPosIdx) {
                                final int toPos = possibleToPositions.getQuick(toPosIdx);
                                if ((toWalk == null || toPos >= 0) && (toPosConstraint < 0 || toPos == toPosConstraint)) {
                                    TIntArrayList possibleFromPositions;
                                    if (fromWalk == null) {
                                        possibleFromPositions = TIntArrayList.wrap(new int[] { -1 });
                                    }
                                    else {
                                        possibleFromPositions = fromWalk.resolveWalk(game, anchorSite, rot, reflectionMult);
                                        final TFloatArrayList steps2 = fromWalk.steps();
                                        if (!steps2.isEmpty()) {
                                            moreRotationsRelevant = true;
                                            if (!moreReflectionsRelevant) {
                                                for (int step2 = 0; step2 < steps2.size(); ++step2) {
                                                    final float turn2 = steps2.getQuick(step2);
                                                    if (turn2 != 0.0f && turn2 != 0.5f && turn2 != -0.5f) {
                                                        moreReflectionsRelevant = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (int fromPosIdx = 0; fromPosIdx < possibleFromPositions.size(); ++fromPosIdx) {
                                        final int fromPos = possibleFromPositions.getQuick(fromPosIdx);
                                        if ((fromWalk == null || fromPos >= 0) && (fromPosConstraint < 0 || fromPos == fromPosConstraint)) {
                                            TIntArrayList possibleLastToPositions;
                                            if (lastToWalk == null) {
                                                possibleLastToPositions = TIntArrayList.wrap(new int[] { -1 });
                                            }
                                            else {
                                                possibleLastToPositions = lastToWalk.resolveWalk(game, anchorSite, rot, reflectionMult);
                                                final TFloatArrayList steps3 = lastToWalk.steps();
                                                if (!steps3.isEmpty()) {
                                                    moreRotationsRelevant = true;
                                                    if (!moreReflectionsRelevant) {
                                                        for (int step3 = 0; step3 < steps3.size(); ++step3) {
                                                            final float turn3 = steps3.getQuick(step3);
                                                            if (turn3 != 0.0f && turn3 != 0.5f && turn3 != -0.5f) {
                                                                moreReflectionsRelevant = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            for (int lastToPosIdx = 0; lastToPosIdx < possibleLastToPositions.size(); ++lastToPosIdx) {
                                                final int lastToPos = possibleLastToPositions.getQuick(lastToPosIdx);
                                                if (lastToPos != -1 || lastToWalk == null) {
                                                    TIntArrayList possibleLastFromPositions;
                                                    if (lastFromWalk == null) {
                                                        possibleLastFromPositions = TIntArrayList.wrap(new int[] { -1 });
                                                    }
                                                    else {
                                                        possibleLastFromPositions = lastFromWalk.resolveWalk(game, anchorSite, rot, reflectionMult);
                                                        final TFloatArrayList steps4 = lastFromWalk.steps();
                                                        if (!steps4.isEmpty()) {
                                                            moreRotationsRelevant = true;
                                                            if (!moreReflectionsRelevant) {
                                                                for (int step4 = 0; step4 < steps4.size(); ++step4) {
                                                                    final float turn4 = steps4.getQuick(step4);
                                                                    if (turn4 != 0.0f && turn4 != 0.5f && turn4 != -0.5f) {
                                                                        moreReflectionsRelevant = true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    for (int lastFromPosIdx = 0; lastFromPosIdx < possibleLastFromPositions.size(); ++lastFromPosIdx) {
                                                        final int lastFromPos = possibleLastFromPositions.getQuick(lastFromPosIdx);
                                                        if (lastFromPos != -1 || lastFromWalk == null) {
                                                            final FeatureInstance newInstance = new FeatureInstance(baseInstance);
                                                            newInstance.setAction(toPos, fromPos);
                                                            newInstance.setLastAction(lastToPos, lastFromPos);
                                                            instancesWithActions.add(newInstance);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        List<FeatureInstance> instancesWithElements = new ArrayList<>(instancesWithActions);
                        for (final FeatureElement element : this.pattern.featureElements()) {
                            final List<FeatureInstance> replaceNewInstances = new ArrayList<>(instancesWithElements.size());
                            if (element instanceof RelativeFeatureElement) {
                                allElementsAbsolute = false;
                            }
                            for (final FeatureInstance instance : instancesWithElements) {
                                TIntArrayList testSites = new TIntArrayList(1);
                                if (element instanceof AbsoluteFeatureElement) {
                                    final AbsoluteFeatureElement absElement = (AbsoluteFeatureElement)element;
                                    testSites.add(absElement.position());
                                }
                                else {
                                    final RelativeFeatureElement relElement = (RelativeFeatureElement)element;
                                    testSites = relElement.walk().resolveWalk(game, anchorSite, rot, reflectionMult);
                                    final TFloatArrayList steps5 = relElement.walk().steps();
                                    if (!steps5.isEmpty()) {
                                        moreRotationsRelevant = true;
                                        if (!moreReflectionsRelevant) {
                                            for (int step5 = 0; step5 < steps5.size(); ++step5) {
                                                final float turn5 = steps5.getQuick(step5);
                                                if (turn5 != 0.0f && turn5 != 0.5f && turn5 != -0.5f) {
                                                    moreReflectionsRelevant = true;
                                                }
                                            }
                                        }
                                    }
                                }
                                for (int testSiteIdx = 0; testSiteIdx < testSites.size(); ++testSiteIdx) {
                                    final int testSite = testSites.getQuick(testSiteIdx);
                                    final FeatureElement.ElementType type = element.type();
                                    if (type == FeatureElement.ElementType.Empty) {
                                        if (testSite >= 0) {
                                            final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                            if (newInstance2.addTest(container, BitSetTypes.Empty, testSite, !element.not())) {
                                                replaceNewInstances.add(newInstance2);
                                            }
                                        }
                                        else if (element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Friend) {
                                        if (testSite >= 0) {
                                            final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                            if (newInstance2.addTest(container, BitSetTypes.Who, testSite, !element.not(), player)) {
                                                replaceNewInstances.add(newInstance2);
                                            }
                                        }
                                        else if (element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Enemy) {
                                        if (element.not()) {
                                            if (testSite < 0) {
                                                replaceNewInstances.add(new FeatureInstance(instance));
                                            }
                                            else if (game.players().count() == 2) {
                                                final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                                if (newInstance2.addTest(container, BitSetTypes.Who, testSite, false, (player == 1) ? 2 : 1)) {
                                                    replaceNewInstances.add(newInstance2);
                                                }
                                            }
                                            else {
                                                FeatureInstance newInstance2 = new FeatureInstance(instance);
                                                if (newInstance2.addTest(container, BitSetTypes.Empty, testSite, true)) {
                                                    replaceNewInstances.add(newInstance2);
                                                }
                                                newInstance2 = new FeatureInstance(instance);
                                                if (newInstance2.addTest(container, BitSetTypes.Who, testSite, true, player)) {
                                                    replaceNewInstances.add(newInstance2);
                                                }
                                            }
                                        }
                                        else if (testSite >= 0) {
                                            if (game.players().count() == 2) {
                                                final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                                if (newInstance2.addTest(container, BitSetTypes.Who, testSite, true, (player == 1) ? 2 : 1)) {
                                                    replaceNewInstances.add(newInstance2);
                                                }
                                            }
                                            else {
                                                final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                                if (newInstance2.addTest(container, BitSetTypes.Empty, testSite, false) && newInstance2.addTest(container, BitSetTypes.Who, testSite, false, player)) {
                                                    replaceNewInstances.add(newInstance2);
                                                }
                                            }
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Off) {
                                        if (testSite < 0 != element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Any) {
                                        replaceNewInstances.add(new FeatureInstance(instance));
                                    }
                                    else if (type == FeatureElement.ElementType.P1) {
                                        if (testSite >= 0) {
                                            final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                            if (newInstance2.addTest(container, BitSetTypes.Who, testSite, !element.not(), 1)) {
                                                replaceNewInstances.add(newInstance2);
                                            }
                                        }
                                        else if (element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.P2) {
                                        if (testSite >= 0) {
                                            final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                            if (newInstance2.addTest(container, BitSetTypes.Who, testSite, !element.not(), 2)) {
                                                replaceNewInstances.add(newInstance2);
                                            }
                                        }
                                        else if (element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Item) {
                                        if (testSite >= 0) {
                                            final FeatureInstance newInstance2 = new FeatureInstance(instance);
                                            if (newInstance2.addTest(container, BitSetTypes.What, testSite, !element.not(), element.itemIndex())) {
                                                replaceNewInstances.add(newInstance2);
                                            }
                                        }
                                        else if (element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.IsPos) {
                                        if (testSite == element.itemIndex() != element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else if (type == FeatureElement.ElementType.Connectivity) {
                                        if (testSite >= 0 && sites.get(testSite).sortedOrthos().length == element.itemIndex() && sites.get(testSite).sortedOrthos().length == element.itemIndex() != element.not()) {
                                            replaceNewInstances.add(new FeatureInstance(instance));
                                        }
                                    }
                                    else {
                                        System.err.println("Warning: Element Type " + type + " not supported by Feature.instantiateFeature()");
                                    }
                                }
                            }
                            instancesWithElements = replaceNewInstances;
                        }
                        if (allElementsAbsolute) {
                            moreRotationsRelevant = false;
                            if (this instanceof AbsoluteFeature) {
                                moreSitesRelevant = false;
                            }
                        }
                        instances.addAll(instancesWithElements);
                        if (!moreRotationsRelevant) {
                            break;
                        }
                    }
                    if (!moreReflectionsRelevant) {
                        break;
                    }
                }
                if (!moreSitesRelevant) {
                    break;
                }
            }
        }
        return FeatureInstance.deduplicate(instances);
    }
    
    public static Feature combineFeatures(final Game game, final FeatureInstance a, final FeatureInstance b) {
        final Feature featureA = a.feature();
        final Feature featureB = b.feature();
        final Pattern patternA = featureA.pattern();
        final Pattern patternB = featureB.pattern();
        final float requiredBRotation = b.reflection() * b.rotation() - a.reflection() * a.rotation();
        final Pattern modifiedPatternA = new Pattern(patternA);
        modifiedPatternA.applyReflection(a.reflection());
        final Pattern modifiedPatternB = new Pattern(patternB);
        modifiedPatternB.applyReflection(b.reflection());
        modifiedPatternB.applyRotation(requiredBRotation);
        final List<? extends TopologyElement> sites = game.graphPlayElements();
        Path anchorsPath;
        Walk anchorsWalk;
        if (a.anchorSite() != b.anchorSite()) {
            anchorsPath = GraphSearch.shortestPathTo(game, sites.get(a.anchorSite()), sites.get(b.anchorSite()));
            if (anchorsPath == null) {
                return a.feature().rotatedCopy(0.0f);
            }
            anchorsWalk = anchorsPath.walk();
            anchorsWalk.applyRotation(-a.rotation() * a.reflection());
            modifiedPatternB.prependWalkWithCorrection(anchorsWalk, anchorsPath, a.rotation(), a.reflection());
        }
        else {
            anchorsPath = null;
            anchorsWalk = null;
        }
        final Pattern newPattern = Pattern.merge(modifiedPatternA, modifiedPatternB);
        if (featureA instanceof AbsoluteFeature && featureB instanceof AbsoluteFeature) {
            final AbsoluteFeature absA = (AbsoluteFeature)featureA;
            final AbsoluteFeature absB = (AbsoluteFeature)featureB;
            final AbsoluteFeature newFeature = new AbsoluteFeature(newPattern, Math.max(absA.toPosition, absB.toPosition), Math.max(absA.fromPosition, absB.fromPosition));
            newFeature.normalise(game);
            newFeature.pattern().removeRedundancies();
            if (!newFeature.pattern().isConsistent()) {
                System.err.println("Generated inconsistent pattern: " + newPattern);
                System.err.println("active feature A = " + featureA);
                System.err.println("rot A = " + a.rotation());
                System.err.println("ref A = " + a.reflection());
                System.err.println("anchor A = " + a.anchorSite());
                System.err.println("active feature B = " + featureB);
                System.err.println("rot B = " + b.rotation());
                System.err.println("ref B = " + b.reflection());
                System.err.println("anchor B = " + b.anchorSite());
            }
            return newFeature;
        }
        if (featureA instanceof RelativeFeature && featureB instanceof RelativeFeature) {
            final RelativeFeature relA = (RelativeFeature)featureA;
            final RelativeFeature relB = (RelativeFeature)featureB;
            Walk newToPosition = null;
            if (relA.toPosition() != null) {
                newToPosition = new Walk(relA.toPosition());
                newToPosition.applyReflection(a.reflection());
            }
            else if (relB.toPosition() != null) {
                newToPosition = new Walk(relB.toPosition());
                newToPosition.applyReflection(b.reflection());
                newToPosition.applyRotation(requiredBRotation);
                if (anchorsWalk != null) {
                    newToPosition.prependWalkWithCorrection(anchorsWalk, anchorsPath, a.rotation(), a.reflection());
                }
            }
            Walk newFromPosition = null;
            if (relA.fromPosition() != null) {
                newFromPosition = new Walk(relA.fromPosition());
                newFromPosition.applyReflection(a.reflection());
            }
            else if (relB.fromPosition() != null) {
                newFromPosition = new Walk(relB.fromPosition());
                newFromPosition.applyReflection(b.reflection());
                newFromPosition.applyRotation(requiredBRotation);
                if (anchorsWalk != null) {
                    newFromPosition.prependWalkWithCorrection(anchorsWalk, anchorsPath, a.rotation(), a.reflection());
                }
            }
            if (featureA.graphElementType != featureB.graphElementType) {
                System.err.println("WARNING: combining two features for different graph element types!");
            }
            final RelativeFeature newFeature2 = new RelativeFeature(newPattern, newToPosition, newFromPosition);
            newFeature2.graphElementType = featureA.graphElementType;
            newFeature2.normalise(game);
            newFeature2.pattern().removeRedundancies();
            if (!newFeature2.pattern().isConsistent()) {
                System.err.println("Generated inconsistent pattern: " + newPattern);
                System.err.println("active feature A = " + featureA);
                System.err.println("rot A = " + a.rotation());
                System.err.println("ref A = " + a.reflection());
                System.err.println("anchor A = " + a.anchorSite());
                System.err.println("active feature B = " + featureB);
                System.err.println("rot B = " + b.rotation());
                System.err.println("ref B = " + b.reflection());
                System.err.println("anchor B = " + b.anchorSite());
            }
            return newFeature2;
        }
        System.err.println("WARNING: Feature.combineFeatures() returning null!");
        return null;
    }
    
    public void normalise(final Game game) {
        final TFloatArrayList allGameRotations = Walk.allGameRotations(game);
        final float turnEqualTolerance = (allGameRotations.getQuick(1) - allGameRotations.getQuick(0)) / 100.0f;
        final TFloatArrayList allowedRotations = this.pattern.allowedRotations();
        if (allowedRotations != null) {
            for (int i = 0; i < allowedRotations.size(); ++i) {
                final float allowedRot = allowedRotations.getQuick(i);
                for (int j = 0; j < allGameRotations.size(); ++j) {
                    if (Math.abs(allowedRot - allGameRotations.getQuick(j)) < turnEqualTolerance) {
                        allowedRotations.setQuick(i, allGameRotations.getQuick(j));
                        break;
                    }
                    if (Math.abs(allGameRotations.getQuick(j) + allowedRot) < turnEqualTolerance) {
                        allowedRotations.setQuick(i, -allGameRotations.getQuick(j));
                        break;
                    }
                }
            }
        }
        for (final FeatureElement featureElement : this.pattern.featureElements()) {
            if (featureElement instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)featureElement;
                final Walk walk = rel.walk();
                for (int k = 0; k < walk.steps().size(); ++k) {
                    float turn;
                    for (turn = walk.steps().getQuick(k); turn < -1.0f; ++turn) {}
                    while (turn > 1.0f) {
                        --turn;
                    }
                    walk.steps().setQuick(k, turn);
                }
            }
        }
        if (this instanceof RelativeFeature) {
            final RelativeFeature relFeature = (RelativeFeature)this;
            for (final Walk walk2 : new Walk[] { relFeature.fromPosition, relFeature.toPosition, relFeature.lastFromPosition, relFeature.lastToPosition }) {
                if (walk2 != null) {
                    for (int l = 0; l < walk2.steps().size(); ++l) {
                        float turn2;
                        for (turn2 = walk2.steps().getQuick(l); turn2 < -1.0f; ++turn2) {}
                        while (turn2 > 1.0f) {
                            --turn2;
                        }
                        walk2.steps().setQuick(l, turn2);
                    }
                }
            }
        }
        if (allowedRotations == null || allowedRotations.equals(allGameRotations)) {
            float mostCommonTurn = Float.MAX_VALUE;
            int numOccurrences = 0;
            final Map<Float, Integer> occurrencesMap = new HashMap<>();
            for (final FeatureElement featureElement2 : this.pattern.featureElements()) {
                if (featureElement2 instanceof RelativeFeatureElement) {
                    final RelativeFeatureElement rel2 = (RelativeFeatureElement)featureElement2;
                    final Walk walk3 = rel2.walk();
                    if (walk3.steps().size() <= 0) {
                        continue;
                    }
                    final float turn3 = walk3.steps().getQuick(0);
                    final int currentOccurrences = occurrencesMap.getOrDefault(turn3, 0);
                    occurrencesMap.put(turn3, currentOccurrences + 1);
                    if (currentOccurrences + 1 > numOccurrences) {
                        numOccurrences = currentOccurrences + 1;
                        mostCommonTurn = turn3;
                    }
                    else {
                        if (currentOccurrences + 1 != numOccurrences) {
                            continue;
                        }
                        mostCommonTurn = Math.min(mostCommonTurn, turn3);
                    }
                }
            }
            if (this instanceof RelativeFeature) {
                final RelativeFeature relFeature2 = (RelativeFeature)this;
                for (final Walk walk4 : new Walk[] { relFeature2.fromPosition, relFeature2.toPosition, relFeature2.lastFromPosition, relFeature2.lastToPosition }) {
                    if (walk4 != null && !walk4.steps().isEmpty()) {
                        final float turn4 = walk4.steps().getQuick(0);
                        final int currentOccurrences2 = occurrencesMap.getOrDefault(turn4, 0);
                        occurrencesMap.put(turn4, currentOccurrences2 + 1);
                        if (currentOccurrences2 + 1 > numOccurrences) {
                            numOccurrences = currentOccurrences2 + 1;
                            mostCommonTurn = turn4;
                        }
                        else if (currentOccurrences2 + 1 == numOccurrences) {
                            mostCommonTurn = Math.min(mostCommonTurn, turn4);
                        }
                    }
                }
            }
            for (final FeatureElement featureElement2 : this.pattern.featureElements()) {
                if (featureElement2 instanceof RelativeFeatureElement) {
                    final RelativeFeatureElement rel2 = (RelativeFeatureElement)featureElement2;
                    final Walk walk3 = rel2.walk();
                    if (walk3.steps().size() <= 0) {
                        continue;
                    }
                    walk3.steps().set(0, walk3.steps().getQuick(0) - mostCommonTurn);
                }
            }
            if (this instanceof RelativeFeature) {
                final RelativeFeature relFeature2 = (RelativeFeature)this;
                for (final Walk walk4 : new Walk[] { relFeature2.fromPosition, relFeature2.toPosition, relFeature2.lastFromPosition, relFeature2.lastToPosition }) {
                    if (walk4 != null && !walk4.steps().isEmpty()) {
                        walk4.steps().set(0, walk4.steps().getQuick(0) - mostCommonTurn);
                    }
                }
            }
        }
        for (final FeatureElement featureElement : this.pattern.featureElements()) {
            if (featureElement instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)featureElement;
                final TFloatArrayList steps = rel.walk().steps();
                for (int k = 0; k < steps.size(); ++k) {
                    if (steps.getQuick(k) > 0.5f) {
                        steps.setQuick(k, steps.getQuick(k) - 1.0f);
                    }
                    else if (steps.getQuick(k) < -0.5f) {
                        steps.setQuick(k, steps.getQuick(k) + 1.0f);
                    }
                }
            }
        }
        if (this instanceof RelativeFeature) {
            final RelativeFeature relFeature = (RelativeFeature)this;
            for (final Walk walk2 : new Walk[] { relFeature.fromPosition, relFeature.toPosition, relFeature.lastFromPosition, relFeature.lastToPosition }) {
                if (walk2 != null) {
                    final TFloatArrayList steps2 = walk2.steps();
                    for (int m = 0; m < steps2.size(); ++m) {
                        if (steps2.getQuick(m) > 0.5f) {
                            steps2.setQuick(m, steps2.getQuick(m) - 1.0f);
                        }
                        else if (steps2.getQuick(m) < -0.5f) {
                            steps2.setQuick(m, steps2.getQuick(m) + 1.0f);
                        }
                    }
                }
            }
        }
        if (this.pattern.allowsReflection()) {
            boolean havePositiveTurns = false;
            for (final FeatureElement featureElement3 : this.pattern.featureElements()) {
                if (featureElement3 instanceof RelativeFeatureElement) {
                    final RelativeFeatureElement rel3 = (RelativeFeatureElement)featureElement3;
                    final TFloatArrayList steps3 = rel3.walk().steps();
                    for (int l = 0; l < steps3.size(); ++l) {
                        if (steps3.getQuick(l) > 0.0f) {
                            havePositiveTurns = true;
                            break;
                        }
                    }
                }
                if (havePositiveTurns) {
                    break;
                }
            }
            if (this instanceof RelativeFeature) {
                final RelativeFeature relFeature3 = (RelativeFeature)this;
                for (final Walk walk5 : new Walk[] { relFeature3.fromPosition, relFeature3.toPosition, relFeature3.lastFromPosition, relFeature3.lastToPosition }) {
                    if (walk5 != null) {
                        final TFloatArrayList steps4 = walk5.steps();
                        for (int i2 = 0; i2 < steps4.size(); ++i2) {
                            if (steps4.getQuick(i2) > 0.0f) {
                                havePositiveTurns = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!havePositiveTurns) {
                for (final FeatureElement featureElement3 : this.pattern.featureElements()) {
                    if (featureElement3 instanceof RelativeFeatureElement) {
                        final RelativeFeatureElement rel3 = (RelativeFeatureElement)featureElement3;
                        final TFloatArrayList steps3 = rel3.walk().steps();
                        for (int l = 0; l < steps3.size(); ++l) {
                            steps3.setQuick(l, steps3.getQuick(l) * -1.0f);
                        }
                    }
                }
                if (this instanceof RelativeFeature) {
                    final RelativeFeature relFeature3 = (RelativeFeature)this;
                    for (final Walk walk5 : new Walk[] { relFeature3.fromPosition, relFeature3.toPosition, relFeature3.lastFromPosition, relFeature3.lastToPosition }) {
                        if (walk5 != null) {
                            final TFloatArrayList steps4 = walk5.steps();
                            for (int i2 = 0; i2 < steps4.size(); ++i2) {
                                steps4.setQuick(i2, steps4.getQuick(i2) * -1.0f);
                            }
                        }
                    }
                }
            }
        }
        for (final FeatureElement featureElement : this.pattern.featureElements()) {
            if (featureElement instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)featureElement;
                final TFloatArrayList steps = rel.walk().steps();
                for (int k = 0; k < steps.size(); ++k) {
                    final float turn = steps.getQuick(k);
                    if (turn == -0.0f) {
                        steps.setQuick(k, 0.0f);
                    }
                    else {
                        for (int j2 = 0; j2 < allGameRotations.size(); ++j2) {
                            if (Math.abs(turn - allGameRotations.getQuick(j2)) < turnEqualTolerance) {
                                steps.setQuick(k, allGameRotations.getQuick(j2));
                                break;
                            }
                            if (Math.abs(allGameRotations.getQuick(j2) + turn) < turnEqualTolerance) {
                                steps.setQuick(k, -allGameRotations.getQuick(j2));
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (this instanceof RelativeFeature) {
            final RelativeFeature relFeature = (RelativeFeature)this;
            for (final Walk walk2 : new Walk[] { relFeature.fromPosition, relFeature.toPosition, relFeature.lastFromPosition, relFeature.lastToPosition }) {
                if (walk2 != null) {
                    final TFloatArrayList steps2 = walk2.steps();
                    for (int m = 0; m < steps2.size(); ++m) {
                        final float turn3 = steps2.getQuick(m);
                        if (turn3 == -0.0f) {
                            steps2.setQuick(m, 0.0f);
                        }
                        else {
                            for (int j3 = 0; j3 < allGameRotations.size(); ++j3) {
                                if (Math.abs(turn3 - allGameRotations.getQuick(j3)) < turnEqualTolerance) {
                                    steps2.setQuick(m, allGameRotations.getQuick(j3));
                                    break;
                                }
                                if (Math.abs(allGameRotations.getQuick(j3) + turn3) < turnEqualTolerance) {
                                    steps2.setQuick(m, -allGameRotations.getQuick(j3));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static List<Feature> deduplicate(final List<Feature> features) {
        final List<Feature> deduplicated = new ArrayList<>(features.size());
        for (final Feature feature : features) {
            boolean foundDuplicate = false;
            for (final Feature alreadyAdded : deduplicated) {
                if (alreadyAdded.equals(feature)) {
                    foundDuplicate = true;
                    break;
                }
            }
            if (!foundDuplicate) {
                deduplicated.add(feature);
            }
        }
        return deduplicated;
    }
    
    public static Feature fromString(final String string) {
        if (string.contains("abs:")) {
            return new AbsoluteFeature(string);
        }
        return new RelativeFeature(string);
    }
    
    public String comment() {
        return this.comment;
    }
    
    public Feature setComment(final String newComment) {
        this.comment = newComment;
        return this;
    }
    
    public SiteType graphElementType() {
        return this.graphElementType;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Feature)) {
            return false;
        }
        final Feature otherFeature = (Feature)other;
        return this.pattern.equals(otherFeature.pattern);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.pattern == null) ? 0 : this.pattern.hashCode());
        return result;
    }
    
    public boolean equalsIgnoreRotRef(final Feature other) {
        return this.pattern.equalsIgnoreRotRef(other.pattern);
    }
    
    public int hashCodeIgnoreRotRef() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.pattern == null) ? 0 : this.pattern.hashCodeIgnoreRotRef());
        return result;
    }
    
    public enum BitSetTypes
    {
        Empty, 
        Who, 
        What, 
        None
    }
}
