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
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.move.ActionMove;

import java.util.List;

public final class Step extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final RegionFunction startRegionFn;
    private final BooleanFunction rule;
    private final Moves sideEffect;
    private final boolean stack;
    private final DirectionsFunction dirnChoice;
    private final To toRule;
    private SiteType type;
    
    public Step(@Opt final From from, @Opt final Direction directions, final To to, @Opt @Name final Boolean stack, @Opt final Then then) {
        super(then);
        if (from != null) {
            this.startRegionFn = from.region();
            this.startLocationFn = from.loc();
        }
        else {
            this.startRegionFn = null;
            this.startLocationFn = new game.functions.ints.context.From(null);
        }
        this.type = ((from == null) ? null : from.type());
        this.rule = ((to == null) ? BooleanConstant.construct(true) : ((to.cond() == null) ? BooleanConstant.construct(true) : to.cond()));
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.sideEffect = ((to == null) ? null : to.effect());
        this.stack = (stack != null && stack);
        this.toRule = to;
    }
    
    @Override
    public Moves eval(final Context context) {
        if (this.startRegionFn != null) {
            return this.evalRegion(context);
        }
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return moves;
        }
        final int origFrom = context.from();
        final int origTo = context.to();
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= graph.getGraphElements(realType).size()) {
            return moves;
        }
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        for (final AbsoluteDirection direction : directions) {
            final List<game.util.graph.Step> steps = graph.trajectories().steps(realType, fromV.index(), realType, direction);
            for (final game.util.graph.Step step : steps) {
                final int to = step.to().id();
                context.setFrom(from);
                context.setTo(to);
                if (!this.rule.eval(context)) {
                    continue;
                }
                if (alreadyCompute(moves, from, to)) {
                    continue;
                }
                final int level = context.game().isStacking() ? (this.stack ? -1 : (context.state().containerStates()[0].sizeStack(from, this.type) - 1)) : -1;
                final ActionMove action = new ActionMove(this.type, from, level, this.type, to, -1, -1, -1, this.stack);
                if (this.isDecision()) {
                    action.setDecision(true);
                }
                Move move = new Move(action);
                if (this.stack) {
                    move.setLevelMinNonDecision(0);
                    move.setLevelMaxNonDecision(context.state().containerStates()[0].sizeStack(from, this.type) - 1);
                }
                move = MoveUtilities.chainRuleWithAction(context, this.sideEffect, move, true, false);
                MoveUtilities.chainRuleCrossProduct(context, moves, null, move, false);
                move.setFromNonDecision(from);
                move.setToNonDecision(to);
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
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
    
    private Moves evalRegion(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int fromLocforRegion = this.startLocationFn.eval(context);
        final int[] froms = this.startRegionFn.eval(context).sites();
        if (froms.length == 0) {
            return moves;
        }
        final int origFrom = context.from();
        final int origTo = context.to();
        for (final int from : froms) {
            if (from != -1) {
                final Topology graph = context.topology();
                final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
                if (from >= graph.getGraphElements(realType).size()) {
                    return moves;
                }
                final TopologyElement fromV = graph.getGraphElements(realType).get(from);
                final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
                for (final AbsoluteDirection direction : directions) {
                    final List<game.util.graph.Step> steps = graph.trajectories().steps(realType, fromV.index(), direction);
                    for (final game.util.graph.Step step : steps) {
                        final int to = step.to().id();
                        context.setTo(to);
                        if (!this.rule.eval(context)) {
                            continue;
                        }
                        final ActionMove action = new ActionMove(SiteType.Cell, from, -1, SiteType.Cell, to, -1, -1, -1, this.stack);
                        if (this.isDecision()) {
                            action.setDecision(true);
                        }
                        Move thisAction = new Move(action);
                        if (this.stack) {
                            thisAction.setLevelMinNonDecision(0);
                            thisAction.setLevelMaxNonDecision(context.state().containerStates()[0].sizeStack(from, this.type) - 1);
                        }
                        final int origFrom2 = context.from();
                        final int origTo2 = context.to();
                        context.setFrom(fromLocforRegion);
                        context.setTo(from);
                        thisAction = new Move(action, this.sideEffect.eval(context).moves().get(0));
                        context.setFrom(origFrom2);
                        context.setTo(origTo2);
                        MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                        thisAction.setFromNonDecision(from);
                        thisAction.setToNonDecision(to);
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.rule.gameFlags(game);
        gameFlags |= 0x1L;
        if (this.stack) {
            gameFlags |= 0x10L;
        }
        if (this.startLocationFn != null) {
            gameFlags |= this.startLocationFn.gameFlags(game);
        }
        if (this.startRegionFn != null) {
            gameFlags |= this.startRegionFn.gameFlags(game);
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
        this.rule.preprocess(game);
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.startRegionFn != null) {
            this.startRegionFn.preprocess(game);
        }
        if (this.sideEffect != null) {
            this.sideEffect.preprocess(game);
        }
        if (this.rule != null) {
            this.rule.preprocess(game);
        }
        if (this.sideEffect != null) {
            this.sideEffect.preprocess(game);
        }
        if (this.dirnChoice != null) {
            this.dirnChoice.preprocess(game);
        }
    }
    
    private static boolean alreadyCompute(final Moves moves, final int from, final int to) {
        for (final Move m : moves.moves()) {
            if (m.fromNonDecision() == from && m.toNonDecision() == to) {
                return true;
            }
        }
        return false;
    }
    
    public BooleanFunction goRule() {
        return this.rule;
    }
    
    public DirectionsFunction directions() {
        return this.dirnChoice;
    }
    
    public To toRule() {
        return this.toRule;
    }
}
