// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.in.IsIn;
import game.functions.directions.Directions;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.sites.Sites;
import game.functions.region.sites.SitesIndexType;
import game.functions.region.sites.index.SitesEmpty;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
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
import util.Move;
import util.MoveUtilities;
import util.action.Action;
import util.action.move.ActionAdd;

import java.util.List;

public final class Shoot extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final Directions dirnChoice;
    private final BooleanFunction goRule;
    private final BooleanFunction toRule;
    private final IntFunction pieceFn;
    protected SiteType type;
    
    public Shoot(final Piece what, @Opt final From from, @Opt final AbsoluteDirection dirn, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new LastTo(null) : from.loc());
        this.goRule = ((between == null || between.condition() == null) ? IsIn.construct(null, new IntFunction[] { game.functions.ints.context.Between.instance() }, SitesEmpty.construct(null, null)) : between.condition());
        this.toRule = ((to == null) ? IsIn.construct(null, new IntFunction[] { game.functions.ints.context.To.instance() }, Sites.construct(SitesIndexType.Empty, SiteType.Cell, null)) : to.cond());
        this.dirnChoice = ((dirn == null) ? new Directions(AbsoluteDirection.Adjacent, null) : new Directions(dirn, null));
        this.pieceFn = what.component();
        this.type = ((from == null) ? null : from.type());
    }
    
    @Override
    public final Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
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
        final int pieceType = this.pieceFn.eval(context);
        if (pieceType < 0) {
            return moves;
        }
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        for (final AbsoluteDirection direction : directions) {
            final List<Radial> radialList = graph.trajectories().radials(this.type, fromV.index(), direction);
            for (final Radial radial : radialList) {
                context.setBetween(origBetween);
                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                    final int to = radial.steps()[toIdx].id();
                    context.setTo(to);
                    if (!this.toRule.eval(context)) {
                        break;
                    }
                    context.setBetween(to);
                    final Action actionAdd = new ActionAdd(this.type, to, pieceType, 1, -1, -1, null, null, null);
                    if (this.isDecision()) {
                        actionAdd.setDecision(true);
                    }
                    final Move thisAction = new Move(actionAdd);
                    MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                    thisAction.setFromNonDecision(to);
                    thisAction.setToNonDecision(to);
                }
            }
        }
        for (final Move m : moves.moves()) {
            m.setMover(context.state().mover());
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setBetween(origBetween);
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = super.gameFlags(game);
        flags |= 0x1L;
        if (this.startLocationFn != null) {
            flags |= this.startLocationFn.gameFlags(game);
        }
        if (this.goRule != null) {
            flags |= this.goRule.gameFlags(game);
        }
        if (this.toRule != null) {
            flags |= this.toRule.gameFlags(game);
        }
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.type = SiteType.use(this.type, game);
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.goRule != null) {
            this.goRule.preprocess(game);
        }
        if (this.toRule != null) {
            this.toRule.preprocess(game);
        }
    }
    
    public BooleanFunction goRule() {
        return this.goRule;
    }
}
