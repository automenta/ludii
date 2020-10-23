// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import game.util.moves.From;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionRemove;
import util.state.containerState.ContainerState;

import java.util.List;

public final class Push extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final AbsoluteDirection dirnChoice;
    protected SiteType type;
    
    public Push(@Opt final From from, final AbsoluteDirection dirn, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.dirnChoice = (dirn);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return moves;
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = topology.getGraphElements(realType).get(from);
        final ContainerState cs = context.state().containerStates()[0];
        final List<Radial> radials = topology.trajectories().radials(this.type, fromV.index(), this.dirnChoice);
        for (final Radial radial : radials) {
            int currentPiece = cs.what(radial.steps()[0].id(), realType);
            final ActionRemove removeAction = new ActionRemove(realType, from, true);
            moves.moves().add(new Move(removeAction));
            for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                final int to = radial.steps()[toIdx].id();
                final int what = cs.what(to, realType);
                if (what == 0) {
                    final Action actionAdd = new ActionAdd(realType, to, currentPiece, 1, -1, -1, null, null, null);
                    final Move move = new Move(actionAdd);
                    moves.moves().add(move);
                    break;
                }
                final ActionRemove removeTo = new ActionRemove(realType, to, true);
                moves.moves().add(new Move(removeTo));
                final Action actionAdd2 = new ActionAdd(realType, to, currentPiece, 1, -1, -1, null, null, null);
                final Move move2 = new Move(actionAdd2);
                moves.moves().add(move2);
                currentPiece = what;
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
        long gameFlags = this.startLocationFn.gameFlags(game) | super.gameFlags(game);
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
        this.type = SiteType.use(this.type, game);
        this.startLocationFn.preprocess(game);
    }
}
