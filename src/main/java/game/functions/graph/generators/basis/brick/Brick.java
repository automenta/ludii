// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.brick;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimConstant;
import game.functions.dim.DimFunction;
import game.functions.dim.math.Add;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import util.Context;

public class Brick extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public static GraphFunction construct(@Opt final BrickShapeType shape, final DimFunction dimA, @Opt final DimFunction dimB, @Opt @Name final Boolean trim) {
        final BrickShapeType st = (shape != null) ? shape : ((dimB == null || dimB == dimA) ? BrickShapeType.Square : BrickShapeType.Rectangle);
        switch (st) {
            case Square, Rectangle -> {
                return new SquareOrRectangleOnBrick(dimA, dimB, trim);
            }
            case Limping -> {
                final DimFunction dimAplus1 = new Add(dimA, new DimConstant(1));
                return new SquareOrRectangleOnBrick(dimA, dimAplus1, trim);
            }
            case Diamond -> {
                return new DiamondOrPrismOnBrick(dimA, null, trim);
            }
            case Prism -> {
                return new DiamondOrPrismOnBrick(dimA, (dimB == null) ? dimA : dimB, trim);
            }
            case Spiral -> {
                return new SpiralOnBrick(dimA);
            }
            default -> throw new IllegalArgumentException("Shape " + st + " not supported for Brick tiling.");
        }
    }
    
    private Brick() {
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    static void addBrick(final Graph graph, final int row, final int col, final double tolerance) {
        final Vertex vertexA = graph.findOrAddVertex(col, row, tolerance);
        final Vertex vertexB = graph.findOrAddVertex(col, row + 1, tolerance);
        final Vertex vertexC = graph.findOrAddVertex(col + 1, row + 1, tolerance);
        final Vertex vertexD = graph.findOrAddVertex(col + 2, row + 1, tolerance);
        final Vertex vertexE = graph.findOrAddVertex(col + 2, row, tolerance);
        final Vertex vertexF = graph.findOrAddVertex(col + 1, row, tolerance);
        graph.findOrAddEdge(vertexA.id(), vertexB.id());
        graph.findOrAddEdge(vertexB.id(), vertexC.id());
        graph.findOrAddEdge(vertexC.id(), vertexD.id());
        graph.findOrAddEdge(vertexD.id(), vertexE.id());
        graph.findOrAddEdge(vertexE.id(), vertexF.id());
        graph.findOrAddEdge(vertexF.id(), vertexA.id());
        graph.findOrAddFace(vertexA.id(), vertexB.id(), vertexC.id(), vertexD.id(), vertexE.id(), vertexF.id());
    }
    
    static void addHalfBrick(final Graph graph, final int row, final int col, final double tolerance) {
        final Vertex vertexA = graph.findOrAddVertex(col, row, tolerance);
        final Vertex vertexB = graph.findOrAddVertex(col, row + 1, tolerance);
        final Vertex vertexC = graph.findOrAddVertex(col + 1, row + 1, tolerance);
        final Vertex vertexD = graph.findOrAddVertex(col + 1, row, tolerance);
        graph.findOrAddEdge(vertexA.id(), vertexB.id());
        graph.findOrAddEdge(vertexB.id(), vertexC.id());
        graph.findOrAddEdge(vertexC.id(), vertexD.id());
        graph.findOrAddEdge(vertexD.id(), vertexA.id());
        graph.findOrAddFace(vertexA.id(), vertexB.id(), vertexC.id(), vertexD.id());
    }
    
    static void addVerticalBrick(final Graph graph, final int row, final int col, final double tolerance) {
        final Vertex vertexA = graph.findOrAddVertex(col, row, tolerance);
        final Vertex vertexB = graph.findOrAddVertex(col, row + 1, tolerance);
        final Vertex vertexC = graph.findOrAddVertex(col, row + 2, tolerance);
        final Vertex vertexD = graph.findOrAddVertex(col + 1, row + 2, tolerance);
        final Vertex vertexE = graph.findOrAddVertex(col + 1, row + 1, tolerance);
        final Vertex vertexF = graph.findOrAddVertex(col + 1, row, tolerance);
        graph.findOrAddEdge(vertexA.id(), vertexB.id());
        graph.findOrAddEdge(vertexB.id(), vertexC.id());
        graph.findOrAddEdge(vertexC.id(), vertexD.id());
        graph.findOrAddEdge(vertexD.id(), vertexE.id());
        graph.findOrAddEdge(vertexE.id(), vertexF.id());
        graph.findOrAddEdge(vertexF.id(), vertexA.id());
        graph.findOrAddFace(vertexA.id(), vertexB.id(), vertexC.id(), vertexD.id(), vertexE.id(), vertexF.id());
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
