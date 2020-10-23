// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.player.IsEnemy;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastFrom;
import game.functions.ints.last.LastTo;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import game.util.graph.Step;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.MoveUtilities;

import java.util.List;

public final class DirectionCapture extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final BooleanFunction targetRule;
    private final BooleanFunction oppositeFn;
    private final Moves effect;
    private SiteType type;
    
    public DirectionCapture(@Opt final From from, @Opt final To to, @Opt @Name final BooleanFunction opposite, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.targetRule = ((to == null || to.cond() == null) ? new IsEnemy(game.functions.ints.context.To.instance(), null) : to.cond());
        this.effect = ((to == null || to.effect() == null) ? new Remove(null, new game.functions.ints.context.From(null), null, null, null, null) : to.effect());
        this.oppositeFn = ((opposite == null) ? BooleanConstant.construct(false) : opposite);
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int fromOrig = context.from();
        final int toOrig = context.to();
        final Topology topology = context.topology();
        final boolean opposite = this.oppositeFn.eval(context);
        AbsoluteDirection lastDirection = null;
        final int lastFrom = new LastFrom(null).eval(context);
        final int lastTo = new LastTo(null).eval(context);
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = topology.getGraphElements(realType).get(from);
        final List<DirectionFacing> directions = topology.supportedDirections(realType);
        for (final DirectionFacing direction : directions) {
            final AbsoluteDirection absoluteDirection = direction.toAbsolute();
            final List<Step> steps = topology.trajectories().steps(realType, lastFrom, realType, absoluteDirection);
            for (final Step step : steps) {
                if (step.to().id() == lastTo) {
                    lastDirection = (opposite ? direction.opposite().toAbsolute() : absoluteDirection);
                    break;
                }
            }
        }
        final List<Radial> radials = topology.trajectories().radials(this.type, fromV.index(), lastDirection);
        for (final Radial radial : radials) {
            for (int i = 1; i < radial.steps().length; ++i) {
                final int locUnderThreat = radial.steps()[i].id();
                if (!this.isTarget(context, locUnderThreat)) {
                    break;
                }
                final int saveFrom = context.from();
                final int saveTo = context.to();
                context.setFrom(-1);
                context.setTo(locUnderThreat);
                MoveUtilities.chainRuleCrossProduct(context, moves, this.effect, null, false);
                context.setTo(saveTo);
                context.setFrom(saveFrom);
            }
        }
        if (lastDirection == null) {
            return moves;
        }
        context.setFrom(fromOrig);
        context.setTo(toOrig);
        return moves;
    }
    
    private final boolean isTarget(final Context context, final int location) {
        context.setTo(location);
        return this.targetRule.eval(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.startLocationFn.gameFlags(game) | this.targetRule.gameFlags(game) | this.effect.gameFlags(game) | super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.startLocationFn.isStatic() && this.targetRule.isStatic() && this.effect.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.startLocationFn.preprocess(game);
        this.targetRule.preprocess(game);
        this.effect.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "DirectionCapture";
    }
}
