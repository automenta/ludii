// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import collections.FVector;
import collections.ListUtils;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.is.line.IsLine;
import game.functions.ints.IntFunction;
import game.rules.phase.Phase;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.GraphElement;
import game.util.graph.Radial;
import gnu.trove.list.array.TFloatArrayList;
import main.ReflectionUtils;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Ludeme;
import util.Trial;
import util.locations.Location;
import util.state.containerState.ContainerState;
import util.state.owned.Owned;

import java.lang.reflect.Field;
import java.util.*;

public class LineCompletionHeuristic extends HeuristicTerm
{
    private final boolean autoComputeTargetLength;
    private int targetLength;
    
    public LineCompletionHeuristic(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight, @Name @Opt final Integer targetLength) {
        super(transformation, weight);
        if (targetLength == null) {
            this.autoComputeTargetLength = true;
        }
        else {
            this.autoComputeTargetLength = false;
            this.targetLength = targetLength;
        }
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        final Game game = context.game();
        final Owned owned = context.state().owned();
        final List<? extends Location>[] pieces = owned.positions(player);
        final List<? extends TopologyElement> sites = game.graphPlayElements();
        final boolean[] ignore = new boolean[sites.size()];
        final SiteType siteType = game.board().defaultSite();
        final TFloatArrayList lineValues = new TFloatArrayList();
        for (final List<? extends Location> piecesList : pieces) {
            for (final Location piecePos : piecesList) {
                final int pieceSite = piecePos.site();
                if (context.containerId()[pieceSite] > 0) {
                    continue;
                }
                final ContainerState state = context.state().containerStates()[0];
                final List<Radial> radials = game.board().graph().trajectories().radials(piecePos.siteType(), pieceSite).distinctInDirection(AbsoluteDirection.Adjacent);
                for (final Radial radial : radials) {
                    final GraphElement[] path = radial.steps();
                    final List<Radial> opposites = radial.opposites();
                    final List<GraphElement[]> oppositePaths = new ArrayList<>();
                    if (opposites != null) {
                        for (final Radial opposite : opposites) {
                            oppositePaths.add(opposite.steps());
                        }
                    }
                    else {
                        oppositePaths.add(new GraphElement[0]);
                    }
                    for (final GraphElement[] oppositePath : oppositePaths) {
                        final boolean[] endPathsBlocked = new boolean[this.targetLength];
                        final boolean[] endOppositePathsBlocked = new boolean[this.targetLength];
                        final int[] potentialLineLengths = new int[this.targetLength];
                        final int[] realPieces = new int[this.targetLength];
                        Arrays.fill(potentialLineLengths, 1);
                        Arrays.fill(realPieces, 1);
                        final int indexBound = Math.min(path.length, this.targetLength + 1);
                        int indexPath = 1;
                        while (indexPath < indexBound) {
                            final int site = path[indexPath].id();
                            final int who = state.who(site, siteType);
                            if (ignore[site]) {
                                break;
                            }
                            if (who != 0 && who != player) {
                                assert !endPathsBlocked[this.targetLength - indexPath];
                                endPathsBlocked[this.targetLength - indexPath] = true;
                                break;
                            }
                            else {
                                for (int j = 0; j < this.targetLength - indexPath; ++j) {
                                    final int[] array2 = potentialLineLengths;
                                    final int n = j;
                                    ++array2[n];
                                    if (who == player) {
                                        final int[] array3 = realPieces;
                                        final int n2 = j;
                                        ++array3[n2];
                                    }
                                }
                                ++indexPath;
                            }
                        }
                        final int oppositeIndexBound = Math.min(oppositePath.length, this.targetLength + 1);
                        int indexPath2 = 1;
                        while (indexPath2 < oppositeIndexBound) {
                            final int site2 = oppositePath[indexPath2].id();
                            final int who2 = state.who(site2, siteType);
                            if (ignore[site2]) {
                                break;
                            }
                            if (who2 != 0 && who2 != player) {
                                assert !endOppositePathsBlocked[indexPath2 - 1];
                                endOppositePathsBlocked[indexPath2 - 1] = true;
                                break;
                            }
                            else {
                                for (int i = indexPath2; i < this.targetLength; ++i) {
                                    final int[] array4 = potentialLineLengths;
                                    final int n3 = i;
                                    ++array4[n3];
                                    if (who2 == player) {
                                        final int[] array5 = realPieces;
                                        final int n4 = i;
                                        ++array5[n4];
                                    }
                                }
                                ++indexPath2;
                            }
                        }
                        for (int k = 0; k < potentialLineLengths.length; ++k) {
                            if (potentialLineLengths[k] == this.targetLength) {
                                float value = realPieces[k] / (float)potentialLineLengths[k];
                                if (endPathsBlocked[k]) {
                                    value *= 0.5f;
                                }
                                if (endOppositePathsBlocked[k]) {
                                    value *= 0.5f;
                                }
                                lineValues.add(value);
                            }
                        }
                    }
                }
                ignore[pieceSite] = true;
            }
        }
        final int argMax = ListUtils.argMax(lineValues);
        final float maxVal = lineValues.getQuick(argMax);
        lineValues.setQuick(argMax, -1.0f);
        final int secondArgMax = ListUtils.argMax(lineValues);
        final float secondMaxVal = lineValues.getQuick(secondArgMax);
        return maxVal + secondMaxVal / 2.0f;
    }
    
    @Override
    public FVector computeStateFeatureVector(final Context context, final int player) {
        final FVector featureVector = new FVector(1);
        featureVector.set(0, this.computeValue(context, player, -1.0f));
        return featureVector;
    }
    
    @Override
    public FVector paramsVector() {
        return null;
    }
    
    @Override
    public void init(final Game game) {
        if (this.autoComputeTargetLength) {
            final List<IsLine> lineLudemes = new ArrayList<>();
            if (game.rules().end() != null) {
                collectLineLudemes(lineLudemes, game.rules().end(), new HashMap<>());
            }
            for (final Phase phase : game.rules().phases()) {
                if (phase != null && phase.end() != null) {
                    collectLineLudemes(lineLudemes, phase.end(), new HashMap<>());
                }
            }
            int maxTargetLength = 2;
            if (lineLudemes.isEmpty()) {
                final Topology graph = game.board().topology();
                final SiteType siteType = game.board().defaultSite();
                final int[] distancesToCentre = graph.distancesToCentre(siteType);
                if (distancesToCentre != null) {
                    for (final int dist : distancesToCentre) {
                        maxTargetLength = Math.max(maxTargetLength, dist);
                    }
                }
                else {
                    maxTargetLength = 15;
                }
            }
            else {
                final Context dummyContext = new Context(game, new Trial(game));
                for (final IsLine line : lineLudemes) {
                    maxTargetLength = Math.max(maxTargetLength, line.length().eval(dummyContext));
                }
            }
            this.targetLength = maxTargetLength;
        }
    }
    
    public static boolean isApplicableToGame(final Game game) {
        if (game.isGraphGame()) {
            return false;
        }
        final Component[] components = game.equipment().components();
        return components.length > 1;
    }
    
    private static void collectLineLudemes(final List<IsLine> outList, final Ludeme ludeme, final Map<Object, Set<String>> visited) {
        final Class<? extends Ludeme> clazz = ludeme.getClass();
        final List<Field> fields = ReflectionUtils.getAllFields(clazz);
        try {
            for (final Field field : fields) {
                if (field.getName().contains("$")) {
                    continue;
                }
                field.setAccessible(true);
                if ((field.getModifiers() & 0x8) != 0x0) {
                    continue;
                }
                if (visited.containsKey(ludeme) && visited.get(ludeme).contains(field.getName())) {
                    continue;
                }
                final Object value = field.get(ludeme);
                if (!visited.containsKey(ludeme)) {
                    visited.put(ludeme, new HashSet<>());
                }
                visited.get(ludeme).add(field.getName());
                if (value == null) {
                    continue;
                }
                final Class<?> valueClass = value.getClass();
                if (Enum.class.isAssignableFrom(valueClass)) {
                    continue;
                }
                if (Ludeme.class.isAssignableFrom(valueClass)) {
                    if (IsLine.class.isAssignableFrom(valueClass)) {
                        final IsLine line = (IsLine)value;
                        final IntFunction length = line.length();
                        if (length.isStatic()) {
                            outList.add(line);
                        }
                    }
                    collectLineLudemes(outList, (Ludeme)value, visited);
                }
                else if (valueClass.isArray()) {
                    final Object[] castArray;
                    final Object[] array = castArray = ReflectionUtils.castArray(value);
                    for (final Object element : castArray) {
                        if (element != null) {
                            final Class<?> elementClass = element.getClass();
                            if (Ludeme.class.isAssignableFrom(elementClass)) {
                                if (IsLine.class.isAssignableFrom(elementClass)) {
                                    final IsLine line2 = (IsLine)element;
                                    final IntFunction length2 = line2.length();
                                    if (length2.isStatic()) {
                                        outList.add(line2);
                                    }
                                }
                                collectLineLudemes(outList, (Ludeme)element, visited);
                            }
                        }
                    }
                }
                else {
                    if (!Iterable.class.isAssignableFrom(valueClass)) {
                        continue;
                    }
                    final Iterable<?> iterable = (Iterable<?>)value;
                    for (final Object element2 : iterable) {
                        if (element2 != null) {
                            final Class<?> elementClass2 = element2.getClass();
                            if (!Ludeme.class.isAssignableFrom(elementClass2)) {
                                continue;
                            }
                            if (IsLine.class.isAssignableFrom(elementClass2)) {
                                final IsLine line3 = (IsLine)element2;
                                final IntFunction length3 = line3.length();
                                if (length3.isStatic()) {
                                    outList.add(line3);
                                }
                            }
                            collectLineLudemes(outList, (Ludeme)element2, visited);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(lineCompletionHeuristic");
        if (this.transformation != null) {
            sb.append(" transformation:").append(this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:").append(this.weight);
        }
        if (!this.autoComputeTargetLength) {
            sb.append(" targetLength:").append(this.targetLength);
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public String toStringThresholded(final float threshold) {
        boolean shouldPrint = false;
        if (Math.abs(this.weight) >= threshold) {
            shouldPrint = true;
        }
        if (shouldPrint) {
            final StringBuilder sb = new StringBuilder();
            sb.append("(lineCompletionHeuristic");
            if (this.transformation != null) {
                sb.append(" transformation:").append(this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:").append(this.weight);
            }
            if (!this.autoComputeTargetLength) {
                sb.append(" targetLength:").append(this.targetLength);
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}
