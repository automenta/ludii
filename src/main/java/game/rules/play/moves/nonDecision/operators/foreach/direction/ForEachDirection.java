// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.direction;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import game.util.graph.Step;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.MoveUtilities;

import java.util.List;

@Hide
public final class ForEachDirection extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final IntFunction min;
    private final IntFunction limit;
    private final DirectionsFunction dirnChoice;
    private final BooleanFunction rule;
    private final BooleanFunction betweenRule;
    private final Moves movesToApply;
    private SiteType type;
    
    public ForEachDirection(@Opt final From from, @Opt final Direction directions, @Opt final Between between, @Or final To to, @Or final Moves moves, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.limit = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().maxFn());
        this.min = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().minFn());
        this.betweenRule = ((between != null) ? between.condition() : null);
        this.rule = ((to != null) ? to.cond() : null);
        this.movesToApply = ((to != null && to.effect() != null) ? to.effect().effect() : moves);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final List<DirectionFacing> directionsSupported = graph.supportedDirections(realType);
        final int minPathLength = this.min.eval(context);
        final int maxPathLength = this.limit.eval(context);
        final int contextFrom = context.from();
        final int contextTo = context.to();
        final Component component = context.components()[context.containerState(context.containerId()[contextFrom]).what(contextFrom, this.type)];
        if (component == null) {
            return moves;
        }
        DirectionFacing newDirection = null;
        if (contextTo != -1) {
            for (final DirectionFacing direction : directionsSupported) {
                final AbsoluteDirection absoluteDirection = direction.toAbsolute();
                final List<Step> steps = graph.trajectories().steps(realType, context.from(), realType, absoluteDirection);
                for (final Step step : steps) {
                    if (step.to().id() == contextTo) {
                        newDirection = direction;
                        break;
                    }
                }
            }
        }
        final int origFrom = context.from();
        final int origBetween = context.between();
        final int origTo = context.to();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, component, newDirection, null, context);
        for (final AbsoluteDirection direction2 : directions) {
            final List<Radial> radials = graph.trajectories().radials(this.type, fromV.index(), direction2);
            for (final Radial radial : radials) {
                for (int toIdx = 1; toIdx < radial.steps().length && toIdx <= maxPathLength; ++toIdx) {
                    final int to = radial.steps()[toIdx].id();
                    if (this.betweenRule != null && minPathLength > 1 && toIdx < minPathLength) {
                        context.setBetween(to);
                        if (!this.betweenRule.eval(context)) {
                            break;
                        }
                        context.setBetween(origBetween);
                    }
                    context.setTo(to);
                    if (this.rule != null && !this.rule.eval(context)) {
                        break;
                    }
                    if (toIdx >= minPathLength) {
                        final Moves movesApplied = this.movesToApply.eval(context);
                        for (final Move m : movesApplied.moves()) {
                            final int saveFrom = context.from();
                            final int saveTo = context.to();
                            context.setFrom(to);
                            context.setTo(-1);
                            MoveUtilities.chainRuleCrossProduct(context, moves, null, m, false);
                            context.setTo(saveTo);
                            context.setFrom(saveFrom);
                        }
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setBetween(origBetween);
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
        long gameFlags = super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.movesToApply.gameFlags(game);
        if (this.startLocationFn != null) {
            gameFlags |= this.startLocationFn.gameFlags(game);
        }
        if (this.min != null) {
            gameFlags |= this.min.gameFlags(game);
        }
        if (this.limit != null) {
            gameFlags |= this.limit.gameFlags(game);
        }
        if (this.rule != null) {
            gameFlags |= this.rule.gameFlags(game);
        }
        if (this.betweenRule != null) {
            gameFlags |= this.betweenRule.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.startLocationFn == null || this.startLocationFn.isStatic()) && (this.rule == null || this.rule.isStatic()) && (this.betweenRule == null || this.betweenRule.isStatic()) && this.movesToApply.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        if (this.rule != null) {
            this.rule.preprocess(game);
        }
        if (this.betweenRule != null) {
            this.betweenRule.preprocess(game);
        }
        this.movesToApply.preprocess(game);
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.min != null) {
            this.min.preprocess(game);
        }
        if (this.limit != null) {
            this.limit.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "ForDirn";
    }
}
