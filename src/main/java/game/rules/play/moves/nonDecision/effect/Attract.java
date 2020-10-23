// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.directions.Directions;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import game.util.moves.From;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionRemove;
import util.state.containerState.ContainerState;

import java.util.List;

public final class Attract extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final AbsoluteDirection dirnChoice;
    protected SiteType type;
    
    public Attract(@Opt final From from, @Opt final AbsoluteDirection dirn, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.dirnChoice = ((dirn == null) ? AbsoluteDirection.Adjacent : dirn);
        this.type = ((from == null) ? null : from.type());
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return moves;
        }
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final ContainerState containerState = context.state().containerStates()[0];
        final List<AbsoluteDirection> directions = new Directions(this.dirnChoice, null).convertToAbsolute(realType, fromV, null, null, null, context);
        for (final AbsoluteDirection direction : directions) {
            final List<Radial> radialList = graph.trajectories().radials(this.type, fromV.index(), direction);
            for (final Radial radial : radialList) {
                final TIntArrayList piecesInThisDirection = new TIntArrayList();
                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                    final int to = radial.steps()[toIdx].id();
                    final int what = containerState.what(to, realType);
                    if (what != 0) {
                        piecesInThisDirection.add(what);
                        final ActionRemove removeAction = new ActionRemove(context.board().defaultSite(), to, true);
                        final Move move = new Move(removeAction);
                        moves.moves().add(move);
                    }
                }
                for (int toIdx = 1; toIdx <= piecesInThisDirection.size(); ++toIdx) {
                    final int to = radial.steps()[toIdx].id();
                    final int what = piecesInThisDirection.getQuick(toIdx - 1);
                    final Action actionAdd = new ActionAdd(this.type, to, what, 1, -1, -1, null, null, null);
                    final Move move = new Move(actionAdd);
                    moves.moves().add(move);
                }
            }
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
        long gameFlags = super.gameFlags(game) | this.startLocationFn.gameFlags(game);
        gameFlags |= 0x1L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.startLocationFn.preprocess(game);
        this.type = SiteType.use(this.type, game);
    }
}
