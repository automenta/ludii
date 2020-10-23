// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import util.Context;

public final class Subdivide extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private final int min;
    private Graph precomputedGraph;
    
    public Subdivide(final GraphFunction graph, @Opt @Name final DimFunction min) {
        this.precomputedGraph = null;
        this.graphFn = graph;
        this.min = ((min == null) ? 1 : min.eval());
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        if (siteType == SiteType.Vertex) {
            graph.makeFaces(true);
        }
        for (final Face face : graph.faces()) {
            face.setFlag(false);
        }
        for (int fid = graph.faces().size() - 1; fid >= 0; --fid) {
            final Face face = graph.faces().get(fid);
            if (face.vertices().size() >= this.min) {
                final Vertex pivot = graph.addVertex(face.pt());
                for (final Vertex vertex : face.vertices()) {
                    graph.findOrAddEdge(pivot.id(), vertex.id());
                }
                face.setFlag(true);
            }
        }
        if (siteType == SiteType.Cell) {
            for (int fid = graph.faces().size() - 1; fid >= 0; --fid) {
                if (graph.faces().get(fid).flag()) {
                    graph.removeFace(fid, false);
                }
            }
            graph.makeFaces(true);
        }
        else {
            graph.clear(SiteType.Cell);
        }
        graph.resetBasis();
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.graphFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.graphFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
