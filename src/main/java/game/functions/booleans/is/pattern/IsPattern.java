// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.pattern;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.util.directions.DirectionFacing;
import game.util.graph.Step;
import topology.Topology;
import util.ContainerId;
import util.Context;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class IsPattern extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] whatsFn;
    private final StepType[] walk;
    private final IntFunction fromFn;
    private SiteType type;
    
    public IsPattern(final StepType[] walk, @Opt final SiteType type, @Opt @Name final IntFunction from, @Or @Opt @Name final IntFunction what, @Or @Opt @Name final IntFunction[] whats) {
        this.walk = walk;
        this.fromFn = ((from == null) ? new LastTo(null) : from);
        this.whatsFn = (whats != null) ? whats : ((what != null) ? new IntFunction[] { what } : null);
        this.type = type;
    }
    
    @Override
    public boolean eval(final Context context) {
        final int from = this.fromFn.eval(context);
        if (from <= -1) {
            return false;
        }
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final int cid = new ContainerId(null, null, null, null, new IntConstant(from)).eval(context);
        final Topology graph = context.containers()[cid].topology();
        final ContainerState cs = context.containerState(0);
        if (from >= graph.getGraphElements(realType).size()) {
            return false;
        }
        final int[] whats = (this.whatsFn != null) ? new int[this.whatsFn.length] : new int[1];
        if (this.whatsFn != null) {
            for (int i = 0; i < whats.length; ++i) {
                whats[i] = this.whatsFn[i].eval(context);
            }
        }
        else {
            final int what = cs.what(from, realType);
            if (what == 0) {
                return false;
            }
            whats[0] = what;
        }
        final List<DirectionFacing> orthogonalSupported = graph.supportedOrthogonalDirections(realType);
        final List<DirectionFacing> walkDirection = graph.supportedOrthogonalDirections(realType);
        for (final DirectionFacing startDirection : walkDirection) {
            int currentLoc = from;
            DirectionFacing currentDirection = startDirection;
            int whatIndex = 0;
            if (cs.what(from, realType) != whats[whatIndex]) {
                return false;
            }
            if (++whatIndex == whats.length) {
                whatIndex = 0;
            }
            boolean found = true;
            for (final StepType step : this.walk) {
                if (step == StepType.F) {
                    final List<Step> stepsDirection = graph.trajectories().steps(realType, currentLoc, currentDirection.toAbsolute());
                    int to = -1;
                    for (final Step stepDirection : stepsDirection) {
                        if (stepDirection.from().siteType() != stepDirection.to().siteType()) {
                            continue;
                        }
                        to = stepDirection.to().id();
                    }
                    currentLoc = to;
                    if (to == -1 || cs.what(to, realType) != whats[whatIndex]) {
                        found = false;
                        break;
                    }
                    if (++whatIndex == whats.length) {
                        whatIndex = 0;
                    }
                }
                else if (step == StepType.R) {
                    for (currentDirection = currentDirection.right(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.right()) {}
                }
                else if (step == StepType.L) {
                    for (currentDirection = currentDirection.left(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.left()) {}
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        if (this.fromFn != null) {
            flag |= this.fromFn.gameFlags(game);
        }
        if (this.whatsFn != null) {
            for (final IntFunction what : this.whatsFn) {
                flag |= what.gameFlags(game);
            }
        }
        flag |= SiteType.stateFlags(this.type);
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.fromFn != null) {
            this.fromFn.preprocess(game);
        }
        if (this.whatsFn != null) {
            for (final IntFunction what : this.whatsFn) {
                what.preprocess(game);
            }
        }
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        if (!this.eval(context)) {
            return new ArrayList<>();
        }
        final List<Location> winningSites = new ArrayList<>();
        final int from = this.fromFn.eval(context);
        if (from <= -1) {
            return new ArrayList<>();
        }
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final int cid = new ContainerId(null, null, null, null, new IntConstant(from)).eval(context);
        final Topology graph = context.containers()[cid].topology();
        final ContainerState cs = context.containerState(0);
        if (from >= graph.getGraphElements(realType).size()) {
            return new ArrayList<>();
        }
        final int[] whats = (this.whatsFn != null) ? new int[this.whatsFn.length] : new int[1];
        if (this.whatsFn != null) {
            for (int i = 0; i < whats.length; ++i) {
                whats[i] = this.whatsFn[i].eval(context);
            }
        }
        else {
            final int what = cs.what(from, realType);
            if (what == 0) {
                return new ArrayList<>();
            }
            whats[0] = what;
        }
        final List<DirectionFacing> orthogonalSupported = graph.supportedOrthogonalDirections(realType);
        final List<DirectionFacing> walkDirection = graph.supportedOrthogonalDirections(realType);
        for (final DirectionFacing startDirection : walkDirection) {
            int currentLoc = from;
            DirectionFacing currentDirection = startDirection;
            int whatIndex = 0;
            if (cs.what(from, realType) != whats[whatIndex]) {
                return new ArrayList<>();
            }
            if (++whatIndex == whats.length) {
                whatIndex = 0;
            }
            winningSites.add(new FullLocation(from, 0, realType));
            boolean found = true;
            for (final StepType step : this.walk) {
                if (step == StepType.F) {
                    final List<Step> stepsDirection = graph.trajectories().steps(realType, currentLoc, currentDirection.toAbsolute());
                    int to = -1;
                    for (final Step stepDirection : stepsDirection) {
                        if (stepDirection.from().siteType() != stepDirection.to().siteType()) {
                            continue;
                        }
                        to = stepDirection.to().id();
                    }
                    currentLoc = to;
                    winningSites.add(new FullLocation(to, 0, realType));
                    if (to == -1 || cs.what(to, realType) != whats[whatIndex]) {
                        found = false;
                        winningSites.clear();
                        break;
                    }
                    if (++whatIndex == whats.length) {
                        whatIndex = 0;
                    }
                }
                else if (step == StepType.R) {
                    for (currentDirection = currentDirection.right(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.right()) {}
                }
                else if (step == StepType.L) {
                    for (currentDirection = currentDirection.left(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.left()) {}
                }
            }
            if (found) {
                return winningSites;
            }
        }
        return new ArrayList<>();
    }
}
