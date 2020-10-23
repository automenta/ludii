// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.player.IsEnemy;
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
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.MoveUtilities;

import java.util.List;

public final class Intervene extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final AbsoluteDirection dirnChoice;
    private final IntFunction limit;
    private final IntFunction min;
    private final BooleanFunction targetRule;
    private final Moves targetEffect;
    private SiteType type;
    
    public Intervene(@Opt final From from, @Opt final AbsoluteDirection dirnChoice, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.limit = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().maxFn());
        this.min = ((between == null || between.range() == null) ? new IntConstant(1) : between.range().minFn());
        this.dirnChoice = ((dirnChoice == null) ? AbsoluteDirection.Adjacent : dirnChoice);
        this.targetRule = ((to == null || to.cond() == null) ? new IsEnemy(game.functions.ints.context.To.instance(), null) : to.cond());
        this.targetEffect = ((to == null || to.effect() == null) ? new Remove(null, game.functions.ints.context.To.instance(), null, null, null, null) : to.effect());
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int fromOrig = context.from();
        final int toOrig = context.to();
        final Topology graph = context.topology();
        if (from == -1) {
            return new BaseMoves(null);
        }
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<Radial> radialList = graph.trajectories().radials(this.type, fromV.index()).distinctInDirection(this.dirnChoice);
        final int minPathLength = this.min.eval(context);
        final int maxPathLength = this.limit.eval(context);
        if (maxPathLength == 1 && minPathLength == 1) {
            this.shortSandwich(context, moves, fromV, radialList);
        }
        else {
            this.longSandwich(context, moves, fromV, radialList, maxPathLength, minPathLength);
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        context.setTo(toOrig);
        context.setFrom(fromOrig);
        return moves;
    }
    
    private void shortSandwich(final Context context, final Moves actions, final TopologyElement fromV, final List<Radial> radials) {
        for (final Radial radial : radials) {
            if (radial.steps().length >= 2) {
                if (!this.isTarget(context, radial.steps()[1].id())) {
                    continue;
                }
                final List<Radial> oppositeRadials = radial.opposites();
                if (oppositeRadials == null) {
                    continue;
                }
                boolean oppositeFound = false;
                for (final Radial oppositeRadial : oppositeRadials) {
                    if (oppositeRadial.steps().length >= 2) {
                        if (!this.isTarget(context, oppositeRadial.steps()[1].id())) {
                            continue;
                        }
                        context.setTo(oppositeRadial.steps()[1].id());
                        MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
                        oppositeFound = true;
                    }
                }
                if (oppositeFound) {
                    context.setTo(radial.steps()[1].id());
                }
                MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
            }
        }
    }
    
    private boolean isTarget(final Context context, final int location) {
        context.setTo(location);
        return this.targetRule.eval(context);
    }
    
    private void longSandwich(final Context context, final Moves actions, final TopologyElement fromV, final List<Radial> radials, final int maxPathLength, final int minPathLength) {
        for (final Radial radial : radials) {
            final TIntArrayList sitesToIntervene = new TIntArrayList();
            for (int posIdx = 1; posIdx < radial.steps().length && posIdx <= maxPathLength && this.isTarget(context, radial.steps()[posIdx].id()); ++posIdx) {
                sitesToIntervene.add(radial.steps()[posIdx].id());
            }
            if (sitesToIntervene.size() >= minPathLength) {
                if (sitesToIntervene.size() > maxPathLength) {
                    continue;
                }
                final List<Radial> oppositeRadials = radial.opposites();
                if (oppositeRadials == null) {
                    continue;
                }
                final TIntArrayList sitesOppositeToIntervene = new TIntArrayList();
                boolean oppositeFound = false;
                for (final Radial oppositeRadial : oppositeRadials) {
                    for (int posOppositeIdx = 1; posOppositeIdx < oppositeRadial.steps().length && posOppositeIdx <= maxPathLength && this.isTarget(context, oppositeRadial.steps()[posOppositeIdx].id()); ++posOppositeIdx) {
                        sitesOppositeToIntervene.add(oppositeRadial.steps()[posOppositeIdx].id());
                    }
                    if (sitesOppositeToIntervene.size() >= minPathLength) {
                        if (sitesOppositeToIntervene.size() > maxPathLength) {
                            continue;
                        }
                        for (int i = 0; i < sitesOppositeToIntervene.size(); ++i) {
                            final int oppositeSite = sitesOppositeToIntervene.get(i);
                            context.setTo(oppositeSite);
                            MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
                        }
                        oppositeFound = true;
                    }
                }
                if (!oppositeFound) {
                    continue;
                }
                for (int j = 0; j < sitesToIntervene.size(); ++j) {
                    final int oppositeSite2 = sitesToIntervene.get(j);
                    context.setTo(oppositeSite2);
                    MoveUtilities.chainRuleCrossProduct(context, actions, this.targetEffect, null, false);
                }
            }
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.startLocationFn.gameFlags(game) | this.limit.gameFlags(game) | this.min.gameFlags(game) | this.targetRule.gameFlags(game) | this.targetEffect.gameFlags(game);
        gameFlags |= 0x1L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.startLocationFn.isStatic() && this.limit.isStatic() && this.targetRule.isStatic() && this.targetEffect.isStatic() && this.min.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.startLocationFn.preprocess(game);
        this.min.preprocess(game);
        this.limit.preprocess(game);
        this.targetRule.preprocess(game);
        this.targetEffect.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Custodial";
    }
}
