// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import main.math.MathRoutines;
import util.Context;

public final class Union extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction[] graphFns;
    private final boolean connect;
    private Graph precomputedGraph;
    
    public Union(final GraphFunction graphA, final GraphFunction graphB, @Name @Opt final Boolean connect) {
        this.precomputedGraph = null;
        (this.graphFns = new GraphFunction[2])[0] = graphA;
        this.graphFns[1] = graphB;
        this.connect = (connect != null && connect);
    }
    
    public Union(final GraphFunction[] graphs, @Name @Opt final Boolean connect) {
        this.precomputedGraph = null;
        this.graphFns = graphs;
        this.connect = (connect != null && connect);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph[] graphs = new Graph[this.graphFns.length];
        for (int n = 0; n < this.graphFns.length; ++n) {
            final GraphFunction fn = this.graphFns[n];
            graphs[n] = fn.eval(context, siteType);
        }
        for (int n = 1; n < this.graphFns.length; ++n) {
            for (final Vertex vertex : graphs[n].vertices()) {
                graphs[0].addVertex(vertex);
            }
            for (final Edge edge : graphs[n].edges()) {
                graphs[0].addEdge(edge);
            }
            for (final Face face : graphs[n].faces()) {
                graphs[0].addFace(face);
            }
            graphs[0].synchroniseIds();
        }
        if (this.connect) {
            final double threshold = 1.1 * graphs[0].averageEdgeLength();
            for (final Vertex vertexA : graphs[0].vertices()) {
                for (final Vertex vertexB : graphs[0].vertices()) {
                    if (vertexA.id() == vertexB.id()) {
                        continue;
                    }
                    if (MathRoutines.distance(vertexA.pt2D(), vertexB.pt2D()) >= threshold) {
                        continue;
                    }
                    graphs[0].findOrAddEdge(vertexA.id(), vertexB.id());
                }
            }
            graphs[0].makeFaces(true);
        }
        graphs[0].resetBasis();
        graphs[0].resetShape();
        return graphs[0];
    }
    
    @Override
    public boolean isStatic() {
        for (final GraphFunction fn : this.graphFns) {
            if (!fn.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        for (final GraphFunction fn : this.graphFns) {
            flags |= fn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final GraphFunction fn : this.graphFns) {
            fn.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
    
    public class PivotPair
    {
        public int id;
        public int pivotId;
        
        public PivotPair(final int id, final int pivotId) {
            this.id = id;
            this.pivotId = pivotId;
        }
    }
}
