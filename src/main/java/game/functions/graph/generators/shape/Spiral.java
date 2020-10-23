// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.shape;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import math.Vector;
import util.Context;

public class Spiral extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public Spiral(@Name final DimFunction turns, @Name final DimFunction sites, @Opt @Name final Boolean clockwise) {
        this.basis = BasisType.Spiral;
        this.shape = ShapeType.Spiral;
        final int dirn = (clockwise == null) ? 1 : (clockwise ? 1 : 0);
        this.dim = new int[] { turns.eval(), sites.eval(), dirn };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int numTurns = this.dim[0];
        final int numSites = this.dim[1];
        final boolean clockwise = this.dim[2] != 0;
        final Graph graph = new Graph();
        final double a = 0.0;
        final double b = 1.0;
        final double x0 = 0.0;
        final double y0 = 0.0;
        final Vertex pivot = graph.addVertex(0.0, 0.0);
        final int base = baseNumber(numTurns, numSites);
        final double[] thetas = new double[4 * numSites];
        int index = 1;
        int steps = base;
        for (int ring = 1; ring <= numTurns + 1; ++ring) {
            final double dTheta = 6.283185307179586 / steps;
            double theta = 6.283185307179586 * ring;
            if (ring <= 2 || ring % 2 == 1) {
                theta -= dTheta / 2.0;
            }
            for (int step = 0; step < steps; ++step) {
                thetas[index++] = theta;
                theta += dTheta;
            }
            if (ring <= 2) {
                steps *= 2;
            }
        }
        for (int vid = 2; vid < numSites; ++vid) {
            thetas[vid] = (thetas[vid - 1] + thetas[vid + 1]) / 2.0;
        }
        for (int vid = 2; vid < numSites; ++vid) {
            thetas[vid] = (thetas[vid - 1] + thetas[vid + 1]) / 2.0;
        }
        final double[] array = thetas;
        final int n = 1;
        array[n] -= 0.5 * (thetas[2] - thetas[1]);
        for (int vid = 1; vid < numSites; ++vid) {
            final double theta2 = thetas[vid];
            final double r = 0.0 + 1.0 * theta2;
            final double x2 = clockwise ? (0.0 - r * Math.cos(theta2)) : (0.0 + r * Math.cos(theta2));
            final double y2 = 0.0 + r * Math.sin(theta2);
            graph.addVertex(x2, y2);
        }
        for (int vid = 0; vid < graph.vertices().size() - 1; ++vid) {
            final Vertex vertexN = graph.vertices().get(vid);
            final Vertex vertexO = graph.vertices().get(vid + 1);
            final Vertex vertexM = graph.vertices().get(Math.max(0, vid - 1));
            final Vertex vertexP = graph.vertices().get(Math.min(graph.vertices().size() - 1, vid + 1));
            final Vector tangentN = new Vector(vertexO.pt2D().getX() - vertexM.pt2D().getX(), vertexO.pt2D().getY() - vertexM.pt2D().getY());
            final Vector tangentO = new Vector(vertexP.pt2D().getX() - vertexN.pt2D().getX(), vertexP.pt2D().getY() - vertexN.pt2D().getY());
            tangentN.normalise();
            tangentO.normalise();
            graph.addEdge(vertexN, vertexO, tangentN, tangentO);
        }
        for (final Vertex vertex : graph.vertices()) {
            vertex.setPivot(pivot);
        }
        graph.setBasisAndShape(this.basis, this.shape);
        return graph;
    }
    
    private static int baseNumber(final int numTurns, final int numSites) {
        for (int base = 1; base < numSites; ++base) {
            int steps = base;
            int total = 1;
            int ring = 1;
            while (ring < numTurns) {
                total += steps;
                if (total > numSites) {
                    if (ring <= numTurns) {
                        return base - 1;
                    }
                    break;
                }
                else {
                    if (ring <= 2) {
                        steps *= 2;
                    }
                    ++ring;
                }
            }
        }
        System.out.println("** Spiral.baseNumber(): Couldn't find base number for spiral.");
        return 0;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
