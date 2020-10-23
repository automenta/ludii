// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.mesh;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import main.math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class Mesh extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public static GraphFunction construct(final DimFunction numVertices, @Opt final Poly poly) {
        return new CustomOnMesh(numVertices, (poly == null) ? new Polygon(4) : poly.polygon(), null);
    }
    
    public static GraphFunction construct(final Float[][] points) {
        final List<Point2D> pointsList = new ArrayList<>();
        for (final Float[] xy : points) {
            if (xy.length < 2) {
                System.out.println("** Mesh: Points should have two values.");
            }
            else {
                pointsList.add(new Point2D.Double(xy[0], xy[1]));
            }
        }
        return new CustomOnMesh(null, null, pointsList);
    }
    
    private Mesh() {
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
