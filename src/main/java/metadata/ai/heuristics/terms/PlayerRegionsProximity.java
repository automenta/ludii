// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.other.Regions;
import gnu.trove.list.array.TIntArrayList;
import main.StringRoutines;
import main.collections.FVector;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import metadata.ai.misc.Pair;
import util.Context;
import util.locations.Location;
import util.state.owned.Owned;

import java.util.List;

public class PlayerRegionsProximity extends HeuristicTerm
{
    private final String[] pieceWeightNames;
    private final float[] gameAgnosticWeightsArray;
    private FVector pieceWeights;
    private int maxDistance;
    private final int regionPlayer;
    private int[] regionIndices;
    
    public PlayerRegionsProximity(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight, @Name final Integer player, @Name @Opt final Pair[] pieceWeights) {
        super(transformation, weight);
        this.pieceWeights = null;
        this.maxDistance = -1;
        this.regionIndices = null;
        this.regionPlayer = player;
        if (pieceWeights == null) {
            this.pieceWeightNames = new String[] { "" };
            this.gameAgnosticWeightsArray = new float[] { 1.0f };
        }
        else {
            this.pieceWeightNames = new String[pieceWeights.length];
            this.gameAgnosticWeightsArray = new float[pieceWeights.length];
            for (int i = 0; i < pieceWeights.length; ++i) {
                this.pieceWeightNames[i] = pieceWeights[i].key();
                this.gameAgnosticWeightsArray[i] = pieceWeights[i].floatVal();
            }
        }
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        if (this.maxDistance == 0) {
            return 0.0f;
        }
        final int[][] distances = context.game().distancesToRegions();
        final Owned owned = context.state().owned();
        final List<? extends Location>[] pieces = owned.positions(player);
        float value = 0.0f;
        for (int i = 0; i < pieces.length; ++i) {
            final float pieceWeight = this.pieceWeights.get(owned.reverseMap(player, i));
            if (Math.abs(pieceWeight) >= absWeightThreshold) {
                for (final Location position : pieces[i]) {
                    final int site = position.site();
                    if (site >= distances.length) {
                        continue;
                    }
                    int minDist = Integer.MAX_VALUE;
                    for (final int regionIdx : this.regionIndices) {
                        final int dist = distances[regionIdx][site];
                        if (dist < minDist) {
                            minDist = dist;
                        }
                    }
                    final float proximity = 1.0f - minDist / (float)this.maxDistance;
                    value += pieceWeight * proximity;
                }
            }
        }
        return value;
    }
    
    @Override
    public FVector computeStateFeatureVector(final Context context, final int player) {
        final FVector featureVector = new FVector(this.pieceWeights.dim());
        if (this.maxDistance != 0) {
            final int[][] distances = context.game().distancesToRegions();
            final Owned owned = context.state().owned();
            final List<? extends Location>[] pieces = owned.positions(player);
            for (int i = 0; i < pieces.length; ++i) {
                final int compIdx = owned.reverseMap(player, i);
                for (final Location position : pieces[i]) {
                    final int site = position.site();
                    if (site >= distances.length) {
                        continue;
                    }
                    int minDist = Integer.MAX_VALUE;
                    for (final int regionIdx : this.regionIndices) {
                        final int dist = distances[regionIdx][site];
                        if (dist < minDist) {
                            minDist = dist;
                        }
                    }
                    final float proximity = 1.0f - minDist / (float)this.maxDistance;
                    featureVector.addToEntry(compIdx, proximity);
                }
            }
        }
        return featureVector;
    }
    
    @Override
    public FVector paramsVector() {
        return this.pieceWeights;
    }
    
    @Override
    public void init(final Game game) {
        this.pieceWeights = HeuristicTerm.pieceWeightsVector(game, this.pieceWeightNames, this.gameAgnosticWeightsArray);
        final TIntArrayList relevantIndices = new TIntArrayList();
        int max = 0;
        for (int i = 0; i < game.equipment().regions().length; ++i) {
            final Regions region = game.equipment().regions()[i];
            if (region.owner() == this.regionPlayer) {
                final int[] distances = game.distancesToRegions()[i];
                if (distances != null) {
                    relevantIndices.add(i);
                    for (int j = 0; j < distances.length; ++j) {
                        if (distances[j] > max) {
                            max = distances[j];
                        }
                    }
                }
            }
        }
        this.maxDistance = max;
        this.regionIndices = relevantIndices.toArray();
    }
    
    @Override
    public int updateParams(final Game game, final FVector newParams, final int startIdx) {
        final int retVal = super.updateParams(game, newParams, startIdx);
        HeuristicTerm.updateGameAgnosticWeights(game, this.pieceWeights, this.pieceWeightNames, this.gameAgnosticWeightsArray);
        return retVal;
    }
    
    public static boolean isApplicableToGame(final Game game) {
        if (game.distancesToRegions() == null) {
            return false;
        }
        if (game.equipment().components().length <= 1) {
            return false;
        }
        final Regions[] regions = game.equipment().regions();
        if (regions.length == 0) {
            return false;
        }
        boolean foundOwnedRegion = false;
        for (final Regions region : regions) {
            if (region.owner() > 0 && region.owner() <= game.players().count()) {
                foundOwnedRegion = true;
                break;
            }
        }
        return foundOwnedRegion;
    }
    
    public int regionPlayer() {
        return this.regionPlayer;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(playerRegionsProximity");
        if (this.transformation != null) {
            sb.append(" transformation:" + this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:" + this.weight);
        }
        sb.append(" player:" + this.regionPlayer);
        if (this.pieceWeightNames.length > 1 || (this.pieceWeightNames.length == 1 && this.pieceWeightNames[0].length() > 0)) {
            sb.append(" pieceWeights:{\n");
            for (int i = 0; i < this.pieceWeightNames.length; ++i) {
                sb.append("        (pair " + StringRoutines.quote(this.pieceWeightNames[i]) + " " + this.gameAgnosticWeightsArray[i] + ")\n");
            }
            sb.append("    }");
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public String toStringThresholded(final float threshold) {
        boolean shouldPrint = false;
        boolean haveRelevantPieces = false;
        final StringBuilder pieceWeightsSb = new StringBuilder();
        if (this.pieceWeightNames.length > 1 || (this.pieceWeightNames.length == 1 && this.pieceWeightNames[0].length() > 0)) {
            for (int i = 0; i < this.pieceWeightNames.length; ++i) {
                if (Math.abs(this.weight * this.gameAgnosticWeightsArray[i]) >= threshold) {
                    pieceWeightsSb.append("        (pair " + StringRoutines.quote(this.pieceWeightNames[i]) + " " + this.gameAgnosticWeightsArray[i] + ")\n");
                    haveRelevantPieces = true;
                    shouldPrint = true;
                }
            }
        }
        else if (Math.abs(this.weight) >= threshold) {
            shouldPrint = true;
        }
        if (shouldPrint) {
            final StringBuilder sb = new StringBuilder();
            sb.append("(playerRegionsProximity");
            if (this.transformation != null) {
                sb.append(" transformation:" + this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:" + this.weight);
            }
            sb.append(" player:" + this.regionPlayer);
            if (haveRelevantPieces) {
                sb.append(" pieceWeights:{\n");
                sb.append(pieceWeightsSb);
                sb.append("    }");
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}
