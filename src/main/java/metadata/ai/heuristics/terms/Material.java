// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import collections.FVector;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import main.StringRoutines;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import metadata.ai.misc.Pair;
import util.Context;
import util.locations.Location;
import util.state.owned.Owned;

import java.util.List;

public class Material extends HeuristicTerm
{
    private final String[] pieceWeightNames;
    private final float[] gameAgnosticWeightsArray;
    private FVector pieceWeights;
    private int[] handIndices;
    
    public Material(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight, @Name @Opt final Pair[] pieceWeights) {
        super(transformation, weight);
        this.pieceWeights = null;
        this.handIndices = null;
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
        final Owned owned = context.state().owned();
        final List<? extends Location>[] pieces = owned.positions(player);
        float value = 0.0f;
        for (int i = 0; i < pieces.length; ++i) {
            final float pieceWeight = this.pieceWeights.get(owned.reverseMap(player, i));
            if (Math.abs(pieceWeight) >= absWeightThreshold) {
                value += pieceWeight * pieces[i].size();
            }
        }
        if (this.handIndices != null) {
            final List<? extends Location>[] neutralPieces = owned.positions(0);
            for (int j = 0; j < neutralPieces.length; ++j) {
                final float pieceWeight2 = this.pieceWeights.get(owned.reverseMap(0, j));
                if (Math.abs(pieceWeight2) >= absWeightThreshold) {
                    for (final Location pos : neutralPieces[j]) {
                        final int site = pos.site();
                        if (context.game().equipment().containerId()[site] == this.handIndices[player]) {
                            value += pieceWeight2 * context.state().containerStates()[this.handIndices[player]].countCell(site);
                        }
                    }
                }
            }
        }
        return value;
    }
    
    @Override
    public FVector computeStateFeatureVector(final Context context, final int player) {
        final FVector featureVector = new FVector(this.pieceWeights.dim());
        final Owned owned = context.state().owned();
        final List<? extends Location>[] pieces = owned.positions(player);
        for (int i = 0; i < pieces.length; ++i) {
            final int compIdx = owned.reverseMap(player, i);
            featureVector.addToEntry(compIdx, pieces[i].size());
        }
        if (this.handIndices != null) {
            final List<? extends Location>[] neutralPieces = owned.positions(0);
            for (int j = 0; j < neutralPieces.length; ++j) {
                final int compIdx2 = owned.reverseMap(player, j);
                for (final Location pos : neutralPieces[j]) {
                    final int site = pos.site();
                    if (context.game().equipment().containerId()[site] == this.handIndices[player]) {
                        featureVector.addToEntry(compIdx2, context.state().containerStates()[this.handIndices[player]].countCell(site));
                    }
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
        this.computeHandIndices(game);
    }
    
    @Override
    public int updateParams(final Game game, final FVector newParams, final int startIdx) {
        final int retVal = super.updateParams(game, newParams, startIdx);
        HeuristicTerm.updateGameAgnosticWeights(game, this.pieceWeights, this.pieceWeightNames, this.gameAgnosticWeightsArray);
        return retVal;
    }
    
    private void computeHandIndices(final Game game) {
        boolean foundHands = false;
        final int[] handContainerIndices = new int[game.players().count() + 1];
        for (final Container c : game.equipment().containers()) {
            if (c.isHand()) {
                final int owner = c.owner();
                if (owner > 0 && owner < handContainerIndices.length && handContainerIndices[owner] == 0) {
                    foundHands = true;
                    handContainerIndices[owner] = c.index();
                }
            }
        }
        if (!foundHands) {
            this.handIndices = null;
        }
        else {
            this.handIndices = handContainerIndices;
        }
    }
    
    public static boolean isApplicableToGame(final Game game) {
        final Component[] components = game.equipment().components();
        return components.length > 1;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(material");
        if (this.transformation != null) {
            sb.append(" transformation:").append(this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:").append(this.weight);
        }
        if (this.pieceWeightNames.length > 1 || (this.pieceWeightNames.length == 1 && !this.pieceWeightNames[0].isEmpty())) {
            sb.append(" pieceWeights:{\n");
            for (int i = 0; i < this.pieceWeightNames.length; ++i) {
                sb.append("        (pair ").append(StringRoutines.quote(this.pieceWeightNames[i])).append(" ").append(this.gameAgnosticWeightsArray[i]).append(")\n");
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
        if (this.pieceWeightNames.length > 1 || (this.pieceWeightNames.length == 1 && !this.pieceWeightNames[0].isEmpty())) {
            for (int i = 0; i < this.pieceWeightNames.length; ++i) {
                if (Math.abs(this.weight * this.gameAgnosticWeightsArray[i]) >= threshold) {
                    pieceWeightsSb.append("        (pair ").append(StringRoutines.quote(this.pieceWeightNames[i])).append(" ").append(this.gameAgnosticWeightsArray[i]).append(")\n");
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
            sb.append("(material");
            if (this.transformation != null) {
                sb.append(" transformation:").append(this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:").append(this.weight);
            }
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
