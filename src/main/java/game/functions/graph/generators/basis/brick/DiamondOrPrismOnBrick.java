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
public class DiamondOrPrismOnBrick extends Basis
{
    private static final long serialVersionUID = 1L;
    private final boolean trim;
    
    public DiamondOrPrismOnBrick(final DimFunction dimA, final DimFunction dimB, final Boolean trim) {
        this.basis = BasisType.Brick;
        this.shape = ((dimB == null) ? ShapeType.Diamond : ShapeType.Prism);
        if (dimB == null) {
            this.dim = new int[] { dimA.eval(), dimA.eval() };
        }
        else {
            this.dim = new int[] { dimA.eval(), dimB.eval() };
        }
        this.trim = (trim != null && trim);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 1.0E-4;
        final int side = this.dim[0];
        final int mid = (this.shape == ShapeType.Diamond) ? 0 : this.dim[1];
        final int rows = 2 * side + 2 * mid;
        final int cols = 2 * side;
        final Graph graph = new Graph();
        for (int r = 0; r < rows; ++r) {
            for (int c = (r + 1) % 2; c < cols; c += 2) {
                if (r + c >= side - 1) {
                    if (c - r < side) {
                        if (r + c < 3 * side - 1) {
                            if (r - c <= side) {
                                if (this.trim && c == 0) {
                                    Brick.addHalfBrick(graph, r, c + 1, 1.0E-4);
                                }
                                else if (this.trim && c >= cols - 1) {
                                    Brick.addHalfBrick(graph, r, c, 1.0E-4);
                                }
                                else {
                                    Brick.addBrick(graph, r, c, 1.0E-4);
                                }
                            }
                        }
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
