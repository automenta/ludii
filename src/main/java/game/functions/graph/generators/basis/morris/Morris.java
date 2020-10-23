// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.morris;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

public class Morris extends Basis
{
    private static final long serialVersionUID = 1L;
    private final DimFunction ringsFn;
    private final BooleanFunction joinCornersFn;
    
    public Morris(final DimFunction rings, @Opt @Name final BooleanFunction joinCorners) {
        this.basis = BasisType.Morris;
        this.shape = ShapeType.Square;
        this.ringsFn = rings;
        this.joinCornersFn = joinCorners;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int numRings = this.ringsFn.eval();
        final boolean joinCorners = this.joinCornersFn != null && this.joinCornersFn.eval(context);
        final Graph graph = new Graph();
        for (int ring = 0; ring < numRings; ++ring) {
            final int base = 8 * ring;
            final int x0 = ring;
            final int y0 = ring;
            final int x2 = 2 * numRings - ring;
            final int y2 = 2 * numRings - ring;
            graph.addVertex(x0, y0);
            graph.addVertex(x0, y2);
            graph.addVertex(x2, y2);
            graph.addVertex(x2, y0);
            final double xmid = 0.5 * (x0 + x2);
            final double ymid = 0.5 * (y0 + y2);
            graph.addVertex(x0, ymid);
            graph.addVertex(xmid, y2);
            graph.addVertex(x2, ymid);
            graph.addVertex(xmid, y0);
            for (int n = 0; n < 4; ++n) {
                graph.findOrAddEdge(base + n, base + n + 4);
                graph.findOrAddEdge(base + n + 4, base + (n + 1) % 4);
                if (ring > 0) {
                    graph.findOrAddEdge(base + n + 4, base + n - 4);
                    if (joinCorners) {
                        graph.findOrAddEdge(base + n, base + n - 8);
                    }
                }
            }
        }
        graph.setBasisAndShape(this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
