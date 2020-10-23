// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.board.custom;

import annotations.Name;
import annotations.Opt;
import game.equipment.container.board.Board;
import game.equipment.container.board.Track;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import metadata.graphics.util.ContainerStyleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SurakartaBoard extends Board
{
    private static final long serialVersionUID = 1L;
    private final int numLoops;
    private final int startAtRow;
    
    public SurakartaBoard(final GraphFunction graphFn, @Opt @Name final Integer loops, @Opt @Name final Integer from) {
        super(graphFn, null, null, null, null, SiteType.Vertex);
        this.numLoops = ((loops != null) ? loops : -1);
        this.startAtRow = ((from != null) ? from : 1);
    }
    
    @Override
    public void createTopology(final int beginIndex, final int numEdges) {
        super.createTopology(beginIndex, numEdges);
        for (final SiteType type : SiteType.values()) {
            this.topology.computeRows(type, false);
            this.topology.computeColumns(type, false);
        }
        final int dim0 = this.topology().rows(SiteType.Vertex).size() - 1;
        final int dim2 = this.topology().columns(SiteType.Vertex).size() - 1;
        int totalLoops = 0;
        if (this.numLoops != -1) {
            totalLoops = this.numLoops;
        }
        else {
            switch (this.topology().graph().basis()) {
                case Square -> totalLoops = (Math.min(dim0, dim2) - 1) / 2;
                case Triangular -> totalLoops = dim0 / 2;
                default -> System.out.println("** Board type " + this.topology().graph().basis() + " not supported for Surkarta.");
            }
        }
        switch (this.topology().graph().basis()) {
            case Square -> this.createTracksSquare(dim0, dim2, totalLoops);
            case Triangular -> this.createTracksTriangular(dim0, totalLoops);
            default -> System.out.println("** Board type " + this.topology().graph().basis() + " not supported for Surkarta.");
        }
        this.numSites = this.topology.vertices().size();
        this.style = ContainerStyleType.Graph;
    }
    
    void createTracksSquare(final int dim0, final int dim1, final int totalLoops) {
        final int rows = dim0 + 1;
        final int cols = dim1 + 1;
        final List<Integer> track = new ArrayList<>();
        for (int lid = 0; lid < totalLoops; ++lid) {
            final int loop = this.startAtRow + lid;
            track.clear();
            for (int col = 0; col < cols; ++col) {
                int site = loop * cols + col;
                if (col == 0 || col == cols - 1) {
                    site = -site;
                }
                track.add(site);
            }
            for (int row = 0; row < rows; ++row) {
                int site = cols - 1 - loop + row * cols;
                if (row == 0 || row == rows - 1) {
                    site = -site;
                }
                track.add(site);
            }
            for (int col = 0; col < cols; ++col) {
                int site = rows * cols - 1 - loop * cols - col;
                if (col == 0 || col == cols - 1) {
                    site = -site;
                }
                track.add(site);
            }
            for (int row = 0; row < rows; ++row) {
                int site = rows * cols - cols + loop - row * cols;
                if (row == 0 || row == rows - 1) {
                    site = -site;
                }
                track.add(site);
            }
            final List<Integer> forward = new ArrayList<>();
            for (int n = 0; n < track.size(); ++n) {
                final int a = track.get(n);
                final int b = track.get((n + 1) % track.size());
                forward.add(Math.abs(a));
                if (a < 0 && b < 0) {
                    forward.add(Math.abs(a));
                }
            }
            final List<Integer> backward = new ArrayList<>();
            Collections.reverse(track);
            for (int n2 = 0; n2 < track.size(); ++n2) {
                final int a2 = track.get(n2);
                final int b2 = track.get((n2 + 1) % track.size());
                backward.add(Math.abs(a2));
                if (a2 < 0 && b2 < 0) {
                    backward.add(Math.abs(a2));
                }
            }
            final Integer[] arrayForward = forward.toArray(new Integer[0]);
            final Integer[] arrayBackward = backward.toArray(new Integer[0]);
            final String nameForward = "Track" + loop + "F";
            final String nameBackward = "Track" + loop + "B";
            final Track trackForward = new Track(nameForward, arrayForward, null, Boolean.TRUE, null, null, Boolean.TRUE);
            final Track trackBackward = new Track(nameBackward, arrayBackward, null, Boolean.TRUE, null, null, Boolean.TRUE);
            this.tracks.add(trackForward);
            this.tracks.add(trackBackward);
        }
    }
    
    void createTracksTriangular(final int dim, final int totalLoops) {
        final int rows = dim + 1;
        final int cols = dim + 1;
        final List<Integer> track = new ArrayList<>();
        for (int lid = 0; lid < totalLoops; ++lid) {
            final int loop = this.startAtRow + lid;
            track.clear();
            int v = 0;
            int dec = cols;
            for (int step = 0; step < loop; ++step) {
                v += dec--;
            }
            for (int step = 0; step < rows - loop; ++step) {
                int site = v;
                if (step == 0 || step >= rows - loop - 1) {
                    site = -site;
                }
                track.add(site);
                ++v;
            }
            v = cols - 1 - loop;
            dec = rows - 1;
            for (int step = 0; step < rows - loop; ++step) {
                int site = v;
                if (step == 0 || step >= rows - loop - 1) {
                    site = -site;
                }
                track.add(site);
                v += dec--;
            }
            dec += 3;
            for (int step = 0; step < rows - loop; ++step) {
                int site = v;
                if (step == 0 || step >= rows - loop - 1) {
                    site = -site;
                }
                track.add(site);
                v -= dec++;
            }
            final List<Integer> forward = new ArrayList<>();
            for (int n = 0; n < track.size(); ++n) {
                final int a = track.get(n);
                final int b = track.get((n + 1) % track.size());
                forward.add(Math.abs(a));
                if (a < 0 && b < 0) {
                    forward.add(Math.abs(a));
                }
            }
            final List<Integer> backward = new ArrayList<>();
            Collections.reverse(track);
            for (int n2 = 0; n2 < track.size(); ++n2) {
                final int a2 = track.get(n2);
                final int b2 = track.get((n2 + 1) % track.size());
                backward.add(Math.abs(a2));
                if (a2 < 0 && b2 < 0) {
                    backward.add(Math.abs(a2));
                }
            }
            final Integer[] arrayForward = forward.toArray(new Integer[0]);
            final Integer[] arrayBackward = backward.toArray(new Integer[0]);
            final String nameForward = "Track" + loop + "F";
            final String nameBackward = "Track" + loop + "B";
            final Track trackForward = new Track(nameForward, arrayForward, null, Boolean.TRUE, null, null, Boolean.TRUE);
            final Track trackBackward = new Track(nameBackward, arrayBackward, null, Boolean.TRUE, null, null, Boolean.TRUE);
            this.tracks.add(trackForward);
            this.tracks.add(trackBackward);
        }
    }
}
