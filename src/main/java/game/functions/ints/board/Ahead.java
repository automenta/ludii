// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.directions.RelativeDirection;
import game.util.graph.Radial;
import game.util.graph.Step;
import topology.Topology;
import topology.TopologyElement;
import util.Context;

import java.util.List;

public final class Ahead extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    private final IntFunction stepsFn;
    private final RelativeDirection relativeDirection;
    private final AbsoluteDirection absoluteDirection;
    private SiteType type;
    
    public Ahead(@Opt final SiteType type, final IntFunction site, @Opt @Name final IntFunction steps, @Opt @Or final RelativeDirection relativeDirection, @Opt @Or final AbsoluteDirection absoluteDirection) {
        this.siteFn = site;
        int numNonNull = 0;
        if (relativeDirection != null) {
            ++numNonNull;
        }
        if (absoluteDirection != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        if (absoluteDirection != null) {
            this.absoluteDirection = absoluteDirection;
            this.relativeDirection = null;
        }
        else {
            this.absoluteDirection = null;
            this.relativeDirection = ((relativeDirection == null) ? RelativeDirection.Forward : relativeDirection);
        }
        this.stepsFn = ((steps == null) ? new IntConstant(1) : steps);
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final int site = this.siteFn.eval(context);
        if (site < 0) {
            return -1;
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = topology.getGraphElements(realType).get(site);
        AbsoluteDirection absoluteResult = this.absoluteDirection;
        if (this.relativeDirection != null) {
            switch (this.relativeDirection) {
                case OppositeDirection -> {
                    final int from = context.from();
                    final int to = context.to();
                    final List<DirectionFacing> directionsSupported = topology.supportedDirections(realType);
                    for (final DirectionFacing direction : directionsSupported) {
                        final AbsoluteDirection absDirection = direction.toAbsolute();
                        final List<Step> steps = topology.trajectories().steps(realType, to, realType, absDirection);
                        boolean found = false;
                        for (final Step step : steps) {
                            if (step.to().id() == from) {
                                absoluteResult = absDirection;
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                    break;
                }
                case SameDirection -> {
                    final int from = context.from();
                    final int to = context.to();
                    final List<DirectionFacing> directionsSupported = topology.supportedDirections(realType);
                    for (final DirectionFacing direction : directionsSupported) {
                        final AbsoluteDirection absDirection = direction.toAbsolute();
                        final List<Step> steps = topology.trajectories().steps(realType, from, realType, absDirection);
                        boolean found = false;
                        for (final Step step : steps) {
                            if (step.to().id() == to) {
                                absoluteResult = absDirection;
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (absoluteResult == null) {
            final DirectionsFunction dirnChoice = new Directions(this.relativeDirection, null, null, null);
            final List<AbsoluteDirection> absDirections = dirnChoice.convertToAbsolute(realType, topology.getGraphElement(realType, site), null, null, null, context);
            if (absDirections.isEmpty()) {
                return -1;
            }
            absoluteResult = absDirections.get(0);
        }
        final int steps2 = this.stepsFn.eval(context);
        final List<Radial> radials = topology.trajectories().radials(realType, fromV.index(), absoluteResult);
        if (radials.size() < 1 || radials.get(0).steps().length < steps2 + 1) {
            return site;
        }
        return radials.get(0).steps()[steps2].id();
    }
    
    @Override
    public boolean isStatic() {
        return this.siteFn.isStatic() && this.stepsFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.siteFn.gameFlags(game) | this.stepsFn.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.siteFn.preprocess(game);
        this.stepsFn.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "ForwardSite(" + this.siteFn + ")";
    }
}
