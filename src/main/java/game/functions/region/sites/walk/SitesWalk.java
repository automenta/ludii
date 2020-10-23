// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.walk;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.context.From;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.util.directions.DirectionFacing;
import game.util.equipment.Region;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import util.ContainerId;
import util.Context;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class SitesWalk extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final StepType[][] possibleSteps;
    private final BooleanFunction rotations;
    
    public SitesWalk(@Opt final SiteType type, @Opt final IntFunction startLocationFn, final StepType[][] possibleSteps, @Opt @Name final BooleanFunction rotations) {
        this.startLocationFn = ((startLocationFn == null) ? new From(null) : startLocationFn);
        this.type = type;
        this.possibleSteps = possibleSteps;
        this.rotations = ((rotations == null) ? BooleanConstant.construct(true) : rotations);
    }
    
    @Override
    public Region eval(final Context context) {
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return new Region(new TIntArrayList().toArray());
        }
        final int cid = new ContainerId(null, null, null, null, new IntConstant(from)).eval(context);
        final Topology graph = context.containers()[cid].topology();
        final boolean allRotations = this.rotations.eval(context);
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final List<DirectionFacing> orthogonalSupported = graph.supportedOrthogonalDirections(realType);
        List<DirectionFacing> walkDirection;
        if (allRotations) {
            walkDirection = graph.supportedOrthogonalDirections(realType);
        }
        else {
            walkDirection = new ArrayList<>();
            walkDirection.add(graph.supportedOrthogonalDirections(realType).get(0));
        }
        final TIntArrayList sitesAfterWalk = new TIntArrayList();
        for (final DirectionFacing startDirection : walkDirection) {
            for (final StepType[] steps : this.possibleSteps) {
                int currentLoc = from;
                DirectionFacing currentDirection = startDirection;
                for (final StepType step : steps) {
                    if (step == StepType.F) {
                        final List<Step> stepsDirection = graph.trajectories().steps(realType, currentLoc, currentDirection.toAbsolute());
                        int to = -1;
                        for (final Step stepDirection : stepsDirection) {
                            if (stepDirection.from().siteType() != stepDirection.to().siteType()) {
                                continue;
                            }
                            to = stepDirection.to().id();
                        }
                        if ((currentLoc = to) == -1) {
                            break;
                        }
                    }
                    else if (step == StepType.R) {
                        for (currentDirection = currentDirection.right(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.right()) {}
                    }
                    else if (step == StepType.L) {
                        for (currentDirection = currentDirection.left(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.left()) {}
                    }
                }
                if (currentLoc != -1) {
                    sitesAfterWalk.add(currentLoc);
                }
            }
        }
        return new Region(sitesAfterWalk.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.rotations.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.startLocationFn.preprocess(game);
        this.rotations.preprocess(game);
    }
}
