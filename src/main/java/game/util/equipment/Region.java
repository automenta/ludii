// 
// Decompiled by Procyon v0.5.36
// 

package game.util.equipment;

import annotations.Hide;
import game.equipment.container.board.Board;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import collections.ChunkSet;
import topology.Edge;
import topology.SiteFinder;
import topology.Topology;
import topology.TopologyElement;
import util.BaseLudeme;
import util.Context;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

public final class Region extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final ChunkSet bitSet;
    private final String name;
    
    @Hide
    public Region(final String name, final Board board, final String... coords) {
        assert board != null;
        this.bitSet = new ChunkSet(1, board.topology().cells().size());
        for (final String coord : coords) {
            final TopologyElement element = SiteFinder.find(board, coord, SiteType.Cell);
            if (element == null) {
                System.out.println("** Region: Coord " + coord + " not found.");
            }
            else {
                this.bitSet.setChunk(element.index(), 1);
            }
        }
        this.name = name;
    }
    
    @Hide
    public Region(final int count) {
        (this.bitSet = new ChunkSet(1, count)).set(0, count);
        this.name = "?";
    }
    
    @Hide
    public Region(final ChunkSet bitSet) {
        this.bitSet = bitSet.clone();
        this.name = "?";
    }
    
    @Hide
    public Region(final Region other) {
        this.bitSet = other.bitSet().clone();
        this.name = "?";
    }
    
    @Hide
    public Region() {
        this.bitSet = new ChunkSet();
        this.name = "?";
    }
    
    @Hide
    public Region(final int[] bitsToSet) {
        this.bitSet = new ChunkSet();
        for (final int bit : bitsToSet) {
            this.bitSet.set(bit);
        }
        this.name = "?";
    }
    
    @Hide
    public Region(final List<? extends TopologyElement> elements) {
        this.bitSet = new ChunkSet();
        for (final TopologyElement v : elements) {
            this.bitSet.set(v.index());
        }
        this.name = "?";
    }
    
    public ChunkSet bitSet() {
        return this.bitSet;
    }
    
    public String name() {
        return this.name;
    }
    
    public int count() {
        return this.bitSet.cardinality();
    }
    
    public boolean isEmpty() {
        return this.bitSet.isEmpty();
    }
    
    public int[] sites() {
        final int[] sites = new int[this.count()];
        int i = 0;
        for (int n = this.bitSet.nextSetBit(0); n >= 0; n = this.bitSet.nextSetBit(n + 1)) {
            sites[i] = n;
            ++i;
        }
        return sites;
    }
    
    public void set(final int newCount) {
        this.bitSet.clear();
        this.bitSet.set(0, newCount);
    }
    
    public void set(final Region other) {
        this.bitSet.clear();
        this.bitSet.or(other.bitSet());
    }
    
    public int nthValue(final int n) {
        int bit = -1;
        for (int i = 0; i <= n; ++i) {
            bit = this.bitSet.nextSetBit(bit + 1);
        }
        return bit;
    }
    
    public void add(final int val) {
        this.bitSet.set(val);
    }
    
    public void remove(final int val) {
        this.bitSet.clear(val);
    }
    
    public boolean contains(final int loc) {
        return this.bitSet.get(loc);
    }
    
    public void removeNth(final int n) {
        this.remove(this.nthValue(n));
    }
    
    public void union(final Region other) {
        this.bitSet.or(other.bitSet());
    }
    
    public void intersection(final Region other) {
        this.bitSet.and(other.bitSet());
    }
    
    public void remove(final Region other) {
        this.bitSet.andNot(other.bitSet());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.bitSet.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Region && this.bitSet.equals(((Region)other).bitSet);
    }
    
    public static void expand(final Region region, final Topology graph, final int numLayers, final SiteType type) {
        for (int layer = 0; layer < numLayers; ++layer) {
            final BitSet nbors = new BitSet();
            final int[] sites2;
            final int[] sites = sites2 = region.sites();
            for (final int site : sites2) {
                final TopologyElement element = graph.getGraphElements(type).get(site);
                for (final TopologyElement elementAdj : element.adjacent()) {
                    nbors.set(elementAdj.index(), true);
                }
            }
            for (int n = nbors.nextSetBit(0); n != -1; n = nbors.nextSetBit(n + 1)) {
                if (!region.contains(n)) {
                    region.add(n);
                }
            }
        }
    }
    
    public static void expand(final Region region, final Topology graph, final int numLayers, final Context context, final AbsoluteDirection dirnChoice, final SiteType type) {
        for (int i = 0; i < numLayers; ++i) {
            final int[] sites = region.sites();
            if (type == SiteType.Edge) {
                for (final int site : sites) {
                    final Edge edge = graph.edges().get(site);
                    for (final Edge edgeAdj : edge.adjacent()) {
                        if (!region.contains(edgeAdj.index())) {
                            region.add(edgeAdj.index());
                        }
                    }
                }
            }
            else {
                for (final int site : sites) {
                    final List<Step> steps = graph.trajectories().steps(type, site, dirnChoice);
                    for (final Step step : steps) {
                        if (step.from().siteType() != step.to().siteType()) {
                            continue;
                        }
                        final int to = step.to().id();
                        if (region.contains(to)) {
                            continue;
                        }
                        region.add(to);
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        String str = "Region: (";
        for (int n = this.bitSet.nextSetBit(0); n >= 0; n = this.bitSet.nextSetBit(n + 1)) {
            str = str + n + " ";
        }
        str += ")";
        return str;
    }
}
