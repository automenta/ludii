// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph;

import annotations.Hide;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import main.math.MathRoutines;
import util.BaseLudeme;
import util.Context;

import java.util.ArrayList;
import java.util.List;

@Hide
public abstract class BaseGraphFunction extends BaseLudeme implements GraphFunction
{
    private static final long serialVersionUID = 1L;
    protected BasisType basis;
    protected ShapeType shape;
    protected int[] dim;
    protected static final double unit = 1.0;
    
    public BaseGraphFunction() {
        this.basis = null;
        this.shape = null;
    }
    
    public BasisType basis() {
        return this.basis;
    }
    
    public ShapeType shape() {
        return this.shape;
    }
    
    public void resetBasis() {
        this.basis = BasisType.NoBasis;
    }
    
    public void resetShape() {
        this.shape = ShapeType.NoShape;
    }
    
    @Override
    public int[] dim() {
        return this.dim;
    }
    
    public int maxDim() {
        int max = 0;
        for (int d = 0; d < this.dim.length; ++d) {
            if (this.dim[d] > max) {
                max = this.dim[d];
            }
        }
        return max;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        System.out.println("BaseGraphFunction.eval(): Should not be called directly; call subclass e.g. RectangleOnSquare.eval().");
        return null;
    }
    
    public static Graph createGraphFromVertexList(final List<double[]> vertexList, final double u, final BasisType basisIn, final ShapeType shapeIn) {
        final List<int[]> edgeList = new ArrayList<>();
        for (int aid = 0; aid < vertexList.size(); ++aid) {
            final double[] ptA = vertexList.get(aid);
            final double ax = ptA[0];
            final double ay = ptA[1];
            for (int bid = aid + 1; bid < vertexList.size(); ++bid) {
                final double[] ptB = vertexList.get(bid);
                final double dist = MathRoutines.distance(ptB[0], ptB[1], ax, ay);
                if (Math.abs(dist - u) < 0.01) {
                    edgeList.add(new int[] { aid, bid });
                }
            }
        }
        final Graph graph = game.util.graph.Graph.createFromLists(vertexList, edgeList);
        graph.setBasisAndShape(basisIn, shapeIn);
        return graph;
    }
    
    public static Graph createGraphFromVertexList3D(final List<double[]> vertexList, final double u, final BasisType basisIn, final ShapeType shapeIn) {
        final List<int[]> edgeList = new ArrayList<>();
        for (int aid = 0; aid < vertexList.size(); ++aid) {
            final double[] ptA = vertexList.get(aid);
            final double ax = ptA[0];
            final double ay = ptA[1];
            final double az = (ptA.length > 2) ? ptA[2] : 0.0;
            for (int bid = aid + 1; bid < vertexList.size(); ++bid) {
                final double[] ptB = vertexList.get(bid);
                final double bx = ptB[0];
                final double by = ptB[1];
                final double bz = (ptB.length > 2) ? ptB[2] : 0.0;
                final double dist = MathRoutines.distance(ax, ay, az, bx, by, bz);
                if (Math.abs(dist - u) < 0.01) {
                    edgeList.add(new int[] { aid, bid });
                }
            }
        }
        final Graph graph = game.util.graph.Graph.createFromLists(vertexList, edgeList);
        graph.setBasisAndShape(basisIn, shapeIn);
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
}
