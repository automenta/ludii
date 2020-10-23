// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.sitePlayer;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.hidden.ActionSetVisible;

@Hide
public final class SetVisible extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    private final IntFunction levelFn;
    private final IntFunction playerFn;
    protected SiteType type;
    
    public SetVisible(@Opt final SiteType type, final IntFunction site, @Opt @Name final IntFunction level, @Opt @Or final Player player, @Opt @Or final RoleType role, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("SetVisible(): Only one player or role parameter must be non-null.");
        }
        this.siteFn = site;
        this.levelFn = level;
        this.playerFn = ((player == null && role == null) ? null : ((role != null) ? new Id(null, role) : player.index()));
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final int site = this.siteFn.eval(context);
        if (this.playerFn != null) {
            final int who = this.playerFn.eval(context);
            final ActionSetVisible action = (this.levelFn == null) ? new ActionSetVisible(this.type, site, -1, who) : new ActionSetVisible(this.type, site, this.levelFn.eval(context), who);
            final Move move = new Move(action);
            result.moves().add(move);
        }
        else {
            final int who = 1;
            final ActionSetVisible action = (this.levelFn == null) ? new ActionSetVisible(this.type, site, -1, 1) : new ActionSetVisible(this.type, site, this.levelFn.eval(context), 1);
            final Move move = new Move(action);
            for (int idPlayer = 2; idPlayer < context.players().size(); ++idPlayer) {
                final ActionSetVisible actionOtherPlayer = (this.levelFn == null) ? new ActionSetVisible(this.type, site, -1, idPlayer) : new ActionSetVisible(this.type, site, this.levelFn.eval(context), idPlayer);
                move.actions().add(actionOtherPlayer);
            }
            result.moves().add(move);
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.siteFn.gameFlags(game) | 0x8L | super.gameFlags(game);
        if (this.playerFn != null) {
            gameFlags |= this.playerFn.gameFlags(game);
        }
        if (this.levelFn != null) {
            gameFlags |= this.levelFn.gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.playerFn == null || this.playerFn.isStatic()) && (this.levelFn == null || this.levelFn.isStatic()) && this.siteFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        if (this.type == null) {
            this.type = game.board().defaultSite();
        }
        this.siteFn.preprocess(game);
        if (this.playerFn != null) {
            this.playerFn.preprocess(game);
        }
        if (this.levelFn != null) {
            this.levelFn.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "Observe";
    }
}
