// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.other.Regions;
import gnu.trove.list.array.TIntArrayList;
import collections.FVector;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import util.Context;

public class OwnRegionsCount extends HeuristicTerm
{
    private int[][] regionIndices;
    
    public OwnRegionsCount(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight) {
        super(transformation, weight);
        this.regionIndices = null;
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        if (this.regionIndices[player].length == 0) {
            return 0.0f;
        }
        final Regions[] regions = context.game().equipment().regions();
        int sumCounts = 0;
        for (int i = 0; i < this.regionIndices[player].length; ++i) {
            final int regionIdx = this.regionIndices[player][i];
            final Regions region = regions[regionIdx];
            final int[] eval;
            final int[] sites = eval = region.eval(context);
            for (final int site : eval) {
                sumCounts += context.containerState(0).countCell(site);
            }
        }
        return sumCounts;
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
        this.regionIndices = new int[game.players().count() + 1][];
        for (int p = 1; p <= game.players().count(); ++p) {
            final TIntArrayList relevantIndices = new TIntArrayList();
            for (int i = 0; i < game.equipment().regions().length; ++i) {
                final Regions region = game.equipment().regions()[i];
                if (region.owner() == p) {
                    final int[] distances = game.distancesToRegions()[i];
                    if (distances != null) {
                        relevantIndices.add(i);
                    }
                }
            }
            this.regionIndices[p] = relevantIndices.toArray();
        }
    }
    
    public static boolean isApplicableToGame(final Game game) {
        if (game.distancesToRegions() == null) {
            return false;
        }
        final Regions[] regions = game.equipment().regions();
        if (regions.length == 1) {
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
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(ownRegionsCount");
        if (this.transformation != null) {
            sb.append(" transformation:" + this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:" + this.weight);
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
            sb.append("(ownRegionsCount");
            if (this.transformation != null) {
                sb.append(" transformation:" + this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:" + this.weight);
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}
