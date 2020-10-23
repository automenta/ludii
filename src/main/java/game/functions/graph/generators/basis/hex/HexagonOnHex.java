// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.hex;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import util.Context;

import java.awt.geom.Point2D;

@Hide
public class HexagonOnHex extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public HexagonOnHex(final DimFunction dim) {
        this.basis = BasisType.Hexagonal;
        this.shape = ShapeType.Hexagon;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = 2 * this.dim[0] - 1;
        final int cols = 2 * this.dim[0] - 1;
        final Graph graph = new Graph();
        final double[][] pts = new double[6][2];
        final Vertex[] verts = new Vertex[6];
        for (int row = 0; row <= rows / 2; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (col <= cols / 2 + row) {
                    if (row - col <= cols / 2) {
                        final Point2D ptRef = Hex.xy(row, col);
                        for (int n = 0; n < Hex.ref.length; ++n) {
                            pts[n][0] = ptRef.getX() + Hex.ref[n][0];
                            pts[n][1] = ptRef.getY() + Hex.ref[n][1];
                        }
                        verts[4] = graph.addVertex(pts[4][0], pts[4][1]);
                        verts[3] = graph.addVertex(pts[3][0], pts[3][1]);
                        if (col + 1 > cols / 2 + row) {
                            verts[2] = graph.addVertex(pts[2][0], pts[2][1]);
                        }
                    }
                }
            }
        }
        for (int row = rows / 2; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (col <= cols / 2 + row) {
                    if (row - col <= cols / 2) {
                        final Point2D ptRef = Hex.xy(row, col);
                        for (int n = 0; n < Hex.ref.length; ++n) {
                            pts[n][0] = ptRef.getX() + Hex.ref[n][0];
                            pts[n][1] = ptRef.getY() + Hex.ref[n][1];
                        }
                        verts[5] = graph.addVertex(pts[5][0], pts[5][1]);
                        verts[0] = graph.addVertex(pts[0][0], pts[0][1]);
                        if (col == cols - 1) {
                            verts[1] = graph.addVertex(pts[1][0], pts[1][1]);
                        }
                    }
                }
            }
        }
        int vid = 0;
        for (int row2 = 0; row2 <= rows / 2; ++row2) {
            for (int col2 = 0; col2 < cols / 2 + 1 + row2; ++col2) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
            }
            ++vid;
        }
        for (int row2 = rows / 2; row2 < rows; ++row2) {
            for (int col2 = 0; col2 < cols + rows / 2 - row2; ++col2) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
            }
            ++vid;
        }
        vid = 0;
        for (int row2 = 0; row2 < rows / 2; ++row2) {
            final int off = rows + 2 * row2 + 3;
            for (int col3 = 0; col3 <= cols / 2 + 1 + row2; ++col3) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + off));
                vid += 2;
            }
            --vid;
        }
        final int offM = 2 * rows + 1;
        for (int col2 = 0; col2 < cols + 1; ++col2) {
            graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + offM));
            vid += 2;
        }
        for (int row3 = rows / 2; row3 < rows; ++row3) {
            final int off2 = 3 * rows - 1 - 2 * row3;
            for (int col4 = 0; col4 < cols + rows / 2 - row3; ++col4) {
                if (vid + off2 < graph.vertices().size()) {
                    graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + off2));
                }
                vid += 2;
            }
            ++vid;
        }
        graph.makeFaces(false);
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
