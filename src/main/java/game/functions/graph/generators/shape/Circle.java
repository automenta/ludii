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
import math.MathRoutines;
import math.Vector;
import util.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Circle extends Basis
{
    private static final long serialVersionUID = 1L;
    private final int[] cellsPerRing;
    private final boolean stagger;
    
    public Circle(final DimFunction[] cells, @Opt @Name final Boolean stagger) {
        this.basis = BasisType.Circle;
        this.shape = ShapeType.Circle;
        this.cellsPerRing = new int[cells.length];
        for (int c = 0; c < cells.length; ++c) {
            this.cellsPerRing[c] = cells[c].eval();
        }
        this.stagger = (stagger != null && stagger);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 0.01;
        final Graph graph = new Graph();
        final Vertex pivot = graph.addVertex(0.0, 0.0);
        final int numRings = this.cellsPerRing.length;
        final List<Sample>[] samples = (List<Sample>[])new ArrayList[numRings + 1];
        for (int ring = 0; ring < numRings + 1; ++ring) {
            samples[ring] = new ArrayList<>();
        }
        final double ref = 1.5707963267948966;
        for (int ring2 = 0; ring2 < numRings; ++ring2) {
            final double rI = Math.max(0.0, ring2 - 0.5);
            final double rO = ring2 + 0.5;
            final double ringOffset = (this.stagger && ring2 % 2 == 1) ? (6.283185307179586 / this.cellsPerRing[ring2] / 2.0) : 0.0;
            if (this.cellsPerRing[ring2] >= 2) {
                for (int step = 0; step < this.cellsPerRing[ring2]; ++step) {
                    final double theta = 1.5707963267948966 + ringOffset - 6.283185307179586 * step / this.cellsPerRing[ring2];
                    final double ix = rI * Math.cos(theta);
                    final double iy = rI * Math.sin(theta);
                    final double ox = rO * Math.cos(theta);
                    final double oy = rO * Math.sin(theta);
                    samples[ring2].add(new Sample(ix, iy, theta));
                    samples[ring2 + 1].add(new Sample(ox, oy, theta));
                    graph.findOrAddVertex(ix, iy, 0.01);
                    graph.findOrAddVertex(ox, oy, 0.01);
                }
            }
        }
        for (int ring2 = 0; ring2 < numRings + 1; ++ring2) {
            samples[ring2].sort((a, b) -> {
                if (a.theta == b.theta) {
                    return 0;
                }
                return (a.theta < b.theta) ? -1 : 1;
            });
        }
        for (int ring2 = 0; ring2 < numRings + 1; ++ring2) {
            for (int n = samples[ring2].size() - 1; n > 0; --n) {
                final Sample sampleA = samples[ring2].get(n);
                final Sample sampleB = samples[ring2].get((n + samples[ring2].size() - 1) % samples[ring2].size());
                if (MathRoutines.distance(sampleA.x, sampleA.y, sampleB.x, sampleB.y) < 0.1) {
                    samples[ring2].remove(n);
                }
            }
        }
        for (int ring2 = 0; ring2 < numRings + 1; ++ring2) {
            final int ringSize = samples[ring2].size();
            if (samples[ring2].size() > 1) {
                for (int n2 = 0; n2 < ringSize; ++n2) {
                    final Sample sampleA2 = samples[ring2].get(n2);
                    final Sample sampleB2 = samples[ring2].get((n2 + 1) % ringSize);
                    final Vertex vertexA = graph.findOrAddVertex(sampleA2.x, sampleA2.y, 0.01);
                    final Vertex vertexB = graph.findOrAddVertex(sampleB2.x, sampleB2.y, 0.01);
                    if (vertexA.id() != vertexB.id()) {
                        final Sample sampleAA = samples[ring2].get((n2 - 1 + ringSize) % ringSize);
                        final Sample sampleBB = samples[ring2].get((n2 + 2) % ringSize);
                        final Vector tangentA = new Vector(sampleB2.x - sampleAA.x, sampleB2.y - sampleAA.y);
                        final Vector tangentB = new Vector(sampleA2.x - sampleBB.x, sampleA2.y - sampleBB.y);
                        tangentA.normalise();
                        tangentB.normalise();
                        graph.findOrAddEdge(vertexA, vertexB, tangentA, tangentB);
                    }
                }
            }
        }
        for (int ring2 = 0; ring2 < numRings; ++ring2) {
            final double rI = Math.max(0.0, ring2 - 0.5);
            final double rO = ring2 + 0.5;
            final double ringOffset = (this.stagger && ring2 % 2 == 1) ? (6.283185307179586 / this.cellsPerRing[ring2] / 2.0) : 0.0;
            if (this.cellsPerRing[ring2] >= 2) {
                for (int step = 0; step < this.cellsPerRing[ring2]; ++step) {
                    final double theta = 1.5707963267948966 + ringOffset - 6.283185307179586 * step / this.cellsPerRing[ring2];
                    final double ix = rI * Math.cos(theta);
                    final double iy = rI * Math.sin(theta);
                    final double ox = rO * Math.cos(theta);
                    final double oy = rO * Math.sin(theta);
                    final Vertex vertexA2 = graph.findOrAddVertex(ix, iy, 0.01);
                    final Vertex vertexB2 = graph.findOrAddVertex(ox, oy, 0.01);
                    if (vertexA2.id() != vertexB2.id()) {
                        graph.findOrAddEdge(vertexA2, vertexB2);
                    }
                }
            }
        }
        for (final Vertex vertex : graph.vertices()) {
            vertex.setPivot(pivot);
        }
        if (siteType == SiteType.Cell) {
            graph.makeFaces(true);
        }
        return graph;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    public static class Sample
    {
        public double x;
        public double y;
        public double theta;
        
        public Sample(final double x, final double y, final double theta) {
            this.x = x;
            this.y = y;
            this.theta = theta;
        }
    }
}
