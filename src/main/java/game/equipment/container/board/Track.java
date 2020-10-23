// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.board;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Topology;
import topology.Vertex;
import util.BaseLudeme;

import java.io.Serializable;
import java.util.List;

public class Track extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String name;
    private Elem[] elems;
    private Integer[] track;
    private final String trackDirection;
    private final int owner;
    private final boolean looped;
    private final boolean direct;
    private boolean internalLoop;
    private int trackIdx;
    
    public Track(final String name, @Or final Integer[] track, @Or final String trackDirection, @Opt @Name final Boolean loop, @Opt @Or final Integer owner, @Opt @Or final RoleType role, @Opt @Name final Boolean directed) {
        this.internalLoop = false;
        this.trackIdx = -1;
        int numNonNull = 0;
        if (track != null) {
            ++numNonNull;
        }
        if (trackDirection != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (track != null) {
            ++numNonNull2;
        }
        if (trackDirection != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        this.name = ((name == null) ? "Track" : name);
        this.owner = ((owner == null) ? ((role == null) ? 0 : role.owner()) : owner);
        this.looped = (loop != null && loop);
        this.direct = (directed != null && directed);
        this.track = track;
        this.trackDirection = trackDirection;
        this.elems = null;
    }
    
    public void buildTrack(final Game game) {
        final Topology topology = game.board().topology();
        if (game.board().defaultSite() == SiteType.Cell) {
            if (this.trackDirection != null) {
                final String[] steps = this.trackDirection.split(",");
                if (steps.length < 1) {
                    throw new IllegalArgumentException("The track " + this.name + " is not correct");
                }
                if (!isNumber(steps[0])) {
                    throw new IllegalArgumentException("The first step in the track " + this.name + " is not a number");
                }
                final int start = Integer.parseInt(steps[0]);
                final TIntArrayList trackList = new TIntArrayList();
                trackList.add(start);
                Cell current = (start < game.board().topology().cells().size()) ? game.board().topology().cells().get(start) : null;
                for (int i = 1; i < steps.length; ++i) {
                    final String step = steps[i];
                    final boolean stepIsNumber = isNumber(step);
                    if (stepIsNumber) {
                        final int site = Integer.parseInt(step);
                        current = ((site < game.board().topology().cells().size()) ? game.board().topology().cells().get(site) : null);
                        trackList.add(site);
                    }
                    else if (step.equals("End")) {
                        final int site = -2;
                        trackList.add(site);
                    }
                    else {
                        if (current == null) {
                            throw new IllegalArgumentException("The step " + step + " in the track " + this.name + " is impossible without a correct site in the main board.");
                        }
                        String direction = "";
                        for (int j = 0; j < step.length(); ++j) {
                            if (!Character.isDigit(step.charAt(j))) {
                                direction += step.charAt(j);
                            }
                        }
                        int size = -1;
                        if (direction.length() != step.length()) {
                            size = Integer.parseInt(step.substring(direction.length()));
                        }
                        final DirectionFacing dirn = convertStringDirection(direction, game.board().topology().supportedDirections(SiteType.Cell));
                        if (dirn == null) {
                            throw new IllegalArgumentException("The step " + step + " is wrong in the track " + this.name);
                        }
                        final List<Radial> radials = topology.trajectories().radials(SiteType.Cell, current.index(), dirn.toAbsolute());
                        if (size == -1) {
                            for (final Radial radial : radials) {
                                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                                    final int site2 = radial.steps()[toIdx].id();
                                    current = game.board().topology().cells().get(site2);
                                    trackList.add(site2);
                                }
                            }
                        }
                        else {
                            for (final Radial radial : radials) {
                                for (int toIdx = 1; toIdx < radial.steps().length && toIdx < size + 1; ++toIdx) {
                                    final int site2 = radial.steps()[toIdx].id();
                                    current = game.board().topology().cells().get(site2);
                                    trackList.add(site2);
                                }
                            }
                        }
                    }
                }
                this.track = new Integer[trackList.size()];
                for (int i = 0; i < trackList.size(); ++i) {
                    final int loc = trackList.getQuick(i);
                    this.track[i] = loc;
                }
            }
        }
        else if (game.board().defaultSite() == SiteType.Vertex && this.trackDirection != null) {
            final String[] steps = this.trackDirection.split(",");
            if (steps.length < 1) {
                throw new IllegalArgumentException("The track " + this.name + " is not correct.");
            }
            if (!isNumber(steps[0])) {
                throw new IllegalArgumentException("The first step in the track " + this.name + " is not a number.");
            }
            final int start = Integer.parseInt(steps[0]);
            final TIntArrayList trackList = new TIntArrayList();
            trackList.add(start);
            Vertex current2 = (start < game.board().topology().vertices().size()) ? game.board().topology().vertices().get(start) : null;
            for (int i = 1; i < steps.length; ++i) {
                final String step = steps[i];
                final boolean stepIsNumber = isNumber(step);
                if (stepIsNumber) {
                    final int site = Integer.parseInt(step);
                    current2 = ((site < game.board().topology().vertices().size()) ? game.board().topology().vertices().get(site) : null);
                    trackList.add(site);
                }
                else if (step.equals("End")) {
                    final int site = -2;
                    trackList.add(site);
                }
                else {
                    if (current2 == null) {
                        throw new IllegalArgumentException("The step " + step + " in the track " + this.name + " is impossible without a correct site in the main board.");
                    }
                    String direction = "";
                    for (int j = 0; j < step.length(); ++j) {
                        if (!Character.isDigit(step.charAt(j))) {
                            direction += step.charAt(j);
                        }
                    }
                    int size = -1;
                    if (direction.length() != step.length()) {
                        size = Integer.parseInt(step.substring(direction.length()));
                    }
                    final DirectionFacing dirn = convertStringDirection(direction, game.board().topology().supportedDirections(SiteType.Vertex));
                    if (dirn == null) {
                        throw new IllegalArgumentException("The step " + step + " is wrong in the track " + this.name);
                    }
                    final List<Radial> radials = topology.trajectories().radials(SiteType.Vertex, current2.index(), dirn.toAbsolute());
                    if (size == -1) {
                        for (final Radial radial : radials) {
                            for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                                final int site2 = radial.steps()[toIdx].id();
                                current2 = game.board().topology().vertices().get(site2);
                                trackList.add(site2);
                            }
                        }
                    }
                    else {
                        for (final Radial radial : radials) {
                            for (int toIdx = 1; toIdx < radial.steps().length && toIdx < size + 1; ++toIdx) {
                                final int site2 = radial.steps()[toIdx].id();
                                current2 = game.board().topology().vertices().get(site2);
                                trackList.add(site2);
                            }
                        }
                    }
                }
            }
            this.track = new Integer[trackList.size()];
            for (int i = 0; i < trackList.size(); ++i) {
                final int loc = trackList.getQuick(i);
                this.track[i] = loc;
            }
        }
        final TIntArrayList trackWithoutBump = new TIntArrayList();
        final TIntArrayList nbBumpByElem = new TIntArrayList();
        boolean hasBump = false;
        int countBump = 0;
        for (int i = 0; i < this.track.length; ++i) {
            if (i == this.track.length - 1 || this.track[i] != (int)this.track[i + 1]) {
                trackWithoutBump.add(this.track[i]);
                nbBumpByElem.add(countBump);
                countBump = 0;
            }
            else {
                hasBump = true;
                ++countBump;
            }
        }
        final int[] newTrack = trackWithoutBump.toArray();
        this.elems = new Elem[newTrack.length];
        for (int k = 0; k < newTrack.length; ++k) {
            Elem e = null;
            if (k == 0 && this.looped) {
                e = new Elem(newTrack[k], newTrack[newTrack.length - 1], newTrack.length - 1, newTrack[k + 1], k + 1, nbBumpByElem.getQuick(k));
            }
            else if (k == 0 && !this.looped) {
                e = new Elem(newTrack[k], null, null, newTrack[k + 1], k + 1, nbBumpByElem.getQuick(k));
            }
            else if (k == newTrack.length - 1 && this.looped) {
                e = new Elem(newTrack[k], newTrack[k - 1], k - 1, newTrack[0], 0, nbBumpByElem.getQuick(k));
            }
            else if (k == newTrack.length - 1 && !this.looped) {
                e = new Elem(newTrack[k], newTrack[k - 1], k - 1, null, null, nbBumpByElem.getQuick(k));
            }
            else if (this.direct) {
                e = new Elem(newTrack[k], null, null, newTrack[k + 1], k + 1, nbBumpByElem.getQuick(k));
            }
            else {
                e = new Elem(newTrack[k], newTrack[k - 1], k - 1, newTrack[k + 1], k + 1, nbBumpByElem.getQuick(k));
            }
            this.elems[k] = e;
        }
        if (!hasBump) {
            final TIntArrayList listSites = new TIntArrayList();
            for (final Elem elem : this.elems) {
                final int site3 = elem.site;
                if (listSites.contains(site3)) {
                    this.internalLoop = true;
                    break;
                }
                listSites.add(site3);
            }
        }
    }
    
    private static DirectionFacing convertStringDirection(final String direction, final List<DirectionFacing> supportedDirections) {
        for (final DirectionFacing directionSupported : supportedDirections) {
            if (directionSupported.uniqueName().toString().equals(direction)) {
                return directionSupported;
            }
        }
        return null;
    }
    
    private static boolean isNumber(final String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
    
    public String name() {
        return this.name;
    }
    
    public Elem[] elems() {
        return this.elems;
    }
    
    public int owner() {
        return this.owner;
    }
    
    public boolean islooped() {
        return this.looped;
    }
    
    public boolean hasInternalLoop() {
        return this.internalLoop;
    }
    
    public int trackIdx() {
        return this.trackIdx;
    }
    
    public void setTrackIdx(final int trackIdx) {
        this.trackIdx = trackIdx;
    }
    
    public static class Elem implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public final int site;
        public final int prev;
        public final int prevIndex;
        public final int next;
        public final int nextIndex;
        public final int bump;
        
        public Elem(final Integer site, final Integer prev, final Integer prevIndex, final Integer next, final Integer nextIndex, final Integer bump) {
            this.site = ((site == null) ? -1 : site);
            this.prev = ((prev == null) ? -1 : prev);
            this.prevIndex = ((prevIndex == null) ? -1 : prevIndex);
            this.next = ((next == null) ? -1 : next);
            this.nextIndex = ((nextIndex == null) ? -1 : nextIndex);
            this.bump = bump;
        }
    }
}
