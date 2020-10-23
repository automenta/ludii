// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.graph.Radial;
import game.util.graph.Step;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.To;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.move.ActionMove;

import java.util.List;

public final class Hop extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final DirectionsFunction dirnChoice;
    private final BooleanFunction goRule;
    private final BooleanFunction hurdleRule;
    private final BooleanFunction stopRule;
    private final Moves stopEffect;
    private final IntFunction maxDistanceFromHurdleFn;
    private final IntFunction minLengthHurdleFn;
    private final IntFunction maxLengthHurdleFn;
    private final IntFunction maxDistanceHurdleToFn;
    private final Moves sideEffect;
    private final boolean stack;
    protected SiteType type;
    
    public Hop(@Opt final From from, @Opt final Direction directions, @Opt final Between between, final To to, @Opt @Name final Boolean stack, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.maxDistanceFromHurdleFn = ((between == null || between.before() == null) ? new IntConstant(0) : between.before());
        this.minLengthHurdleFn = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().minFn());
        this.maxLengthHurdleFn = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().maxFn());
        this.maxDistanceHurdleToFn = ((between == null || between.after() == null) ? new IntConstant(0) : between.after());
        this.sideEffect = ((between == null) ? null : between.effect());
        this.goRule = to.cond();
        this.hurdleRule = ((between == null) ? BooleanConstant.construct(true) : between.condition());
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.stopRule = ((to.effect() == null) ? null : to.effect().condition());
        this.stopEffect = ((to.effect() == null) ? null : to.effect().effect());
        this.stack = (stack != null && stack);
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return moves;
        }
        final int origFrom = context.from();
        final int origTo = context.to();
        final int origBetween = context.between();
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        final int maxDistanceFromHurdle = this.maxDistanceFromHurdleFn.eval(context);
        final int minLengthHurdle = this.minLengthHurdleFn.eval(context);
        final int maxLengthHurdle = this.maxLengthHurdleFn.eval(context);
        final int maxDistanceHurdleTo = this.maxDistanceHurdleToFn.eval(context);
        if (minLengthHurdle == 0) {
            for (final AbsoluteDirection direction : directions) {
                final List<Step> steps = graph.trajectories().steps(realType, fromV.index(), realType, direction);
                for (final Step step : steps) {
                    final int to = step.to().id();
                    context.setFrom(from);
                    context.setTo(to);
                    if (!this.goRule.eval(context)) {
                        continue;
                    }
                    if (alreadyCompute(moves, from, to)) {
                        continue;
                    }
                    final ActionMove action = new ActionMove(this.type, from, -1, this.type, to, -1, -1, -1, this.stack);
                    if (this.isDecision()) {
                        action.setDecision(true);
                    }
                    Move thisAction = new Move(action);
                    if (this.stack) {
                        thisAction.setLevelMinNonDecision(0);
                        thisAction.setLevelMaxNonDecision(context.state().containerStates()[0].sizeStack(from, this.type) - 1);
                    }
                    thisAction = MoveUtilities.chainRuleWithAction(context, this.sideEffect, thisAction, true, false);
                    MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                    thisAction.setFromNonDecision(from);
                    thisAction.setToNonDecision(to);
                }
            }
            context.setTo(origTo);
            context.setFrom(origFrom);
        }
        if (maxLengthHurdle > 0) {
            for (final AbsoluteDirection direction : directions) {
                final List<Radial> radialList = graph.trajectories().radials(this.type, fromV.index(), direction);
                for (final Radial radial : radialList) {
                    int toIdx = 1;
                    while (toIdx < radial.steps().length) {
                        final int between = radial.steps()[toIdx].id();
                        context.setFrom(from);
                        context.setBetween(between);
                        if (this.hurdleRule != null && this.hurdleRule.eval(context)) {
                            final TIntArrayList hurdleLocs = new TIntArrayList();
                            hurdleLocs.add(between);
                            int lengthHurdle = 1;
                            int hurdleIdx = toIdx + 1;
                            boolean hurdleWrong = false;
                            while (hurdleIdx < radial.steps().length && lengthHurdle < maxLengthHurdle) {
                                final int hurdleLoc = radial.steps()[hurdleIdx].id();
                                context.setBetween(hurdleLoc);
                                if (!this.hurdleRule.eval(context)) {
                                    hurdleWrong = true;
                                    break;
                                }
                                hurdleLocs.add(hurdleLoc);
                                ++lengthHurdle;
                                ++hurdleIdx;
                            }
                            if (lengthHurdle < minLengthHurdle) {
                                break;
                            }
                            if (hurdleWrong && lengthHurdle == maxLengthHurdle) {
                                break;
                            }
                            for (int fromMinHurdle = lengthHurdle - minLengthHurdle; fromMinHurdle >= 0; --fromMinHurdle) {
                                int afterHurdleToIdx = hurdleIdx - fromMinHurdle;
                                while (afterHurdleToIdx < radial.steps().length) {
                                    final int afterHurdleTo = radial.steps()[afterHurdleToIdx].id();
                                    context.setFrom(from);
                                    context.setTo(afterHurdleTo);
                                    if (!this.goRule.eval(context)) {
                                        if (this.stopRule != null && this.stopRule.eval(context)) {
                                            final ActionMove action2 = new ActionMove(this.type, from, -1, this.type, afterHurdleTo, -1, -1, -1, this.stack);
                                            if (this.isDecision()) {
                                                action2.setDecision(true);
                                            }
                                            Move thisAction2 = new Move(action2);
                                            if (this.stopEffect != null) {
                                                thisAction2 = MoveUtilities.chainRuleWithAction(context, this.stopEffect, thisAction2, true, false);
                                            }
                                            MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction2, false);
                                            thisAction2.setFromNonDecision(from);
                                            thisAction2.setToNonDecision(afterHurdleTo);
                                            break;
                                        }
                                        break;
                                    }
                                    else {
                                        if (this.stopRule == null) {
                                            final ActionMove action2 = new ActionMove(this.type, from, -1, this.type, afterHurdleTo, -1, -1, -1, this.stack);
                                            if (this.isDecision()) {
                                                action2.setDecision(true);
                                            }
                                            Move thisAction2 = new Move(action2);
                                            if (this.stopEffect != null) {
                                                thisAction2 = MoveUtilities.chainRuleWithAction(context, this.stopEffect, thisAction2, true, false);
                                            }
                                            for (int hurdleLocIndex = 0; hurdleLocIndex < hurdleLocs.size() - fromMinHurdle; ++hurdleLocIndex) {
                                                final int hurdleLoc2 = hurdleLocs.getQuick(hurdleLocIndex);
                                                context.setBetween(hurdleLoc2);
                                                if (this.sideEffect != null) {
                                                    thisAction2 = MoveUtilities.chainRuleWithAction(context, this.sideEffect, thisAction2, true, false);
                                                }
                                            }
                                            MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction2, false);
                                            thisAction2.setFromNonDecision(from);
                                            thisAction2.setToNonDecision(afterHurdleTo);
                                            if (this.stack) {
                                                thisAction2.setLevelMinNonDecision(0);
                                                thisAction2.setLevelMaxNonDecision(context.state().containerStates()[0].sizeStack(from, this.type) - 1);
                                            }
                                        }
                                        if (afterHurdleToIdx - hurdleIdx + 1 > maxDistanceHurdleTo - fromMinHurdle) {
                                            break;
                                        }
                                        ++afterHurdleToIdx;
                                    }
                                }
                            }
                            break;
                        }
                        else {
                            context.setTo(between);
                            if (toIdx > maxDistanceFromHurdle) {
                                break;
                            }
                            if (!this.goRule.eval(context)) {
                                break;
                            }
                            ++toIdx;
                        }
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setBetween(origBetween);
        for (final Move m : moves.moves()) {
            m.setMover(context.state().mover());
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= 0x1L;
        if (this.stack) {
            gameFlags |= 0x10L;
        }
        if (this.goRule != null) {
            gameFlags |= this.goRule.gameFlags(game);
        }
        if (this.startLocationFn != null) {
            gameFlags |= this.startLocationFn.gameFlags(game);
        }
        if (this.hurdleRule != null) {
            gameFlags |= this.hurdleRule.gameFlags(game);
        }
        if (this.maxDistanceFromHurdleFn != null) {
            gameFlags |= this.maxDistanceFromHurdleFn.gameFlags(game);
        }
        if (this.maxDistanceHurdleToFn != null) {
            gameFlags |= this.maxDistanceHurdleToFn.gameFlags(game);
        }
        if (this.maxLengthHurdleFn != null) {
            gameFlags |= this.maxLengthHurdleFn.gameFlags(game);
        }
        if (this.minLengthHurdleFn != null) {
            gameFlags |= this.minLengthHurdleFn.gameFlags(game);
        }
        if (this.sideEffect != null) {
            gameFlags |= this.sideEffect.gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        if (this.goRule != null) {
            this.goRule.preprocess(game);
        }
        if (this.stopRule != null) {
            this.stopRule.preprocess(game);
        }
        if (this.stopEffect != null) {
            this.stopEffect.preprocess(game);
        }
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.hurdleRule != null) {
            this.hurdleRule.preprocess(game);
        }
        if (this.maxDistanceFromHurdleFn != null) {
            this.maxDistanceFromHurdleFn.preprocess(game);
        }
        if (this.maxDistanceHurdleToFn != null) {
            this.maxDistanceHurdleToFn.preprocess(game);
        }
        if (this.maxLengthHurdleFn != null) {
            this.maxLengthHurdleFn.preprocess(game);
        }
        if (this.minLengthHurdleFn != null) {
            this.minLengthHurdleFn.preprocess(game);
        }
        if (this.sideEffect != null) {
            this.sideEffect.preprocess(game);
        }
    }
    
    public BooleanFunction goRule() {
        return this.goRule;
    }
    
    public BooleanFunction hurdleRule() {
        return this.hurdleRule;
    }
    
    public IntFunction maxDistanceFromHurdleFn() {
        return this.maxDistanceFromHurdleFn;
    }
    
    public IntFunction minLengthHurdleFn() {
        return this.minLengthHurdleFn;
    }
    
    public IntFunction maxLengthHurdleFn() {
        return this.maxLengthHurdleFn;
    }
    
    public IntFunction maxDistanceHurdleToFn() {
        return this.maxDistanceHurdleToFn;
    }
    
    private static boolean alreadyCompute(final Moves moves, final int from, final int to) {
        for (final Move m : moves.moves()) {
            if (m.fromNonDecision() == from && m.toNonDecision() == to) {
                return true;
            }
        }
        return false;
    }
}
