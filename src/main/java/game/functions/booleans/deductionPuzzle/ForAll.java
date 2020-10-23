// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.sites.Sites;
import game.types.board.PuzzleElementType;
import topology.Edge;
import topology.TopologyElement;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

public class ForAll extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final PuzzleElementType type;
    private final BooleanFunction constraint;
    
    public ForAll(final PuzzleElementType type, final BooleanFunction constraint) {
        this.type = type;
        this.constraint = constraint;
    }
    
    @Override
    public boolean eval(final Context context) {
        final int saveTo = context.to();
        final int saveFrom = context.to();
        final int saveHint = context.hint();
        final int saveEdge = context.edge();
        if (!this.type.equals(PuzzleElementType.Hint)) {
            final List<? extends TopologyElement> elements = context.topology().getGraphElements(PuzzleElementType.convert(this.type));
            for (int i = 0; i < elements.size(); ++i) {
                final TopologyElement element = elements.get(i);
                context.setFrom(element.index());
                if (!this.constraint.eval(context)) {
                    context.setHint(saveHint);
                    context.setEdge(saveEdge);
                    context.setTo(saveTo);
                    context.setFrom(saveFrom);
                    return false;
                }
            }
        }
        else {
            final Integer[][] regions = context.game().equipment().withHints(context.board().defaultSite());
            final Integer[] hints = context.game().equipment().hints(context.board().defaultSite());
            for (int size = Math.min(regions.length, hints.length), j = 0; j < size; ++j) {
                int nbEdges = 0;
                boolean allEdgesSet = true;
                for (int k = 0; k < regions[j].length; ++k) {
                    nbEdges = 0;
                    final int indexVertex = regions[j][k];
                    final ContainerState ps = context.state().containerStates()[0];
                    final List<Edge> edges = context.game().board().topology().edges();
                    for (int indexEdge = 0; indexEdge < edges.size(); ++indexEdge) {
                        final Edge edge = edges.get(indexEdge);
                        if (edge.containsVertex(indexVertex)) {
                            if (ps.isResolvedEdges(indexEdge)) {
                                nbEdges += ps.whatEdge(indexEdge);
                            }
                            else {
                                allEdgesSet = false;
                            }
                        }
                    }
                }
                if (!allEdgesSet && hints[j] != null && nbEdges < hints[j]) {
                    context.setEdge(hints[j]);
                }
                else {
                    context.setEdge(nbEdges);
                }
                if (regions[j].length > 0) {
                    context.setFrom(regions[j][0]);
                }
                if (regions[j].length > 1) {
                    context.setTo(regions[j][1]);
                }
                if (hints[j] != null) {
                    context.setHint(hints[j]);
                }
                final IntFunction[] setFn = new IntFunction[regions.length];
                for (int h = 0; h < regions[j].length; ++h) {
                    setFn[h] = new IntConstant(regions[j][h]);
                }
                context.setHintRegion(Sites.construct(setFn));
                if (!this.constraint.eval(context)) {
                    context.setHint(saveHint);
                    context.setEdge(saveEdge);
                    context.setTo(saveTo);
                    context.setFrom(saveFrom);
                    return false;
                }
            }
        }
        context.setHint(saveHint);
        context.setEdge(saveEdge);
        context.setTo(saveTo);
        context.setFrom(saveFrom);
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return this.constraint.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.constraint != null) {
            this.constraint.preprocess(game);
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 128L;
        if (this.constraint != null) {
            flags |= this.constraint.gameFlags(game);
        }
        return flags;
    }
    
    public PuzzleElementType type() {
        return this.type;
    }
    
    public BooleanFunction constraint() {
        return this.constraint;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "AllTrue " + this.type + ": " + this.constraint;
        return str;
    }
}
