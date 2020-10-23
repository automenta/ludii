// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.celtic;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.*;
import gnu.trove.list.array.TIntArrayList;
import math.MathRoutines;
import math.Polygon;
import math.Vector;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;
import java.util.List;

public class Celtic extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    
    public Celtic(final DimFunction rows, @Opt final DimFunction columns) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Celtic;
        this.shape = ((columns == null || rows == columns) ? ShapeType.Square : ShapeType.Rectangle);
        if (columns == null) {
            this.dim = new int[] { rows.eval(), rows.eval() };
        }
        else {
            this.dim = new int[] { rows.eval(), columns.eval() };
        }
    }
    
    public Celtic(@Or final Poly poly, @Or final DimFunction[] sides) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
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
        this.basis = BasisType.Celtic;
        this.shape = ShapeType.NoShape;
        if (poly != null) {
            this.polygon.setFrom(poly.polygon());
        }
        else {
            for (DimFunction side : sides) {
                this.sides.add(side.eval());
            }
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 0.001;
        final double uu = 1.0 / Math.sqrt(2.0);
        final double uu2 = 2.0 * uu;
        final double[][] steps = { { uu2, 0.0 }, { 0.0, uu2 }, { -uu2, 0.0 }, { 0.0, -uu2 } };
        final double[][] ref = { { uu, 0.0 }, { 0.0, uu }, { -uu, 0.0 }, { 0.0, -uu } };
        int fromCol = 0;
        int fromRow = 0;
        int toCol = 0;
        int toRow = 0;
        if (this.polygon.isEmpty() && !this.sides.isEmpty()) {
            this.polygon.fromSides(this.sides, steps);
        }
        if (this.polygon.isEmpty()) {
            toCol = this.dim[1] - 1;
            toRow = this.dim[0] - 1;
        }
        else {
            this.polygon.inflate(0.1);
            final Rectangle2D bounds = this.polygon.bounds();
            fromCol = (int)bounds.getMinX() - 1;
            fromRow = (int)bounds.getMinY() - 1;
            toCol = (int)bounds.getMaxX() + 1;
            toRow = (int)bounds.getMaxY() + 1;
        }
        final Graph graph = new Graph();
        for (int row = fromRow; row < toRow + 1; ++row) {
            for (int col = fromCol; col < toCol + 1; ++col) {
                final Point2D ptRef = new Point2D.Double(col * uu2, row * uu2);
                if (this.polygon.isEmpty() || this.polygon.contains(ptRef)) {
                    for (double[] doubles : ref) {
                        final double x = ptRef.getX() + doubles[0];
                        final double y = ptRef.getY() + doubles[1];
                        graph.findOrAddVertex(x, y, 0.001);
                    }
                }
            }
        }
        graph.makeEdges();
        graph.makeFaces(true);
        MeasureGraph.measurePerimeter(graph);
        final List<GraphElement> list = graph.perimeters().get(0).elements();
        final BitSet keypoints = new BitSet();
        int flatRun = 0;
        for (int n = 0; n < list.size(); ++n) {
            final Vertex vl = (game.util.graph.Vertex) list.get((n - 1 + list.size()) % list.size());
            final Vertex vm = (game.util.graph.Vertex) list.get(n);
            final Vertex vn = (game.util.graph.Vertex) list.get((n + 1) % list.size());
            final double diff = MathRoutines.angleDifference(vl.pt2D(), vm.pt2D(), vn.pt2D());
            if (diff > 0.7853981633974483) {
                keypoints.set(n);
                flatRun = 0;
            }
            else if (Math.abs(diff) < 0.3141592653589793 && ++flatRun % 2 == 0) {
                keypoints.set(n);
            }
        }
        for (int n = keypoints.nextSetBit(0); n >= 0; n = keypoints.nextSetBit(n + 1)) {
            final Vertex vb = (game.util.graph.Vertex) list.get((n - 1 + list.size()) % list.size());
            final Vertex vc = (game.util.graph.Vertex) list.get(n);
            final Vertex vd = (game.util.graph.Vertex) list.get((n + 1) % list.size());
            final Vertex ve = (game.util.graph.Vertex) list.get((n + 2) % list.size());
            final double diffC = MathRoutines.angleDifference(vb.pt2D(), vc.pt2D(), vd.pt2D());
            final double diffD = MathRoutines.angleDifference(vc.pt2D(), vd.pt2D(), ve.pt2D());
            if (diffD < -0.7853981633974483) {
                final Vector tangentA = new Vector(vd.pt(), ve.pt());
                final Vector tangentB = new Vector(vd.pt(), vc.pt());
                tangentA.normalise();
                tangentB.normalise();
                tangentA.scale(1.25);
                tangentB.scale(1.25);
                graph.addEdge(vc.id(), ve.id(), tangentA, tangentB);
                graph.findOrAddFace(vd.id(), vc.id(), ve.id());
            }
            else {
                Vector tangentAX = new Vector(vb.pt2D(), vc.pt2D());
                Vector tangentBX = new Vector(vb.pt2D(), vc.pt2D());
                if (Math.abs(diffC) < 0.3141592653589793) {
                    tangentAX = new Vector(vc.pt2D(), vd.pt2D());
                    tangentBX = new Vector(vc.pt2D(), vd.pt2D());
                    tangentAX.perpendicular();
                    tangentBX.perpendicular();
                    tangentAX.reverse();
                    tangentBX.reverse();
                }
                final Point2D midCD = new Point2D.Double((vc.pt().x() + vd.pt().x()) / 2.0, (vc.pt().y() + vd.pt().y()) / 2.0);
                final Point2D ptV = new Point2D.Double(midCD.getX() + 0.5 * tangentAX.x(), midCD.getY() + 0.5 * tangentAX.y());
                final Point2D ptX = new Point2D.Double(midCD.getX() + 0.9 * tangentAX.x(), midCD.getY() + 0.9 * tangentAX.y());
                final Vertex vx = graph.addVertex(ptX);
                tangentAX.normalise();
                tangentBX.normalise();
                tangentAX.scale(1.333);
                tangentBX.scale(1.333);
                final Vector tangentXA = new Vector(ptV, vc.pt2D());
                final Vector tangentXB = new Vector(ptV, vd.pt2D());
                tangentXA.normalise();
                tangentXB.normalise();
                graph.addEdge(vc.id(), vx.id(), tangentAX, tangentXA);
                graph.addEdge(vx.id(), vd.id(), tangentXB, tangentBX);
                graph.findOrAddFace(vc.id(), vx.id(), vd.id());
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
