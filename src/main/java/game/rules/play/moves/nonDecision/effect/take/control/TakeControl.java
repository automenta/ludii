// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.take.control;

import annotations.*;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.move.ActionAdd;
import util.action.move.ActionRemove;
import util.state.containerState.ContainerState;

@Hide
public final class TakeControl extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction master;
    private final IntFunction slave;
    private SiteType type;
    
    public TakeControl(@Or @Name final RoleType of, @Or @Name final IntFunction Of, @Or2 @Name final RoleType by, @Or2 @Name final IntFunction By, @Opt final SiteType type, @Opt final Then then) {
        super(then);
        this.master = ((by != null) ? new Id(null, by) : By);
        this.slave = ((of != null) ? new Id(null, of) : Of);
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final int newOwner = this.master.eval(context);
        final int previousOwner = this.slave.eval(context);
        final boolean add = newOwner - previousOwner > 0;
        final int diff = Math.abs(newOwner - previousOwner);
        final TIntArrayList slaveOwnedSites = context.state().owned().sites(previousOwner);
        for (int i = 0; i < slaveOwnedSites.size(); ++i) {
            final int site = slaveOwnedSites.getQuick(i);
            final ContainerState cs = context.containerState(context.containerId()[site]);
            final int what = cs.what(site, this.type);
            final int state = cs.state(site, this.type);
            final int rotation = cs.rotation(site, this.type);
            final int count = cs.count(site, this.type);
            final int newWhat = add ? (what + diff) : (what - diff);
            final ActionRemove actionRemove = new ActionRemove(this.type, site, true);
            final ActionAdd actionAdd = new ActionAdd(this.type, site, newWhat, count, state, rotation, null, null, null);
            final Move move = new Move(actionRemove);
            move.actions().add(actionAdd);
            move.setFromNonDecision(site);
            move.setToNonDecision(site);
            result.moves().add(move);
        }
        if (this.then() != null) {
            for (int j = 0; j < result.moves().size(); ++j) {
                result.moves().get(j).then().add(this.then().moves());
            }
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= this.master.gameFlags(game);
        gameFlags |= this.slave.gameFlags(game);
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
        this.master.preprocess(game);
        this.slave.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "TakeControl";
    }
}
