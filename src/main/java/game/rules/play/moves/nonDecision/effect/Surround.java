// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.player.IsEnemy;
import game.functions.booleans.is.player.IsFriend;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.Piece;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.MoveUtilities;

import java.util.List;

public final class Surround extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final AbsoluteDirection dirnChoice;
    private final BooleanFunction targetRule;
    private final BooleanFunction friendRule;
    private final IntFunction exception;
    private final IntFunction withAtLeastPiece;
    private final Moves effect;
    private SiteType type;
    
    public Surround(@Opt final From from, @Opt final RelationType relation, @Opt final Between between, @Opt final To to, @Opt @Name final IntFunction except, @Opt @Name final Piece with, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.dirnChoice = ((relation == null) ? AbsoluteDirection.Adjacent : RelationType.convert(relation));
        this.targetRule = ((between == null || between.condition() == null) ? new IsEnemy(game.functions.ints.context.Between.instance(), null) : between.condition());
        this.friendRule = ((to == null || to.cond() == null) ? new IsFriend(game.functions.ints.context.To.instance(), null) : to.cond());
        this.effect = ((between == null || between.effect() == null) ? new Remove(null, game.functions.ints.context.Between.instance(), null, null, null, null) : between.effect());
        this.exception = ((except == null) ? new IntConstant(0) : except);
        this.withAtLeastPiece = ((with == null) ? null : with.component());
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int fromOrig = context.from();
        final int toOrig = context.to();
        final int betweenOrig = context.between();
        final int nbExcept = this.exception.eval(context);
        final int withPiece = (this.withAtLeastPiece == null) ? -1 : this.withAtLeastPiece.eval(context);
        final Topology graph = context.topology();
        if (from == -1) {
            return new BaseMoves(null);
        }
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<Radial> radialsFrom = graph.trajectories().radials(realType, fromV.index(), this.dirnChoice);
        for (final Radial radialFrom : radialsFrom) {
            if (radialFrom.steps().length < 2) {
                continue;
            }
            final int locationUnderThreat = radialFrom.steps()[1].id();
            if (!this.isTarget(context, locationUnderThreat)) {
                continue;
            }
            int except = 0;
            boolean withPieceOk = false;
            final List<Radial> radialsUnderThreat = graph.trajectories().radials(this.type, locationUnderThreat, this.dirnChoice);
            for (final Radial radialUnderThreat : radialsUnderThreat) {
                final int friendPieceSite = radialUnderThreat.steps()[1].id();
                final boolean isThreat = radialUnderThreat.steps().length < 2 || friendPieceSite == from || this.isFriend(context, friendPieceSite);
                if (!isThreat) {
                    ++except;
                }
                final int whatFriend = context.containerState(context.containerId()[friendPieceSite]).what(friendPieceSite, realType);
                if (withPiece == -1 || withPiece == whatFriend) {
                    withPieceOk = true;
                }
                if (except > nbExcept) {
                    break;
                }
            }
            if (except > nbExcept || !withPieceOk) {
                continue;
            }
            context.setBetween(locationUnderThreat);
            MoveUtilities.chainRuleCrossProduct(context, moves, this.effect, null, false);
        }
        context.setBetween(betweenOrig);
        context.setFrom(fromOrig);
        context.setTo(toOrig);
        return moves;
    }
    
    private final boolean isFriend(final Context context, final int location) {
        context.setTo(location);
        return this.friendRule.eval(context);
    }
    
    private final boolean isTarget(final Context context, final int location) {
        context.setBetween(location);
        return this.targetRule.eval(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.startLocationFn.gameFlags(game) | this.targetRule.gameFlags(game) | this.friendRule.gameFlags(game) | this.effect.gameFlags(game) | this.exception.gameFlags(game) | super.gameFlags(game);
        if (this.withAtLeastPiece != null) {
            gameFlags |= this.withAtLeastPiece.gameFlags(game);
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
        this.startLocationFn.preprocess(game);
        this.targetRule.preprocess(game);
        this.friendRule.preprocess(game);
        this.effect.preprocess(game);
        this.exception.preprocess(game);
        if (this.withAtLeastPiece != null) {
            this.withAtLeastPiece.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "Surrounded";
    }
}
