// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.square;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimConstant;
import game.functions.dim.DimFunction;
import game.functions.dim.math.Add;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import game.util.graph.Vertex;
import util.Context;

public class Square extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final int[][] steps;
    public static final int[][] diagonalSteps;
    
    public static GraphFunction construct(@Opt final SquareShapeType shape, final DimFunction dim, @Opt @Or @Name final DiagonalsType diagonals, @Opt @Or @Name final Boolean pyramidal) {
        int numNonNull = 0;
        if (diagonals != null) {
            ++numNonNull;
        }
        if (pyramidal != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one of 'diagonals' and 'pyramidal' can be true.");
        }
        final SquareShapeType st = (shape == null) ? SquareShapeType.Square : shape;
        switch (st) {
            case Square: {
                return new RectangleOnSquare(dim, dim, diagonals, pyramidal);
            }
            case Limping: {
                final DimFunction dimAplus1 = new Add(dim, new DimConstant(1));
                return new RectangleOnSquare(dim, dimAplus1, diagonals, pyramidal);
            }
            case Diamond: {
                return new DiamondOnSquare(dim, diagonals);
            }
            default: {
                throw new IllegalArgumentException("Shape " + st + " not supported for square tiling.");
            }
        }
    }
    
    public static GraphFunction construct(@Or final Poly poly, @Or final DimFunction[] sides, @Opt @Name final DiagonalsType diagonals) {
        int numNonNull = 0;
        if (poly != null) {
            ++numNonNull;
        }
        if (sides != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Exactly one array parameter must be non-null.");
        }
        if (poly != null) {
            return new CustomOnSquare(poly.polygon(), diagonals);
        }
        return new CustomOnSquare(sides, diagonals);
    }
    
    private Square() {
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    public static void handleDiagonals(final Graph graph, final int fromRow, final int toRow, final int fromCol, final int toCol, final DiagonalsType diagonals, final double tolerance) {
        if (diagonals == DiagonalsType.Alternating) {
            for (int r = fromRow; r <= toRow; ++r) {
                for (int c = fromCol; c <= toCol; ++c) {
                    final Vertex vertexA = graph.findVertex(c, r, tolerance);
                    final Vertex vertexB = graph.findVertex(c, r + 1, tolerance);
                    final Vertex vertexC = graph.findVertex(c + 1, r + 1, tolerance);
                    final Vertex vertexD = graph.findVertex(c + 1, r, tolerance);
                    if (vertexA != null && vertexB != null && vertexC != null) {
                        if (vertexD != null) {
                            if ((r + c) % 2 == 0) {
                                graph.findOrAddEdge(vertexA, vertexC);
                            }
                            else {
                                graph.findOrAddEdge(vertexB, vertexD);
                            }
                        }
                    }
                }
            }
        }
        else if (diagonals == DiagonalsType.Solid) {
            for (int r = fromRow; r <= toRow; ++r) {
                for (int c = fromCol; c <= toCol; ++c) {
                    final Vertex vertexA = graph.findVertex(c, r, tolerance);
                    final Vertex vertexB = graph.findVertex(c, r + 1, tolerance);
                    final Vertex vertexC = graph.findVertex(c + 1, r + 1, tolerance);
                    final Vertex vertexD = graph.findVertex(c + 1, r, tolerance);
                    if (vertexA != null && vertexB != null && vertexC != null) {
                        if (vertexD != null) {
                            final Vertex vertexX = graph.findOrAddVertex(c + 0.5, r + 0.5, tolerance);
                            if (vertexX != null) {
                                graph.findOrAddEdge(vertexA, vertexX);
                                graph.findOrAddEdge(vertexB, vertexX);
                                graph.findOrAddEdge(vertexC, vertexX);
                                graph.findOrAddEdge(vertexD, vertexX);
                            }
                        }
                    }
                }
            }
        }
        else if (diagonals == DiagonalsType.Concentric) {
            final int midRow = (toRow + fromRow) / 2;
            final int midCol = (toCol + fromCol) / 2;
            for (int r2 = fromRow; r2 <= toRow; ++r2) {
                for (int c2 = fromCol; c2 <= toCol; ++c2) {
                    final Vertex vertexA2 = graph.findVertex(c2, r2, tolerance);
                    final Vertex vertexB2 = graph.findVertex(c2, r2 + 1, tolerance);
                    final Vertex vertexC2 = graph.findVertex(c2 + 1, r2 + 1, tolerance);
                    final Vertex vertexD2 = graph.findVertex(c2 + 1, r2, tolerance);
                    if (vertexA2 != null && vertexB2 != null && vertexC2 != null) {
                        if (vertexD2 != null) {
                            if ((r2 < midRow && c2 < midCol) || (r2 >= midRow && c2 >= midCol)) {
                                graph.findOrAddEdge(vertexB2, vertexD2);
                            }
                            else {
                                graph.findOrAddEdge(vertexA2, vertexC2);
                            }
                        }
                    }
                }
            }
        }
        else if (diagonals == DiagonalsType.Radiating) {
            final int[][] dsteps = { { 1, 1 }, { 1, -1 }, { -1, -1 }, { -1, 1 } };
            final int midRow2 = (toRow + fromRow) / 2;
            final int midCol2 = (toCol + fromCol) / 2;
            for (int numSteps = Math.max((toRow - fromRow) / 2, (toCol - fromCol) / 2) + 1, n = 0; n < numSteps; ++n) {
                for (int d = 0; d < dsteps.length; ++d) {
                    final Vertex vertexA3 = graph.findVertex(midRow2 + n * dsteps[d][0], midCol2 + n * dsteps[d][1], tolerance);
                    final Vertex vertexB3 = graph.findVertex(midRow2 + (n + 1) * dsteps[d][0], midCol2 + (n + 1) * dsteps[d][1], tolerance);
                    if (vertexA3 != null) {
                        if (vertexB3 != null) {
                            graph.findOrAddEdge(vertexA3, vertexB3);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        steps = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        diagonalSteps = new int[][] { { 1, 1 }, { 1, -1 } };
    }
}
