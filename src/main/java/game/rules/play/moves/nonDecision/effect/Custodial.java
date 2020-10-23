// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.player.IsEnemy;
import game.functions.booleans.is.player.IsFriend;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.MoveUtilities;

import java.util.List;

public final class Custodial extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final AbsoluteDirection dirnChoice;
    private final IntFunction limit;
    private final BooleanFunction targetRule;
    private final BooleanFunction friendRule;
    private final Moves targetEffect;
    private SiteType type;
    
    public Custodial(@Opt final From from, @Opt final AbsoluteDirection dirnChoice, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.limit = ((between == null || between.range() == null) ? new IntConstant(1000) : between.range().maxFn());
        this.dirnChoice = ((dirnChoice == null) ? AbsoluteDirection.Adjacent : dirnChoice);
        this.targetRule = ((between == null || between.condition() == null) ? new IsEnemy(game.functions.ints.context.Between.instance(), null) : between.condition());
        this.friendRule = ((to == null || to.cond() == null) ? new IsFriend(game.functions.ints.context.To.instance(), null) : to.cond());
        this.targetEffect = ((between == null || between.effect() == null) ? new Remove(null, game.functions.ints.context.Between.instance(), null, null, null, null) : between.effect());
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int fromOrig = context.from();
        final int toOrig = context.to();
        final int betweenOrig = context.between();
        final Topology graph = context.topology();
        if (from == -1) {
            return new BaseMoves(null);
        }
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<Radial> radialList = graph.trajectories().radials(this.type, fromV.index(), this.dirnChoice);
        final int maxPathLength = this.limit.eval(context);
        if (maxPathLength == 1) {
            this.shortSandwich(context, moves, fromV, radialList);
        }
        else {
            this.longSandwich(context, moves, fromV, radialList, maxPathLength);
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        context.setBetween(betweenOrig);
        context.setTo(toOrig);
        context.setFrom(fromOrig);
        return moves;
    }
    
    private final void shortSandwich(final Context context, final Moves actions, final TopologyElement fromV, final List<Radial> radials) {
        for (final Radial radial : radials) {
            if (radial.steps().length >= 3 && this.isTarget(context, radial.steps()[1].id())) {
                if (!this.isFriend(context, radial.steps()[2].id())) {
                    continue;
                }
                context.setBetween(radial.steps()[1].id());
                MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
            }
        }
    }
    
    private final boolean isFriend(final Context context, final int location) {
        context.setTo(location);
        return this.friendRule.eval(context);
    }
    
    private final boolean isTarget(final Context context, final int location) {
        context.setBetween(location);
        return this.targetRule.eval(context);
    }
    
    private final void longSandwich(final Context context, final Moves actions, final TopologyElement fromV, final List<Radial> radials, final int maxPathLength) {
        for (final Radial radial : radials) {
            boolean foundEnemy = false;
            int posIdx;
            for (posIdx = 1; posIdx < radial.steps().length && posIdx <= maxPathLength && this.isTarget(context, radial.steps()[posIdx].id()); ++posIdx) {
                foundEnemy = true;
            }
            if (!foundEnemy) {
                continue;
            }
            final int friendPos = (posIdx < radial.steps().length) ? radial.steps()[posIdx].id() : -1;
            if (!this.isFriend(context, friendPos)) {
                continue;
            }
            for (int i = 1; i < posIdx; ++i) {
                context.setBetween(radial.steps()[i].id());
                MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
            }
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.startLocationFn.gameFlags(game) | this.limit.gameFlags(game) | this.targetRule.gameFlags(game) | this.friendRule.gameFlags(game) | this.targetEffect.gameFlags(game);
        gameFlags |= 0x1L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.startLocationFn.isStatic() && this.limit.isStatic() && this.targetRule.isStatic() && this.friendRule.isStatic() && this.targetEffect.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.startLocationFn.preprocess(game);
        this.limit.preprocess(game);
        this.targetRule.preprocess(game);
        this.friendRule.preprocess(game);
        this.targetEffect.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Custodial";
    }
}
