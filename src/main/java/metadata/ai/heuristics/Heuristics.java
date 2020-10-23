// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics;

import annotations.Opt;
import collections.FVector;
import game.Game;
import metadata.ai.AIItem;
import metadata.ai.heuristics.terms.HeuristicTerm;
import util.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class Heuristics implements AIItem
{
    protected final HeuristicTerm[] heuristicTerms;
    
    public Heuristics(@Opt final HeuristicTerm term) {
        if (term == null) {
            this.heuristicTerms = new HeuristicTerm[0];
        }
        else {
            this.heuristicTerms = new HeuristicTerm[] { term };
        }
    }
    
    public Heuristics(@Opt final HeuristicTerm[] terms) {
        if (terms == null) {
            this.heuristicTerms = new HeuristicTerm[0];
        }
        else {
            this.heuristicTerms = terms;
        }
    }
    
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        float value = 0.0f;
        for (final HeuristicTerm term : this.heuristicTerms) {
            final float weight = term.weight();
            final float absWeight = Math.abs(weight);
            if (absWeight >= absWeightThreshold) {
                float termOutput = term.computeValue(context, player, absWeightThreshold / absWeight);
                if (term.transformation() != null) {
                    termOutput = term.transformation().transform(context, termOutput);
                }
                value += weight * termOutput;
            }
        }
        return value;
    }
    
    public void init(final Game game) {
        for (final HeuristicTerm term : this.heuristicTerms) {
            term.init(game);
        }
    }
    
    public FVector computeStateFeatureVector(final Context context, final int player) {
        final Game game = context.game();
        final int numPlayers = game.players().count();
        FVector featureVector = new FVector(0);
        for (final HeuristicTerm term : this.heuristicTerms) {
            final FVector vec = term.computeStateFeatureVector(context, player);
            for (int p = 1; p <= numPlayers; ++p) {
                if (p != player) {
                    final FVector oppVector = term.computeStateFeatureVector(context, p);
                    vec.subtract(oppVector);
                }
            }
            for (int j = 0; j < vec.dim(); ++j) {
                if (term.transformation() != null) {
                    vec.set(j, term.transformation().transform(context, vec.get(j)));
                }
            }
            featureVector = FVector.concat(featureVector, vec);
        }
        return featureVector;
    }
    
    public FVector paramsVector() {
        FVector paramsVector = new FVector(0);
        for (final HeuristicTerm term : this.heuristicTerms) {
            final float weight = term.weight();
            final FVector vec = term.paramsVector();
            if (vec == null) {
                paramsVector = paramsVector.append(weight);
            }
            else {
                final FVector weightedVec = new FVector(vec);
                weightedVec.mult(weight);
                paramsVector = FVector.concat(paramsVector, weightedVec);
            }
        }
        return paramsVector;
    }
    
    public void updateParams(final Game game, final FVector newParams, final int startIdx) {
        int currentIdx = startIdx;
        for (final HeuristicTerm term : this.heuristicTerms) {
            currentIdx = term.updateParams(game, newParams, currentIdx);
        }
    }
    
    public HeuristicTerm[] heuristicTerms() {
        return this.heuristicTerms;
    }
    
    public void toFile(final Game game, final String filepath) {
        try (final PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8)) {
            writer.println("(heuristics { \n");
            for (final HeuristicTerm term : this.heuristicTerms) {
                writer.println("    " + term.toString());
            }
            writer.println("} )");
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(heuristics {\n");
        for (final HeuristicTerm term : this.heuristicTerms) {
            sb.append("    ").append(term.toString()).append("\n");
        }
        sb.append("})\n");
        return sb.toString();
    }
    
    public String toStringThresholded(final float thresholdWeight) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(heuristics {\n");
        for (final HeuristicTerm term : this.heuristicTerms) {
            final String termStr = term.toStringThresholded(thresholdWeight);
            if (termStr != null) {
                sb.append("    ").append(termStr).append("\n");
            }
        }
        sb.append("})\n");
        return sb.toString();
    }
}
