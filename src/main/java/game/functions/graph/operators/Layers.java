// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import math.Vector;
import util.Context;

public final class Layers extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final int numLayers;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Layers(final DimFunction layers, final GraphFunction graph) {
        this.precomputedGraph = null;
        this.numLayers = layers.eval();
        this.graphFn = graph;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph[] graphs = new Graph[this.numLayers];
        for (int layer = 0; layer < this.numLayers; ++layer) {
            (graphs[layer] = this.graphFn.eval(context, siteType)).translate(0.0, 0.0, layer);
            if (layer != 0) {
                final int numVerts = graphs[layer].vertices().size();
                final int vertsStartAt = graphs[0].vertices().size();
                for (final Vertex vertex : graphs[layer].vertices()) {
                    graphs[0].addVertex(vertex.pt());
                }
                for (final Vertex vertex : graphs[layer].vertices()) {
                    if (vertex.pivot() != null) {
                        final Vertex newVertex = graphs[0].vertices().get(vertsStartAt + vertex.id());
                        final int newPivotId = vertsStartAt + vertex.pivot().id();
                        newVertex.setPivot(graphs[0].vertices().get(newPivotId));
                    }
                }
                for (final Edge edge : graphs[layer].edges()) {
                    final int vidA = vertsStartAt + edge.vertexA().id();
                    final int vidB = vertsStartAt + edge.vertexB().id();
                    final Edge newEdge = graphs[0].addEdge(vidA, vidB);
                    if (edge.tangentA() != null) {
                        newEdge.setTangentA(new Vector(edge.tangentA()));
                    }
                    if (edge.tangentB() != null) {
                        newEdge.setTangentB(new Vector(edge.tangentB()));
                    }
                }
                for (int v = 0; v < numVerts; ++v) {
                    final Vertex vertexA = graphs[0].vertices().get(vertsStartAt - numVerts + v);
                    final Vertex vertexB = graphs[0].vertices().get(vertsStartAt + v);
                    graphs[0].addEdge(vertexA, vertexB);
                }
                for (final Face face : graphs[layer].faces()) {
                    final int[] vids = new int[face.vertices().size()];
                    for (int n = 0; n < face.vertices().size(); ++n) {
                        vids[n] = vertsStartAt + face.vertices().get(n).id();
                    }
                    graphs[0].findOrAddFace(vids);
                }
            }
        }
        graphs[0].reorder();
        return graphs[0];
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= this.graphFn.gameFlags(game);
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
