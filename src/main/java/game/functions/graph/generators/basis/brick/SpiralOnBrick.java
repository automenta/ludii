// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.brick;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

@Hide
public class SpiralOnBrick extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public SpiralOnBrick(final DimFunction dimA) {
        this.basis = BasisType.Brick;
        this.shape = ShapeType.Spiral;
        this.dim = new int[] { dimA.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 1.0E-4;
        final int rings = this.dim[0];
        final Graph graph = new Graph();
        for (int ring = 0; ring < rings; ++ring) {
            if (ring == 0) {
                Brick.addHalfBrick(graph, rings - 1, rings - 1, 1.0E-4);
            }
            else {
                for (int n = 0; n < 2 * ring; n += 2) {
                    Brick.addVerticalBrick(graph, rings - ring + n, rings - ring - 1, 1.0E-4);
                    Brick.addBrick(graph, rings + ring - 1, rings - ring + n, 1.0E-4);
                    Brick.addVerticalBrick(graph, rings - ring - 1 + n, rings + ring - 1, 1.0E-4);
                    Brick.addBrick(graph, rings - ring - 1, rings - ring - 1 + n, 1.0E-4);
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
