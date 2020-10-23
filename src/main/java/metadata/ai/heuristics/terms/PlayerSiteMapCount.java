// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.other.Map;
import collections.FVector;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import util.Context;

public class PlayerSiteMapCount extends HeuristicTerm
{
    public PlayerSiteMapCount(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight) {
        super(transformation, weight);
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        int sumCounts = 0;
        final Map[] maps2;
        final Map[] maps = maps2 = context.game().equipment().maps();
        for (final Map map : maps2) {
            final int playerVal = map.to(player);
            if (playerVal != -1 && playerVal != map.noEntryValue()) {
                sumCounts += context.containerState(0).countCell(playerVal);
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
    
    public static boolean isApplicableToGame(final Game game) {
        final Map[] maps = game.equipment().maps();
        if (maps.length == 0) {
            return false;
        }
        final int numPlayers = game.players().count();
        boolean foundPlayerMapping = false;
        for (final Map map : maps) {
            for (int p = 1; p <= numPlayers; ++p) {
                final int val = map.to(p);
                if (val != -1 && val != map.noEntryValue()) {
                    foundPlayerMapping = true;
                    break;
                }
            }
            if (foundPlayerMapping) {
                break;
            }
        }
        return foundPlayerMapping;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(playerSiteMapCount");
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
            sb.append("(playerSiteMapCount");
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
